package gpps.service.impl;

import gpps.constant.Pagination;
import gpps.dao.INoticeDao;
import gpps.model.Borrower;
import gpps.model.Lender;
import gpps.model.Letter;
import gpps.model.Notice;
import gpps.service.ILoginService;
import gpps.service.INoticeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
@Service
public class NoticeServiceImpl implements INoticeService{
	@Autowired
	INoticeDao noticeDao;
	@Override
	public void create(Notice notice) {
		if(notice.getUsefor()==Notice.USEFOR_ALL)
			notice.setLevel(0);
		noticeDao.create(notice);
	}

	@Override
	public Notice find(Integer id) {
		return noticeDao.find(id);
	}

	@Override
	public Map<String, Object> findAll(int publicType,int offset,
			int recnum) {
		List<Integer> userfors=null;
		int level=-1;
		HttpSession session=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		Object user=session.getAttribute(ILoginService.SESSION_ATTRIBUTENAME_USER);
		if(user==null)
		{
			userfors=new ArrayList<Integer>();
			userfors.add(Notice.USEFOR_ALL);
		}
		else if(user instanceof Lender)
		{
			level=((Lender)user).getLevel();
			userfors=new ArrayList<Integer>();
			userfors.add(Notice.USEFOR_ALL);
			userfors.add(Notice.USEFOR_LENDER);
		}else if(user instanceof Borrower)
		{
			level=((Borrower)user).getLevel();
			userfors=new ArrayList<Integer>();
			userfors.add(Notice.USEFOR_ALL);
			userfors.add(Notice.USEFOR_BORROWER);
		}
		int count=noticeDao.count(publicType,userfors, level);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<Notice> notices=noticeDao.findAll(publicType,userfors, level, offset, recnum);
		return Pagination.buildResult(notices, count, offset, recnum);
	}

}
