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
	private String title = "";
	private String[] nomi_servizi;
	private WService[] wservice;
	private WUser[] wuser_sugg = null;
	private WUser[] wuser_folling = null;
	private WUser[] wuser_follwer = null;
	private WUser[] wuser_hidden = null;
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
		title = (String) getTitle();
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
		if (wuser_sugg != null) outState.putParcelableArray(Consts.WUSERS_SUG, wuser_sugg);
		if (wuser_folling != null) outState.putParcelableArray(Consts.WUSERS_FOLLWING, wuser_folling);
		if (wuser_follwer != null) outState.putParcelableArray(Consts.WUSERS_FOLLWER, wuser_follwer);
		if (wuser_hidden != null) outState.putParcelableArray(Consts.WUSERS_HIDDEN, wuser_hidden);
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
			if (parcelableArray != null) 
				wservice = Arrays.copyOf(parcelableArray, parcelableArray.length, WService[].class);
			Parcelable[] parcelableArray2 =	savedInstanceState.getParcelableArray(Consts.WUSERS_SUG);
			if (parcelableArray2 != null) 
				wuser_sugg = Arrays.copyOf(parcelableArray2, parcelableArray2.length, WUser[].class);
			Parcelable[] parcelableArray3 =	savedInstanceState.getParcelableArray(Consts.WUSERS_FOLLWING);
			if (parcelableArray3 != null) 
				wuser_folling = Arrays.copyOf(parcelableArray3, parcelableArray3.length, WUser[].class);
			Parcelable[] parcelableArray4 =	savedInstanceState.getParcelableArray(Consts.WUSERS_FOLLWER);
			if (parcelableArray4 != null) 
				wuser_follwer = Arrays.copyOf(parcelableArray4, parcelableArray4.length, WUser[].class);
			Parcelable[] parcelableArray5 =	savedInstanceState.getParcelableArray(Consts.WUSERS_HIDDEN);
			if (parcelableArray5 != null) 
				wuser_hidden = Arrays.copyOf(parcelableArray5, parcelableArray5.length, WUser[].class);

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
		mDrawerToggle_left = new ActionBarDrawerToggle(this,       /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_action_storage,  /* nav drawer icon to replace 'Up' caret */
				R.string.null_string,  /* "open drawer" description */
				R.string.null_string  /* "close drawer" description */) {
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getActionBar().setTitle("");
			}
			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("");
			}
		};
		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle_left);
		ServicesAdapter adapter = new ServicesAdapter(getBaseContext(), 0, wservice, wuser);
		// Set the adapter for the list view
		mDrawerList_left.setAdapter(adapter);
		// Set the list's click listener
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
		mDrawerToggle_right = new ActionBarDrawerToggle(this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_action_group,  /* nav drawer icon to replace 'Up' caret */
				R.string.null_string,  /* "open drawer" description */
				R.string.null_string  /* "close drawer" description */) {
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getActionBar().setTitle("");
			}
			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("");
			}
		};
		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle_right);

		int[] wuLgts_SuFngFrsHid = new int[4];
		if (wuser_sugg == null ) wuLgts_SuFngFrsHid[0] = 0; else wuLgts_SuFngFrsHid[0] = wuser_sugg.length;
		if (wuser_folling == null ) wuLgts_SuFngFrsHid[1] = 0; else wuLgts_SuFngFrsHid[1] = wuser_folling.length;
		if (wuser_follwer == null ) wuLgts_SuFngFrsHid[2] = 0; else wuLgts_SuFngFrsHid[2] = wuser_follwer.length;
		if (wuser_hidden == null ) wuLgts_SuFngFrsHid[3] = 0; else wuLgts_SuFngFrsHid[3] = wuser_hidden.length;

		ArrayList<WUser> allwusers = new ArrayList<WUser>(); 
		for (int i=0; i<wuLgts_SuFngFrsHid[0];i++ ){	allwusers.add(wuser_sugg[i]);	}
		for (int i=0; i<wuLgts_SuFngFrsHid[1];i++ ){	allwusers.add(wuser_folling[i]);	}
		for (int i=0; i<wuLgts_SuFngFrsHid[2];i++ ){	allwusers.add(wuser_follwer[i]);	}
		for (int i=0; i<wuLgts_SuFngFrsHid[3];i++ ){	allwusers.add(wuser_hidden[i]); 	}

		UsersAdapter adapter = new UsersAdapter(getBaseContext(), 0, allwusers, wuLgts_SuFngFrsHid);
		// Set the adapter for the list view
		mDrawerList_right.setAdapter(adapter);
		// Set the list's click listener
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
		if (mDrawerToggle_left.onOptionsItemSelected(item)) {
			return true;
		}
		if (mDrawerToggle_right.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList_left)){
				mDrawerLayout.closeDrawer(this.mDrawerList_left);
			}else{
			if (mDrawerLayout.isDrawerOpen(mDrawerList_right)){
				mDrawerLayout.closeDrawer(this.mDrawerList_right);
			}
			mDrawerLayout.openDrawer(this.mDrawerList_left);
		}
		break;
	case R.id.action_drawer:
		if (mDrawerLayout.isDrawerOpen(mDrawerList_right)){
			mDrawerLayout.closeDrawer(this.mDrawerList_right);
		}else {
			if (mDrawerLayout.isDrawerOpen(mDrawerList_left)){
				mDrawerLayout.closeDrawer(this.mDrawerList_left);
			}
			mDrawerLayout.openDrawer(this.mDrawerList_right);
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


private void loadSuggestedFriends(){
	r = SocialCDERequestFactory.GetSuggestedFriends();
	r.put(Preferences.PROXYSERVER, this.proxy_string);
	r.put(Preferences.USERNAME, this.userName_string);
	r.put(Preferences.PASSWORD, this.passw_string);
	r.setMemoryCacheEnabled(true);
	StartProgressDialog();
	mRequestManager.execute(r, this);
}

private void loadFollowings(){
	r = SocialCDERequestFactory.GetFollowings();
	r.put(Preferences.PROXYSERVER, this.proxy_string);
	r.put(Preferences.USERNAME, this.userName_string);
	r.put(Preferences.PASSWORD, this.passw_string);
	r.setMemoryCacheEnabled(true);
	StartProgressDialog();
	mRequestManager.execute(r, this);
}

private void loadFollowers(){
	r = SocialCDERequestFactory.GetFollowers();
	r.put(Preferences.PROXYSERVER, this.proxy_string);
	r.put(Preferences.USERNAME, this.userName_string);
	r.put(Preferences.PASSWORD, this.passw_string);
	r.setMemoryCacheEnabled(true);
	StartProgressDialog();
	mRequestManager.execute(r, this);
}

private void loadHidden(){
	r = SocialCDERequestFactory.GetHidden();
	r.put(Preferences.PROXYSERVER, this.proxy_string);
	r.put(Preferences.USERNAME, this.userName_string);
	r.put(Preferences.PASSWORD, this.passw_string);
	r.setMemoryCacheEnabled(true);
	//	StartProgressDialog();
	mRequestManager.execute(r, this);		
}

@Override
public void onRequestFinished(Request request, Bundle resultData) {
	//StopProgressDialog();
	if (resultData != null){
		switch(resultData.getInt(Consts.REQUEST_TYPE)){


		case(Consts.REQUESTTYPE_RETRIEVESERVICES):

			int status_request = resultData.getInt(Consts.STAUS_WSERVICESREQUEST);		
		if (status_request < 200 || status_request > 299) {
			Toast.makeText(this, "Status error or connection timeout."  , Toast.LENGTH_LONG).show();
			//gestire i codici di errore

		}else{
			if (resultData.getBoolean(Consts.FOUND_WSERVICES)){
				//TODO
				wservice = null;//da fare
				if (resultData.getBoolean(Consts.FOUND_WSERVICES)){
					//wservice = (WService[])bundle.getParcelableArray(Consts.WSERVICES);

					Parcelable[] parcelableArray =	resultData.getParcelableArray(Consts.WSERVICES);
					if (parcelableArray != null) {
						wservice = Arrays.copyOf(parcelableArray, parcelableArray.length, WService[].class);
					}

					//posso caricare i servizi nel drawer..
					populateDrawerLeft();
					loadSuggestedFriends();
					//populateDrawerRight();
				}
			}else{
				loadSuggestedFriends();
				Toast.makeText(this, "No services found."  , Toast.LENGTH_LONG).show();
			}
		}
		//StopProgressDialog();
		break;


		case(Consts.REQUESTTYPE_SUGGESTEDUSERS):
			//non interrompe il dialog
			int status_sugg = resultData.getInt(Consts.STATUS_WEBSERVICE);		
		if (status_sugg < 200 || status_sugg > 299) {
			Toast.makeText(this, "Status error or connection timeout."  , Toast.LENGTH_LONG).show();
			StopProgressDialog();
			//gestire i codici di errore
		}else{
			wuser_sugg = null;
			if (resultData.getBoolean(Consts.FOUND_WUSERS)){

				//wservice = (WService[])bundle.getParcelableArray(Consts.WSERVICES);

				Parcelable[] parcelableArray =	resultData.getParcelableArray(Consts.WUSERS);
				if (parcelableArray != null) {
					this.wuser_sugg = Arrays.copyOf(parcelableArray, parcelableArray.length, WUser[].class);
				}
				//richiamo la ricerca degli altri utenti
				loadFollowings();
			}else{
				loadFollowings();
				//StopProgressDialog();
				//Toast.makeText(this, "No services found."  , Toast.LENGTH_LONG).show();
			}
		}
		break;


		case(Consts.REQUESTTYPE_FOLLOWINGSUSERS):
			//non interrompe il dialog
			int status_foling = resultData.getInt(Consts.STATUS_WEBSERVICE);		
		if (status_foling < 200 || status_foling > 299) {
			Toast.makeText(this, "Status error or connection timeout."  , Toast.LENGTH_LONG).show();
			StopProgressDialog();
			//gestire i codici di errore
		}else{
			wuser_folling = null;
			if (resultData.getBoolean(Consts.FOUND_WUSERS)){

				//wservice = (WService[])bundle.getParcelableArray(Consts.WSERVICES);

				Parcelable[] parcelableArray =	resultData.getParcelableArray(Consts.WUSERS);
				if (parcelableArray != null) {
					this.wuser_folling = Arrays.copyOf(parcelableArray, parcelableArray.length, WUser[].class);
				}
				//richiamo la ricerca degli altri utenti
				loadFollowers();
			}else{
				loadFollowers();
				//StopProgressDialog();
				//Toast.makeText(this, "No services found."  , Toast.LENGTH_LONG).show();
			}
		}
		break;



		case(Consts.REQUESTTYPE_FOLLOWERSUSERS):
			//non interrompe il dialog
			int status_folers = resultData.getInt(Consts.STATUS_WEBSERVICE);		
		if (status_folers < 200 || status_folers > 299) {
			Toast.makeText(this, "Status error or connection timeout."  , Toast.LENGTH_LONG).show();
			StopProgressDialog();
			//gestire i codici di errore
		}else{
			this.wuser_follwer = null;
			if (resultData.getBoolean(Consts.FOUND_WUSERS)){
				Parcelable[] parcelableArray =	resultData.getParcelableArray(Consts.WUSERS);
				if (parcelableArray != null) {
					this.wuser_follwer = Arrays.copyOf(parcelableArray, parcelableArray.length, WUser[].class);
				}
				//richiamo la ricerca degli altri utenti
				loadHidden();
			}else{
				loadHidden();
				//StopProgressDialog();
				//Toast.makeText(this, "No services found."  , Toast.LENGTH_LONG).show();
			}
		}
		break;



		case(Consts.REQUESTTYPE_HIDDENUSERS):
			int status_hid = resultData.getInt(Consts.STATUS_WEBSERVICE);		
		if (status_hid < 200 || status_hid > 299) {
			Toast.makeText(this, "Status error or connection timeout."  , Toast.LENGTH_LONG).show();
			//gestire i codici di errore
		}else{
			this.wuser_hidden = null;
			if (resultData.getBoolean(Consts.FOUND_WUSERS)){
				Parcelable[] parcelableArray =	resultData.getParcelableArray(Consts.WUSERS);
				if (parcelableArray != null) {
					this.wuser_hidden = Arrays.copyOf(parcelableArray, parcelableArray.length, WUser[].class);
				}
				//richiamo il metodo per popolare il drawer destro
				populateDrawerRight();
			}else{
				populateDrawerRight();
				//StopProgressDialog();
				//Toast.makeText(this, "No services found."  , Toast.LENGTH_LONG).show();
			}
		}
		StopProgressDialog();
		break;
		}
	}
}



@Override
public void onRequestConnectionError(Request request, int statusCode) {
	StopProgressDialog();
	Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
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