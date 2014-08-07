package sg.nyp.groupconnect.entity;

public class Categories {
	private int id;
	private String name;
	private int typeId;

	public Categories(int id, String name, int typeId) {
		super();
		this.id = id;
		this.name = name;
		this.typeId = typeId;
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

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

}
