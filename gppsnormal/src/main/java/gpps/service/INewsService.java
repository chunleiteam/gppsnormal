package gpps.service;

import gpps.model.News;

import java.util.Map;

public interface INewsService {
	public void create(News news);
	public News find(Integer id);
	/**
	 * 
	 * @param publicType -1为不限
	 * @param offset
	 * @param recnum
	 * @return
	 */
	public Map<String,Object> findAll(int publicType,int offset,int recnum);
}
