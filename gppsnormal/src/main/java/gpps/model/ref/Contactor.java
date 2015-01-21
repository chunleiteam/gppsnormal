package gpps.model.ref;

import java.util.ArrayList;
import java.util.List;

public class Contactor {
	List<Single> contactors = new ArrayList<Contactor.Single>();
	
	public List<Single> getContactors() {
		return contactors;
	}

	public void setContactors(List<Single> contactors) {
		this.contactors = contactors;
	}

	public static class Single{
		String name;
		String phone;
		String note;
		public Single(){
			
		}
		public Single(String name, String phone, String note){
			this.name = name;
			this.phone = phone;
			this.note = note;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getNote() {
			return note;
		}
		public void setNote(String note) {
			this.note = note;
		}
		
	}
}
