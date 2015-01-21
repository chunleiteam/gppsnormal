package gpps.service;

import java.util.Map;

public interface IMyAccountService {
	public Map<String, Object> getCurrentUser();
	public Map<String, Object> getBAccountMessage();
	public Map<String, Object> getLAccountMessage();
	
	/**
	 * 获得银行卡开户行名称
	 * 
	 * */
	public String getBankName(String code);
}
