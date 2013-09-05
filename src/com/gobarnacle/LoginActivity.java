package com.gobarnacle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener  {
	 public final static String P2PLoginUri = "https://p2ppostal.appspot.com/signin/mobile";
	 public final static String LOGIN_TOKEN = "com.elaineou.p2ppostal.TOKEN"; 
	 public final static String TAG = "LoginActivity";
	 
	 private EditText pEmail;
	 private EditText pPasswd;	
	 private Button bLogin;	
	 
	 private String txtEmail;
	 private String txtPasswd;
	 private String login_token;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
    	pEmail = (EditText) findViewById(R.id.LoginEmail);
    	pPasswd = (EditText) findViewById(R.id.LoginPassword);
        bLogin = (Button) findViewById(R.id.btnLogin);
        bLogin.setOnClickListener(this);
        
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// Switching to Register screen
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
				finish();
			}
		});
    }
	 public void onClick(View view) {
	      if (view.getId() == R.id.btnLogin) { 	   
	    	 // form validation and login
	    	  txtEmail = pEmail.getText().toString();
	    	  txtPasswd = pPasswd.getText().toString();
		      if(txtEmail.equalsIgnoreCase("") || txtPasswd.equalsIgnoreCase("") ) {
		    	  showToastMessage("All Fields Required.");  
		    	  return;
		      }
		      final Boolean echeck = validEmail(txtEmail);
		      if(echeck==true) {
		    	  //send info to p2ppostal
		    	  GetLogin login_getter = new GetLogin();
		    	  try {
					login_token = login_getter.execute(txtEmail,txtPasswd).get();
			    	if(login_token!=null) {
			    		callPageActivity(login_token);
			    	} else {
			    		showToastMessage("Login Failed.");
				    	return;	
			    	}
				  } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				  } catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      } else {
		    	  showToastMessage("Invalid Email address.");
		    	  return;
		      }
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