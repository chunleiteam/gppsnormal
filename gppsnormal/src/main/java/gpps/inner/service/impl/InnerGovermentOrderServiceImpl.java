package gpps.inner.service.impl;

import gpps.dao.IGovermentOrderDao;
import gpps.dao.ILenderDao;
import gpps.dao.IProductDao;
import gpps.dao.IProductSeriesDao;
import gpps.dao.IStateLogDao;
import gpps.dao.ISubmitDao;
import gpps.inner.service.IInnerGovermentOrderService;
import gpps.model.GovermentOrder;
import gpps.model.Lender;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.StateLog;
import gpps.model.Submit;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.SMSException;
import gpps.service.message.ILetterSendService;
import gpps.service.message.IMessageService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class InnerGovermentOrderServiceImpl implements
		IInnerGovermentOrderService {
	@Autowired
	IGovermentOrderDao govermentOrderDao;
	@Autowired
	IProductDao productDao;
	@Autowired
	ISubmitDao submitDao;
	@Autowired
	IProductSeriesDao productSeriesDao;
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	IStateLogDao stateLogDao;
	@Autowired
	IMessageService messageService;
	@Autowired
	ILetterSendService letterSendService;
	Logger log = Logger.getLogger(InnerGovermentOrderServiceImpl.class);
	public static int[][] validConverts={
		{GovermentOrder.STATE_UNPUBLISH,GovermentOrder.STATE_PREPUBLISH},
		{GovermentOrder.STATE_PREPUBLISH,GovermentOrder.STATE_FINANCING},
		{GovermentOrder.STATE_FINANCING,GovermentOrder.STATE_QUITFINANCING},
		{GovermentOrder.STATE_FINANCING,GovermentOrder.STATE_REPAYING},
		{GovermentOrder.STATE_REPAYING,GovermentOrder.STATE_WAITINGCLOSE},
		{GovermentOrder.STATE_WAITINGCLOSE,GovermentOrder.STATE_CLOSE}};
	
	@Override
	public void changeState(int orderId, int state) throws IllegalConvertException {
		GovermentOrder order = govermentOrderDao.find(orderId);
		if (order == null)
			throw new RuntimeException("order is not existed");
		for(int[] validStateConvert:validConverts)
		{
			if(order.getState()==validStateConvert[0]&&state==validStateConvert[1])
			{
				StateLog stateLog=new StateLog();
				stateLog.setSource(order.getState());
				stateLog.setTarget(state);
				stateLog.setType(StateLog.TYPE_GOVERMENTORDER);
				stateLog.setRefid(orderId);
				govermentOrderDao.changeState(orderId, state,System.currentTimeMillis());
				stateLogDao.create(stateLog);
				log.info("订单【"+orderId+"】状态由"+stateLog.getSource()+"变为"+stateLog.getTarget());
				return;
			}
		}
		throw new IllegalConvertException();
	}
	@Override
	public void startRepaying(int orderId) throws IllegalConvertException{
		// 修改订单状态，记录状态日志
		changeState(orderId, GovermentOrder.STATE_REPAYING);
		
		GovermentOrder order = govermentOrderDao.find(orderId);
		// 给融资方发送短信与站内信
		Map<String, String> param = new HashMap<String, String>();
		param.put(IMessageService.PARAM_ORDER_NAME, order.getTitle());
		param.put(ILetterSendService.PARAM_TITLE, "融资完成，启动还款");
		try {
			letterSendService.sendMessage(
					ILetterSendService.MESSAGE_TYPE_FINANCINGSUCCESS,
					ILetterSendService.USERTYPE_BORROWER,
					order.getBorrowerId(), param);
			messageService.sendMessage(
					IMessageService.MESSAGE_TYPE_FINANCINGSUCCESS,
					IMessageService.USERTYPE_BORROWER,
					order.getBorrowerId(), param);
		} catch (SMSException e) {
			log.error(e.getMessage());
		}
		
		
		
		//给投资者发送短信与站内信
		Map<Integer, StringBuilder> messages = new HashMap<Integer, StringBuilder>();
		List<Product> products = productDao.findByGovermentOrder(orderId);
		for(Product product:products){
			ProductSeries productSeries = productSeriesDao.find(product.getProductseriesId());
			List<Submit> submits = submitDao.findAllByProductAndState(product.getId(), Submit.STATE_COMPLETEPAY);
			for(Submit submit : submits){
				if(messages.containsKey(submit.getLenderId())){
					messages.get(submit.getLenderId()).append(","+order.getTitle()+"【"+productSeries.getTitle()+"】:金额【"+submit.getAmount().intValue()+"元】");
				}else{
					StringBuilder sb = new StringBuilder();
					sb.append(order.getTitle()+"【"+productSeries.getTitle()+"】:金额【"+submit.getAmount().intValue()+"元】");
					messages.put(submit.getLenderId(), sb);
				}
			}
		}
		
		Calendar cal = Calendar.getInstance();
		String dateStr = cal.get(Calendar.YEAR)+"年"+(cal.get(Calendar.MONTH)+1)+"月"+cal.get(Calendar.DAY_OF_MONTH)+"日";
		
		String dateStrMS = dateStr+cal.get(Calendar.HOUR_OF_DAY)+"时"+cal.get(Calendar.MINUTE)+"分";
		String help = " 客服电话："+IMessageService.PHONE;
		String title = "融资完成，启动还款";
		
		for(Integer id : messages.keySet()){
			Lender lender = lenderDao.find(id);
			String message = "【春蕾政采贷】尊敬的"+lender.getName()+"，温馨提示您投资的"+messages.get(id)+"项目于"+dateStrMS+"正式启动生效。"+help;
			try {
				letterSendService.sendMessage(ILetterSendService.USERTYPE_BORROWER,id, title, message);
				messageService.sendMessage(ILetterSendService.USERTYPE_LENDER, id, message);
			} catch (SMSException e) {
				log.error(e.getMessage());
			}
		}
		
	}
	
	@Override
	public void quitFinancing(int orderId) throws IllegalConvertException{
		// 修改订单状态，记录状态日志
		changeState(orderId,GovermentOrder.STATE_QUITFINANCING);
		
		GovermentOrder order = govermentOrderDao.find(orderId);

		// 给融资方发送短信与站内信
		Map<String, String> param = new HashMap<String, String>();
		param.put(IMessageService.PARAM_ORDER_NAME, order.getTitle());
		param.put(ILetterSendService.PARAM_TITLE, "产品流标");
		try{
			letterSendService.sendMessage(ILetterSendService.MESSAGE_TYPE_FINANCINGFAIL, ILetterSendService.USERTYPE_BORROWER, order.getBorrowerId(), param);
			messageService.sendMessage(IMessageService.MESSAGE_TYPE_FINANCINGFAIL, IMessageService.USERTYPE_BORROWER, order.getBorrowerId(), param);
		}catch(SMSException e){
			log.error(e.getMessage());
		}
		
		//给投资者发送短信与站内信
				Map<Integer, StringBuilder> messages = new HashMap<Integer, StringBuilder>();
				List<Product> products = productDao.findByGovermentOrder(orderId);
				for(Product product:products){
					ProductSeries productSeries = productSeriesDao.find(product.getProductseriesId());
					List<Submit> submits = submitDao.findAllByProductAndState(product.getId(), Submit.STATE_COMPLETEPAY);
					for(Submit submit : submits){
						if(messages.containsKey(submit.getLenderId())){
							messages.get(submit.getId()).append(","+order.getTitle()+"【"+productSeries.getTitle()+"】:金额【"+submit.getAmount().intValue()+"元】");
						}else{
							StringBuilder sb = new StringBuilder();
							sb.append(order.getTitle()+"【"+productSeries.getTitle()+"】:金额【"+submit.getAmount().intValue()+"元】");
							messages.put(submit.getLenderId(), sb);
						}
					}
				}
				
				Calendar cal = Calendar.getInstance();
				String dateStr = cal.get(Calendar.YEAR)+"年"+(cal.get(Calendar.MONTH)+1)+"月"+cal.get(Calendar.DAY_OF_MONTH)+"日";
				
				String dateStrMS = dateStr+cal.get(Calendar.HOUR_OF_DAY)+"时"+cal.get(Calendar.MINUTE)+"分";
				String help = " 客服电话："+IMessageService.PHONE;
				String title = "产品流标";
				
				for(Integer id : messages.keySet()){
					Lender lender = lenderDao.find(id);
					String message = "【春蕾政采贷】尊敬的"+lender.getName()+"，温馨提示您投资的"+messages.get(id)+"项目由于融资未满额于"+dateStrMS+"执行流标，您投资冻结的金额将解冻。"+help;
					try {
						letterSendService.sendMessage(ILetterSendService.USERTYPE_BORROWER,id, title, message);
						messageService.sendMessage(ILetterSendService.USERTYPE_LENDER, id, message);
					} catch (SMSException e) {
						log.error(e.getMessage());
					}
				}
	}
	
	@Override
	public void finishRepay(int orderId) throws IllegalConvertException{
		// 修改订单状态，记录状态日志
		changeState(orderId,GovermentOrder.STATE_WAITINGCLOSE);
	}
}
