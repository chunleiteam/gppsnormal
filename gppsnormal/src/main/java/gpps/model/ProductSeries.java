/**
 * 
 */
package gpps.model;

import java.util.ArrayList;
import java.util.List;

import gpps.model.ref.TagCol;
import gpps.model.ref.TagCol.Item;
import gpps.tools.StringUtil;

import com.easyservice.xml.EasyObjectXMLTransformerImpl;
import com.easyservice.xml.IEasyObjectXMLTransformer;
import com.easyservice.xml.XMLParseException;

/**
 * @author wangm
 *
 */
public class ProductSeries {
	private Integer id;
	private String title;
	private String tag;//schema title description
	private String description;
	/**
	 * 0： 等额本息 1：按月还息，到期还本  2：按月还息,本金以订单回款为准
	 */
	public static final int TYPE_AVERAGECAPITALPLUSINTEREST=0;
	public static final int TYPE_FIRSTINTERESTENDCAPITAL=1;
	public static final int TYPE_FINISHPAYINTERESTANDCAPITAL=2;
	private int type=TYPE_AVERAGECAPITALPLUSINTEREST;
	private String typeDetail;
	private static final IEasyObjectXMLTransformer xmlTransformer=new EasyObjectXMLTransformerImpl(); 
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
		if(!StringUtil.isEmpty(tag))
		{
			try {
				this.tagCol=xmlTransformer.parse(tag, TagCol.class);
			} catch (XMLParseException e) {
				e.printStackTrace();
			}
		}
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTypeDetail() {
		return typeDetail;
	}
	public void setTypeDetail(String typeDetail) {
		this.typeDetail = typeDetail;
	}




	private TagCol tagCol;
	public TagCol getTagCol() {
		return tagCol;
	}
	public void setTagCol(TagCol tagCol) {
		this.tagCol = tagCol;
		if(tagCol!=null)
			this.tag=xmlTransformer.export(tagCol);
	}
	
	
	
	public static void main(String[] args)
	{
		TagCol tagCol=new TagCol();
		List<Item> tags=new ArrayList<Item>();
		tags.add(new Item("有担保","低风险"));
		tags.add(new Item("无担保","搞风险"));
		tagCol.setItems(tags);
		String text=xmlTransformer.export(tagCol);
		System.out.println(text);
		try {
			tagCol=xmlTransformer.parse(text, TagCol.class);
		} catch (XMLParseException e) {
			e.printStackTrace();
		}
		
	}
}
