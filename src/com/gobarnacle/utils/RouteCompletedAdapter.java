package com.gobarnacle.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gobarnacle.PageListActivity;
import com.gobarnacle.R;
import com.loopj.android.http.JsonHttpResponseHandler;


public class RouteCompletedAdapter extends ArrayAdapter<Route> {
	public final static String StatusUri = "/track/status";
	
	private final Context context;
	private final ArrayList<Route> values;
	private Button deleteBtn=null;

    public RouteCompletedAdapter(Context context, ArrayList<Route> values) {
      super(context, R.layout.manage_list, values);
      this.context = context;
      this.values = values;
    }
  
    
	@Override
	public View getView(int position, final View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		final View rowView = inflater.inflate(R.layout.manage_list, parent, false);
		TextView startText = (TextView) rowView.findViewById(R.id.start);
		startText.setText(values.get(position).locstart()); 
		TextView destText = (TextView) rowView.findViewById(R.id.dest);
		destText.setText(values.get(position).locend());
		TextView dateText = (TextView) rowView.findViewById(R.id.date);
		dateText.setText(values.get(position).delivend()); 
		return rowView;
	}
}	