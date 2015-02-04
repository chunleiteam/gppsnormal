package gpps.service.impl;

import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import gpps.dao.IBorrowerDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.ILenderDao;
import gpps.dao.IPayBackDao;
import gpps.dao.IProductDao;
import gpps.dao.IProductSeriesDao;
import gpps.dao.IStateLogDao;
import gpps.inner.service.IInnerGovermentOrderService;
import gpps.inner.service.IInnerPayBackService;
import gpps.inner.service.IInnerProductService;
import gpps.model.Borrower;
import gpps.model.CashStream;
import gpps.model.GovermentOrder;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.StateLog;
import gpps.model.Submit;
import gpps.service.CashStreamSum;
import gpps.service.IAccountService;
import gpps.service.IBorrowerService;
import gpps.service.IPayBackService;
import gpps.service.ISubmitService;
import gpps.service.exception.CheckException;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.SMSException;
import gpps.service.message.ILetterSendService;
import gpps.service.message.IMessageService;
import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.IAuditBuyService;
import gpps.service.thirdpay.IHttpClientService;
import gpps.service.thirdpay.IThirdPaySupportService;
import gpps.service.thirdpay.ResultCodeException;
import gpps.service.thirdpay.ThirdPartyAssistent;
@Service
public class AuditBuyServiceImpl implements IAuditBuyService {
	@Autowired
	IThirdPaySupportService thirdPayService;
	@Autowired
	IHttpClientService httpClientService;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	ISubmitService submitService;
	@Autowired
	IGovermentOrderDao orderDao;
	@Autowired
	IInnerGovermentOrderService innerOrderService;
	@Autowired
	IInnerProductService innerProductService;
	@Autowired
	IProductDao productDao;
	@Autowired
	IBorrowerService borrowerService;
	@Autowired
	IBorrowerDao borrowerDao;
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	IAccountService accountService;
	@Autowired
	IPayBackDao payBackDao;
	@Autowired
	IInnerPayBackService innerPayBackService;
	@Autowired
	IPayBackService payBackService;
	@Autowired
	IProductSeriesDao productSeriesDao;
	@Autowired
	IStateLogDao stateLogDao;
	
	private Logger log=Logger.getLogger(AuditBuyServiceImpl.class);
	
	@Override
	public void auditBuy(List<String> loanNos, int auditType) throws Exception {
		if(loanNos==null||loanNos.size()==0)
			throw new Exception("无效的审核列表！");
		
		String baseUrl=thirdPayService.getBaseUrl(ThirdPartyAssistent.ACTION_CHECK);
		StringBuilder loanNoSBuilder=new StringBuilder();
		Map<String,String> params=new HashMap<String, String>();
		params.put("PlatformMoneymoremore", thirdPayService.getPlatformMoneymoremore());
		params.put("AuditType", String.valueOf(auditType));
		params.put("ReturnURL", "http://www.aaccbb.com/account/checkBuy/response/bg");
		params.put("NotifyURL", params.get("ReturnURL"));
		for(int i=0;i<loanNos.size();i++)
		{
			if(loanNoSBuilder.length()!=0)
				loanNoSBuilder.append(",");
			loanNoSBuilder.append(loanNos.get(i));
		}
		
		params.put("LoanNoList", loanNoSBuilder.toString());
		
		//签名
		String signInfo = ThirdPartyAssistent.signForAudit(params, thirdPayService.getPrivateKey());
		params.put("SignInfo", signInfo);
				
		//将处理好的参数post到第三方，并接受其马上返回的参数
		String body=httpClientService.post(baseUrl, params);
		
		
		//由于购买审核会前后台都返回处理结果，因此能接收到直接返回结果，因此直接就执行相应的后续处理
		auditBuyProcessor(body);
	}

	@Override
	public void auditBuyProcessor(String retJson) throws AlreadyDoneException,
			ResultCodeException, SignatureException, Exception {
		//校验返回结果签名，并解析返回参数
		List<String> loanNos = ThirdPartyAssistent.handleAuditReturnParams(retJson);
		
		Gson gson = new Gson();
		Map<String,String> returnParams=gson.fromJson(retJson, Map.class);
		//得到审核操作的类型：1为通过，2为退回
		String auditType=returnParams.get("AuditType");
		
		if(!"1".equals(auditType) && !"2".equals(auditType)){
			throw new Exception("审核状态出了问题，必须是1：通过；或2：退回");
		}
		
		//根据返回参数处理平台相关信息，维护与第三方的一致性
		if(auditType.equals("1"))
		{
			buyAuditSuccessHandle(loanNos);
		}else if(auditType.equals("2")){
			buyAuditReturnHandle(loanNos);
		}
	}
	
	private void buyAuditReturnHandle(List<String> loanNos) throws Exception{
		if(loanNos==null || loanNos.isEmpty()){
			throw new Exception("审核返回列表为空");
		}
		
		// 每一次批量提交的投标审核一定是针对同一个产品，因此取出第一条投标对应的产品，判断其状态，如果已经为还款中，则说明已经执行成功
		String eLoanNo = loanNos.get(0);
		List<CashStream> eCashStreams = cashStreamDao
				.findSuccessByActionAndLoanNo(-1, eLoanNo);
		CashStream eCashStream = eCashStreams.get(0);
		Submit eSubmit = submitService.find(eCashStream.getSubmitId());
		Product product = productDao.find(eSubmit.getProductId());
		GovermentOrder order = orderDao.find(product.getGovermentorderId());

		if (Product.STATE_QUITFINANCING == product.getState()) {
			// 产品已经处于流标状态，说明针对本产品的购买审核已经执行完毕
			return;
		}
		
		for(String loanNo:loanNos)
		{
			List<CashStream> cashStreams=cashStreamDao.findSuccessByActionAndLoanNo(-1, loanNo);
			CashStream cashStream=cashStreams.get(0);
			
			if(cashStreams.size()==2)
			{
				//针对本个LoanNo,在平台上有两条执行成功的现金流与之对应，那肯定说明这次购买已经处理成功，一条冻结，一条流标解冻
				continue;    //重复的命令
			}
			
			try {
				Integer cashStreamId=null;
				cashStreamId=accountService.unfreezeLenderAccount(cashStream.getLenderAccountId(), cashStream.getChiefamount().negate(), cashStream.getSubmitId(), "流标");
				cashStreamDao.updateLoanNo(cashStreamId, loanNo,null);
			} catch (IllegalConvertException e) {
				e.printStackTrace();
			}
		}
		
		//校验 Product实际流标解冻额=所有Lender的支付资金流之和
		CashStreamSum sum=cashStreamDao.sumProduct(product.getId(), CashStream.ACTION_UNFREEZE);
		if(sum.getChiefAmount().negate().compareTo(product.getRealAmount())!=0)
			throw new CheckException("流标解冻总金额与产品实际融资金额不符，查看是否有尚未审核完毕的投标");
		
		//状态转换为流标，及一系列后续操作
		innerProductService.quitFinancing(product.getId());
	}
	
	//每一次批量提交的投标审核一定是针对同一个产品，因此不会出现一批投标购买针对不同产品的情况
	//审核提交给第三方后，对第三方返回的执行结果参数的后续处理
	private void buyAuditSuccessHandle(List<String> loanNos) throws Exception{
		if(loanNos==null || loanNos.isEmpty()){
			throw new Exception("审核返回列表为空");
		}
		
		//每一次批量提交的投标审核一定是针对同一个产品，因此取出第一条投标对应的产品，判断其状态，如果已经为还款中，则说明已经执行成功
		String eLoanNo = loanNos.get(0);
		List<CashStream> eCashStreams=cashStreamDao.findSuccessByActionAndLoanNo(-1, eLoanNo);
		CashStream eCashStream=eCashStreams.get(0);
		Submit eSubmit=submitService.find(eCashStream.getSubmitId());
		Product product = productDao.find(eSubmit.getProductId());
		GovermentOrder order=orderDao.find(product.getGovermentorderId());
		
		Borrower borrower = borrowerDao.find(order.getBorrowerId());
		if(Product.STATE_REPAYING==product.getState()){
			//产品已经处于还款状态，说明针对本产品的购买审核已经执行完毕
			return;
		}

		for(String loanNo:loanNos)
		{
			List<CashStream> cashStreams=cashStreamDao.findSuccessByActionAndLoanNo(-1, loanNo);
			CashStream cashStream=cashStreams.get(0);
			
			if(cashStreams.size()==2)
			{
				//针对本个LoanNo,在平台上有两条执行成功的现金流与之对应，那肯定说明这次购买已经处理成功，一条冻结，一条转账
				continue;    //重复的命令
			}
			
			try {
				Integer cashStreamId=null;
				cashStreamId=accountService.pay(cashStream.getLenderAccountId(), borrower.getAccountId(),cashStream.getChiefamount().negate(),cashStream.getSubmitId(), "支付");
				cashStreamDao.updateLoanNo(cashStreamId, loanNo,null);
			} catch (IllegalConvertException e) {
				e.printStackTrace();
			}
		}
		
		//校验 Product实际支付额=所有Lender的支付资金流之和
		CashStreamSum sum=cashStreamDao.sumProduct(product.getId(), CashStream.ACTION_PAY);
		if(sum.getChiefAmount().negate().compareTo(product.getRealAmount())!=0)
			throw new CheckException("投标购买审核完成总金额与产品实际融资金额不符，查看是否有尚未审核完毕的投标");
		
		//根据产品实际融资额度重新计算并更新还款计划
		innerPayBackService.refreshPayBack(product.getId());
		
		//状态转换为还款中，及一系列后续操作
		innerProductService.startRepaying(product.getId());
	}

}
