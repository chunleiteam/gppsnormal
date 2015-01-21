package gpps.service;

public interface IAccountCheckService {
	/**
	 * 提交验证请求,校验创建一个线程执行，在该线程结束前再次提交验证请求不重新创建线程
	 */
	public void checkAccount();
	/**
	 * 查看校验是否执行完毕
	 * @return
	 */
	public boolean finished();
	/**
	 * 获取校验错误报告，如在校验执行中，则返回已校验部分的报告
	 * @return
	 */
	public String getReport();
}
