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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomCategoryList extends ArrayAdapter<String> {
	
	private final Activity context;
	//private final ArrayList<String> web;
	//private final Integer[] imageId;
	private final ArrayList<String> categoryNameChosen;
	private final ArrayList<String> categoryTypeChosen;
	
	public CustomCategoryList(Activity context, ArrayList<String> categoryNameChosen, ArrayList<String> categoryTypeChosen)//, Integer[] imageId)
	{
		super(context, R.layout.list_category_single, categoryNameChosen);
		this.context = context;
		this.categoryNameChosen = categoryNameChosen;
		this.categoryTypeChosen = categoryTypeChosen;
		//this.imageId = imageId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//Set up the inflater...
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_category_single, null, true);
		
		//Reference the widgets...
		TextView txtCategoryName = (TextView) rowView.findViewById(R.id.txt1);
		TextView txtCategoryType = (TextView) rowView.findViewById(R.id.txt2);		
		
		txtCategoryName.setText(categoryNameChosen.get(position).replaceAll(",", ""));
		txtCategoryType.setText(categoryTypeChosen.get(position).replaceAll(",", ""));
		
		/*LinearLayout llRow = (LinearLayout) rowView.findViewById(R.id.llRow);
		rowView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v)
			{
				Toast.makeText(context, "You have Click llrow" , Toast.LENGTH_SHORT).show();
			}
			
		});*/
		
		Button btnAdd = (Button) rowView.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v)
			{
				Toast.makeText(context, "You have selected button" , Toast.LENGTH_SHORT).show();
			}
			
		});
		
		
		return rowView;
	}

	
	
}
