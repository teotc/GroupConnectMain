package sg.nyp.groupconnect.entity;

public class Schools {
	private int id;
	private String name;
	private String category;
	private double latitude;
	private double longitude;

	public Schools(int id, String name, String category, double latitude,
			double longitude) {
		super();
		this.id = id;
		this.name = name;
		this.category = category;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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
