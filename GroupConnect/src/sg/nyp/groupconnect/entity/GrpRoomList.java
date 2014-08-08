package sg.nyp.groupconnect.entity;

public class GrpRoomList {
	
	long room_id;
	String title;
	String category;
	long noOfLearner;
	String location;
	String latlng;
	public GrpRoomList(long room_id, String title, String category,
			long noOfLearner, String location, String latlng) {
		super();
		this.room_id = room_id;
		this.title = title;
		this.category = category;
		this.noOfLearner = noOfLearner;
		this.location = location;
		this.latlng = latlng;
	}
	public long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(long room_id) {
		this.room_id = room_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public long getNoOfLearner() {
		return noOfLearner;
	}
	public void setNoOfLearner(long noOfLearner) {
		this.noOfLearner = noOfLearner;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLatlng() {
		return latlng;
	}
	public void setLatlng(String latlng) {
		this.latlng = latlng;
	}
}
