package com.gobarnacle.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateReceiver extends BroadcastReceiver {
    private static final String DEBUG_TAG = "UpdateReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
    	String msgExtra = intent.getStringExtra(com.gobarnacle.maps.MapActivity.EXTRA_MSG);
        Log.d(DEBUG_TAG, "Recurring alarm; requesting upload service.");
    	
        Intent uploader = new Intent(context, UploadLocService.class);
        uploader.putExtra(com.gobarnacle.maps.MapActivity.EXTRA_MSG,msgExtra);
        context.startService(uploader);
    }
}
