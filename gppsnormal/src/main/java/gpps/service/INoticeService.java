package gpps.service;

import gpps.model.Notice;

import java.util.Map;

public interface INoticeService {
	public void create(Notice notice);
	public Notice find(Integer id);
	/**
	 * @param publicType -1为不限
	 * @param offset
	 * @param recnum
	 * @return
	 */
	public Map<String,Object> findAll(int publicType,int offset,int recnum);
}
