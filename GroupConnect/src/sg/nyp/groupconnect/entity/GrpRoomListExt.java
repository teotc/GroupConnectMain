package sg.nyp.groupconnect.entity;

import com.google.android.gms.maps.model.LatLng;

public class GrpRoomListExt extends GrpRoomListing {
	LatLng roomLatLng;
	double distance;
	int icon;
	public GrpRoomListExt(long room_id, String title, String category,
			long noOfLearner, String location, String latlng,
			LatLng roomLatLng, double distance, int icon) {
		super(room_id, title, category, noOfLearner, location, latlng);
		this.roomLatLng = roomLatLng;
		this.distance = distance;
		this.icon = icon;
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
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
}
