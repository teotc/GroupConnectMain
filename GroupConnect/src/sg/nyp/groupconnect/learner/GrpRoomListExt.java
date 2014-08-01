package sg.nyp.groupconnect.learner;


import com.google.android.gms.maps.model.LatLng;

public class GrpRoomListExt extends GrpRoomListing {
	LatLng roomLatLng;
	double distance;
	public GrpRoomListExt(long room_id, String title, String category,
			long noOfLearner, String location, String latlng,
			LatLng roomLatLng, double distance) {
		super(room_id, title, category, noOfLearner, location, latlng);
		this.roomLatLng = roomLatLng;
		this.distance = distance;
	}
	public LatLng getRoomLatLng() {
		return roomLatLng;
	}
	public void setRoomLatLng(LatLng roomLatLng) {
		this.roomLatLng = roomLatLng;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
}
