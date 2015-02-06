package gpps.inner.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpps.dao.IGovermentOrderDao;
import gpps.dao.IStateLogDao;
import gpps.inner.service.IInnerGovermentOrderService;
import gpps.model.GovermentOrder;
import gpps.model.StateLog;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.SMSException;
import gpps.service.message.ILetterSendService;
import gpps.service.message.IMessageService;
@Service
public class InnerGovermentOrderServiceImpl implements
		IInnerGovermentOrderService {
	@Autowired
	IGovermentOrderDao govermentOrderDao;
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
		
		
		//TODO：给投资者发送短信与站内信
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
		
		// TODO：给投资者发送短信与站内信
	}
	
	@Override
	public void finishRepay(int orderId) throws IllegalConvertException{
		// 修改订单状态，记录状态日志
		changeState(orderId,GovermentOrder.STATE_WAITINGCLOSE);
	}
}
