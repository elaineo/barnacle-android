package com.gobarnacle.utils;

import java.util.ArrayList;

import android.content.Context;
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
	public static void sendEmail(ArrayList<String> emailAddrs, String subj, String msg ) {
		
	}
}
