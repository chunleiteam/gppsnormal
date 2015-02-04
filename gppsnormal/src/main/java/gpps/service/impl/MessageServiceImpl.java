package gpps.service.impl;

import gpps.dao.IBorrowerDao;
import gpps.dao.ILenderDao;
import gpps.model.Borrower;
import gpps.model.Lender;
import gpps.model.ref.Contactor;
import gpps.model.ref.Contactor.Single;
import gpps.service.exception.SMSException;
import gpps.service.message.IMessageService;
import gpps.service.message.IMessageSupportService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easyservice.xml.EasyObjectXMLTransformerImpl;
import com.easyservice.xml.IEasyObjectXMLTransformer;
import com.easyservice.xml.XMLParseException;

@Service
public class MessageServiceImpl implements IMessageService {
@Autowired
ILenderDao lenderDao;
@Autowired
IBorrowerDao borrowerDao;
@Autowired
IMessageSupportService supportService;
private static final IEasyObjectXMLTransformer xmlTransformer=new EasyObjectXMLTransformerImpl(); 
	@Override
	public void sendMessage(int messageType, int userType, Integer userId,
			Map<String, String> param) throws SMSException {
		List<String> phones = new ArrayList<String>();
		String message = null;
		
		
		if(messageType==this.MESSAGE_TYPE_SENDVALIDATECODE){
			message = getMessage(messageType, userType, param);
			phones.add(param.get(this.PARAM_PHONE));
		}else{
		
		
		if(userType==this.USERTYPE_BORROWER){
			Borrower borrower = borrowerDao.find(userId);
			if(borrower==null){
				throw new SMSException("企业用户不存在："+userId);
			}
			phones.add(borrower.getTel());
			try{
			String contactor = borrower.getContactor();
			if(contactor!=null && !"".equals(contactor)){
			
			Contactor con = xmlTransformer.parse(borrower.getContactor(), Contactor.class);
			List<Single> cs = con.getContactors();
			for(Single c:cs){
				phones.add(c.getPhone());
			}
			}
			}catch(XMLParseException e){
				
			}
			String companyName = borrower.getCompanyName();
			param.put(this.PARAM_NAME, companyName);
		}else if(userType==this.USERTYPE_LENDER){
			Lender lender = lenderDao.find(userId);
			if(lender==null){
				throw new SMSException("用户不存在："+userId);
			}
			phones.add(lender.getTel());
			String name = lender.getName()==null?lender.getLoginId():lender.getName();
			param.put(this.PARAM_NAME, name);
		}
		message = getMessage(messageType, userType, param);
		}
		
//		System.out.println(phones);
//		System.out.println(message);
		supportService.sendSMS(phones, message);
		
	}
	
	public void sendMessage(Map<String, String> phone_messages) throws SMSException{
		if(phone_messages==null || phone_messages.isEmpty())
		{
			return;
		}
		for(String phone : phone_messages.keySet())
		{
			List<String> phs = new ArrayList<String>();
			phs.add(phone);
			supportService.sendSMS(phs, phone_messages.get(phone));
		}
	}
	
	
	private String getMessage(int messageType, int userType, Map<String, String> param) throws SMSException{
		String result = "";
		Calendar cal = Calendar.getInstance();
		String dateStr = cal.get(Calendar.YEAR)+"年"+(cal.get(Calendar.MONTH)+1)+"月"+cal.get(Calendar.DAY_OF_MONTH)+"日";
		
		String dateStrMS = dateStr+cal.get(Calendar.HOUR_OF_DAY)+"时"+cal.get(Calendar.MINUTE)+"分";
		
//		String help = "\n详情请登录政采贷查看，网址"+WEBADDR+"。如仍有问题请咨询春蕾客服，电话："+this.PHONE;
		String help = " 客服电话："+this.PHONE;
		
		switch (messageType) {
		case MESSAGE_TYPE_SENDVALIDATECODE:
			result = "【春蕾政采贷】您的验证码为"+param.get(this.PARAM_VALIDATE_CODE)
//			+"，为保障账户安全，请勿将验证码泄露给他人。"
			;
			break;
		case MESSAGE_TYPE_CASHOUTSUCCESS:
			if(userType == this.USERTYPE_LENDER)
			{
				result = "【春蕾政采贷】尊敬的"+param.get(this.PARAM_NAME)+"，您的账户于"+dateStrMS+"进行一笔提现交易，提现金额为"+param.get(this.PARAM_AMOUNT)+"元，其中由第三方资金平台收取手续费"+param.get(this.PARAM_FEE)+"元，扣款方式为内扣，具体到账时间以第三方平台乾多多为准。";
			}else if(userType == this.USERTYPE_BORROWER){
				result = "【春蕾政采贷】尊敬的企业客户"+param.get(this.PARAM_NAME)+"，您的账户于"+dateStrMS+"进行一笔提现交易，提现金额为"+param.get(this.PARAM_AMOUNT)+"元，其中由第三方资金平台收取手续费"+param.get(this.PARAM_FEE)+"元，扣款方式为内扣，具体到账时间以第三方平台乾多多为准。";
			}
			break;
		case MESSAGE_TYPE_FINANCINGSUCCESS:
			if(userType == this.USERTYPE_LENDER){
				result = "【春蕾政采贷】尊敬的"+param.get(this.PARAM_NAME)+"，温馨提示您投资的"+param.get(this.PARAM_ORDER_NAME)+"【"+param.get(this.PARAM_PRODUCT_SERIES_NAME)+"】项目于"+dateStr+"正式启动生效，您本笔投资为"+param.get(this.PARAM_AMOUNT)+"元。";
			}else if(userType == this.USERTYPE_BORROWER){
				result = "【春蕾政采贷】尊敬的企业客户"+param.get(this.PARAM_NAME)+"，政采贷提醒：您的融资项目"+param.get(this.PARAM_ORDER_NAME)+"于"+dateStr+"正式启动生效，为了保障您的权益，请注意还款安排并及时缴纳还款，以免影响您的信用记录。";
			}
			break;
		case MESSAGE_TYPE_FINANCINGFAIL:
			if(userType == this.USERTYPE_LENDER){
				result = "【春蕾政采贷】尊敬的"+param.get(this.PARAM_NAME)+"，温馨提示您投资的"+param.get(this.PARAM_ORDER_NAME)+"【"+param.get(this.PARAM_PRODUCT_SERIES_NAME)+"】项目由于融资未满额于"+dateStr+"执行流标，您本笔投资冻结的"+param.get(this.PARAM_AMOUNT)+"元将解冻。";
			}else if(userType == this.USERTYPE_BORROWER){
				result = "【春蕾政采贷】尊敬的企业客户"+param.get(this.PARAM_NAME)+"，政采贷提醒：您的融资项目"+param.get(this.PARAM_ORDER_NAME)+"于"+dateStr+"执行流标，为了保障您的权益，请及时关注您的平台账户，";
			}
			break;
		case MESSAGE_TYPE_PAYBACKSUCCESS:
			if(userType==this.USERTYPE_LENDER){
				result = "【春蕾政采贷】尊敬的"+param.get(this.PARAM_NAME)+"，您于"+dateStrMS+"收到"+param.get(this.PARAM_ORDER_NAME)+"【"+param.get(this.PARAM_PRODUCT_SERIES_NAME)+"】项目的收益，金额为"+param.get(this.PARAM_AMOUNT)+"，";
			}else if(userType == this.USERTYPE_BORROWER){
				result = "【春蕾政采贷】尊敬的企业客户"+param.get(this.PARAM_NAME)+"，政采贷提醒：您的融资项目"+param.get(this.PARAM_ORDER_NAME)+"【"+param.get(this.PARAM_PRODUCT_SERIES_NAME)+"】于"+dateStrMS+"审核通过并执行了一笔还款，还款金额为"+param.get(this.PARAM_AMOUNT)+"，为了保障您的权益，请及时关注您的平台账户，";
			}
			break;
		case MESSAGE_TYPE_LASTPAYBACKSUCCESS:
			if(userType==this.USERTYPE_LENDER){
				result = "【春蕾政采贷】尊敬的"+param.get(this.PARAM_NAME)+"，您于"+dateStrMS+"收到"+param.get(this.PARAM_ORDER_NAME)+"【"+param.get(this.PARAM_PRODUCT_SERIES_NAME)+"】项目的最后一笔收益，金额为"+param.get(this.PARAM_AMOUNT)+"，本产品已经还款完毕。";
			}else if(userType == this.USERTYPE_BORROWER){
				result = "【春蕾政采贷】尊敬的企业客户"+param.get(this.PARAM_NAME)+"，政采贷提醒：您的融资项目"+param.get(this.PARAM_ORDER_NAME)+"【"+param.get(this.PARAM_PRODUCT_SERIES_NAME)+"】于"+dateStrMS+"审核通过并执行了一笔还款，还款金额为"+param.get(this.PARAM_AMOUNT)+"，本产品已还款完毕，为了保障您的权益，请及时关注您的平台账户，";
			}
			break;
		case MESSAGE_TYPE_TOAFFORD:
			result = "【春蕾政采贷】尊敬的"+param.get(this.PARAM_NAME)+"，您有一笔投资尚未完成支付，投资的产品为："+param.get(this.PARAM_ORDER_NAME)+"【"+param.get(this.PARAM_PRODUCT_SERIES_NAME)+"】， 投资金额："+param.get(this.PARAM_AMOUNT)+"，请及时登录‘我的账户->投资查看->待支付’进行支付，如未支付，本笔投资将在半小时后失效！";
			break;
		case MESSAGE_TYPE_COMPANYINVESTSUCCESS:
			result = "【春蕾政采贷】尊敬的企业客户"+param.get(this.PARAM_NAME)+"，政采贷提醒：您的实地尽调申请已经完成并审核通过，请您尽快登录并注册第三方资金托管账户，只有在第三方账户开通以后，您的产品才能启动融资。";
			break;
		case MESSAGE_TYPE_COMPANYINVESTFAIL:
			result = "【春蕾政采贷】尊敬的企业客户"+param.get(this.PARAM_NAME)+"，政采贷提醒：您的实地尽调申请审核未通过，您可以在材料准备齐全后重新申请尽调，";
			break;
		case MESSAGE_TYPE_REQUESTINVESTSUCCESS:
			result = "【春蕾政采贷】尊敬的企业客户"+param.get(this.PARAM_NAME)+"，政采贷提醒：您的申请的融资产品【"+param.get(this.PARAM_ORDER_NAME)+"】已经审核通过,目前处于预发布状态，请您尽快登录并注册第三方资金托管账户，只有在第三方账户开通以后，您的产品才能启动融资。如您已经注册成功，请忽略此提醒。";
			break;
		case MESSAGE_TYPE_REQUESTINVESTFAIL:
			result = "【春蕾政采贷】尊敬的企业客户"+param.get(this.PARAM_NAME)+"，政采贷提醒：您的申请的融资产品【"+param.get(this.PARAM_ORDER_NAME)+"】审核未通过,您可以在材料准备齐全后重新申请融资。";
			break;
		case MESSAGE_TYPE_STARTFINANCE:
			result = "【春蕾政采贷】尊敬的企业客户"+param.get(this.PARAM_NAME)+"，政采贷提醒：您的融资产品【"+param.get(this.PARAM_ORDER_NAME)+"】已经启动融资，请关注平台上的融资进展。";
			break;
		case MESSAGE_TYPE_REMIND_PAYBACK:
			result = "【春蕾政采贷】尊敬的企业客户"+param.get(this.PARAM_NAME)+"，政采贷提醒：您的融资产品【"+param.get(this.PARAM_ORDER_NAME)+"（"+param.get(this.PARAM_PRODUCT_SERIES_NAME)+"）】于"+dateStr+"应还款"+param.get(this.PARAM_AMOUNT)+"元,请确保您在该日期前执行还款，以免影响您的信用记录。";
			break;
		case MESSAGE_TYPE_SELFDEFINE:
			result = "【春蕾政采贷】尊敬的"+param.get(this.PARAM_NAME)+"，"+param.get(this.PARAM_SELFDEFINE);
			break;
		default:
			result = null;
			break;
		}
		if(result!=null)
		return result+help;
		else
		return "none";
	}

}
