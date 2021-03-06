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
import android.widget.ImageButton;
import android.widget.TextView;

import com.gobarnacle.MenuListActivity;
import com.gobarnacle.R;
import com.loopj.android.http.JsonHttpResponseHandler;


public class RouteLinkAdapter extends ArrayAdapter<Route> {
	public final static String StatusUri = "/track/status";
	
	private static Context context;
	private final ArrayList<Route> values;
	private ImageButton deleteBtn=null;
	private static Button statusBtn=null;

    public RouteLinkAdapter(Context context, ArrayList<Route> values) {
      super(context, R.layout.manage_list, values);
      this.values = values;
    }
  
    public static void killView(View convertView) {
    	convertView.setVisibility(View.GONE);
    }
    public static String updateStatBtn(View convertView, Integer s) {
    	statusBtn= (Button) convertView.findViewById(R.id.status);	
		if (s==1)
			statusBtn.setBackgroundResource(R.drawable.button_inactive);
		else if (s==0)
			statusBtn.setBackgroundResource(R.drawable.button_active);
		else 
			statusBtn.setBackgroundResource(R.drawable.button_wait);
		return Route.statusStr(s);
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
		
		Integer statInt = values.get(position).status();			
		updateStatBtn(rowView, statInt);
		statusBtn.setTag(values.get(position).routekey());
		statusBtn.setFocusableInTouchMode(false);
		statusBtn.setFocusable(false);
        statusBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View btnView) {            	
            	final String routekey = btnView.getTag()+"";
				submitStatus(routekey,1, rowView);	 			
            }            
        });
        
		deleteBtn= (ImageButton) rowView.findViewById(R.id.delete);
        deleteBtn.setTag(values.get(position).routekey());
        deleteBtn.setFocusableInTouchMode(false);
        deleteBtn.setFocusable(false);
        deleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View btnView) {            	
            	final String routekey = btnView.getTag()+"";
            	new AlertDialog.Builder(context)
            	.setMessage("Are you sure you want to delete this route?")
            	.setIcon(android.R.drawable.ic_dialog_alert)
            	.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						submitStatus(routekey,0, rowView);						
					}})
            	 .setNegativeButton(android.R.string.cancel, null).show();            			
            }
        });		
		
		return rowView;
	}
	
	public static void submitStatus(final String routekey, final Integer statAction, final View rowView) {
    	JSONObject postParams = new JSONObject();
    	
    	try {
			postParams.put("routekey",routekey);
			postParams.put("status",statAction);
			BarnacleClient.postJSON(context, StatusUri, postParams, new JsonHttpResponseHandler() {
			    @Override
			    public void onSuccess(JSONObject response) {
			        String status;
					try {
						status = response.getString("status");
				        if (status.equals("ok")) {
				        	if (statAction<1) {
				        		Tools.showToast("Deleted.", context);
				        		MenuListActivity.deleteRoute(routekey);
				        		killView(rowView);
				        	} else {
				        		int newStat = Integer.parseInt(response.getString("newstat"));
				        		String statDescr = updateStatBtn(rowView, newStat);
				        		MenuListActivity.updateRoute(routekey, newStat);
				        		Tools.showToast("Route is now "+statDescr+".", context);
				        	}
				        } else {
				        	Tools.showToast(status, context);
				        }
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			});
			
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}	