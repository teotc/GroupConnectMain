package sg.nyp.groupconnect.utilities;

import java.util.ArrayList;

import sg.nyp.groupconnect.*;
import sg.nyp.groupconnect.entity.Model;
import sg.nyp.groupconnect.room.RoomDetails;
import sg.nyp.groupconnect.room.db.retrieveRmMem;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ListAdapter extends ArrayAdapter<Model> {
 
        private final Context context;
        private final ArrayList<Model> modelsArrayList;
        private final String educatorId;
        private final ArrayList<String> learnerIdList;
 
        public ListAdapter(Context context, ArrayList<Model> modelsArrayList, String educatorId, ArrayList<String> learnerIdList) {
 
            super(context, R.layout.listitem_roomdetails, modelsArrayList);
 
            this.context = context;
            this.modelsArrayList = modelsArrayList;
            this.educatorId = educatorId;
            this.learnerIdList = learnerIdList;
        }
        
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	
        	String creatorName = "";
        	if (learnerIdList != null)
        	{

        		for(int i = 0; i<learnerIdList.size(); i++)
        		{
        			if (learnerIdList.get(i).equals(RoomDetails.creatorId))
        			{
        				creatorName = retrieveRmMem.memberArray.get(i);
        			}
        		}
        	}
        	if (educatorId != null)
        	{
        		if (educatorId.equals(RoomDetails.creatorId))
        			creatorName = retrieveRmMem.educatorArray.get(0);
        	}
        	
            // 1. Create inflater
            LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
            // 2. Get rowView from inflater
 
            View rowView = null;
            if(!modelsArrayList.get(position).isGroupHeader()){
                rowView = inflater.inflate(R.layout.listitem_roomdetails, parent, false);
 
                // 3. Get icon,title & counter views from the rowView
                ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon);
                TextView titleView = (TextView) rowView.findViewById(R.id.item_title);
                TextView creatorView = (TextView) rowView.findViewById(R.id.item_creator);
 
                // 4. Set the text for textView
                imgView.setImageResource(modelsArrayList.get(position).getIcon());
                titleView.setText(modelsArrayList.get(position).getTitle());
                
                
                if (creatorName.equals(titleView.getText()))
                	creatorView.setVisibility(View.VISIBLE);
                else
                	creatorView.setVisibility(View.GONE);
                
                Log.i("ListAdapter", creatorName);
                Log.i("ListAdapter", titleView.getText().toString());
            }
            else{
                    rowView = inflater.inflate(R.layout.listheader_roomdetails, parent, false);
                    TextView titleView = (TextView) rowView.findViewById(R.id.header);
                    titleView.setText(modelsArrayList.get(position).getTitle());
 
            }
 
            // 5. return rowView
            return rowView;
        }
}
