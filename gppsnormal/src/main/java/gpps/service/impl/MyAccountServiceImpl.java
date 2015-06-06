package gpps.service.impl;

import gpps.dao.IActivityRefDao;
import gpps.dao.IBorrowerAccountDao;
import gpps.dao.IBorrowerDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.IFinancingRequestDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.IHelpDao;
import gpps.dao.ILenderAccountDao;
import gpps.dao.ILenderDao;
import gpps.dao.ILetterDao;
import gpps.dao.IPayBackDao;
import gpps.dao.ISubmitDao;
import gpps.model.Admin;
import gpps.model.Borrower;
import gpps.model.BorrowerAccount;
import gpps.model.CardBinding;
import gpps.model.CashStream;
import gpps.model.FinancingRequest;
import gpps.model.GovermentOrder;
import gpps.model.Help;
import gpps.model.Lender;
import gpps.model.LenderAccount;
import gpps.model.Letter;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.Submit;
import gpps.service.IAccountService;
import gpps.service.IBorrowerService;
import gpps.service.IGovermentOrderService;
import gpps.service.IHelpService;
import gpps.service.ILenderService;
import gpps.service.ILetterService;
import gpps.service.ILoginService;
import gpps.service.IMyAccountService;
import gpps.service.INoticeService;
import gpps.service.IPayBackService;
import gpps.service.ISubmitService;
import gpps.tools.BankCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class MyAccountServiceImpl implements IMyAccountService {
	@Autowired
	ILenderService lenderService;
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	ILenderAccountDao lenderAccountDao;
	@Autowired
	IBorrowerService borrowerService;
	@Autowired
	IBorrowerDao borrowerDao;
	@Autowired
	IBorrowerAccountDao borrowerAccountDao;
	@Autowired
	ILetterDao letterDao;
	@Autowired
	IHelpService helpService;
	@Autowired
	IHelpDao helpDao;
	@Autowired
	IGovermentOrderService orderService;
	@Autowired
	IPayBackService paybackService;
	@Autowired
	IPayBackDao paybackDao;
	@Autowired
	IAccountService accountService;
	@Autowired
	IFinancingRequestDao requestDao;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	ISubmitDao submitDao;
	@Autowired
	ISubmitService submitService;
	@Autowired
	IActivityRefDao activityRefDao;
	
	
	@Override
	public Map<String, Object> getCurrentUser(){
		Map<String, Object> res = new HashMap<String, Object>();
		HttpSession session=lenderService.getCurrentSession();
		if(session==null)
			return null;
		Object user=session.getAttribute(ILoginService.SESSION_ATTRIBUTENAME_USER);
		
		if(user instanceof Lender){
			res.put("usertype", "lender");
			res.put("letter", letterDao.countByReceiver(Letter.MARKREAD_NO, Letter.RECEIVERTYPE_LENDER, ((Lender)user).getId()));
			
		}else if(user instanceof Borrower){
			res.put("usertype", "borrower");
			res.put("letter", letterDao.countByReceiver(Letter.MARKREAD_NO, Letter.RECEIVERTYPE_BORROWER, ((Borrower)user).getId()));
		}else if(user instanceof Admin){
			res.put("usertype", "admin");
		}else{
			res.put("usertype", null);
		}
		res.put("value", user);
		
		return res;
	}
	
	@Override
	public Map<String, Object> getBAccountMessage() {
		Map<String, Object> message = new HashMap<String, Object>();
		Borrower cborrower = borrowerService.getCurrentUser();
		Borrower borrower = borrowerService.find(cborrower.getId());
		BorrowerAccount account = borrowerAccountDao.find(borrower.getAccountId());
		message.put("name", borrower.getLoginId());
		message.put("companyname", borrower.getCompanyName());
		message.put("license", borrower.getLicense());
		message.put("total", account.getTotal());
		message.put("used", account.getUsed());
		message.put("usable", account.getUsable());
		message.put("totalFee", account.getTotalFee());
		message.put("freeze", account.getFreeze());
		
		CardBinding cb = borrower.getCardBinding();
		if(cb!=null)
		{
			message.put("bankname", borrower.getCardBinding().getBranchBankName());
			message.put("bankcode", borrower.getCardBinding().getCardNo());
		}else{
			message.put("bankname", null);
			message.put("bankcode", null);
		}
		message.put("qdd", borrower.getAccountNumber());
		message.put("authorize", borrower.getAuthorizeTypeOpen());
		message.put("privilege", borrower.getPrivilege());
		
		message.put("score", borrower.getCreditValue());
		message.put("level", borrower.getLevel());
		
		message.put("letters", letterDao.countByReceiver(Letter.MARKREAD_NO, Letter.RECEIVERTYPE_BORROWER, borrower.getId()));
		
		message.put("helps",  helpDao.countPrivateHelps(-1, Help.QUESTIONERTYPE_BORROWER, borrower.getId()));
		
		
		
		
		message.put("request_init", requestDao.findByBorrowerAndState(borrower.getId(), FinancingRequest.STATE_INIT).size());
		
		message.put("request_processed", requestDao.findByBorrowerAndState(borrower.getId(), FinancingRequest.STATE_PROCESSED).size());
		
		message.put("request_refused", requestDao.findByBorrowerAndState(borrower.getId(), FinancingRequest.STATE_REFUSE).size());
		
		message.put("request_all", requestDao.findByBorrowerAndState(borrower.getId(), -1).size());
		
		
		
		List<GovermentOrder> orders_prepublish = orderService.findBorrowerOrderByStates(GovermentOrder.STATE_PREPUBLISH);
		message.put("orders_prepublish", orders_prepublish.size());
		
		List<GovermentOrder> orders_financing = orderService.findBorrowerOrderByStates(GovermentOrder.STATE_FINANCING);
		message.put("orders_financing", orders_financing.size());
		
		List<GovermentOrder> orders_repaying = orderService.findBorrowerOrderByStates(GovermentOrder.STATE_REPAYING);
		message.put("orders_repaying", orders_repaying.size());
		
		List<GovermentOrder> orders_waitingclose = orderService.findBorrowerOrderByStates(GovermentOrder.STATE_WAITINGCLOSE);
		message.put("orders_waitingclose", orders_waitingclose.size());
		
		List<GovermentOrder> orders_close = orderService.findBorrowerOrderByStates(GovermentOrder.STATE_CLOSE);
		message.put("orders_close", orders_close.size());
		
		
		message.put("pbs_waitforrepay", paybackService.findBorrowerWaitForRepayed().size());
		message.put("pbs_finishrepay", paybackDao.countByBorrowerAndState2(borrower.getAccountId(), PayBack.STATE_FINISHREPAY, -1, -1));
		message.put("pbs_waitforcheck", paybackDao.countByBorrowerAndState2(borrower.getAccountId(), PayBack.STATE_WAITFORCHECK, -1, -1));
		message.put("pbs_canberepayed", paybackService.findBorrowerCanBeRepayedPayBacks().size());
		message.put("pbs_canberepayedinadvance", paybackService.findBorrowerCanBeRepayedInAdvancePayBacks().size());
		
		
		
		message.put("cash_recharge", cashStreamDao.countByActionAndState(null, borrower.getAccountId(), CashStream.ACTION_RECHARGE, CashStream.STATE_SUCCESS));
		message.put("cash_withdraw", cashStreamDao.countByActionAndState(null, borrower.getAccountId(), CashStream.ACTION_CASH, CashStream.STATE_SUCCESS));
		message.put("cash_financing", cashStreamDao.countByActionAndState(null, borrower.getAccountId(), CashStream.ACTION_PAY, CashStream.STATE_SUCCESS));
		
		if(borrower.getPrivilege()!=borrower.PRIVILEGE_PURCHASEBACK)
		{
			message.put("cash_payback", cashStreamDao.countByActionAndState(null, borrower.getAccountId(), CashStream.ACTION_REPAY, CashStream.STATE_SUCCESS));
		}else{
			Lender lender = lenderDao.findByLoginId(borrower.getCorporationName());
			if(lender!=null)
			{
				message.put("cash_payback", cashStreamDao.countByActionAndState(lender.getAccountId(), null, CashStream.ACTION_REPAY, CashStream.STATE_SUCCESS));
			}else{
				message.put("cash_payback", 0);
			}
		}
		message.put("cash_purchase", cashStreamDao.countByActionAndState(null, borrower.getAccountId(), CashStream.ACTION_PURCHASE, CashStream.STATE_SUCCESS));
		message.put("cash_purchaseback", cashStreamDao.countByActionAndState(null, borrower.getAccountId(), CashStream.ACTION_PURCHASEBACK, CashStream.STATE_SUCCESS));
		
		
		
		List<Integer> stateList = new ArrayList<Integer>();
		stateList.add(Product.STATE_REPAYING);
		stateList.add(Product.STATE_POSTPONE);
		message.put("to_purchase", submitDao.countByStateAndProductStatesAndPurchaseFlag(Submit.STATE_COMPLETEPAY, stateList, Submit.PURCHASE_FLAG_PURCHASEBACK));
		
		message.put("to_purchase_back", submitDao.countByStateAndProductStatesAndPurchaseFlag(Submit.STATE_WAITFORPURCHASEBACK, stateList, Submit.PURCHASE_FLAG_UNPURCHASE));
		
		return message;
	}

	@Override
	public Map<String, Object> getLAccountMessage() {

		Map<String, Object> message = new HashMap<String, Object>();
		Lender lender = lenderService.getCurrentUser();
		LenderAccount account = lenderAccountDao.find(lender.getAccountId());
		String name = lender.getName()==null?lender.getLoginId():lender.getName();
		message.put("name", name);
		message.put("total", account.getTotal());
		message.put("freeze", account.getFreeze());
		message.put("usable", account.getUsable());
		message.put("used", account.getUsed());
		message.put("totalincome", account.getTotalincome());
		
		CardBinding cb = lender.getCardBinding();
		if(cb!=null)
		{
			message.put("bankname", lender.getCardBinding().getBranchBankName());
			message.put("bankcode", lender.getCardBinding().getCardNo());
		}else{
			message.put("bankname", null);
			message.put("bankcode", null);
		}
		message.put("qdd", lender.getAccountNumber());
		
		message.put("identityCard", lender.getIdentityCard());
		
		message.put("grade", lender.getGrade());
		message.put("level", lender.getLevel());
		
		message.put("letters_unread", letterDao.countByReceiver(Letter.MARKREAD_NO, Letter.RECEIVERTYPE_LENDER, lender.getId()));
		
		message.put("letters_readed", letterDao.countByReceiver(Letter.MARKREAD_YES, Letter.RECEIVERTYPE_LENDER, lender.getId()));
		
		message.put("helps",  helpDao.countPrivateHelps(-1, Help.QUESTIONERTYPE_LENDER, lender.getId()));
		
		message.put("activitys", activityRefDao.countByLender(lender.getId()));
		
		
		message.put("pbs_waitforrepay", accountService.findLenderWaitforRepay().size());
		message.put("pbs_finishrepay", cashStreamDao.countByActionAndState(lender.getAccountId(), null, CashStream.ACTION_REPAY, CashStream.STATE_SUCCESS));
		
		
		message.put("cash_total", cashStreamDao.countByActionAndState(lender.getAccountId(), null, -1, CashStream.STATE_SUCCESS));
		message.put("cash_recharge", cashStreamDao.countByActionAndState(lender.getAccountId(), null, CashStream.ACTION_RECHARGE, CashStream.STATE_SUCCESS));
		message.put("cash_withdraw", cashStreamDao.countByActionAndState(lender.getAccountId(), null, CashStream.ACTION_CASH, CashStream.STATE_SUCCESS));
		message.put("cash_financing", cashStreamDao.countByActionAndState(lender.getAccountId(), null, CashStream.ACTION_PAY, CashStream.STATE_SUCCESS));
		message.put("cash_payback", cashStreamDao.countByActionAndState(lender.getAccountId(), null, CashStream.ACTION_REPAY, CashStream.STATE_SUCCESS));

		
		
		List<Integer> stateList = new ArrayList<Integer>();
		
		message.put("submit_all", submitDao.countByLenderAndProductStates(lender.getId(), null));
		message.put("submit_waitforpay", submitService.findMyAllWaitforPayingSubmits().size());
		message.put("submit_subscribeforpay", submitService.findMyAllWaitforPayingSubscribeSubmits().size());
		
		stateList.add(Product.STATE_REPAYING);
		stateList.add(Product.STATE_POSTPONE);
		message.put("submit_paying", submitDao.countByLenderAndProductStates(lender.getId(), stateList));
		
		stateList.clear();
		stateList.add(Product.STATE_APPLYTOCLOSE);
		stateList.add(Product.STATE_CLOSE);
		stateList.add(Product.STATE_FINISHREPAY);
		message.put("submit_done", submitDao.countByLenderAndProductStates(lender.getId(), stateList));
		
		stateList.clear();
		stateList.add(Product.STATE_APPLYTOCLOSE);
		stateList.add(Product.STATE_CLOSE);
		stateList.add(Product.STATE_FINISHREPAY);
		message.put("submit_retreat", submitService.findMyAllRetreatSubmits().size());
		
		stateList.clear();
		stateList.add(Product.STATE_REPAYING);
		stateList.add(Product.STATE_POSTPONE);
		message.put("can_purchase", submitDao.countByStateAndProductStatesAndPurchaseFlag(Submit.STATE_COMPLETEPAY, stateList, Submit.PURCHASE_FLAG_PURCHASEBACK));
		
		
		
		
		return message;
	}
	
	@Override
	public String getBankName(String code){
		return BankCode.getName(code);
	}

}
