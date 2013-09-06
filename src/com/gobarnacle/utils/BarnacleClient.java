package com.gobarnacle.utils;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class BarnacleClient {
  private static final String BASE_URL = "http://www.gobarnacle.com";

  private static AsyncHttpClient client = new AsyncHttpClient();
  

  public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	  PersistentCookieStore bCookie = new PersistentCookieStore(context);
	  client.setCookieStore(bCookie);
      client.get(getAbsoluteUrl(url), params, responseHandler);
  }

  public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	  PersistentCookieStore bCookie = new PersistentCookieStore(context);
	  client.setCookieStore(bCookie);
	  client.post(getAbsoluteUrl(url), params, responseHandler);
  }
  public static void postJSON(Context context, String url,  JSONObject params, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
	  StringEntity entity;
	  entity = new StringEntity(params.toString());
	  PersistentCookieStore bCookie = new PersistentCookieStore(context);
	  client.setCookieStore(bCookie);
	  client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);		
  }
 
  private static String getAbsoluteUrl(String relativeUrl) {
      return BASE_URL + relativeUrl;
  }
  
  public static void setCookie(Context context, String key, String value) {
	  BasicClientCookie fbCookie = new BasicClientCookie(key, value);
	  PersistentCookieStore bCookie = new PersistentCookieStore(context);
	  fbCookie.setVersion(1);
	  fbCookie.setDomain("www.gobarnacle.com");
	  fbCookie.setPath("/");
	  bCookie.addCookie(fbCookie);
  }
}