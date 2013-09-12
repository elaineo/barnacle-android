package com.gobarnacle;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.gobarnacle.utils.BarnacleClient;
import com.gobarnacle.utils.Route;
import com.gobarnacle.utils.RouteLinkAdapter;
import com.gobarnacle.utils.Tools;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;

/* Manage Routes */
public class ManageActivity extends FragmentActivity {
	
	 
    public final static String TrackUri = "/track/status";
	public final static String TAG = "ManageActivity";

    private TextView mRouteView;	
    private static TextView mAddrView;	
    private ArrayList<Route> routes;


	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ManageActivity() {
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_manage);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        //Populate list
        final ListView listview = (ListView) findViewById(R.id.listrview);
        Intent intent = getIntent();
        routes = intent.getParcelableArrayListExtra(PageListActivity.ROUTE_LINKS);
        
        RouteLinkAdapter adapter = new RouteLinkAdapter(this, routes);
	    listview.setAdapter(adapter);    
	    
	    // set click listener
        

    }

    public void callTrackingPage(Route r) {
    	// load up webview
    }

}        
	

