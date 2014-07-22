package sg.nyp.groupconnect.item;

import sg.nyp.groupconnect.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@SuppressLint("NewApi")
public class fragment_educator_suggest extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);
		// Get ListView object from xml
		ListView listView = (ListView) rootView.findViewById(R.id.listView1);

		// Defined Array values to show in ListView
		String[] values = new String[] { "Joined Group 1", "Joined Group 2",
				"Joined Group 3", "Joined Group 4", "Joined Group 5",
				"Joined Group 6", "Joined Group 7", "Joined Group 8",
				"Joined Group 9" };

		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				rootView.getContext(), android.R.layout.simple_list_item_1,
				android.R.id.text1, values);

		// Assign adapter to ListView
		listView.setAdapter(adapter);
		return rootView;
	}
}