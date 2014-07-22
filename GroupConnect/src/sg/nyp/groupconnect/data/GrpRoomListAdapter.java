package sg.nyp.groupconnect.data;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.learner.GrpRoomListing;

import java.util.ArrayList;

import android.content.Context;
import android.view.*;
import android.widget.*;

public class GrpRoomListAdapter extends BaseAdapter {

	private ArrayList<GrpRoomListing> _data;
	Context _c;

	public GrpRoomListAdapter(ArrayList<GrpRoomListing> data, Context c) {
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
			v = vi.inflate(R.layout.list_item_room, null);
		}

		//ImageView image = (ImageView) v.findViewById(R.id.icon);
		//image.setImageResource(msg.icon);
		TextView lmTitle = (TextView) v.findViewById(R.id.lmTitle);
		TextView lmCategory = (TextView) v.findViewById(R.id.lmCategory);
		TextView lmLocation = (TextView) v.findViewById(R.id.lmLocation);

		GrpRoomListing msg = _data.get(position);
		
		lmTitle.setText(msg.getTitle());
		lmCategory.setText("Subject: " + msg.getCategory());
		lmLocation.setText(msg.getLocation());

		// image.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// AlertDialog.Builder adb = new AlertDialog.Builder(_c);
		// adb.setMessage("Add To Contacts?");
		// adb.setNegativeButton("Cancel", null);
		// final int selectedid = position;
		// final String itemname = (String) _data.get(position).getName();
		//
		// adb.setPositiveButton("OK", new AlertDialog.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		//
		// System.out.println("Select " + selectedid);
		// System.out.println("Project " + itemname);
		//
		// Bundle b = new Bundle();
		// b.putString("project", itemname);
		// Intent createTask = new Intent(
		// "com.loginworks.tasktrek.CREATETASK");
		// createTask.putExtras(b);
		// _c.startActivity(createTask);
		// }
		// });
		//
		// adb.show();
		// }
		// });

		return v;
	}

}
