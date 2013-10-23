package it.uniba.socialcde4android.activity;

import java.util.ArrayList;
import java.util.Arrays;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.adapters.ServicesAdapter;
import it.uniba.socialcde4android.adapters.UsersAdapter;
import it.uniba.socialcde4android.data.requestmanager.SocialCDERequestFactory;
import it.uniba.socialcde4android.data.requestmanager.SocialCDERequestManager;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WService;
import it.uniba.socialcde4android.shared.library.WUser;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends Activity   implements RequestListener {



	private DrawerLayout mDrawerLayout;

	private ListView mDrawerList_left;
	private ListView mDrawerList_right;
	private ActionBarDrawerToggle mDrawerToggle_left;
	private ActionBarDrawerToggle mDrawerToggle_right;
	private String[] nomi_servizi;
	private WService[] wservice;
	private ArrayList<WUser> wuser_all = null;
	private int[] wUsersNumType_SuggFingFersHidd = null;
	private WUser wuser;
	private String proxy_string;
	private String userName_string;
	private String passw_string;
	private boolean doubleBackToExitPressedOnce = false;
	private static final String DIALOG_SHOWN = "DIALOG_SHOWN";
	private static final String PARCELABLE_REQUEST = "PARCELABLE_REQUEST";
	private static ProgressDialog progressDialog; 
	private RequestManager mRequestManager;
	private Request r;





	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		getActionBar().setDisplayShowTitleEnabled(false);
		//in services metto i nomi dei servizi..
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList_left = (ListView) findViewById(R.id.drawer_services_left);
		mDrawerList_right = (ListView) findViewById(R.id.drawer_users_right);
		mRequestManager = SocialCDERequestManager.from(this);
		if (getIntent().hasExtra("bundle") && savedInstanceState==null){
			Bundle bundle = getIntent().getExtras().getBundle("bundle");
			if (bundle != null){
				wuser = (WUser)bundle.getParcelable(Consts.WUSER);
				proxy_string = (String)getIntent().getExtras().getString(Preferences.PROXYSERVER);
				userName_string = (String)getIntent().getExtras().getString(Preferences.USERNAME);
				passw_string = (String)getIntent().getExtras().getString(Preferences.PASSWORD);
				loadServices();
			}
		}
	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (wservice != null) outState.putParcelableArray(Consts.WSERVICES, wservice);
		if (wuser_all != null) outState.putParcelableArrayList(Consts.WUSERS, this.wuser_all);
		if (wUsersNumType_SuggFingFersHidd != null) outState.putIntArray(Consts.WUSERS_NUMBERS, wUsersNumType_SuggFingFersHidd);
		if (wuser != null) outState.putParcelable(Consts.WUSER, wuser);
		if (progressDialog != null && progressDialog.isShowing()) {
			// Dismiss the dialog, in order to avoid a memory leak
			StopProgressDialog();
			// Adds the status to the outState Bundle
			outState.putBoolean(DIALOG_SHOWN, true);
			outState.putParcelable(PARCELABLE_REQUEST, r);
		} else
			outState.putBoolean(DIALOG_SHOWN, false);
	}


	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			r = savedInstanceState.getParcelable(PARCELABLE_REQUEST);
			// Show the dialog, if there has to be one
			if (savedInstanceState.getBoolean(DIALOG_SHOWN) && mRequestManager.isRequestInProgress(r))
				StartProgressDialog();

			Parcelable[] parcelableArray =	savedInstanceState.getParcelableArray(Consts.WSERVICES);
			if (parcelableArray != null) wservice = Arrays.copyOf(parcelableArray, parcelableArray.length, WService[].class);
			wuser_all = savedInstanceState.getParcelableArrayList(Consts.WUSERS);
			wUsersNumType_SuggFingFersHidd = savedInstanceState.getIntArray(Consts.WUSERS_NUMBERS);
			wuser = savedInstanceState.getParcelable(Consts.WUSER);
			populateDrawerLeft();
			populateDrawerRight();
		}
	}


	public   void StartProgressDialog(){
		if (progressDialog == null || !progressDialog.isShowing()){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			progressDialog = ProgressDialog.show(this, "Logging..", "Wait a moment please", true, false);
		}
	}


	public  void StopProgressDialog(){
		if (progressDialog != null){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			progressDialog.dismiss();
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		if (r != null && mRequestManager.isRequestInProgress(r)){
			StartProgressDialog();
			mRequestManager.addRequestListener(this, r);
		}
	}

	private void populateDrawerLeft(){

		nomi_servizi = new String[wservice.length];
		for (int i=0; i<wservice.length; i++){
			nomi_servizi[i] = wservice[i].getName();
		}
		
		mDrawerToggle_left = new ActionBarDrawerToggle(this, mDrawerLayout, 
				R.drawable.ic_action_storage, R.string.null_string, R.string.null_string ) {
			
			public void onDrawerClosed(View view) {
				getActionBar().setTitle("");
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("");
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle_left);
		ServicesAdapter adapter = new ServicesAdapter(getBaseContext(), 0, wservice, wuser);
		mDrawerList_left.setAdapter(adapter);
		mDrawerList_left.setOnItemClickListener(new DrawerLeftItemClickListener());
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}


	private class DrawerLeftItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			selectItem(position);
		}
	}


	private void populateDrawerRight(){

		nomi_servizi = new String[wservice.length];
		for (int i=0; i<wservice.length; i++){
			nomi_servizi[i] = wservice[i].getName();
		}
		mDrawerToggle_right = new ActionBarDrawerToggle(this,   mDrawerLayout,  R.drawable.ic_action_group, 
				R.string.null_string, R.string.null_string  ) {

			public void onDrawerClosed(View view) {
				getActionBar().setTitle("");
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("");
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle_right);

		UsersAdapter adapter = new UsersAdapter(getBaseContext(), 0, this.wuser_all, this.wUsersNumType_SuggFingFersHidd);
		mDrawerList_right.setAdapter(adapter);
		mDrawerList_right.setOnItemClickListener(new DrawerRightItemClickListener());
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setHomeButtonEnabled(true);
	}


	
	private class DrawerRightItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			selectItem(position);
		}
	}



	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (mDrawerToggle_left != null)
			mDrawerToggle_left.syncState();
		if (mDrawerToggle_right != null)
			mDrawerToggle_right.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle_left.onConfigurationChanged(newConfig);
		mDrawerToggle_right.onConfigurationChanged(newConfig);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
//		if (mDrawerToggle_left.onOptionsItemSelected(item)) {
//			return true;
//		}
//		if (mDrawerToggle_right.onOptionsItemSelected(item)) {
//			return true;
//		}
		// Handle your other action bar items...
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList_left)){
				mDrawerLayout.closeDrawer(mDrawerList_left);
			}else{
				if (mDrawerLayout.isDrawerOpen(mDrawerList_right)){
					mDrawerLayout.closeDrawer(mDrawerList_right);
				}
				mDrawerLayout.openDrawer(mDrawerList_left);
			}
			break;
		case R.id.action_drawer:
			if (mDrawerLayout.isDrawerOpen(mDrawerList_right)){
				mDrawerLayout.closeDrawer(mDrawerList_right);
			}else {
				if (mDrawerLayout.isDrawerOpen(mDrawerList_left)){
					mDrawerLayout.closeDrawer(mDrawerList_left);
				}
				mDrawerLayout.openDrawer(mDrawerList_right);
			}
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}



	/** Swaps fragments in the main content view */

	private void selectItem(int position) {
		// create a new fragment and specify the planet to show based on position
		//		Fragment fragment = new OpertingSystemFragment();
		//		Bundle args = new Bundle();
		//		args.putInt(OpertingSystemFragment.ARG_OS, position);
		//		fragment.setArguments(args);
		//
		//		// Insert the fragment by replacing any existing fragment
		//		FragmentManager fragmentManager = getFragmentManager();
		//		fragmentManager.beginTransaction()
		//		.replace(R.id.content_frame, fragment)
		//		.commit();
		//
		//		// Highlight the selected item, update the title, and close the drawer
		//	mDrawerList_left.setItemChecked(position, true);
		//		getActionBar().setTitle((services[position]));
		mDrawerLayout.closeDrawer(mDrawerList_left);
	}


	private void loadServices(){
		r = SocialCDERequestFactory.getWServiceRequest();
		r.put(Preferences.PROXYSERVER, this.proxy_string);
		r.put(Preferences.USERNAME, this.userName_string);
		r.put(Preferences.PASSWORD, this.passw_string);
		r.setMemoryCacheEnabled(true);
		StartProgressDialog();
		mRequestManager.execute(r, this);
	}


	private void loadFriends(){
		r = SocialCDERequestFactory.GetFriends();
		r.put(Preferences.PROXYSERVER, this.proxy_string);
		r.put(Preferences.USERNAME, this.userName_string);
		r.put(Preferences.PASSWORD, this.passw_string);
		r.setMemoryCacheEnabled(true);
		StartProgressDialog();
		mRequestManager.execute(r, this);
	}


	@Override
	public void onRequestFinished(Request request, Bundle resultData) {
		//StopProgressDialog();
		if (resultData != null){
			switch(resultData.getInt(Consts.REQUEST_TYPE)){


			case(Consts.REQUESTTYPE_RETRIEVESERVICES):
				if (resultData.getBoolean(Consts.FOUND_WSERVICES)){
						Parcelable[] parcelableArray =	resultData.getParcelableArray(Consts.WSERVICES);
						if (parcelableArray != null) 
							wservice = Arrays.copyOf(parcelableArray, parcelableArray.length, WService[].class);
				}else{
					Toast.makeText(this, "No services found."  , Toast.LENGTH_LONG).show();
				}
			populateDrawerLeft(); //posso il drawer sinistro
			loadFriends(); //carico gli utenti dal server..
			break;


			case(Consts.REQUESTTYPE_ALL_USERS):
				if (resultData.getBoolean(Consts.FOUND_WUSERS)){
					wuser_all = resultData.getParcelableArrayList(Consts.WUSERS);
					wUsersNumType_SuggFingFersHidd = resultData.getIntArray(Consts.WUSERS_NUMBERS);
				}
			populateDrawerRight();
			StopProgressDialog();
			break;
			}
		}
	}



	@Override
	public void onRequestConnectionError(Request request, int statusCode) {
		StopProgressDialog();
		Toast.makeText(this, "Connection error, status code: "+ statusCode, Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onRequestDataError(Request request) {
		StopProgressDialog();
		Toast.makeText(this, "Data error", Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onRequestCustomError(Request request, Bundle resultData) {
		StopProgressDialog();		
		Toast.makeText(this, "Custom error", Toast.LENGTH_SHORT).show();
	}


	@Override
	protected void onPause() {
		super.onPause();
		mRequestManager.removeRequestListener(this);
	}


	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce=false;   
			}
		}, 2000);
	}

} 