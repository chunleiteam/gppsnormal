package gpps.tools;

import java.util.ArrayList;
import java.util.List;

public class Area {
	public static final int TYPE_PROVINCE = 1;
	public static final int TYPE_CITY = 10;
	private String name;
	private int type = TYPE_CITY;
	private String code;
	private List<Area> subAreas = new ArrayList<Area>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<Area> getSubAreas() {
		return subAreas;
	}
	public void setSubAreas(List<Area> subAreas) {
		this.subAreas = subAreas;
	}
}
