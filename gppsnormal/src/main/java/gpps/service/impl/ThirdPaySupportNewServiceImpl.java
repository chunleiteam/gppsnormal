package gpps.service.impl;

import gpps.model.PayBack;
import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.IAuditBuyService;
import gpps.service.thirdpay.IAuditRepayService;
import gpps.service.thirdpay.IBalanceWithTPService;
import gpps.service.thirdpay.IThirdPaySupportNewService;
import gpps.service.thirdpay.ITransferApplyService;
import gpps.service.thirdpay.LoanFromTP;
import gpps.service.thirdpay.ResultCodeException;
import gpps.service.thirdpay.Transfer.LoanJson;

import java.security.SignatureException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThirdPaySupportNewServiceImpl implements IThirdPaySupportNewService {

@Autowired
IAuditBuyService auditBuyService;
@Autowired
IAuditRepayService auditRepayService;
@Autowired
ITransferApplyService transferApplyService;
@Autowired
IBalanceWithTPService balanceWithTPService;
	
	@Override
	public void auditBuy(List<String> loanNos, int auditType) throws Exception{
		auditBuyService.auditBuy(loanNos, auditType);
	}
	
	@Override
	public void justAuditBuy(List<String> loanNos, int auditType) throws Exception{
		auditBuyService.justAuditBuy(loanNos, auditType);
	}

	@Override
	public void auditRepay(List<String> loanNos, int auditType) throws Exception{
		auditRepayService.auditRepay(loanNos, auditType);

	}

	@Override
	public void repayApply(List<LoanJson> loanJsons, PayBack payback) throws Exception{
		transferApplyService.repayApply(loanJsons, payback);

	}
	
	@Override
	public void justTransferApply(List<LoanJson> loanJsons) throws Exception{
		transferApplyService.justTransferApplyNeedAudit(loanJsons);
	}
	
	
	@Override
	public LoanFromTP viewByOrderNo(String orderNo, String action) throws Exception{
		return balanceWithTPService.viewByOrderNo(orderNo, action);
	}
	
	@Override
	public LoanFromTP viewByLoanNo(String loanNo, String action) throws Exception{
		return balanceWithTPService.viewByLoanNo(loanNo, action);
	}

	@Override
	public void auditBuyProcessor(Map<String, String> returnParams) throws AlreadyDoneException,
			ResultCodeException, SignatureException, Exception {
		auditBuyService.auditBuyProcessor(returnParams);
	}

	@Override
	public void auditRepayProcessor(Map<String, String> returnParams)
			throws AlreadyDoneException, ResultCodeException,
			SignatureException, Exception {
		auditRepayService.auditRepayProcessor(returnParams);
		
	}

	@Override
	public void repayApplyProcessor(Map<String, String> returnParams)
			throws AlreadyDoneException, ResultCodeException,
			SignatureException, Exception {
		transferApplyService.repayApplyProcessor(returnParams);
	}
}
