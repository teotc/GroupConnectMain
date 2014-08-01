package sg.nyp.groupconnect.custom;

import java.util.ArrayList;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.R.id;
import sg.nyp.groupconnect.R.layout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomList extends ArrayAdapter<String> {
	
	private final Activity context;
	private final ArrayList<String> web;
	private final Integer[] imageId;
	
	public CustomList(Activity context, ArrayList<String> web, Integer[] imageId)
	{
		super(context, R.layout.list_single, web);
		this.context = context;
		this.web = web;
		this.imageId = imageId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//Set up the inflater...
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_single, null, true);
		
		//Reference the widgets...
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		
		txtTitle.setText(web.get(position).replaceAll(",", ""));
		imageView.setImageResource(imageId[position]);
		
		//What happens when the button is clicked?
		final String titleClicked = web.get(position);
		Button clickBtn = (Button) rowView.findViewById(R.id.btn);
		clickBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v)
			{
				Toast.makeText(context, "You have selected at " + titleClicked , Toast.LENGTH_SHORT).show();
			}
			
		});
		
		
		return rowView;
	}

	
	
}
