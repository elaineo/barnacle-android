package com.gobarnacle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.gobarnacle.utils.BarnacleClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class LoginActivity extends FragmentActivity implements FBFragment.LoginListener  {
	 public final static String LoginUri = "/signup/fb";
	 public final static String TAG = "LoginActivity";
	 public final static String LOGIN_TOKEN = "com.gobarnacle.TOKEN"; 

	 HttpClient httpClient = new DefaultHttpClient();
	 
	 private FBFragment fbFragment;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callPageActivity("219360");
//        if (savedInstanceState == null) {
//            // Add the fragment on initial activity setup
//            fbFragment = new FBFragment();
//            getSupportFragmentManager()
//            .beginTransaction()
//            .add(android.R.id.content, fbFragment)
//            .commit();
//        } else {
//            // Or set the fragment from restored state info
//            fbFragment = (FBFragment) getSupportFragmentManager()
//            .findFragmentById(android.R.id.content);
//        }

        /* Check if already logged in, bypass to home screen */
        /* SharedPreferences */
    }
 

	@Override
	public void onLoggedIn(GraphUser user) {
		try {
			PostLogin(user);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		GetLogin login_getter = new PostLogin();
//  	  	try {
//			login_getter.execute(user).get();
//		  } catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		  } catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

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
		Context context = this.getApplicationContext();
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
			        	showToastMessage("Barnacle login failed.");
			        }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });		
	}

	 
	class GetLogin extends AsyncTask<GraphUser,Void,String> {
		@Override
		protected String doInBackground(GraphUser... params) {		      
			HttpPost httpPost = new HttpPost(LoginUri);
			httpPost.setHeader("Content-Type", "application/json");
			String userid = params[0].getId(); 
			 			
	    	StringEntity se;
			try {
				se = new StringEntity( params[0].getInnerJSONObject().toString());
		        Log.d(TAG, params[0].getInnerJSONObject().toString());
		        httpPost.setEntity(se);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				return null;
			}  
		        
		    // Making HTTP Request
		    try {             
		    	HttpResponse response = httpClient.execute(httpPost);
		        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		        StringBuilder builder = new StringBuilder();
		        for (String line = null; (line = reader.readLine()) != null;) {
		        	builder.append(line).append("\n");
		        }
		        Log.d("Http Response:", builder.toString());
		        try {
			        JSONObject tokener = new JSONObject(builder.toString());
			        String status = tokener.getString("status");
			        if (status.equals("new") || status.equals("existing")) {
			      	  // start page activity
			        	callPageActivity(userid);
			        } else {
			        	showToastMessage("Barnacle login failed.");
			        }
		        } catch (JSONException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }  			 
		    } catch (ClientProtocolException e) {
		        // writing exception to log
		        e.printStackTrace();
		    } catch (IOException e) {
		        // writing exception to log
		        e.printStackTrace();
		    }

		    return userid;
	   }
	    protected void onPostExecute(String result) {
	        returnToken(result);
	    }
	    private String returnToken(String result) {
	    	return result;
	    }
	 }	 
	 /**
	 * Helper method to show the toast message
	 **/
	 void showToastMessage(String message){
	  Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	 }

}