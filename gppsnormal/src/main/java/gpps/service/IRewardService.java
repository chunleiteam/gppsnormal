package gpps.service;

import java.util.List;

public interface IRewardService {
	/**
	 * 根据参数执行奖励请求，生成相应的转账冻结现金流，发送转账请求，并处理返回结果，确认现金流
	 * @Param rewards 奖励列表
	 * @Param batchCode 奖励的批号
	 * @return 奖励申请执行的结果
	 * */
	public List<String> reward(List<Reward> rewards, int batchCode) throws Exception;
	
	/**
	 * 确认奖励转账申请，并相应的修改被奖励账户的金额
	 * @Param batchCode 奖励的批号
	 * @return 奖励确认执行的结果
	 * */
	public List<String> confirmReward(int batchCode) throws Exception;
}
