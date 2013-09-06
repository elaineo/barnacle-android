package com.gobarnacle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class LoginActivity extends FragmentActivity implements OnClickListener  {
	 public final static String P2PLoginUri = "https://p2ppostal.appspot.com/signin/mobile";
	 public final static String LOGIN_TOKEN = "com.elaineou.p2ppostal.TOKEN"; 
	 public final static String TAG = "LoginActivity";
	 
	 private EditText pEmail;
	 private EditText pPasswd;	
	 private Button bLogin;	
	 
	 private String txtEmail;
	 private String txtPasswd;
	 private String login_token;
	 
	 private MainFragment mainFragment;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager()
            .beginTransaction()
            .add(android.R.id.content, mainFragment)
            .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager()
            .findFragmentById(android.R.id.content);
        }
        //setContentView(R.layout.login);                
        /*
        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
    	pEmail = (EditText) findViewById(R.id.LoginEmail);
    	pPasswd = (EditText) findViewById(R.id.LoginPassword);
        bLogin = (Button) findViewById(R.id.btnLogin);
        bLogin.setOnClickListener(this);
        */
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }    
	 public void onClick(View view) {
	      if (view.getId() == R.id.btnLogin) { 	   
	  		// start Facebook Login
	  		Session.openActiveSession(this, true, new Session.StatusCallback() {
	  			
	  			// callback when session changes state
	  			@SuppressWarnings("deprecation")
	  			@Override
	  			public void call(Session session, SessionState state, Exception exception) {
	  				if (session.isOpened()) {
	  					// make request to the /me API
	  					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

	  					  // callback after Graph API response with user object
	  					  @Override
	  					  public void onCompleted(GraphUser user, Response response) {
	  						  Log.d(TAG,"Logged in");
	  						  if (user != null) {
	  							  TextView welcome = (TextView) findViewById(R.id.link_to_register);
	  							  welcome.setText("Hello " + user.getName() + "!");
	  							}
	  					  }
	  					});
	  				}
	  			}
	  		});
  		}		
	 }
 
	public Boolean validEmail(String email)
	{
	    Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
	    Matcher matcher = pattern.matcher(email);
	    return matcher.matches();
	}
	
		
	 public void callPageActivity(String token) {
	        //Start the actual app
	        Intent intent = new Intent(this, PageListActivity.class);
	        intent.putExtra(LOGIN_TOKEN, token);
			startActivity(intent);
			finish();
	 }
	 
	 class GetLogin extends AsyncTask<String,Void,String> {
		 @Override
		 protected String doInBackground(String... params) {		      
		      // Creating HTTP client

			 HttpClient httpClient = new DefaultHttpClient();
			 String paramstr = "?email="+txtEmail+"&password="+txtPasswd;
			 HttpPost httpPost = new HttpPost(P2PLoginUri+paramstr);
		      
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
			          if (status.equals("success")) {
				          String token = tokener.getString("login_token");
				          return token;
			          } else {
			        	  return null;
			          }
  		          } catch (JSONException e) {
		              // TODO Auto-generated catch block
		              e.printStackTrace();
		          }    
		          // writing response to log
		      } catch (ClientProtocolException e) {
		          // writing exception to log
		          e.printStackTrace();
		      } catch (IOException e) {
		          // writing exception to log
		          e.printStackTrace();
		      }
		      return null;
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