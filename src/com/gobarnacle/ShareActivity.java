package com.gobarnacle;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.gobarnacle.maps.PostActivity;
import com.gobarnacle.utils.BarnacleClient;
import com.gobarnacle.utils.Route;
import com.gobarnacle.utils.Tools;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ShareActivity extends FragmentActivity {
	
	public final static String ROUTE_POST = "com.gobarnacle.ROUTE_POST"; 
	private static final String TAG = "ShareActivity";  
	private static final String SMS_TAG = "sms";
	private static final String EMAIL_TAG = "eml";
	private static final int CONTACT_PICKER_RESULT = 1001;  
	public final static String TRACK_URL = "com.gobarnacle.TRACK_URL"; 
	public final static String EmailUri = "/mobile/trackemail"; 
	
	private LinearLayout cl;
	private Route route;
	
	private ArrayList<String> emailAddrs, smsNumbers;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_share);
        cl = (LinearLayout) findViewById(R.id.contact_list); 
		Intent intent = getIntent();
        route = intent.getParcelableExtra(PostActivity.ROUTE_POST);
        
        emailAddrs = new ArrayList<String>();
        smsNumbers = new ArrayList<String>();
    }
	
	public void doLaunchContactPicker(View view) {  
	    Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,  
	            Contacts.CONTENT_URI);  
	    startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);  
	} 	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	    if (resultCode == RESULT_OK) {  
	        switch (requestCode) {  
	        case CONTACT_PICKER_RESULT:  
	            Cursor cursor = null;  
	            String email = "";  
	            String phone = "";  
	            String name = "";
	            try {  
	                Uri result = data.getData();  
	                Log.v(TAG, "Got a contact result: " + result.toString());  
	                // get the contact id from the Uri  
	                String id = result.getLastPathSegment();  
	                // query for email & sms 
	                cursor = getContentResolver().query(Email.CONTENT_URI,  
	                        null, Email.CONTACT_ID + "=?", new String[] { id },  
	                        null);  
	                int emailIdx = cursor.getColumnIndex(Email.DATA);  
	                if (cursor.moveToFirst()) {  
	                    email = cursor.getString(emailIdx);
	                    emailAddrs.add(email);
	                    Log.v(TAG, "Got email: " + email);  
	                } 
	                int phoneIdx = cursor.getColumnIndex(Phone.DATA);  
	                if (cursor.moveToFirst()) {  
	                    phone = cursor.getString(phoneIdx);
	                    smsNumbers.add(phone);
	                    Log.v(TAG, "Got phone: " + phone);  
	                }
	                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));

	            } catch (Exception e) {  
	                Log.e(TAG, "Failed to get email data", e);  
	            } finally {  
	                if (cursor != null) {  
	                    cursor.close();  
	                }  
	                addtoContactList(name,true,true);
                }  
	            break;  
	        }  
	    } else {  
	        Log.w(TAG, "Contactpicker not ok");  
	    }  
	}  
	
	public void addtoContactList(String name, Boolean smsBox, Boolean emlBox) {
        LinearLayout cline = new LinearLayout(this);
        cline.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams clineParams =  new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        cl.addView(cline, clineParams);
        TextView cname = new TextView(this);
        LayoutParams cnameParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);
        LayoutParams cboxParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        cname.setText(name);
        cline.addView(cname, cnameParams);
        CheckBox sms = new CheckBox(this);
        sms.setTag(SMS_TAG);
        if (smsBox) 
	        sms.setChecked(true);
         else 
        	 sms.setEnabled(false);
        cline.addView(sms, cboxParams);
        CheckBox eml = new CheckBox(this);
        eml.setTag(EMAIL_TAG);
        if (emlBox) 
	        eml.setChecked(true);
         else 
        	 eml.setEnabled(false);
        cline.addView(eml, cboxParams);		
	}
	
	public void addEmailContact(View view) {
		EditText textField = (EditText) findViewById(R.id.invite_email);
		String newAddr = textField.getText().toString();
		Boolean valid = Tools.validEmail(newAddr);
		if (valid) {
			textField.setText("");
			emailAddrs.add(newAddr);
			smsNumbers.add("");
			addtoContactList(newAddr, false, true);
		} else
			Tools.showToast("Invalid Email.", this.getApplicationContext() );
	}
	
	public void addPhoneContact(View view) {
		EditText textField = (EditText) findViewById(R.id.invite_phone);
		String newAddr = textField.getText().toString();
		textField.setText("");
		smsNumbers.add(newAddr);
		emailAddrs.add("");
		addtoContactList(newAddr, true, false);
	}
	
	public void shareMsg(View view) throws UnsupportedEncodingException, JSONException {  
		String name = LoginActivity.userFirstName();
		String track_url = route.shareURL();
		String descr = String.format("%s is driving to %s.\n Track %s's progress along the way with the Barnacle Driver Tracker.", name, route.locend(), name);   
		String smsdescr = String.format("Track %s's progress at %s. Sent From Barnacle.", name, track_url);		
		// String emaildescr = descr + " " + track_url;
		// String emailsubj = String.format("Track %s's location at Barnacle", name);
		String smsList = "";
		ArrayList <String> emailList = new ArrayList<String>();
		
		
		/** Send to selected contacts **/
		LinearLayout clist = (LinearLayout) findViewById(R.id.contact_list); 
				
		for (int i = 1; i < clist.getChildCount(); i++) {
	        View v = clist.getChildAt(i);
	        CheckBox smsCbox = (CheckBox) getViewByTag((ViewGroup) v, SMS_TAG);
	        CheckBox emlCbox = (CheckBox) getViewByTag((ViewGroup) v, EMAIL_TAG);
	        if (smsCbox.isChecked())
	        	smsList = smsList + ";" + smsNumbers.get(i-1);
	        if (emlCbox.isChecked())
	        	emailList.add(emailAddrs.get(i-1));
	    }

		if (smsList.length() > 7) {
			smsList = smsList.substring(0, smsList.length() - 1);
			Tools.sendSMS(smsList, smsdescr);
		}
		if (emailList.size() > 0)
			sendEmail(emailList, route.routekey());
		
		/** Share on FB **/
		CheckBox fbCbox = (CheckBox) findViewById(R.id.fb_checkbox); 
		Boolean fb = fbCbox.isChecked();
				
		if (fb) {
			Bundle bundle = new Bundle();
			bundle.putString("description", descr);
			bundle.putString("caption", "www.GoBarnacle.com");
			bundle.putString("link", track_url);
			bundle.putString("name", String.format("%s's Tracking Page", name));
			bundle.putString("picture", "http://www.gobarnacle.com/static/img/barnacle.png");
			WebDialog fbFeed = new WebDialog.FeedDialogBuilder(this, Session.getActiveSession(), bundle).build();
			fbFeed.setOnCompleteListener(new OnCompleteListener() {
				@Override
                public void onComplete(Bundle values, FacebookException error) {
					gotoTracking(route.URL());
                }
			});
			fbFeed.show();
		} else		  
			 gotoTracking(route.URL());
	} 	
	
	
	private void gotoTracking(String url) {
		Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(TRACK_URL, url);
		startActivity(intent);
		finish();
	}
	
	private static View getViewByTag(ViewGroup root, String tag){
	    View v = null;
	    final int childCount = root.getChildCount();
	    for (int i = 0; i < childCount; i++) {
	        final View child = root.getChildAt(i);
	        final Object tagObj = child.getTag();
	        if (tagObj != null && tagObj.equals(tag)) 
	            v=child;	        
	    }
	    return v;
	}
	public void sendEmail(ArrayList<String> emailAddrs, String routekey ) throws UnsupportedEncodingException, JSONException {
    	
    	final Context context = this.getApplicationContext();
    	JSONObject postParams = new JSONObject();		
		
    	postParams.put("routekey",routekey);
    	postParams.put("emails",new JSONArray(emailAddrs));
    	
		BarnacleClient.postJSON(context, EmailUri, postParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                String status;
				try {
					status = response.getString("status");
			        if (status.equals("ok")) {
			        	Log.d(TAG, "Emails sent");
			        } else {
			        	Tools.showToast(status, context);
			        }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            @Override
            public void onFailure(Throwable e, String response) {
            	Log.e(TAG, ""+e);
            }
        });			
		
	}	
}
