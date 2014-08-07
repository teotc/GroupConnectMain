package sg.nyp.groupconnect.utilities;

import java.util.ArrayList;

import sg.nyp.groupconnect.*;
import sg.nyp.groupconnect.entity.Model;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ListAdapter extends ArrayAdapter<Model> {
 
        private final Context context;
        private final ArrayList<Model> modelsArrayList;
 
        public ListAdapter(Context context, ArrayList<Model> modelsArrayList) {
 
            super(context, R.layout.listitem_roomdetails, modelsArrayList);
 
            this.context = context;
            this.modelsArrayList = modelsArrayList;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
 
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
                //TextView counterView = (TextView) rowView.findViewById(R.id.item_counter);
 
                // 4. Set the text for textView
                imgView.setImageResource(modelsArrayList.get(position).getIcon());
                titleView.setText(modelsArrayList.get(position).getTitle());
                //counterView.setText(modelsArrayList.get(position).getCounter());
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
