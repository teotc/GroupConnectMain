package sg.nyp.groupconnect.entity;

public class Member {
	private int id;
	private String name;
	private String location;
	private double latitude;
	private double longitude;
	private String gender;
	private int schoolId;
	private String password;
	private String type;
	private String device;
	private String interestedSub;

	public Member(int id, String name, String location, double latitude,
			double longitude, String gender, int schoolId, String password,
			String type, String device, String interestedSub) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
		this.gender = gender;
		this.schoolId = schoolId;
		this.password = password;
		this.type = type;
		this.device = device;
		this.interestedSub = interestedSub;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getInterestedSub() {
		return interestedSub;
	}

	public void setInterestedSub(String interestedSub) {
		this.interestedSub = interestedSub;
	}

}
