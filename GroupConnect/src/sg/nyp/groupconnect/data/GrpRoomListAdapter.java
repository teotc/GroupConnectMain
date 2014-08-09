package sg.nyp.groupconnect.data;

import java.util.ArrayList;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.entity.Room;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GrpRoomListAdapter extends BaseAdapter {

	private ArrayList<Room> _data;
	Context _c;

	public GrpRoomListAdapter(ArrayList<Room> data, Context c) {
		_data = data;
		_c = c;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return _data.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return _data.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) _c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item_room_org, null);
		}

		TextView lmTitle = (TextView) v.findViewById(R.id.lmTitle);
		TextView lmCategory = (TextView) v.findViewById(R.id.lmCategory);
		TextView lmLocation = (TextView) v.findViewById(R.id.lmLocation);
		TextView lmStatus = (TextView) v.findViewById(R.id.lmStatus);

		Room msg = _data.get(position);

		lmTitle.setText(msg.getTitle());
		lmCategory.setText("Subject: " + msg.getCategory());
		lmLocation.setText(msg.getLocation());
		lmStatus.setText(msg.getStatus());

		if (msg.getStatus().equalsIgnoreCase("Ended")) {
			lmStatus.setBackgroundResource(R.drawable.rectangle_red);
		} else if (msg.getStatus().equalsIgnoreCase("Ongoing")) {
			lmStatus.setBackgroundResource(R.drawable.rectangle_green);
		}

		return v;
	}

}
