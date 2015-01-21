package gpps.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpps.constant.Pagination;
import gpps.dao.INewsDao;
import gpps.model.News;
import gpps.service.INewsService;
@Service
public class NewsServiceImpl implements INewsService{
	@Autowired
	INewsDao newsDao;
	@Override
	public void create(News news) {
		newsDao.create(news);
	}

	@Override
	public News find(Integer id) {
		return newsDao.find(id);
	}

	@Override
	public Map<String, Object> findAll(int publicType,int offset, int recnum) {
		int count=newsDao.count(publicType);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<News> news=newsDao.findAll(publicType,offset, recnum);
		return Pagination.buildResult(news, count, offset, recnum);
	}

}
