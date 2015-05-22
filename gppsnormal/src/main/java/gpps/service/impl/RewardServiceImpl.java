package gpps.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpps.dao.ICashStreamDao;
import gpps.dao.ILenderDao;
import gpps.dao.IPayBackDao;
import gpps.inner.service.IInnerThirdPaySupportService;
import gpps.model.CashStream;
import gpps.model.Lender;
import gpps.model.PayBack;
import gpps.model.Submit;
import gpps.service.IAccountService;
import gpps.service.IRewardService;
import gpps.service.Reward;
import gpps.service.exception.CheckException;
import gpps.service.thirdpay.IAuditBuyService;
import gpps.service.thirdpay.ITransferApplyService;
import gpps.service.thirdpay.LoanFromTP;
import gpps.service.thirdpay.Transfer.LoanJson;
@Service
public class RewardServiceImpl implements IRewardService {
	@Autowired
	IAccountService accountService;
	@Autowired
	IInnerThirdPaySupportService innerSupportService;
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	ITransferApplyService transferApplyService;
	@Autowired
	ICashStreamDao cashstreamDao;
	@Autowired
	IPayBackDao paybackDao;
	@Autowired
	IAuditBuyService auditBuyService;
	Logger log=Logger.getLogger(RewardServiceImpl.class);
	@Override
	public List<String> reward(List<Reward> rewards, int batchCode) throws Exception {
		String platformMM = innerSupportService.getPlatformMoneymoremore();
		if(rewards==null)
			throw new Exception("奖励列表为空！");
		
		PayBack payback = null;
		List<PayBack> paybacks = paybackDao.findByTypeAndCheckResult(PayBack.TYPE_REWARD, batchCode);
		
		if(paybacks!=null && paybacks.size()>1){
			throw new Exception("批号【"+batchCode+"】对应了一个以上的奖励payback");
		}
		
		
		if(paybacks==null || paybacks.isEmpty()){
			payback = new PayBack();
			payback.setBorrowerAccountId(null);
			payback.setProductId(null);
			payback.setCheckResult(batchCode);
			payback.setState(PayBack.STATE_INVALID);
			payback.setType(PayBack.TYPE_REWARD);
			paybackDao.create(payback);
		}else{
			payback = paybacks.get(0);
		}
		
		//状态为success的“奖励”现金流存在，说明奖励已经发放成功
		List<CashStream> donecss = cashstreamDao.findByRepayAndActionAndState(payback.getId(), CashStream.ACTION_AWARD,CashStream.STATE_SUCCESS);
		if(donecss!=null && !donecss.isEmpty()){
			throw new Exception("第"+batchCode+"批次的奖励已经发放成功，请不要重复发放！");
		}
		
		//状态为init,并且有loanNo的“奖励”现金流，说明已经提出了奖励申请，但是尚未审核确认
		List<CashStream> css = cashstreamDao.findByRepayAndActionAndState(payback.getId(), CashStream.ACTION_AWARD,CashStream.STATE_INIT);
		if(css!=null && !css.isEmpty()){
			String loanNo = css.get(0).getLoanNo();
			if(loanNo!=null && !"".equals(loanNo))
			{
			throw new Exception("第"+batchCode+"批次的奖励已经申请发放，请不要重复发放！");
			}
		}
		
		List<LoanJson> loanJsons=new ArrayList<LoanJson>();
		for(Reward reward : rewards){
			Lender lender = lenderDao.find(reward.getLenderId());
			Integer cashStreamId = accountService.freezeAdminAccount(lender.getAccountId(), new BigDecimal(reward.getAmount()), payback.getId(), "奖励");
			String toMoneyMoreMore = lender.getThirdPartyAccount();
			
			LoanJson loadJson=new LoanJson();
			loadJson.setLoanOutMoneymoremore(platformMM);
			loadJson.setLoanInMoneymoremore(toMoneyMoreMore);
			loadJson.setOrderNo(String.valueOf(cashStreamId));
			loadJson.setBatchNo(String.valueOf(batchCode));
			loadJson.setAmount(reward.getAmount()+"");
			loanJsons.add(loadJson);
		}
		
		
		List<String> result = new ArrayList<String>();
		
		//还款转账申请
		try{
			List<LoanFromTP> lftps = transferApplyService.justTransferApply(loanJsons);
			for(LoanFromTP lftp : lftps){
				String orderNo = lftp.getOrderNo();
				
				if(orderNo==null || "".equals(orderNo)){
					throw new CheckException("返回参数中，现金流ID异常");
				}
				
				int cashStreamId = 0;
				try{
					cashStreamId = Integer.parseInt(orderNo);
				}catch(Exception e){
					throw new CheckException("返回参数中，现金流ID异常:"+e.getMessage());
				}
				
				CashStream cashStream = cashstreamDao.find(cashStreamId);
				
				//如果“奖励”现金流的状态为成功，或者“奖励”现金流的loanNo已存在，并且与第三方返回的一致，说明已经执行过奖励申请了
				if(cashStream.getState()==CashStream.STATE_SUCCESS || lftp.getLoanNo().equals(cashStream.getLoanNo()))
				{
					log.debug("奖励现金流【"+cashStreamId+"】:已执行完毕，重复提交");
					continue;
				}
//				accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
				
				//修改现金流的LoanNo,表示本次转账冻结已经与第三方一致.结果是该条现金流状态为init,但是存在loanNo,说明提交了奖励申请，但是尚未审核
				cashstreamDao.updateLoanNo(cashStreamId, lftp.getLoanNo(),null);
				String str = "奖励给"+cashStream.getLenderAccountId()+"的"+cashStream.getChiefamount().intValue()+"元已经成功冻结！";
				result.add(str);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new CheckException(e.getMessage());
		}
		
		return result;
	}

	@Override
	public List<String> confirmReward(int batchCode) throws Exception {
		List<PayBack> paybacks = paybackDao.findByTypeAndCheckResult(PayBack.TYPE_REWARD, batchCode);
		if(paybacks!=null && paybacks.size()>1){
			throw new Exception("批号【"+batchCode+"】对应了一个以上的奖励payback");
		}
		if(paybacks==null || paybacks.isEmpty()){
			throw new Exception("批号【"+batchCode+"】没找到对应的奖励payback");
		}
		
		PayBack payback = paybacks.get(0);
		
		
		//状态为success的“奖励”现金流存在，说明奖励已经发放成功
		List<CashStream> donecss = cashstreamDao.findByRepayAndActionAndState(payback.getId(), CashStream.ACTION_AWARD,CashStream.STATE_SUCCESS);
		if(donecss!=null && !donecss.isEmpty()){
			throw new Exception("第"+batchCode+"批次的奖励已经发放成功，请不要重复审核！");
		}
		
		List<CashStream> css = cashstreamDao.findByRepayAndActionAndState(payback.getId(), CashStream.ACTION_AWARD, CashStream.STATE_INIT);
		if(css==null || css.isEmpty()){
			throw new Exception("第"+batchCode+"批次的奖励没有申请成功，或者无奖励可发！");
		}
		
		List<String> loanNos = new ArrayList<String>();
		for(CashStream cs : css){
			if(cs.getLoanNo()!=null && !"".equals(cs.getLoanNo()))
			{
			loanNos.add(cs.getLoanNo());
			}
		}
		
		if(loanNos.isEmpty()){
			throw new Exception("第"+batchCode+"批次的奖励没有申请成功！");
		}
		
		auditBuyService.justAuditBuy(loanNos, 1);
		
		List<String> result = new ArrayList<String>();
		
		for(String loanNo : loanNos){
			List<CashStream> cashStreams=cashstreamDao.findByActionAndLoanNo(CashStream.ACTION_AWARD, loanNo);
			if(cashStreams.size()!=1)
				throw new Exception("非法，LoanNo为【"+loanNo+"】的奖励现金流不是一条");    //不会出现的状况
			CashStream cashStream=cashStreams.get(0);
			
			if(cashStream.getState()==CashStream.STATE_SUCCESS){
				continue;		//该条奖励现金流已经审核通过了
			}
			
			accountService.reward(cashStream.getId(), cashStream.getLenderAccountId(), cashStream.getChiefamount(), cashStream.getPaybackId(), "奖励");
			String str = "奖励给"+cashStream.getLenderAccountId()+"的"+cashStream.getChiefamount().intValue()+"元已经成功转账！";
			result.add(str);
		}
		
		
		return result;
	}

}
