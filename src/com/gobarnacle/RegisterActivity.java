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
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener {
	 public final static String P2PLoginUri = "https://p2ppostal.appspot.com/signup/mobile";
	 public final static String LOGIN_TOKEN = "com.elaineou.p2ppostal.TOKEN"; 
	 public final static String TAG = "RegisterActivity";
	 
	 private EditText pName;	
	 private EditText pEmail;
	 private EditText pPasswd;	
	 private Button bRegister;	
	 
	 private String txtName;
	 private String txtEmail;
	 private String txtPasswd;
	 private String login_token;
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.register);
        
        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
        pName = (EditText) findViewById(R.id.RegName);
        pEmail = (EditText) findViewById(R.id.RegEmail);
    	pPasswd = (EditText) findViewById(R.id.RegPassword);
        bRegister = (Button) findViewById(R.id.btnRegister);
        bRegister.setOnClickListener(this);
        
        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// Switching to Login Screen
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(i);
				finish();
			}
		});
    }
	 public void onClick(View view) {
	      if (view.getId() == R.id.btnRegister) { 	   
	    	 // form validation and login
	    	  txtName = pName.getText().toString();
	    	  txtEmail = pEmail.getText().toString();
	    	  txtPasswd = pPasswd.getText().toString();
		      if(txtEmail.equalsIgnoreCase("") || txtPasswd.equalsIgnoreCase("")) {
		    	  showToastMessage("Email and Password Required.");  
		    	  return;
		      }
		      final Boolean echeck = validEmail(txtEmail);
		      if(echeck==true) {
		    	  //send info to p2ppostal
		    	  GetRego rego_getter = new GetRego();
		    	  try {
					login_token = rego_getter.execute(txtEmail,txtPasswd,txtName).get();
			    	if(login_token.length()==36) {
			    		callPageActivity(login_token);
			    	} else {
			    		/* get the error msg */
			    		showToastMessage(login_token);
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
	 
	 class GetRego extends AsyncTask<String,Void,String> {
		 @Override
		 protected String doInBackground(String... params) {		      
		      // Creating HTTP client

			 HttpClient httpClient = new DefaultHttpClient();
			 String paramstr = "?email="+params[0]+"&password="+params[1]+"&name="+params[2];
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
			        	  String error = tokener.getString("error");
			        	  return error;
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