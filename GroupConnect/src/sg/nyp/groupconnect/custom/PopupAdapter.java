/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.

  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package sg.nyp.groupconnect.custom;

import java.util.ArrayList;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.R.id;
import sg.nyp.groupconnect.R.layout;
import sg.nyp.groupconnect.room.RoomMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class PopupAdapter implements InfoWindowAdapter {
	LayoutInflater inflater=null;
	RoomMap rm = new RoomMap();

	public PopupAdapter(LayoutInflater inflater) {
		this.inflater=inflater;
	}

	public PopupAdapter()
	{

	}

	@Override
	public View getInfoWindow(Marker marker) {
		return(null);
	}


	@Override
	public View getInfoContents(Marker marker) {
		View popup=inflater.inflate(R.layout.popup, null);
		Log.i("PopupAdapter", String.valueOf(rm.resourceForIconArr.get(rm.count)));
		Log.i("PopupAdapter", String.valueOf(rm.count));
		//Split up the snippet and take out category
		if (marker.getSnippet()!= null)
		{
			if (!marker.getTitle().equals("Search Location"))
			{
				String [] content = marker.getSnippet().split("\n");
				String [] categoryArr = content[0].split(":");
				String category = categoryArr[1].substring(1);
				int resource = 0;
				//Check through which marker we are opening an info window
				for (int i = 0; i<rm.schSubList.size(); i++)
				{
					if (rm.schSubList.get(i).equals(category))
					{
						resource = R.drawable.schsub_roomicon;
					}
				}
				for (int i = 0; i<rm.musicList.size(); i++)
				{
					if (rm.musicList.get(i).equals(category))
					{
						resource = R.drawable.music_roomicon;
					}
				}
				for (int i = 0; i<rm.computerList.size(); i++)
				{
					if (rm.computerList.get(i).equals(category))
					{
						resource = R.drawable.computer_roomicon;
					}
				}
				for (int i = 0; i<rm.othersList.size(); i++)
				{
					if (rm.othersList.get(i).equals(category))
					{
						resource = R.drawable.ic_launcher;
					}
				}



				ImageView imgView = (ImageView) popup.findViewById(R.id.icon);
				imgView.setImageResource(resource);
				Log.i("PopupAdapter", "SchsubList: " + rm.schSubList);
				Log.i("PopupAdapter", "musicList: " + rm.musicList);
				Log.i("PopupAdapter", "computerList: " + rm.computerList);
				Log.i("PopupAdapter", "othersList: " + rm.othersList);
				Log.i("PopupAdapter", "Marker Name: " + marker.getTitle() + "\n" + "Marker category: " + category);
			}
		}

		TextView tv=(TextView)popup.findViewById(R.id.title);

		tv.setText(marker.getTitle());
		tv=(TextView)popup.findViewById(R.id.snippet);
		tv.setText(marker.getSnippet());

		return(popup);
	}
}