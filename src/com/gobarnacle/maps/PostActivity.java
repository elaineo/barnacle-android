package com.gobarnacle.maps;

import java.io.IOException;
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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gobarnacle.PageDetailActivity;
import com.gobarnacle.PageListActivity;
import com.gobarnacle.R;
import com.gobarnacle.ShareActivity;
import com.gobarnacle.utils.BarnacleClient;
import com.gobarnacle.utils.Route;
import com.gobarnacle.utils.Tools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
public class PostActivity extends FragmentActivity implements
								ConnectionCallbacks,
								OnConnectionFailedListener,
								LocationListener,  OnMapClickListener,
								OnMyLocationButtonClickListener, android.location.LocationListener {
	
    private static AsyncHttpClient client = new AsyncHttpClient();
	 
	public final static String ROUTE_POST = "com.gobarnacle.ROUTE_POST"; 
    public final static String PostUri = "/track/create";
	public final static String TAG = "PostActivity";
	public static final Integer ZOOM = 8;
	
	private Boolean startSel = false;
	private double startLat, startLon, destLat, destLon;
	private LatLng lastTapped;
	
    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private LocationClient mLocationClient;
    private TextView mLocStart;
    private TextView mLocEnd;
    private static TextView mTapped;	
    private TextView mAddr;	
    private static DatePicker mDate;
    private Button mStartBtn, mDestBtn;
    private Button mPostBtn;
    
    private Boolean initialized = false;

    
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PostActivity() {
	}
    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fillpost);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);         

        mDate = (DatePicker) findViewById(R.id.arrival_date);
        mDate.setMinDate(System.currentTimeMillis() - 1000);
        
        mLocStart = (TextView) findViewById(R.id.locstart);
        mLocEnd = (TextView) findViewById(R.id.locend);
        mTapped = (TextView) findViewById(R.id.tapped_text);
        mAddr = (TextView) findViewById(R.id.tapped_addr);
        mStartBtn = (Button) findViewById(R.id.set_start_btn);
        mDestBtn = (Button) findViewById(R.id.set_dest_btn); 
        mPostBtn = (Button) findViewById(R.id.post_btn); 
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER        
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
    	
    	postParams.put("startlat",startLat); 
    	postParams.put("startlon",startLon);
    	postParams.put("destlat",destLat);
    	postParams.put("destlon",destLon);
    	postParams.put("locstart",mLocStart.getText());
    	postParams.put("locend",mLocEnd.getText());
    	
    	postParams.put("delivend",(mDate.getMonth()+1) + "/" + mDate.getDayOfMonth()+"/" + mDate.getYear());
    	    	
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

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng;
        if (initialized == false) {
        	latLng = new LatLng(location.getLatitude(), location.getLongitude());
        	CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM);
	        mMap.animateCamera(cameraUpdate);
	        locationManager.removeUpdates(this);
	        Log.d(TAG,""+latLng);
	        startLat = latLng.latitude;
	        startLon = latLng.longitude;
			try {
				getStringFromLatLng(latLng, mLocStart);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initialized = true;
        }
    }

    @Override
    public void onMapClick(LatLng point) {
    	Log.d(TAG, "tapped");
        mTapped.setText("" + point);
        lastTapped = point;
        if (startSel)
        	mStartBtn.setEnabled(true);
        mDestBtn.setEnabled(true);
        try {
			getStringFromLatLng(point, mAddr);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void getStringFromLatLng(final LatLng loc, final TextView addr)
            throws ClientProtocolException, IOException, JSONException {
    	
        String address = String
                .format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
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

	 @Override
	 public void onStatusChanged(String provider, int status, Bundle extras) { }

	 @Override
	 public void onProviderEnabled(String provider) { }

	 @Override
	 public void onProviderDisabled(String provider) { }
    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener	        	        
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
		
		PageListActivity.addRoute(route);
		finish();
    }
}        
	

