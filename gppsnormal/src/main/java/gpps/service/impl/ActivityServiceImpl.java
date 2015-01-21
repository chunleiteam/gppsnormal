package gpps.service.impl;

import gpps.constant.Pagination;
import gpps.dao.IActivityDao;
import gpps.model.Activity;
import gpps.model.ref.Accessory;
import gpps.model.ref.Accessory.MimeCol;
import gpps.model.ref.Accessory.MimeItem;
import gpps.service.IActivityService;
import gpps.tools.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easyservice.xml.EasyObjectXMLTransformerImpl;
import com.easyservice.xml.IEasyObjectXMLTransformer;
import com.easyservice.xml.XMLParseException;
@Service
public class ActivityServiceImpl implements IActivityService{
	@Autowired
	IActivityDao activityDao;
	private static final IEasyObjectXMLTransformer xmlTransformer=new EasyObjectXMLTransformerImpl(); 
	public static final int ACTIVITY_ACCESSORY_CATEGORY=0;
	@Override
	public void create(Activity activity) {
		activity.setCreatetime(System.currentTimeMillis());
		activityDao.create(activity);
	}

	@Override
	public Activity find(Integer id) {
		return activityDao.find(id);
	}

	@Override
	public Map<String, Object> findByState(int state, int offset, int recnum) {
		int count=activityDao.countByState(state);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<Activity> activities=activityDao.findByState(state, offset, recnum);
		return Pagination.buildResult(activities, count, offset, recnum);
	}

	@Override
	public void update(Integer id, String name, long applyendtime,
			long starttime, String url, int state, String description) {
		activityDao.update(id, name, applyendtime, starttime, url, state, description);
	}

	@Override
	public void addAccessory(Integer id, MimeItem item)
			throws XMLParseException {
		String text=activityDao.findAccessory(id);
		Accessory accessory=null;
		if(StringUtil.isEmpty(text))
			accessory=new Accessory();
		else {
			accessory=xmlTransformer.parse(text, Accessory.class);
		}
		if(accessory.getCols()==null)
			accessory.setCols(new ArrayList<Accessory.MimeCol>());
		MimeCol col=accessory.findMimeCol(ACTIVITY_ACCESSORY_CATEGORY);
		if(col==null)
		{
			col=new MimeCol();
			col.setCategory(ACTIVITY_ACCESSORY_CATEGORY);
			accessory.getCols().add(col);
		}
		if(col.getItems()==null)
			col.setItems(new ArrayList<Accessory.MimeItem>());
		col.getItems().add(item);
		text=xmlTransformer.export(accessory);
		activityDao.updateAccessory(id, text);
	}

	@Override
	public void delAccessory(Integer id, String itemId)
			throws XMLParseException {
		String text=activityDao.findAccessory(id);
		if(StringUtil.isEmpty(text))
			return;
		Accessory accessory=xmlTransformer.parse(text, Accessory.class);
		if(accessory.getCols()==null)
			return;
		MimeCol col=accessory.findMimeCol(ACTIVITY_ACCESSORY_CATEGORY);
		if(col==null)
			return;
		List<MimeItem> items=col.getItems();
		if(items==null||items.size()==0)
			return;
		for(int i=0;i<items.size();i++)
		{
			if(items.get(i).getId().equals(itemId))
			{
				items.remove(i);
				break;
			}
		}
		text=xmlTransformer.export(accessory);
		activityDao.updateAccessory(id, text);
	}

	@Override
	public MimeItem findMimeItem(Integer id) throws XMLParseException {
		String text=activityDao.findAccessory(id);
		if(StringUtil.isEmpty(text))
			return null;
		Accessory accessory=xmlTransformer.parse(text, Accessory.class);
		MimeCol col=accessory.findMimeCol(ACTIVITY_ACCESSORY_CATEGORY);
		if(col==null)
			return null;
		return col.getItems().size()==0?null:col.getItems().get(0);
	}

}
