package sg.nyp.groupconnect.entity;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
	private final LatLng mPosition;
	private final String mTitle;
	private final String mSnippet;
	private final BitmapDescriptor mIcon;

	public MyItem(LatLng mPosition, String mTitle, String mSnippet,
			BitmapDescriptor mIcon) {
		super();
		this.mPosition = mPosition;
		this.mTitle = mTitle;
		this.mSnippet = mSnippet;
		this.mIcon = mIcon;
	}

	public MyItem(LatLng mPosition, String mTitle,
			BitmapDescriptor mIcon) {
		super();
		this.mPosition = mPosition;
		this.mTitle = mTitle;
		this.mSnippet = "";
		this.mIcon = mIcon;
	}
	
	@Override
	public LatLng getPosition() {
		return mPosition;
	}

	public String getmTitle() {
		return mTitle;
	}

	public String getmSnippet() {
		return mSnippet;
	}

	public BitmapDescriptor getmIcon() {
		return mIcon;
	}

}
