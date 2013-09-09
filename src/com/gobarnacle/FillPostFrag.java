package com.gobarnacle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Page detail screen. This fragment is either
 * contained in a {@link PageListActivity} in two-pane mode (on tablets) or a
 * {@link PageDetailActivity} on handsets.
 */
public class FillPostFrag extends Fragment {
	public final static String TAG = "FillPost";
	public static final String ARG_ITEM_ID = "item_id";

	private MenuContent.MenuItem mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FillPostFrag() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fillpost,
				container, false);

		return rootView;
	}
}
