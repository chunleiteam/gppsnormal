package gpps.service.impl;

import gpps.dao.IBorrowerDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.ILenderDao;
import gpps.dao.IPayBackDao;
import gpps.dao.IProductDao;
import gpps.dao.ISubmitDao;
import gpps.inner.service.IInnerPayBackService;
import gpps.inner.service.IInnerThirdPaySupportService;
import gpps.inner.service.impl.InnerPayBackServiceImpl;
import gpps.model.Borrower;
import gpps.model.CashStream;
import gpps.model.GovermentOrder;
import gpps.model.Lender;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.Submit;
import gpps.service.IAccountService;
import gpps.service.exception.SMSException;
import gpps.service.message.ILetterSendService;
import gpps.service.message.IMessageService;
import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.IAuditRepayService;
import gpps.service.thirdpay.IHttpClientService;
import gpps.service.thirdpay.ResultCodeException;
import gpps.service.thirdpay.ThirdPartyAssistent;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;
@Service
public class AuditRepayServiceImpl implements IAuditRepayService {
	@Autowired
	IInnerThirdPaySupportService innerThirdPayService;
	@Autowired
	IHttpClientService httpClientService;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	ISubmitDao submitDao;
	@Autowired
	IProductDao productDao;
	@Autowired
	IGovermentOrderDao orderDao;
	@Autowired
	IBorrowerDao borrowerDao;
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	IPayBackDao payBackDao;
	@Autowired
	IAccountService accountService;
	@Autowired
	IInnerPayBackService innerPayBackService;
	
	@Override
	public void auditRepay(List<String> loanNos, int auditType)
			throws Exception {
		if(loanNos==null||loanNos.size()==0)
			throw new Exception("无效的审核列表！");
		
		HttpServletRequest req=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		String baseUrl=innerThirdPayService.getBaseUrl(ThirdPartyAssistent.ACTION_CHECK);
		StringBuilder loanNoSBuilder=new StringBuilder();
		Map<String,String> params=new HashMap<String, String>();
		params.put("PlatformMoneymoremore", innerThirdPayService.getPlatformMoneymoremore());
		params.put("AuditType", String.valueOf(auditType));
		params.put("ReturnURL", req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/account/repayaudit/response/bg");
		params.put("NotifyURL", params.get("ReturnURL"));
		for(int i=0;i<loanNos.size();i++)
		{
			if(loanNoSBuilder.length()!=0)
				loanNoSBuilder.append(",");
			loanNoSBuilder.append(loanNos.get(i));
		}
		
		params.put("LoanNoList", loanNoSBuilder.toString());
		
		//签名
		String signInfo = ThirdPartyAssistent.signForAudit(params, innerThirdPayService.getPrivateKey());
		params.put("SignInfo", signInfo);
				
		//将处理好的参数post到第三方，并接受其马上返回的参数
		String body=httpClientService.post(baseUrl, params);
		
		
		//由于还款审核会前后台都返回处理结果，因此能接收到直接返回结果，因此直接就执行相应的后续处理
		auditRepayProcessor(body);

	}

	@Override
	public void auditRepayProcessor(Map<String, String> returnParams) throws AlreadyDoneException, ResultCodeException,SignatureException, Exception{
		//校验返回结果签名，并解析返回参数
		List<String> loanNos = ThirdPartyAssistent.handleAuditReturnParams(returnParams);
			
		//根据返回参数处理平台相关信息，维护与第三方的一致性
		repayAuditHandle(loanNos);
	}
	
	
	
	
	public void auditRepayProcessor(String retJson) throws AlreadyDoneException, ResultCodeException,
			SignatureException, Exception {
		Gson gson = new Gson();
		Map<String,String> returnParams=gson.fromJson(retJson, Map.class);
		auditRepayProcessor(returnParams);
	}
	
	
		//审核提交给第三方后，对第三方返回的执行结果参数的后续处理
		private void repayAuditHandle(List<String> loanNos) throws Exception{
			if(loanNos==null || loanNos.isEmpty()){
				throw new Exception("审核返回列表为空");
			}
			
			//每一次批量提交的投标审核一定是针对同一个产品，因此取出第一条投标对应的产品，判断其状态，如果已经为还款中，则说明已经执行成功
			String eLoanNo = loanNos.get(0);
			List<CashStream> eCashStreams=cashStreamDao.findSuccessByActionAndLoanNo(-1, eLoanNo);
			CashStream eCashStream=eCashStreams.get(0);
			PayBack ePayBack = payBackDao.find(eCashStream.getPaybackId());
			Submit eSubmit=submitDao.find(eCashStream.getSubmitId());
			Product product = productDao.find(eSubmit.getProductId());
			GovermentOrder order=orderDao.find(product.getGovermentorderId());
			
			if(PayBack.STATE_FINISHREPAY==ePayBack.getState()){
				//还款已经处于还款完毕状态
				return;
			}
			
			for(String loanNo:loanNos)
			{
			List<CashStream> cashStreams=cashStreamDao.findSuccessByActionAndLoanNo(-1, loanNo);
			if(cashStreams.size()==2)
				continue;    //重复的命令
			CashStream cashStream=cashStreams.get(0);
			Submit submit=null;
			Lender lender = null;
			if(cashStream.getSubmitId()!=null)
			{
				submit=submitDao.find(cashStream.getSubmitId());
				lender = lenderDao.find(submit.getLenderId());
			}else{
				submit = new Submit();
				order = new GovermentOrder();
				product = new Product();
				product.setProductSeries(new ProductSeries());
			}
			
			Integer cashStreamId = null;
			//增加解冻现金流
			if(lender!=null){
				cashStreamId=accountService.repay(lender.getAccountId(), cashStream.getBorrowerAccountId(), cashStream.getChiefamount().negate(), cashStream.getInterest().negate(), cashStream.getSubmitId(), cashStream.getPaybackId(), "还款");
			}
			else{
				cashStreamId=accountService.storeChange(cashStream.getBorrowerAccountId(), cashStream.getPaybackId(), cashStream.getChiefamount().negate(), cashStream.getInterest().negate(), "还款存零");
			}
			}
			//完成还款
			innerPayBackService.finishPayBack(ePayBack.getId());
		}

}
