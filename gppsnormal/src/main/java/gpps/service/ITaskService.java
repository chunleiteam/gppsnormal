package gpps.service;

import gpps.model.Task;

public interface ITaskService {
//	public void execute(Task task,boolean interrupted);
	/**
	 * 提交一个任务，任务会在队列中排队单线程执行
	 * @param task
	 */
	public void submit(Task task);
}
