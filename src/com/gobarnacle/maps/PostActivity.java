package com.gobarnacle.maps;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gobarnacle.MenuListActivity;
import com.gobarnacle.R;
import com.gobarnacle.ShareActivity;
import com.gobarnacle.layout.BarnacleView;
import com.gobarnacle.layout.SliderContainer;
import com.gobarnacle.utils.BarnacleClient;
import com.gobarnacle.utils.Route;
import com.gobarnacle.utils.Tools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * A fragment representing a single Page detail screen. This fragment is either
 * contained in a {@link PageListActivity} in two-pane mode (on tablets) or a
 * {@link PageDetailActivity} on handsets.
 */
public class PostActivity extends BarnacleView implements
								ConnectionCallbacks,
								OnConnectionFailedListener,
								OnMapClickListener, OnMyLocationButtonClickListener {
	
    private static AsyncHttpClient client = new AsyncHttpClient();
	 
	public final static String ROUTE_POST = "com.gobarnacle.ROUTE_POST"; 
    public final static String PostUri = "/track/create";
	public final static String TAG = "PostActivity";
	private static final String MAP_URI = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language=";
	public static final Integer ZOOM = 8;
	
	private Boolean startSel = false;
	private double startLat, startLon, destLat, destLon;
	private LatLng lastTapped;
	
    private GoogleMap mMap;
    private LocationManager locationManager;    

    private LocationClient mLocationClient;
    private TextView mLocStart;
    private TextView mLocEnd;
    private static TextView mTapped;	
    private TextView mAddr;	
    private Button mStartBtn, mDestBtn;
    private Button mPostBtn;
    
    private SliderContainer mContainer;
    
    private static int timeZone = 0;
    private int destTZ;
    
    Context context;

    
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PostActivity() {
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fillpost);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);     
        context = this;
        
        mLocStart = (TextView) findViewById(R.id.locstart);
        mLocEnd = (TextView) findViewById(R.id.locend);
        mTapped = (TextView) findViewById(R.id.tapped_text);
        mAddr = (TextView) findViewById(R.id.tapped_addr);
        mStartBtn = (Button) findViewById(R.id.set_start_btn);
        mDestBtn = (Button) findViewById(R.id.set_dest_btn); 
        mPostBtn = (Button) findViewById(R.id.post_btn); 
        
        mContainer = (SliderContainer) this.findViewById(R.id.dateSliderContainer);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);                
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maptap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
                mMap.setOnMapClickListener(this);
            }
        }
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }

    /** 
     * @throws ClientProtocolException 
     * @throws IOException 
     * @throws JSONException 
     */
    public void submitPost(View view) throws ClientProtocolException, IOException, JSONException {
    	final Context context = this.getApplicationContext();
    	JSONObject postParams = new JSONObject();
    	
    	postParams.put("tzoffset",destTZ);
    	postParams.put("startlat",startLat); 
    	postParams.put("startlon",startLon);
    	postParams.put("destlat",destLat);
    	postParams.put("destlon",destLon);
    	postParams.put("locstart",mLocStart.getText());
    	postParams.put("locend",mLocEnd.getText());
    	
    	Log.v(TAG,mContainer.getTime().toString());
    	Calendar c = mContainer.getTime();
    	postParams.put("delivend",(c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.DAY_OF_MONTH)+"/" + c.get(Calendar.YEAR));
    	    	
    	BarnacleClient.postJSON(context, PostUri, postParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                String status;
				try {
					status = response.getString("status");
			        if (status.equals("ok")) {
			        	Tools.showToast("Route Created.", context);
			        	// respond with route info to update list
			        	JSONObject j = response.getJSONObject("route");
		        		Route r = new Route(j.getString("routekey"), j.getString("locstart"), 
		        				j.getString("locend"), j.getString("delivend"), Integer.parseInt(j.getString("statusint")));
		        		callSharePage(r);
			        } else {
			        	Tools.showToast("Route failed.", context);
			        }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });				
    }


    @Override
    public void onMapClick(LatLng point) {
    	Log.d(TAG, "tapped");
        mTapped.setText("" + point);
        lastTapped = point;
        if (startSel)
        	mStartBtn.setEnabled(true);
        mDestBtn.setEnabled(true);
        TimeZoneConverter(point);
		LocationConverter(point, mAddr);
    }
    
    public static void getStringFromLatLng(final LatLng loc, final TextView addr)
            throws ClientProtocolException, IOException, JSONException {
    	
        String address = String
                .format(Locale.ENGLISH, MAP_URI
                                + Locale.getDefault().getCountry(), loc.latitude, loc.longitude);
        addr.setText(" " +loc);
        client.get(address, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                String status;
                String indiStr;

				try {
					status = response.getString("status");
		            if ("OK".equalsIgnoreCase(status)) {
		                JSONArray results = response.getJSONArray("results");
	                    indiStr = results.getJSONObject(0).getString("formatted_address");
	                    addr.setText(indiStr);
	                }			        
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
	    });
    }    

    public void setStartLocation(View view) {
    	mLocStart.setText(mAddr.getText());
    	startLat = lastTapped.latitude;
    	startLon = lastTapped.longitude;
    }	        
    public void setDestLocation(View view) {
    	mLocEnd.setText(mAddr.getText());
    	destLat = lastTapped.latitude;
    	destLon = lastTapped.longitude;
    	destTZ = timeZone;
    	mPostBtn.setEnabled(true);
    }	            
    public void onStartSelClicked(View view) {
    	boolean checked = ((RadioButton) view).isChecked();
        
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.startsel0:
                if (checked) {
                	startSel=false;
                	mStartBtn.setEnabled(false);
                }
                break;
            case R.id.startsel1:
                if (checked)
                	startSel=true;
                break;
        }
    }	            

    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
    	String locationProvider = LocationManager.NETWORK_PROVIDER;
    	// Or use LocationManager.GPS_PROVIDER

    	Location location = locationManager.getLastKnownLocation(locationProvider);
        LatLng latLng;
    	latLng = new LatLng(location.getLatitude(), location.getLongitude());
    	CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM);
        mMap.animateCamera(cameraUpdate);
		LocationConverter(latLng, mLocStart);
    	startLat = latLng.latitude;
    	startLon = latLng.longitude;
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }	    
	    	 
	    
    public void callSharePage(Route route) {
        Intent intent = new Intent(this, ShareActivity.class);
        intent.putExtra(ROUTE_POST, route);
		startActivity(intent);
		
		MenuListActivity.addRoute(route);
		finish();
    }
    
    
    
    
    /** ANDROID IS SO STUPID!!! WHY DO I HAVE TO COPY AND PASTE CODE HERE??? **/
    public static void LocationConverter(LatLng latlng, final TextView t) {
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
                        t.setText(formAddress);
                    }			        
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }	    
    	});
    	
    }

    public static void TimeZoneConverter(LatLng latlng) {
    	final String TZ_URI = "https://maps.googleapis.com/maps/api/timezone/json?location=%1$f,%2$f&timestamp=%3$s&sensor=true";    			
    	final String TAG = "TimeZoneConverter";
        AsyncHttpClient client = new AsyncHttpClient();
        		
        Long tsLong = System.currentTimeMillis()/1000;
		String address = String.format(Locale.ENGLISH, TZ_URI, latlng.latitude, latlng.longitude, tsLong.toString());
		Log.v(TAG,address);
        client.get(address, null, new JsonHttpResponseHandler() {
    		
    		@Override
            public void onSuccess(JSONObject response) {
                String status;				                
    			try {
    				status = response.getString("status");
    				Log.v(TAG,status);
    	            if ("OK".equalsIgnoreCase(status)) {
    	                timeZone = Integer.parseInt(response.getString("rawOffset"));
                        Log.v(TAG,timeZone+"");
                    }			        
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
            }	    
    	});
    	
    }    
}        
	

