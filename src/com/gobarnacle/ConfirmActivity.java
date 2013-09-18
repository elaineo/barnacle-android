package com.gobarnacle;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gobarnacle.layout.BarnacleView;
import com.gobarnacle.utils.BarnacleClient;
import com.gobarnacle.utils.Route;
import com.gobarnacle.utils.Tools;
import com.loopj.android.http.JsonHttpResponseHandler;

/* Manage Routes */
public class ConfirmActivity extends BarnacleView {
	
	 
	public final static String TAG = "ConfirmActivity";
	public final static String ConfirmUri = "/track/confirm";
	public final static String SendConfirmUri = "/track/sendconfirm";

	private EditText confirmCode;
	private RadioGroup locRadios;
	private Button sendConfirmBtn;
	private Button checkCodeBtn;
	private ArrayList<Route> activeRoutes;
	
	private String activeRouteKey="";
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ConfirmActivity() {
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_confirm);
        
        confirmCode = (EditText) findViewById(R.id.confirm_code);
        sendConfirmBtn = (Button)  findViewById(R.id.send_confirm_btn);
        checkCodeBtn = (Button)  findViewById(R.id.check_code_btn);
        //Populate Location Radio buttons
        locRadios = (RadioGroup)  findViewById(R.id.radio_locations); 
        activeRoutes = MenuListActivity.getActives();
        for(int i=0;i<activeRoutes.size();i++) {
            RadioButton locEnd = new RadioButton(this);
            locEnd.setText(activeRoutes.get(i).locend());
            locEnd.setTag(activeRoutes.get(i).routekey());
            locEnd.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
	                locSelClicked();
				}
            });
            locRadios.addView(locEnd);
        }
    }
    
    public void locSelClicked() {      	
    	// Figure out who is selected
		for (int i = 1; i < locRadios.getChildCount(); i++) {
	        RadioButton locButt = (RadioButton) locRadios.getChildAt(i);
	        if (locButt.isChecked())
	        	activeRouteKey = (String) locButt.getTag();
	    }		    
		checkCodeBtn.setEnabled(true);
		sendConfirmBtn.setEnabled(true);
    }
    
    public void checkCode(View view) throws UnsupportedEncodingException, JSONException {
    	
    	String code = confirmCode.getText().toString();
    	final Context context = this.getApplicationContext();
    	JSONObject postParams = new JSONObject();		

    	postParams.put("routekey",activeRouteKey);
    	postParams.put("code",code);
    	
		BarnacleClient.postJSON(context, ConfirmUri, postParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                String status;
				try {
					status = response.getString("status");
			        if (status.equals("ok")) {
			        	Tools.showToast("Delivery Confirmed! We will send a notification to the sender.", context);
			        	MenuListActivity.updateRoute(activeRouteKey, 99);
			        	finish();
			        } else {
			        	MenuListActivity.updateRoute(activeRouteKey, 2);
			        	Tools.showToast(status, context);
			        }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		});
    	
	} 	   
    public void sendConfirm(View view) throws UnsupportedEncodingException, JSONException {  
    	final Context context = this.getApplicationContext();
    	JSONObject postParams = new JSONObject();		
		
    	postParams.put("routekey",activeRouteKey);
    	
		BarnacleClient.postJSON(context, SendConfirmUri, postParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                String status;
				try {
					status = response.getString("status");
			        if (status.equals("ok")) {
			        	Tools.showToast("Delivery notification sent! Now awaiting confirmation from the recipient.", context);
			        	// change route's status to waiting
			        	MenuListActivity.updateRoute(activeRouteKey, 2);
			        	finish();
			        } else {
			        	Tools.showToast(status, context);
			        }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		});
    	
	} 	    

}        
	

