package com.gobarnacle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.gobarnacle.utils.BarnacleClient;
import com.gobarnacle.utils.Tools;
import com.loopj.android.http.JsonHttpResponseHandler;

public class LoginActivity extends FragmentActivity implements FBFragment.LoginListener  {
	 public final static String LoginUri = "/signup/fb";
	 public final static String TAG = "LoginActivity";
	 public final static String LOGIN_TOKEN = "com.gobarnacle.TOKEN"; 

	 HttpClient httpClient = new DefaultHttpClient();
	 
	 private FBFragment fbFragment;
	 
	 private static GraphUser FBuser;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //printHashKey();
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            fbFragment = new FBFragment();
            getSupportFragmentManager()
            .beginTransaction()
            .add(android.R.id.content, fbFragment)
            .commit();
        } else {
            // Or set the fragment from restored state info
            fbFragment = (FBFragment) getSupportFragmentManager()
            .findFragmentById(android.R.id.content);
        }

        /* Check if already logged in, bypass to home screen */
        /* SharedPreferences */
    }
 

	@Override
	public void onLoggedIn(GraphUser user) {
		FBuser = user;
		try {
			PostLogin(user);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
				 
			
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }    

		
	 public void callPageActivity(String token) {
	        //Start the actual app
	        Intent intent = new Intent(this, PageListActivity.class);
	        //intent.putExtra(LOGIN_TOKEN, token);
			startActivity(intent);
			finish();
	 }
	 
	public void PostLogin(GraphUser user) throws JSONException, UnsupportedEncodingException {
		final Context context = this.getApplicationContext();
		final String userid = user.getId();
		BarnacleClient.setCookie(context,"user_id", userid);
		BarnacleClient.postJSON(context, LoginUri, user.getInnerJSONObject(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                String status;
				try {
					status = response.getString("status");
					Log.d(TAG, status);
			        if (status.equals("new") || status.equals("existing")) {
				      	// start page activity
			        	callPageActivity(userid);
			        } else {
			        	Tools.showToast("Barnacle login failed.",context);
			        }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });		
	}

	
	 public void printHashKey() {
	        try {
	            PackageInfo info = getPackageManager().getPackageInfo("com.gobarnacle",
	                    PackageManager.GET_SIGNATURES);
	            for (Signature signature : info.signatures) {
	                MessageDigest md = MessageDigest.getInstance("SHA");
	                md.update(signature.toByteArray());
	                Log.d("TEMPTAGHASH KEY:",
	                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	        } catch (NameNotFoundException e) {

	        } catch (NoSuchAlgorithmException e) {

	        }

	 }
	 
	 public static String userFirstName() {
		 return (String) FBuser.getProperty("first_name");
	 }
}