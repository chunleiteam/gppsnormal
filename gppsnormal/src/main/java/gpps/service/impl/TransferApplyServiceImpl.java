package gpps.service.impl;

import gpps.dao.ICashStreamDao;
import gpps.dao.IPayBackDao;
import gpps.inner.service.IInnerThirdPaySupportService;
import gpps.model.CashStream;
import gpps.model.PayBack;
import gpps.service.exception.CheckException;
import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.IHttpClientService;
import gpps.service.thirdpay.ITransferApplyService;
import gpps.service.thirdpay.LoanFromTP;
import gpps.service.thirdpay.LoanHelper;
import gpps.service.thirdpay.ResultCodeException;
import gpps.service.thirdpay.ThirdPartyState;
import gpps.service.thirdpay.Transfer;
import gpps.service.thirdpay.Transfer.LoanJson;
import gpps.service.thirdpay.TransferFromTP;
import gpps.tools.Common;
import gpps.tools.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;
@Service
public class TransferApplyServiceImpl implements ITransferApplyService {
	@Autowired
	IInnerThirdPaySupportService innerThirdPayService;
	@Autowired
	IHttpClientService httpClientService;
	@Autowired
	IPayBackDao payBackDao;
	@Autowired
	ICashStreamDao cashStreamDao;
	Logger log = Logger.getLogger(TransferApplyServiceImpl.class);
	@Override
	public void repayApply(List<LoanJson> loanJsons, PayBack payback)
			throws AlreadyDoneException, ResultCodeException, SignatureException, CheckException {
		if(loanJsons==null || loanJsons.isEmpty()){
			throw new CheckException("无效的转账列表！");
		}
		
		Transfer transfer = new Transfer();
		transfer.setPlatformMoneymoremore(innerThirdPayService.getPlatformMoneymoremore());
		transfer.setTransferAction(ThirdPartyState.THIRD_TRANSFERACTION_REPAY);
		transfer.setAction(ThirdPartyState.THIRD_ACTION_AUTO);
		transfer.setTransferType(ThirdPartyState.THIRD_TRANSFERTYPE_DIRECT);
		
		//转账设置为需要审核
		transfer.setNeedAudit(null);
		
		//非手动转账，不需要returnURL,只需要提供后台处理页面
//		transfer.setNotifyURL(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/account/repay/response/bg");
		transfer.setNotifyURL("http://" + innerThirdPayService.getServerHost() + ":" + innerThirdPayService.getServerPort() + "/account/repay/response/bg");
		
		//签名
		transfer.setLoanJsonList(Common.JSONEncode(loanJsons));
		transfer.setSignInfo(transfer.getSign(innerThirdPayService.getPrivateKey()));
		
		//将转账列表编码
		try {
			transfer.setLoanJsonList(URLEncoder.encode(transfer.getLoanJsonList(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Map<String,String> params=transfer.getParams();
		
		
		String baseUrl = innerThirdPayService.getBaseUrl(IInnerThirdPaySupportService.ACTION_TRANSFER);
		
		//将处理好的参数post到第三方，并接受其马上返回的参数
		String body=httpClientService.post(baseUrl, params);
		
		//由于申请还款（自动）会前后台都返回处理结果，因此能接收到直接返回结果，因此直接就执行相应的后续处理
		repayApplyProcessor(body);
	}
	
	
	@Override
	public List<LoanFromTP> justTransferApply(List<LoanJson> loanJsons)
			throws AlreadyDoneException, ResultCodeException, SignatureException, CheckException{
		if(loanJsons==null || loanJsons.isEmpty()){
			throw new CheckException("无效的转账列表！");
		}
		
		Transfer transfer = new Transfer();
		transfer.setPlatformMoneymoremore(innerThirdPayService.getPlatformMoneymoremore());
		transfer.setTransferAction(ThirdPartyState.THIRD_TRANSFERACTION_REPAY);
		transfer.setAction(ThirdPartyState.THIRD_ACTION_AUTO);
		transfer.setTransferType(ThirdPartyState.THIRD_TRANSFERTYPE_DIRECT);
		
		//转账设置为需要审核
		transfer.setNeedAudit(null);
		
		//非手动转账，不需要returnURL,只需要提供后台处理页面
//		transfer.setNotifyURL(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/account/repay/response/bg");
		transfer.setNotifyURL("http://" + innerThirdPayService.getServerHost() + ":" + innerThirdPayService.getServerPort() + "/account/repay/response/bg");
		
		//签名
		transfer.setLoanJsonList(Common.JSONEncode(loanJsons));
		transfer.setSignInfo(transfer.getSign(innerThirdPayService.getPrivateKey()));
		
		//将转账列表编码
		try {
			transfer.setLoanJsonList(URLEncoder.encode(transfer.getLoanJsonList(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Map<String,String> params=transfer.getParams();
		
		
		String baseUrl = innerThirdPayService.getBaseUrl(IInnerThirdPaySupportService.ACTION_TRANSFER);
		
		//将处理好的参数post到第三方，并接受其马上返回的参数
		String body=httpClientService.post(baseUrl, params);

		//校验返回结果
		Gson gson = new Gson();
		//自动、需要审核转账(Action=2  NeedAudit=null)，只包含一个json，记录了具体的转账信息
		Map returnParams=gson.fromJson(body, Map.class);
		
		List<LoanFromTP> res = handleReturnParams(returnParams);
		return res;
	}
	
	

	@Override
	public void repayApplyProcessor(Map<String, String> returnParams) throws AlreadyDoneException, ResultCodeException,
	SignatureException, CheckException{
		//校验返回结果签名，并解析返回参数
		List<LoanFromTP> result = handleReturnParams(returnParams);
						
		//根据返回参数处理平台相关信息，维护与第三方的一致性
		repayApplyHandle(result);
	}
	
	public void repayApplyProcessor(String retJson)
			throws AlreadyDoneException, ResultCodeException,
			SignatureException, CheckException{
		
		Gson gson = new Gson();
		
		//自动、需要审核转账(Action=2  NeedAudit=null)，只包含一个json，记录了具体的转账信息
		Map returnParams=gson.fromJson(retJson, Map.class);
		
		repayApplyProcessor(returnParams);
	}
	
	
	public List<LoanFromTP> handleReturnParams(Map<String, String> returnParams) throws AlreadyDoneException, ResultCodeException, SignatureException, CheckException
	{
		try{
			innerThirdPayService.checkTransferReturnParams(returnParams);
		}
		catch(SignatureException e){
			throw e;
		}catch(ResultCodeException e){
			throw e;
		}catch(AlreadyDoneException e){
			throw e;
		}
		
		
		String loanJonListStr=null;
		try{
		loanJonListStr = URLDecoder.decode(StringUtil.strFormat(returnParams.get("LoanJsonList")),"UTF-8");
		}catch(UnsupportedEncodingException e){
			
		}
		
		List<LoanFromTP> result = LoanHelper.parseJSON(loanJonListStr, null);
		
		if(result==null || result.isEmpty()){
			throw new CheckException("无效的转账列表！");
		}
		
		return result;
	}
	
	
	// 审核提交给第三方后，对第三方返回的执行结果参数的后续处理
	private void repayApplyHandle(List<LoanFromTP> loanNos) throws CheckException{
		//首先判断是否已经执行了相应的操作，如果是的话，直接返回
		if(loanNos==null || loanNos.isEmpty()){
			throw new CheckException("本次返回参数没有有效的转账记录！");
		}
		LoanFromTP eloanNo = loanNos.get(0);
		String eorderNo = eloanNo.getOrderNo();
		
		if(eorderNo==null || "".equals(eorderNo)){
			throw new CheckException("返回参数中，现金流ID异常");
		}
		int ecashStreamId = 0;
		try{
			ecashStreamId = Integer.parseInt(eorderNo);
		}catch(Exception e){
			throw new CheckException("返回参数中，现金流ID异常:"+e.getMessage());
		}
		CashStream ecash = cashStreamDao.find(ecashStreamId);
		PayBack payBack = payBackDao.find(ecash.getPaybackId());
		if(payBack.getCheckResult()==PayBack.CHECK_SUCCESS){
			return;
		}
		
		for (LoanFromTP loanNo : loanNos) {
			String orderNo = loanNo.getOrderNo();
			
			if(orderNo==null || "".equals(orderNo)){
				throw new CheckException("返回参数中，现金流ID异常");
			}
			
			int cashStreamId = 0;
			try{
				cashStreamId = Integer.parseInt(orderNo);
			}catch(Exception e){
				throw new CheckException("返回参数中，现金流ID异常:"+e.getMessage());
			}
			
			CashStream cashStream = cashStreamDao.find(cashStreamId);
			if(cashStream.getState()==CashStream.STATE_SUCCESS && loanNo.getLoanNo().equals(cashStream.getLoanNo()))
			{
				log.debug("回款现金流【"+cashStreamId+"】:已执行完毕，重复提交");
				continue;
			}
			//修改现金流的LoanNo,表示本次转账冻结已经与第三方一致
			cashStreamDao.updateLoanNo(cashStreamId, loanNo.getLoanNo(),null);
		}
		
		//现金流全部处理完毕后，将对应的还款的审核状态设置为“审核通过”
		payBackDao.changeCheckResult(payBack.getId(), PayBack.CHECK_SUCCESS);
	}

}
