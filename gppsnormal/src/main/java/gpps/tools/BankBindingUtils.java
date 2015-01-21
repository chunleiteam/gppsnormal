package gpps.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankBindingUtils {
	private static List<Area> areas = new ArrayList<Area>();
	static{
		try{
		loadArea();
		}catch(Exception e){
			System.out.println("加载省市信息错误！！");
		}
	}
	
	private static void loadArea() throws Exception{
		Map<String, List<Area>> constructure = new HashMap<String, List<Area>>();
		BufferedReader br = new BufferedReader(new InputStreamReader(BankBindingUtils.class.getResourceAsStream("provincecity.txt"), "utf-8"));
		String temp = br.readLine();
		while(temp!=null){
			String[] single = temp.split("	");
			if(single.length!=3){
				System.out.println("error:"+temp);
			}else{
				String code = single[0].trim();
				String name = single[1].trim();
				String parentCode = single[2].trim();
				
				if("0".equals(parentCode)){
					//对于省的处理
					if(!constructure.containsKey(code)){
						constructure.put(code, new ArrayList<Area>());
					}
					
					Area area = new Area();
					area.setCode(code);
					area.setName(name);
					area.setType(Area.TYPE_PROVINCE);
					areas.add(area);
				}else{
					//对于市的处理
					
					Area area = new Area();
					area.setCode(code);
					area.setName(name);
					area.setType(Area.TYPE_CITY);
					
					if(constructure.containsKey(parentCode)){
						List<Area> citys = constructure.get(parentCode);
						citys.add(area);
					}else{
						List<Area> citys = new ArrayList<Area>();
						constructure.put(parentCode, citys);
						citys.add(area);
					}
				}
			}
			temp = br.readLine();
		}
		
		
		for(Area province : areas){
			List<Area> citys = constructure.get(province.getCode());
			if(citys!=null){
				province.setSubAreas(citys);
			}
		}
	}
	
	public static List<Area> getProvinceCity(){
		return areas;
	}
	
	
	public static void main(String args[]) throws Exception{
		List<Area> areas = BankBindingUtils.getProvinceCity();
		int i=0;
		for(Area province : areas){
			System.out.println("省份："+province.getName()+"/"+province.getCode());
			i++;
			List<Area> citys = province.getSubAreas();
			for(Area city : citys){
				System.out.println("---------城市："+city.getName()+"/"+city.getCode());
				i++;
			}
		}
		System.out.println(i);
	}
}
