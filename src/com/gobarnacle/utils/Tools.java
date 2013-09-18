package com.gobarnacle.utils;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.widget.Toast;

public class Tools {
	 public static void showToast(String message, Context context){
		  Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	 }    
	public static void sendSMS(String sms, String msg) {
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(sms, null, msg, null, null);
			return;
		  } catch (Exception e) {			
			e.printStackTrace();
			return;
		  }
	}
	public final static boolean validEmail(CharSequence target) {
	    if (target == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}
	public final static boolean validPhone(CharSequence target) {
		if (target == null) {
	        return false;
	    } else {
	        return PhoneNumberUtils.isGlobalPhoneNumber((String)target);
	    }
	}
		
}
