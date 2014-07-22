package sg.nyp.groupconnect.entity;

public class fakeRoom {
	private int id;
	private String name;
	private String subjects;
	private String location;
	private String memberId;

	public fakeRoom(int id, String name, String subjects, String location,
			String memberId) {
		super();
		this.id = id;
		this.name = name;
		this.subjects = subjects;
		this.location = location;
		this.memberId = memberId;
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

	public String getSubjects() {
		return subjects;
	}

	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

}
