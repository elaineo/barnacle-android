package com.gobarnacle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


/**
 * An activity representing a list of Pages. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link PageDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PageListFragment} and the item details (if present) is a
 * {@link PageDetailFragment}.
 * <p>
 * This activity also implements the required {@link PageListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class PageListActivity extends FragmentActivity implements
		PageListFragment.Callbacks {

	public final static String TAG = "PageListActivity";
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		Log.d(TAG, "ok...");
		setContentView(R.layout.activity_page_list);
		
		if (findViewById(R.id.page_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((PageListFragment) getSupportFragmentManager().findFragmentById(
					R.id.page_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link PageListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		int pageId;
		int Id = Integer.parseInt(id);
		switch (Id) {
			case 1: pageId = R.id.page_detail_container; 
					break;
			default: pageId = R.id.page_detail_container;						
		}
			
		
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(PageDetailFragment.ARG_ITEM_ID, id);
			PageDetailFragment fragment = new PageDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(pageId, fragment).commit();
			
//			FillPostFrag fragment = new FillPostFrag();

			Log.i(TAG, id+" 2pane");
			

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Log.i(TAG, id+" 1pane");
			
			Intent detailIntent = new Intent(this, PageDetailActivity.class);
			//Intent detailIntent = new Intent(this, PageDetailActivity.class);
			detailIntent.putExtra(PageDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
