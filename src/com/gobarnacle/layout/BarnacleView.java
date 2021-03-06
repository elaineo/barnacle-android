package com.gobarnacle.layout;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gobarnacle.R;

public class BarnacleView extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		final ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.barnhead);
		Typeface font = Typeface.createFromAsset(getAssets(), "sacramento-regular.ttf");  
		this.getActionBar().setDisplayShowCustomEnabled(true);
		this.getActionBar().setDisplayShowTitleEnabled(false);

		LayoutInflater inflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.titleview, null);

		((TextView)v.findViewById(R.id.title)).setText(this.getTitle());
		((TextView)v.findViewById(R.id.title)).setTypeface(font);
		//assign the view to the actionbar
		this.getActionBar().setCustomView(v);  
	}
}
