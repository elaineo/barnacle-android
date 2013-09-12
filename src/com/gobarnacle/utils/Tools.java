package com.gobarnacle.utils;

import android.content.Context;
import android.widget.Toast;

public class Tools {
	 public static void showToast(String message, Context context){
		  Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	 }    
 
}
