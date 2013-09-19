package com.gobarnacle;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.gobarnacle.utils.BarnacleClient;
import com.gobarnacle.utils.Tools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
      //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
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
		mapSetup(user);
    }
				 
			
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }    

		
	 public void callPageActivity(String token) {
	        //Start the actual app
	        Intent intent = new Intent(this, MenuListActivity.class);
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
	 
		public void mapSetup(GraphUser user) {
			/** check for google maps and play services. THEN log in. **/
	        if (!isGoogleMapsInstalled()) {
	            Builder builder = new AlertDialog.Builder(this);
	            builder.setMessage("Please install Google Maps");
	            builder.setCancelable(false);
	            builder.setPositiveButton("Install", getGoogleMapsListener());
	            AlertDialog dialog = builder.create();
	            dialog.show();
	        } else {
		        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		        
		        if (resultCode != ConnectionResult.SUCCESS)
		        	GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1).show();	        	        
		        else {
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
	        }
		}
		public boolean isGoogleMapsInstalled() {
		    try {
		        getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
		        return true;
		    } catch(PackageManager.NameNotFoundException e) {
		        return false;
		    }
		}
		 
		public OnClickListener getGoogleMapsListener() {
		    return new OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
		            startActivity(intent);
		            finish();
		        }
		    };
		}	 
}