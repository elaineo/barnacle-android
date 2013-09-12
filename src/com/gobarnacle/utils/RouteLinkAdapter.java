package com.gobarnacle.utils;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gobarnacle.R;


public class RouteLinkAdapter extends ArrayAdapter<Route> {
	private final Context context;
	private final ArrayList<Route> values;

    public RouteLinkAdapter(Context context, ArrayList<Route> values) {
      super(context, R.layout.manage_list, values);
      this.context = context;
      this.values = values;
    }
  
  
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.manage_list, parent, false);
		TextView startText = (TextView) rowView.findViewById(R.id.start);
		startText.setText(values.get(position).locstart()); 
		TextView destText = (TextView) rowView.findViewById(R.id.dest);
		destText.setText(values.get(position).locend());
		TextView dateText = (TextView) rowView.findViewById(R.id.date);
		dateText.setText(values.get(position).delivend()); 
		return rowView;
	}
}	