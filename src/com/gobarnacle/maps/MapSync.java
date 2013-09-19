package com.gobarnacle.maps;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;

public class MapSync extends AsyncTask<Context,Void,Void> implements android.location.LocationListener {
	private static final String TAG = "MapSync";
	LocationManager locationManager;	
	
	 protected void onPreExecute()
	    {
		 }
	
	@Override
	protected Void doInBackground(Context... context) {		      		 		 
		Looper.prepare();
		locationManager = (LocationManager) context[0].getSystemService(Context.LOCATION_SERVICE);
		String locationProvider = LocationManager.NETWORK_PROVIDER;								
		locationManager.requestLocationUpdates(locationProvider, (long) 0, (float) 0, this);
		return null;
		
	}
   protected void onPostExecute() {
	   locationManager.removeUpdates( this);
   }
   
	@Override
	public void onLocationChanged(Location location) {		   
		   
  }
	 @Override
	 public void onStatusChanged(String provider, int status, Bundle extras) { }

	 @Override
	 public void onProviderEnabled(String provider) { }

	 @Override
	 public void onProviderDisabled(String provider) { }	
   
}	
