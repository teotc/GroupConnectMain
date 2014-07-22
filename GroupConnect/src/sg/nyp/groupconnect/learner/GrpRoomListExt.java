package sg.nyp.groupconnect.learner;


import com.google.android.gms.maps.model.LatLng;

public class GrpRoomListExt extends GrpRoomListing {
	LatLng roomLatLng;

	public GrpRoomListExt(long room_id, String title, String category,
			long noOfLearner, String location, String latlng, LatLng roomLatLng) {
		super(room_id, title, category, noOfLearner, location, latlng);
		this.roomLatLng = roomLatLng;
	}
	public LatLng getRoomLatLng() {
		return roomLatLng;
	}
	public void setRoomLatLng(LatLng roomLatLng) {
		this.roomLatLng = roomLatLng;
	}

}
