package sg.nyp.groupconnect.entity;

public class Room {
	private int room_id;
	private String title;
	private String category;
	private int noOfLearner;
	private String location;
	private String latLng;
	private int creatorId;
	private String description;
	private String status;
	private String dateFrom;
	private String dateTo;
	private String timeFrom;
	private String timeTo;

	public Room(int room_id, String title, String category, int noOfLearner,
			String location, String latLng, int creatorId, String description,
			String status, String dateFrom, String dateTo, String timeFrom,
			String timeTo) {
		super();
		this.room_id = room_id;
		this.title = title;
		this.category = category;
		this.noOfLearner = noOfLearner;
		this.location = location;
		this.latLng = latLng;
		this.creatorId = creatorId;
		this.description = description;
		this.status = status;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.timeFrom = timeFrom;
		this.timeTo = timeTo;
	}

	public int getRoom_id() {
		return room_id;
	}

	public void setRoom_id(int room_id) {
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

	public int getNoOfLearner() {
		return noOfLearner;
	}

	public void setNoOfLearner(int noOfLearner) {
		this.noOfLearner = noOfLearner;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLatLng() {
		return latLng;
	}

	public void setLatLng(String latLng) {
		this.latLng = latLng;
	}

	public int getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}

	public String getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}

}
