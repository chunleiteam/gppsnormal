package gpps.model.ref;

import java.util.ArrayList;
import java.util.List;

public class TagCol {
	List<Item> items=new ArrayList<TagCol.Item>();
	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
	public static class Item{
		private String title;
		private String description;
		public Item(){};
		public Item(String title,String description)
		{
			this.title=title;
			this.description=description;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}
}
