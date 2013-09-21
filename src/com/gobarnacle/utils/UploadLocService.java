package com.gobarnacle.utils;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UploadLocService extends Service implements 
							ConnectionCallbacks,
							OnConnectionFailedListener {

    private static final String TAG = "UploadLocService";
    public final static String TrackUri = "/track/updateloc";
    
    private static Context context;

    LocationConverter locConvert;
    private LocationClient locationClient;
    private static Location location;
    private static UploadResponseHandler urh;
    private static String msgExtra;
	
	class UploadResponseHandler extends JsonHttpResponseHandler {

	    @Override
	    public void onSuccess(JSONObject response) {
	        String status;
			try {
				status = response.getString("status");
	        	Log.e(TAG,status);
		        if (status!="ok") 
		        	locationClient.disconnect();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}   
	
    public static void updateLocation(Location loc, String locstr, String msg, final Context context) throws JSONException, UnsupportedEncodingException  {
    	
    	JSONObject locParams = new JSONObject();
    	locParams.put("lat",loc.getLatitude());
    	locParams.put("lon",loc.getLongitude());
    	
    	locParams.put("locstr",locstr);
    	locParams.put("msg",msg);
    	
		BarnacleClient.postJSON(context, TrackUri, locParams, urh);		    	
    }      
    
    @Override
    public void onCreate() {
    	locationClient = new LocationClient(this, this, this);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	/** Get current location. Convert to formatted address. Upload. **/
    	
    	context = this;
    	msgExtra = intent.getStringExtra(com.gobarnacle.maps.MapActivity.EXTRA_MSG);
    	Log.v(TAG, "starting update  "+msgExtra);    	
    	
    	urh = new UploadResponseHandler();
    	locationClient.connect();
   	    return Service.START_FLAG_REDELIVERY;
    }
    @Override
	public void onDestroy() {
        // Disconnecting the client invalidates it.
        locationClient.disconnect();
    }    	
    	
    	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
 
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		locConvert = new LocationConverter();

    	location = locationClient.getLastLocation();

    	LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    	LocationConverter(latLng);
    			
	}
	 /** ANDROID IS SO STUPID!!! WHY DO I HAVE TO COPY AND PASTE CODE HERE??? **/
    public static void LocationConverter(LatLng latlng) {
    	final String MAP_URI = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language=";
    	final String TAG = "LocationConverter";
        AsyncHttpClient client = new AsyncHttpClient();
        		
		String address = String
                .format(Locale.ENGLISH, MAP_URI + Locale.getDefault().getCountry(), latlng.latitude, latlng.longitude);
        
        client.get(address, null, new JsonHttpResponseHandler() {
    		String formAddress = "";
    		
    		@Override
            public void onSuccess(JSONObject response) {
                String status;
    			try {
    				status = response.getString("status");
    	            if ("OK".equalsIgnoreCase(status)) {
    	                JSONArray results = response.getJSONArray("results");
                        formAddress = results.getJSONObject(0).getString("formatted_address");
                        Log.v(TAG,formAddress);
                    	updateLocation(location, formAddress, msgExtra, context);
                    }			        
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }	    
    	});
    	
    }
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}



}
