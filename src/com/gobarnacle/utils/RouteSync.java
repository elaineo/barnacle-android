package com.gobarnacle.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

public class RouteSync extends AsyncTask<Context,Void,ArrayList<Route>> {
	private static final String RouteUri = "/track/getroutes";
	private static final String TAG = "RouteSync";
	
	class RouteResponseHandler extends JsonHttpResponseHandler {
		ArrayList<Route> routes = new ArrayList<Route>();
		
	    @Override
	    public void onSuccess(JSONObject response) {
	        String status;
			try {
				status = response.getString("status");
				Log.d(TAG, status);
		        if (status.equals("ok")) {
		        	JSONArray results = response.getJSONArray("routes");
		        	for(int i=0;i<results.length();i++) {
		        		JSONObject j = results.getJSONObject(i);
		        		Route r = new Route(j.getString("routekey"), j.getString("locstart"), 
		        				j.getString("locend"), j.getString("delivend"), Integer.parseInt(j.getString("statusint")));
		        		routes.add(r);
		        	}
		        } else
		        	routes = null;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public ArrayList<Route> Routes(){
	    	return routes;
	    }
	}
			
	@Override
	protected ArrayList<Route> doInBackground(Context... context) {		      		 		 
		RouteResponseHandler rr = new RouteResponseHandler();		 
		BarnacleClient.get(context[0], RouteUri, null, rr);
		return rr.Routes();
	}
   protected void onPostExecute(ArrayList<Route> result) {
       returnRoutes(result);
   }
   private ArrayList<Route> returnRoutes(ArrayList<Route> result) {
   	return result;
   }
}	
