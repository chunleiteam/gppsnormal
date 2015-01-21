package gpps.service.message;

import gpps.service.exception.SMSException;

import java.util.Map;

public interface ILetterSendService {
	public static final String PARAM_SUBMIT_ID = "submitId";
	public static final String PARAM_PAYBACK_ID = "paybackId";
	public static final String PARAM_PRODUCT_ID = "productId";
	public static final String PARAM_CASHSTREAM_ID = "cashstreamId";
	public static final String PARAM_AMOUNT = "amount";
	public static final String PARAM_FEE = "fee";
	public static final String PARAM_VALIDATE_CODE = "validateCode";
	public static final String PARAM_ORDER_NAME = "orderName";
	public static final String PARAM_PRODUCT_SERIES_NAME = "seriesName";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_PHONE = "phone";
	public static final String PARAM_SELFDEFINE = "selfDefine";
	public static final String PARAM_TITLE = "title";
	
	public static final int USERTYPE_BORROWER = 1;
	public static final int USERTYPE_LENDER = 0;
	public static final int USERTYPE_ADMIN = 2;
	
//	public static final int MESSAGE_TYPE_REGISTERSUCCESS = 1; //"registerSuccess";
//	public static final int MESSAGE_TYPE_RECHARGESUCCESS = 2; //"rechargeSuccess";
	public static final int MESSAGE_TYPE_CASHOUTSUCCESS = 3; //"cashoutSuccess";
//	public static final int MESSAGE_TYPE_BUYSUCCESS = 4; //"buySuccess";
	public static final int MESSAGE_TYPE_FINANCINGSUCCESS = 5; //"financingSuccess";
	public static final int MESSAGE_TYPE_FINANCINGFAIL = 12; //"financingFail";
	public static final int MESSAGE_TYPE_PAYBACKSUCCESS = 6; //"paybackSuccess";
	public static final int MESSAGE_TYPE_LASTPAYBACKSUCCESS = 17; //"lastPaybackSuccess";
	public static final int MESSAGE_TYPE_TOAFFORD = 7; //"toAfford";
	
	public static final int MESSAGE_TYPE_COMPANYINVESTSUCCESS = 8; //"companyInvestSuccess";
	public static final int MESSAGE_TYPE_COMPANYINVESTFAIL = 15; //"companyInvestFail";
	public static final int MESSAGE_TYPE_REQUESTINVESTSUCCESS = 9; //"requestInvestSuccess";
	public static final int MESSAGE_TYPE_REQUESTINVESTFAIL = 16; //"requestInvestFail";
	public static final int MESSAGE_TYPE_STARTFINANCE = 10; //"startFinance";
//	public static final int MESSAGE_TYPE_STARTPAYBACK = 11; //"startPayback";
	
	
//	public static final int MESSAGE_TYPE_REMIND_REGISTERMMM = 12; //"remindRegistermmm";
	public static final int MESSAGE_TYPE_REMIND_PAYBACK = 13; //"remindPayback";
	public static final int MESSAGE_TYPE_REMIND_TODO = 14; //"remindTodo";
	
	public static final int MESSAGE_TYPE_SELFDEFINE = 18;  //"selfDefine";
	
	
	public static final String PHONE = "13581532923";
	public static final String WEBADDR = "http://www.zhengcaidai.com";
	
	
	public void sendMessage(int messageType, int userType, Integer userId, Map<String, String> param);
}
