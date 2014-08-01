package sg.nyp.groupconnect.room.entity;

public class AvailableLocation {
	private int id;
	private String name;
	private String location;
	private double latitude;
	private double longitude;

	public AvailableLocation(int id, String name, String location, double latitude,
			double longitude) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
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

	public String getlocation() {
		return location;
	}

	public void setlocation(String location) {
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

}
