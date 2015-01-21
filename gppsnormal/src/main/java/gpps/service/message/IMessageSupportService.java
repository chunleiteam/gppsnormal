package gpps.service.message;

import gpps.service.exception.SMSException;

import java.util.List;

public interface IMessageSupportService {
	/**
	 * 发送短信
	 * @param tels 待发送手机列表
	 * @param content 内容
	 */
	public void sendSMS(List<String> tels,String content) throws SMSException;
	public void sendScheduledSMS(List<String> tels,String content,long sendTime)throws SMSException;
	public void getUpSMS() throws SMSException;
	public String queryRest() throws SMSException;
}
