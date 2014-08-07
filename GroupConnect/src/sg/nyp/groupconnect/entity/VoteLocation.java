package sg.nyp.groupconnect.entity;

public class VoteLocation {
	private String memberId;
	private String roomId;
	private String locationId;
	private String status;

	public VoteLocation(String memberId, String roomId, String locationId,
			String status) {
		super();
		this.memberId = memberId;
		this.roomId = roomId;
		this.locationId = locationId;
		this.status = status;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
