package com.gobarnacle;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gobarnacle.layout.BarnacleView;
import com.gobarnacle.maps.MapActivity;
import com.gobarnacle.maps.MapSync;
import com.gobarnacle.maps.PostActivity;
import com.gobarnacle.utils.BarnacleClient;
import com.gobarnacle.utils.Route;
import com.gobarnacle.utils.RouteSync;
import com.gobarnacle.utils.Tools;
import com.loopj.android.http.JsonHttpResponseHandler;


/**
 * Main Menu for Barnacle
 */
public class MenuListActivity extends BarnacleView {

	public final static String TAG = "MenuListActivity";
	public final static String ROUTE_LINKS = "com.gobarnacle.ROUTE_LINKS";
	
	private final static String InactiveStatusUri = "/track/inactivate";

	private static ArrayList<Route> routes;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_buttons);
		
		/** Sync stored routes with server **/
		RouteSync rSync = new RouteSync();
		
		try {
			routes = rSync.execute(this).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		MapSync mSync = new MapSync();
		mSync.execute(this);				
	}

	public void launchTracker(View view) {
		Intent intent = new Intent(this, MapActivity.class);
		intent.putParcelableArrayListExtra(ROUTE_LINKS, routes);
		startActivity(intent);
	}
	public void launchCreateRoute(View view) {
		Intent intent = new Intent(this, PostActivity.class);
		startActivity(intent);
	}
	public void launchManager(View view) {
		Intent intent = new Intent(this, ManageActivity.class);
		intent.putParcelableArrayListExtra(ROUTE_LINKS, routes);
		startActivity(intent);
	}	

	public static void addRoute(Route r) {
		routes.add(r);
		return;
	}
	public static void deleteRoute(String routekey) {
		int d = getRouteByKey(routekey);
		routes.remove(d);
		return;
	}
	public static void updateRoute(String routekey, int newStat) {
		int d = getRouteByKey(routekey);
		Route r = routes.get(d);
		r.setStatus(newStat);
		routes.set(d,r);
		return;
	}
	public static int getRouteByKey(String routekey) {
		Route r = new Route(routekey,"","","",0);
		if (routes.contains(r)) 
		   return routes.indexOf(r);
		else 
			return -1;
	}
	
	public static void setAllInactive(final Context context) {
		BarnacleClient.post(context, InactiveStatusUri, null, new JsonHttpResponseHandler() {
		    @Override
		    public void onSuccess(JSONObject response) {
		        String status;
				try {
					status = response.getString("status");
			        if (status.equals("ok")) {
		        		Tools.showToast("All routes are now inactive.", context);
		        		for(int i=0;i<routes.size();i++) {
		        			if (routes.get(i).status()==0) 
		        				routes.get(i).setStatus(1);
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
	}
	public static ArrayList <Route> getActives() {
		ArrayList <Route> actives = new ArrayList<Route>();
		for(int i=0;i<routes.size();i++) {
			if (routes.get(i).status()==0 || routes.get(i).status()==2) 
				actives.add(routes.get(i));
		}
    	return actives;
	}
	
}
