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
public class PageDetailFragment extends Fragment {
	public final static String TAG = "PageDetailFragment";
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The content this fragment is presenting.
	 */
	private MenuContent.MenuItem mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PageDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle q =  getArguments();
		Log.d(TAG, q.toString());
		Boolean x = q.containsKey(ARG_ITEM_ID);
		Log.d(TAG, x.toString());
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = MenuContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
			Log.d(TAG, getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_page_detail,
				container, false);

		// Each page view
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.page_detail))
					.setText(mItem.content);
		}

		return rootView;
	}
}
