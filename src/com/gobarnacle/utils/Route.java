package com.gobarnacle.utils;

import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable {
    private static final String MOBILE_URL = "http://www.gobarnacle.com/track/mobile/";
    private static final String BASE_URL = "http://www.gobarnacle.com/track/view/";
    
    private String share_url;
    private String mobile_url;
	private String routekey;
	private String locstart;
	private String locend;
	private String delivend;
	private Integer status; // 1 inactive, 0 active, 2 waiting

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((routekey == null) ? 0 : routekey.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		if (routekey == null) {
			if (other.routekey != null)
				return false;
		} else if (!routekey.equals(other.routekey))
			return false;
		return true;
	}
	public Route(String inRoutekey, String inLocstart, String inLocend, String inDelivend, Integer inStatus) {
		routekey = inRoutekey;
		locstart = inLocstart;
		locend = inLocend;
		delivend = inDelivend;
		status = inStatus;
		share_url = BASE_URL + inRoutekey;
		mobile_url = MOBILE_URL + inRoutekey;
	}
	public String URL() {
		return mobile_url;
	}
	public String shareURL() {
		return share_url;
	}
	public String locstart() {
		return locstart;
	}
	public String abbrevstart() {
		List<String> locList = Arrays.asList(locstart.split(","));
		String locAbbrev = locList.get(0);
		if (locList.size()>1)
			locAbbrev = locAbbrev + ", "+locList.get(1);
		return locAbbrev;
	}
	public String locend() {
		return locend;
	}
	public String abbrevend() {
		List<String> locList = Arrays.asList(locend.split(","));
		String locAbbrev = locList.get(0);
		if (locList.size()>1)
			locAbbrev = locAbbrev + ", "+locList.get(1);
		return locAbbrev;
	}	
	public String delivend() {
		return delivend;
	}
	public Integer status() { 
		return status;
	}
	public String routekey() { 
		return routekey;
	}	
	public void setStatus(Integer newStat) {
		status = newStat;
	}
    /* everything below here is for implementing Parcelable */
    public int describeContents() {
        return 0;
    }
    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
    	out.writeStringArray(new String[] {this.routekey,this.mobile_url,this.share_url,this.locstart,this.locend,this.delivend,this.status+""});
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
    public static String statusStr(int s) {
    	String status="waiting";    	
    	switch(s) {
    		case 0:
    			status="active";
    			break;
    		case 1:
    			status="inactive";
    			break;
    		case 2:
    			status="waiting";
    			break;
    		case 99:
    			status="completed";
    			break;
    	}
		return status;
    }

    //takes a Parcel and craps out Route
    private Route(Parcel in) {
        String[] data = new String[7];
        in.readStringArray(data);
        this.routekey = data[0];
        this.mobile_url = data[1];
        this.share_url = data[2];
        this.locstart = data[3];
        this.locend = data[4];
        this.delivend = data[5];
        this.status=Integer.parseInt(data[6]);
    }	
}
