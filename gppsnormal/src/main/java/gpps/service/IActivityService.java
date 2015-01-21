package gpps.service;

import gpps.model.Activity;
import gpps.model.ref.Accessory.MimeItem;

import java.util.Map;

import com.easyservice.xml.XMLParseException;

public interface IActivityService {
	public void create(Activity activity);
	public Activity find(Integer id);
	public Map<String,Object> findByState(int state,int offset,int recnum);
	/**
	 * 更新该活动
	 * @param id
	 * @param name
	 * @param applyendtime
	 * @param starttime
	 * @param url
	 * @param state
	 * @param description
	 */
	public void update(Integer id,String name, long applyendtime,long  starttime,String url,int state, String description);
	/**
	 * 添加附件
	 * @param id
	 * @param item
	 * @throws XMLParseException
	 */
	public void addAccessory(Integer id,MimeItem item) throws XMLParseException;
	/**
	 * 删除附件
	 * @param id
	 * @param itemId
	 * @throws XMLParseException
	 */
	public void delAccessory(Integer id,String itemId) throws XMLParseException;
	/**
	 * 查找活动附件
	 * @param id
	 * @return
	 * @throws XMLParseException
	 */
	public MimeItem findMimeItem(Integer id)throws XMLParseException;
}
