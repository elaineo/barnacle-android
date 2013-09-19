package com.gobarnacle.maps;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

public class MapTools {
	
	static LatLng CASA_ELAINE = new LatLng(37.42, -122.07);
	


	
	 public static Location getLocation(LocationManager locManager){
		String locationProvider = locManager.getBestProvider(new Criteria(), false);
					//LocationManager.NETWORK_PROVIDER;
    	Location location = locManager.getLastKnownLocation(locationProvider);
		 
 	    if (location == null) {		   
		   location = new Location("reverseGeocoded");
		   location.setLatitude(CASA_ELAINE.latitude);
		   location.setLongitude(CASA_ELAINE.longitude);
 	    } 
		  return location;		  
		 
	}
		
}
