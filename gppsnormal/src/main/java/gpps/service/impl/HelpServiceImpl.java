package gpps.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import gpps.constant.Pagination;
import gpps.dao.IHelpDao;
import gpps.model.Borrower;
import gpps.model.Help;
import gpps.model.Lender;
import gpps.service.IHelpService;
import gpps.service.ILoginService;

@Service
public class HelpServiceImpl implements IHelpService {
	@Autowired
	IHelpDao helpDao;

	@Override
	public void createPublic(Help help) {
		help.setCreatetime(System.currentTimeMillis());
		help.setType(Help.TYPE_PUBLIC);
		helpDao.create(help);
	}
	@Override
	public void createPrivate(Help help) {
		help.setCreatetime(System.currentTimeMillis());
		help.setType(Help.TYPE_PRIVATE);
		helpDao.create(help);
	}
	@Override
	public Help find(Integer id) {
		return helpDao.find(id);
	}

	@Override
	public Map<String, Object> findPublicHelps(int publicType, int offset,
			int recnum) {
		int count = helpDao.countPublicHelps(publicType);
		if (count == 0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<Help> helps = helpDao.findPublicHelps(publicType, offset, recnum);
		return Pagination.buildResult(helps, count, offset, recnum);
	}

	@Override
	public Map<String, Object> findMyHelps(int type,int offset, int recnum) {
		Integer questionerId=null;
		int questionerType=Help.QUESTIONERTYPE_LENDER;
		HttpSession session=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		Object user=session.getAttribute(ILoginService.SESSION_ATTRIBUTENAME_USER);
		if(user instanceof Lender)
		{
			questionerId=((Lender)user).getId();
			questionerType=Help.QUESTIONERTYPE_LENDER;
		}else if(user instanceof Borrower)
		{
			questionerId=((Borrower)user).getId();
			questionerType=Help.QUESTIONERTYPE_BORROWER;
		}
		else
			throw new RuntimeException("不支持的用户类型");
		int count=helpDao.countPrivateHelps(type,questionerType, questionerId);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<Help> helps=helpDao.findPrivateHelps(type,questionerType, questionerId, offset, recnum);
		return Pagination.buildResult(helps, count, offset, recnum);
	}
	@Override
	public Map<String, Object> findPrivateHelps(int type, int offset, int recnum) {
		int count=helpDao.countPrivateHelps(type, -1, null);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<Help> helps=helpDao.findPrivateHelps(type, -1, null, offset, recnum);
		return Pagination.buildResult(helps, count, offset, recnum);
	}
	@Override
	public void answer(Integer id, String answer) {
		helpDao.answer(id, answer, System.currentTimeMillis());
	}

}
