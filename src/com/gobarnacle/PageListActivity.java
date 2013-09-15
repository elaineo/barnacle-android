package com.gobarnacle;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.gobarnacle.maps.MapActivity;
import com.gobarnacle.maps.PostActivity;
import com.gobarnacle.utils.Route;
import com.gobarnacle.utils.RouteSync;


/**
 * An activity representing a list of Pages. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link PageDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PageListFragment} and the item details (if present) is a
 * {@link PageDetailFragment}.
 * <p>
 * This activity also implements the required {@link PageListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class PageListActivity extends FragmentActivity implements
		PageListFragment.Callbacks {

	public final static String TAG = "PageListActivity";
	public final static String ROUTE_LINKS = "com.gobarnacle.ROUTE_LINKS"; 
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private static ArrayList<Route> routes;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_list);
		
		if (findViewById(R.id.page_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((PageListFragment) getSupportFragmentManager().findFragmentById(
					R.id.page_list)).setActivateOnItemClick(true);
		}

		/** Sync stored routes with server **/
		RouteSync rSync = new RouteSync();
		Log.d("CALLING", "RouteSync");
		
		try {
			routes = rSync.execute(this).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
				
	}

	/**
	 * Callback method from {@link PageListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		Intent intent;

		int Id = Integer.parseInt(id);
		switch (Id) {
			case 1: intent = new Intent(this, PostActivity.class); 
					break;
			case 3: intent = new Intent(this, ManageActivity.class); 
					break;
			default: intent = new Intent(this, MapActivity.class);
		}										
		
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
}
