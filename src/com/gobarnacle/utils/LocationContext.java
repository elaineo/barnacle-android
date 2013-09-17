package com.gobarnacle.utils;

import android.content.Context;
import android.location.Location;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**stupid wrapper class **/
public class LocationContext {

	private Context context;
	private Location location;
	private double latitude;
	private double longitude;
	//because EFF BroadcastReceivers
	private TextView textview;
	
	public LocationContext(Location l, TextView t, Context c) {
		context = c;
		location = l;
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		textview = t;
	}
	public LocationContext(Context c) {
		context = c;
	}
	public void LatLngContext(double lat, double lng, Context c){
		latitude = lat;
		longitude = lng;
		context = c;
	}
	public double getLat() {
		return latitude;
	}
	public double getLon() {
		return longitude;
	}
	public Context getContext() {
		return context;
	}
	public Location getLocation() {
		return location;
	}
	public void setContext(Context c) {
		context = c;
		return;
	}
	public void setLocation(Location l) {
		location = l;
		return;
	}
	public void setText(String s) {
		textview.setText(s);
	}
	public void setTextView(TextView t){
		textview = t;
	}
	public TextView getTextView() {
		return textview;
	}
	public void setLatLng(LatLng latlng) {
		latitude = latlng.latitude;
		longitude = latlng.longitude;
	}

}
