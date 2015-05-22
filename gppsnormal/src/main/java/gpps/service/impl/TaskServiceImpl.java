package gpps.service.impl;

import gpps.dao.IBorrowerAccountDao;
import gpps.dao.IBorrowerDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.ILenderDao;
import gpps.dao.IPayBackDao;
import gpps.dao.IProductDao;
import gpps.dao.ISubmitDao;
import gpps.dao.ITaskDao;
import gpps.inner.service.IInnerThirdPaySupportService;
import gpps.model.Borrower;
import gpps.model.CashStream;
import gpps.model.GovermentOrder;
import gpps.model.Lender;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.Submit;
import gpps.model.Task;
import gpps.service.IAccountService;
import gpps.service.IPayBackService;
import gpps.service.IProductService;
import gpps.service.ITaskService;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.SMSException;
import gpps.service.message.IMessageService;
import gpps.service.thirdpay.IThirdPaySupportService;
import gpps.service.thirdpay.Transfer.LoanJson;
import gpps.tools.SinglePayBack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskServiceImpl implements ITaskService {
	@Autowired
	ITaskDao taskDao;
	Logger logger = Logger.getLogger(this.getClass());
	BlockingQueue<Task> queue = new LinkedBlockingQueue<Task>();
	@Autowired
	IProductDao productDao;
	@Autowired
	IProductService productService;
	@Autowired
	ISubmitDao submitDao;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	IAccountService accountService;
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	IBorrowerDao borrowerDao;
	@Autowired
	IGovermentOrderDao govermentOrderDao;
	@Autowired
	IPayBackDao payBackDao;
	@Autowired
	IPayBackService payBackService;
	@Autowired
	IThirdPaySupportService thirdPaySupportService;
	@Autowired
	IInnerThirdPaySupportService innerThirdPaySupportService;
	@Autowired
	IBorrowerAccountDao borrowerAccountDao;
	@PostConstruct
	public void init() {
		try {
			List<Task> interruptedTasks = taskDao
					.findByState(Task.STATE_PROCESSING);
			if (interruptedTasks != null && interruptedTasks.size() > 0) {
				logger.info("重新加载" + interruptedTasks.size() + "个中断任务到执行队列");
				for (Task task : interruptedTasks) {
					queue.put(task);
				}
			}
			List<Task> initTasks=taskDao.findByState(Task.STATE_INIT);
			if(initTasks!=null&&initTasks.size()>0)
			{
				logger.info("重新加载" + initTasks.size() + "个未执行任务到执行队列");
				for (Task task : initTasks) {
					queue.put(task);
				}
			}
		} catch (InterruptedException e) {
			logger.error("加载执行任务失败,系统退出,请检查故障原因");
			logger.error(e.getMessage(),e);
			System.exit(-1);
		}
		Thread taskThread=new Thread(){
			public void run()
			{
				logger.info("任务执行线程已启动");
				while(true)
				{
					try
					{
						// 外层添加异常捕捉防止循环跳出
						Task task=queue.peek();//只取不移除，当任务执行完成后移除
						if(task==null)
						{
							try {
								Thread.sleep(1*1000);
								continue;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						logger.info("开始处理任务:"+task);
						boolean interrupted=false;
						if(task.getState()==Task.STATE_INIT)
						{
							taskDao.changeState(task.getId(), Task.STATE_PROCESSING);
							task.setState(Task.STATE_PROCESSING);
						}
						else if(task.getState()==Task.STATE_PROCESSING)
							interrupted=true;
						else if(task.getState()==Task.STATE_FINISH)
						{
							//任务完成，状态未保存成功,基本不会出现
							taskDao.changeState(task.getId(), Task.STATE_FINISH);
							continue;
						}
						execute(task, interrupted);
						task.setState(Task.STATE_FINISH);
						taskDao.changeState(task.getId(), Task.STATE_FINISH);
						queue.poll();
						logger.info("任务:"+task+"处理完毕");
					}catch (Throwable e) {
						logger.error(e.getMessage(),e);
						//执行出错，线程休眠1分钟
						try {
							Thread.sleep(60*1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		};
		taskThread.setName("TaskThread");
		taskThread.start();
	}

	private void execute(Task task, boolean interrupted) throws Exception{
		switch (task.getType()) {
		case Task.TYPE_PAY:
			executePayTask(task,interrupted);
			break;
		case Task.TYPE_QUITFINANCING:
			executeQuitFinancingTask(task,interrupted);
			break;
		case Task.TYPE_REPAY:
			executeRepayTask(task, interrupted);
			break;
		case Task.TYPE_CHECK_REPAY:
			executeRepayCheckTask(task, interrupted);
			break;
		default:
			throw new RuntimeException("不支持的任务类型");
		}
	}
	private void executePayTask(Task task,boolean interrupted)
	{
		//考虑打断情况
		logger.info("开始执行产品id="+task.getProductId()+"的支付任务taskID="+task.getId());
		if(interrupted)
			logger.info("该支付任务曾被打断过，现在继续执行");
		List<Submit> submits=submitDao.findAllByProductAndState(task.getProductId(), Submit.STATE_COMPLETEPAY);
		if(submits==null||submits.size()==0)
			return;
		List<String> loanNos=new ArrayList<String>();
		loop:for(Submit submit:submits)
		{
			CashStream freezeCS=null;
			List<CashStream> cashStreams=cashStreamDao.findSubmitCashStream(submit.getId());
			for(CashStream cashStream:cashStreams)
			{
				//只要有购买成功的现金流，立马跳出循环
				if(cashStream.getAction()==CashStream.ACTION_PAY&&cashStream.getState()==CashStream.STATE_SUCCESS)
				{
					logger.debug("支付任务["+task.getId()+"],Submit["+submit.getId()+"]已执行过。");
					continue loop;
				}
				//如果没有购买成功的现金流，则找到冻结成功的现金流
				if(cashStream.getAction()==CashStream.ACTION_FREEZE&&cashStream.getState()==CashStream.STATE_SUCCESS)
					freezeCS=cashStream;
			}
			if(freezeCS==null)
				continue;
			loanNos.add(freezeCS.getLoanNo());
		}
		thirdPaySupportService.check(loanNos, 1);
		logger.info("支付任务["+task.getId()+"]完毕，涉及Submit"+submits.size()+"个");
	}
	private void executeQuitFinancingTask(Task task,boolean interrupted)
	{
		// 考虑打断情况
		logger.info("开始执行产品id="+task.getProductId()+"的流标任务taskID="+task.getId());
		if(interrupted)
			logger.info("该支付任务曾被打断过，现在继续执行");
		List<Submit> submits=submitDao.findAllByProductAndState(task.getProductId(), Submit.STATE_COMPLETEPAY);
		if(submits==null||submits.size()==0)
			return;
		List<String> loanNos=new ArrayList();
		loop:for(Submit submit:submits)
		{
			CashStream freezeCS=null;
			List<CashStream> cashStreams=cashStreamDao.findSubmitCashStream(submit.getId());
			for(CashStream cashStream:cashStreams)
			{
				if(cashStream.getAction()==CashStream.ACTION_UNFREEZE&&cashStream.getState()==CashStream.STATE_SUCCESS)
				{
					logger.debug("流标任务["+task.getId()+"],Submit["+submit.getId()+"]已执行过。");
					continue loop;
				}
				if(cashStream.getAction()==CashStream.ACTION_FREEZE&&cashStream.getState()==CashStream.STATE_SUCCESS)
					freezeCS=cashStream;
			}
			loanNos.add(freezeCS.getLoanNo());
		}
		thirdPaySupportService.check(loanNos, 2);
		logger.info("流标任务["+task.getId()+"]完毕，涉及Submit"+submits.size()+"个");
	}
	private void executeRepayCheckTask(Task task, boolean interrupted) throws Exception{
		PayBack payback = payBackDao.find(task.getPayBackId());
		Product product = productDao.find(task.getProductId());
		Borrower borrower = borrowerDao.findByAccountID(payback.getBorrowerAccountId());
		
		
		//考虑打断情况
		logger.info("开始执行产品id="+task.getProductId()+"的还款校验任务taskID="+task.getId());
		if(interrupted)
		logger.info("该校验任务曾被打断过，现在继续执行");
		
		List<CashStream> css = cashStreamDao.findByRepayAndActionAndState(task.getPayBackId(), CashStream.ACTION_FREEZE, CashStream.STATE_SUCCESS);
		List<LoanJson> loanJsons=new ArrayList<LoanJson>();
		
		for(CashStream cs : css){
			//已成功通知第三方执行冻结，并保存了第三方返回的loanNo
			if(cs.getState()==CashStream.STATE_SUCCESS&&(cs.getLoanNo()!=null&&!"".equals(cs))){
				continue;
			}
			
			String toMoneyMoreMore = null;
			Submit submit = submitDao.find(cs.getSubmitId());
			if(submit==null){
				toMoneyMoreMore = innerThirdPaySupportService.getPlatformMoneymoremore();
			}else{
				Lender lender = lenderDao.find(submit.getLenderId());
				if(lender==null){
					throw new Exception("投资无法找到投资人！");
				}
				toMoneyMoreMore = lender.getThirdPartyAccount();
			}
			
				LoanJson loadJson=new LoanJson();
				loadJson.setLoanOutMoneymoremore(borrower.getThirdPartyAccount());
				loadJson.setLoanInMoneymoremore(toMoneyMoreMore);
				loadJson.setOrderNo(String.valueOf(cs.getId()));
				loadJson.setBatchNo(String.valueOf(product.getId()));
				loadJson.setAmount(cs.getChiefamount().add(cs.getInterest()).negate().toString());
				loanJsons.add(loadJson);
		}
		thirdPaySupportService.submitForCheckRepay(loanJsons, payback);
	}
	private void executeRepayTask(Task task,boolean interrupted) throws Exception
	{
		
		PayBack payback = payBackDao.find(task.getPayBackId());
		Borrower borrower = borrowerDao.findByAccountID(payback.getBorrowerAccountId());

		//考虑打断情况
				logger.info("开始执行产品id="+task.getProductId()+"的还款任务taskID="+task.getId());
				if(interrupted)
					logger.info("该支付任务曾被打断过，现在继续执行");
		List<CashStream> css = cashStreamDao.findByRepayAndActionAndState(task.getPayBackId(), CashStream.ACTION_FREEZE, CashStream.STATE_SUCCESS);
		
		if(css==null||css.size()==0)
			return;
		List<String> loanNos=new ArrayList<String>();
		loop:for(CashStream cs:css)
		{
			
			if(cs.getLoanNo()==null || "".equals(cs.getLoanNo())){
				continue loop;
			}
			
			CashStream freezeCS=null;
			List<CashStream> cashStreams=cashStreamDao.findRepayCashStreamByAction(cs.getSubmitId(), task.getPayBackId(), CashStream.ACTION_REPAY);
			
			if(cashStreams!=null && !cashStreams.isEmpty()){
				//如果有购买成功的现金流，跳过
				continue loop;
			}
			
			loanNos.add(cs.getLoanNo());
			
//			Submit submit = submitDao.find(cs.getSubmitId());
//			Lender lender = lenderDao.find(submit.getLenderId());
//			
//			//对于非“存零操作”的还款
//			if(cs.getSubmitId()!=null)
//			{
//				
//				//TODO：增加一个解冻现金流
//				Integer cashStreamId=accountService.repay(lender.getAccountId(), borrower.getAccountId(), cs.getChiefamount(), cs.getInterest(), cs.getSubmitId(), task.getPayBackId(), "还款");
//				LoanJson loadJson=new LoanJson();
//				loadJson.setLoanOutMoneymoremore(borrower.getThirdPartyAccount());
//				loadJson.setLoanInMoneymoremore(lender.getThirdPartyAccount());
//				loadJson.setOrderNo(String.valueOf(cashStreamId));
//				loadJson.setBatchNo(String.valueOf(task.getProductId()));
//				loadJson.setAmount(cs.getChiefamount().add(cs.getInterest()).negate().toString());
//				loanJsons.add(loadJson);
//			}else
//			{
//				//TODO：增加一个解冻现金流
//				//对于“存零操作”的还款，有余额则放入自有账户中
//				Integer cashStreamId=accountService.storeChange(borrower.getAccountId(),task.getPayBackId(),cs.getChiefamount(),cs.getInterest(), "存零");
//				LoanJson loadJson=new LoanJson();
//				loadJson.setLoanOutMoneymoremore(borrower.getThirdPartyAccount());
//				loadJson.setLoanInMoneymoremore(thirdPaySupportService.getPlatformMoneymoremore());
//				loadJson.setOrderNo(String.valueOf(cashStreamId));
//				loadJson.setBatchNo(String.valueOf(task.getProductId()));
//				loadJson.setAmount(cs.getChiefamount().add(cs.getInterest()).negate().toString());
//				loanJsons.add(loadJson);
//			}
			
		}
		thirdPaySupportService.repay(loanNos, 1);
		logger.info("还款任务["+task.getId()+"]完毕，涉及还款"+loanNos.size()+"个");
	}
	@Override
	public void submit(Task task) {
		task.setState(Task.STATE_INIT);
		task.setCreateTime(System.currentTimeMillis());
		taskDao.create(task);
		try {
			queue.put(task);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
}
