package com.gobarnacle.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable {
    private static final String BASE_URL = "http://www.gobarnacle.com/track/view/";

	private String post_url;
	private String routekey;
	private String locstart;
	private String locend;
	private String delivend;
	private Integer status;

	
	public Route(String inRoutekey, String inLocstart, String inLocend, String inDelivend, Integer inStatus) {
		routekey = inRoutekey;
		locstart = inLocstart;
		locend = inLocend;
		delivend = inDelivend;
		status = inStatus;
		post_url = BASE_URL + inRoutekey;
	}
	public String URL() {
		return post_url;
	}
	public String locstart() {
		return locstart;
	}
	public String locend() {
		return locend;
	}
	public String delivend() {
		return delivend;
	}
	public Integer status() { 
		return status;
	}
    /* everything below here is for implementing Parcelable */
    public int describeContents() {
        return 0;
    }
    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
    	out.writeStringArray(new String[] {this.routekey,this.post_url,this.locstart,this.locend,this.delivend,this.status+""});
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    //takes a Parcel and craps out Route
    private Route(Parcel in) {
        String[] data = new String[6];
        in.readStringArray(data);
        this.routekey = data[0];
        this.post_url = data[1];
        this.locstart = data[2];
        this.locend = data[3];
        this.delivend = data[4];
        this.status=Integer.parseInt(data[5]);
    }	
}
