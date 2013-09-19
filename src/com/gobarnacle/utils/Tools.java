package com.gobarnacle.utils;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gobarnacle.R;

public class Tools {
	 public static void showToast(String message, Context context){
		  //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		  
			// get your custom_toast.xml ayout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

			View layout = inflater.inflate(R.layout.toast, null);

			// set a dummy image
			ImageView image = (ImageView) layout.findViewById(R.id.image);
			image.setImageResource(R.drawable.barnhead);

			// set a message
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText(message);

			Toast toast = new Toast(context);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(layout);
			toast.show();		  		  
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
