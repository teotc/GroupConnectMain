package sg.nyp.groupconnect.entity;

public class RoomMembers {
	private int room_id;
	private int memberId;
	private String memberType;

	public RoomMembers(int room_id, int memberId, String memberType) {
		super();
		this.room_id = room_id;
		this.memberId = memberId;
		this.memberType = memberType;
	}

	public int getRoom_id() {
		return room_id;
	}

	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

}
