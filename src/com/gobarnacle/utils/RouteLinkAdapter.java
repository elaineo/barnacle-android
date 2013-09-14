package com.gobarnacle.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gobarnacle.R;
import com.loopj.android.http.JsonHttpResponseHandler;


public class RouteLinkAdapter extends ArrayAdapter<Route> {
	public final static String StatusUri = "/track/status";
	
	private final Context context;
	private final ArrayList<Route> values;
	private Button deleteBtn=null;
	private Button statusBtn=null;

    public RouteLinkAdapter(Context context, ArrayList<Route> values) {
      super(context, R.layout.manage_list, values);
      this.context = context;
      this.values = values;
    }
  
    public void killView(View convertView) {
    	convertView.setVisibility(View.GONE);
    }
    public void updateStatBtn(View convertView, Integer s) {
    	Button btn = (Button) convertView.findViewById(R.id.status);		
		if (s==1)
			btn.setText("inactive");
		else if (s==0)
			btn.setText("active");
		else 
			btn.setText("waiting");
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
		statusBtn= (Button) rowView.findViewById(R.id.status);		
		updateStatBtn(rowView, statInt);
		statusBtn.setTag(values.get(position).routekey());
        statusBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View btnView) {            	
            	final String routekey = btnView.getTag()+"";
				submitStatus(routekey,1, convertView, rowView);	 			
            }            
        });
        
		deleteBtn= (Button) rowView.findViewById(R.id.delete);
        deleteBtn.setTag(values.get(position).routekey());
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
						submitStatus(routekey,0, convertView, rowView);						
					}})
            	 .setNegativeButton(android.R.string.cancel, null).show();            			
            }
        });		
		
		return rowView;
	}
	
	public void submitStatus(String routekey, final Integer statAction, final View view, final View rowView) {
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
				        		killView(view);
				        	} else {
				        		Integer newStat = Integer.parseInt(response.getString("newstat"));
				        		updateStatBtn(rowView, newStat);
				        		Tools.showToast("Activated.", context);
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