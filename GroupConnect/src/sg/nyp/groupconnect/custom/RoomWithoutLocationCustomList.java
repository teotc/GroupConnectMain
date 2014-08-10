package sg.nyp.groupconnect.custom;

import java.util.ArrayList;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.R.id;
import sg.nyp.groupconnect.R.layout;
import sg.nyp.groupconnect.room.NotificationDisplay;
import sg.nyp.groupconnect.room.RoomDetails;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RoomWithoutLocationCustomList extends ArrayAdapter<String> {
	
	private final Activity context;
	private final ArrayList<String> title;
	private final ArrayList<String> message;
	//private final Integer[] imageId;
	private final ArrayList<Integer> imageId;
	private final ArrayList<String> status;
	
	public RoomWithoutLocationCustomList(Activity context, ArrayList<String> title, ArrayList<String> message, ArrayList<Integer> imageId, ArrayList<String> status)
	{
		super(context, R.layout.notification_custom_list, title);
		this.context = context;
		this.title = title;
		this.message = message;
		this.imageId = imageId;
		this.status = status;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//Set up the inflater...
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.roomwithoutlocation_custom_list, null, true);
		
		//Reference the widgets...
		TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
		TextView txtStatus = (TextView) rowView.findViewById(R.id.status);
		TextView txtMessage = (TextView) rowView.findViewById(R.id.message);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		
		txtTitle.setText(title.get(position));
		txtStatus.setText(status.get(position));
		txtMessage.setText(message.get(position));
		imageView.setImageResource(imageId.get(position));
		
		//What happens when the button is clicked?
		/*final String titleClicked = title.get(position);
		Button clickBtn = (Button) rowView.findViewById(R.id.btn);
		clickBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v)
			{
				Toast.makeText(context, "You have selected at " + titleClicked , Toast.LENGTH_SHORT).show();
			}
			
		});*/
		
		
		return rowView;
	}

	
	
}
