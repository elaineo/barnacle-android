package com.gobarnacle;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gobarnacle.utils.Route;
import com.gobarnacle.utils.RouteLinkAdapter;

/* Manage Routes */
public class ManageActivity extends FragmentActivity {
	
	 
    public final static String TrackUri = "/track/status";
	public final static String TAG = "ManageActivity";
	public final static String TRACK_URL = "com.gobarnacle.TRACK_URL"; 

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
	    
	    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	        @Override
	        public void onItemClick(AdapterView<?> parent, final View view,
	            int position, long id) {
	          Route item = (Route) parent.getItemAtPosition(position);
	          Log.d(TAG,item.URL());
	          callTrackingPage(item.URL());
	        }

	      });	
        

    }

    public void callTrackingPage(String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(TRACK_URL, url);
		startActivity(intent);
		//finish();
    }

}        
	

