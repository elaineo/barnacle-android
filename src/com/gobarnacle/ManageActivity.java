package com.gobarnacle;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gobarnacle.layout.BarnacleView;
import com.gobarnacle.utils.Route;
import com.gobarnacle.utils.RouteCompletedAdapter;
import com.gobarnacle.utils.RouteLinkAdapter;

/* Manage Routes */
public class ManageActivity extends BarnacleView {
	
	 
	public final static String TAG = "ManageActivity";
	public final static String TRACK_URL = "com.gobarnacle.TRACK_URL"; 

    private ArrayList<Route> routes;
    private ArrayList<Route> completed;

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
        routes = intent.getParcelableArrayListExtra(MenuListActivity.ROUTE_LINKS);
        completed = new ArrayList <Route>();
        
        /** split routes into complete and incomplete **/
		for(int i=0;i<routes.size();i++) {
			if (routes.get(i).status()==99) {
				completed.add(routes.get(i));
				routes.remove(i);
			}
		}
        
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
	    if (completed.size()>0) {
	        final ListView listcompleted = (ListView) findViewById(R.id.listcompleted);        
	        RouteCompletedAdapter adaptercompl = new RouteCompletedAdapter(this, completed);
		    listcompleted.setAdapter(adaptercompl);    
		    
		    listcompleted.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		        @Override
		        public void onItemClick(AdapterView<?> parent, final View view,
		            int position, long id) {
		          Route item = (Route) parent.getItemAtPosition(position);
		          callTrackingPage(item.URL());
		        }
		      });	
	    } else {
	    	TextView completedHead = (TextView) findViewById(R.id.completed_head);
	    	completedHead.setVisibility(View.GONE);
	    }
    }

    public void callTrackingPage(String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(TRACK_URL, url);
		startActivity(intent);
		//finish();
    }

}        
	

