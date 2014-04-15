package it.uniba.socialcde4android.activity;

import java.util.ArrayList;
import java.util.Arrays;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.costants.Error_consts;
import it.uniba.socialcde4android.adapters.ConfiguratedImageLoader;
import it.uniba.socialcde4android.adapters.ServicesAdapter;
import it.uniba.socialcde4android.adapters.UsersAdapter;
import it.uniba.socialcde4android.data.requestmanager.SocialCDERequestFactory;
import it.uniba.socialcde4android.data.requestmanager.SocialCDERequestManager;
import it.uniba.socialcde4android.dialogs.ChangePasswordDialog;
import it.uniba.socialcde4android.dialogs.ChangePasswordDialog.OnChangePasswordListener;
import it.uniba.socialcde4android.dialogs.ChooseAvatarDialog;
import it.uniba.socialcde4android.dialogs.ChooseAvatarDialog.OnChooseAvatarListener;
import it.uniba.socialcde4android.dialogs.HideUnhideDialog;
import it.uniba.socialcde4android.dialogs.HideUnhideDialog.OnHideHunideListener;
import it.uniba.socialcde4android.dialogs.NoNetworkDialog;
import it.uniba.socialcde4android.dialogs.SetServiceFeaturesDialog;
import it.uniba.socialcde4android.dialogs.SetServiceFeaturesDialog.OnFeaturesDialogInteractionListener;
import it.uniba.socialcde4android.dialogs.TFSAuthDialog;
import it.uniba.socialcde4android.dialogs.TFSAuthDialog.OnTFSAuthInteractionListener;
import it.uniba.socialcde4android.fragments.TimeLine_AbstractFragment.OnGenericTimeLineFragmentInteractionListener;
import it.uniba.socialcde4android.fragments.TimeLine_Fragment;
import it.uniba.socialcde4android.fragments.WUserColleagueProfile_Fragment;
import it.uniba.socialcde4android.fragments.WUserColleagueProfile_Fragment.OnProfileFragmentInteractionListener;
import it.uniba.socialcde4android.fragments.WUserProfile_Fragment;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WFeature;
import it.uniba.socialcde4android.shared.library.WHidden;
import it.uniba.socialcde4android.shared.library.WOAuthData;
import it.uniba.socialcde4android.shared.library.WService;
import it.uniba.socialcde4android.shared.library.WUser;
import it.uniba.socialcde4android.utility.ScreenUtility;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity   
implements OnProfileFragmentInteractionListener, 
OnGenericTimeLineFragmentInteractionListener, RequestListener, OnFeaturesDialogInteractionListener,
OnTFSAuthInteractionListener, OnChangePasswordListener, OnHideHunideListener, OnChooseAvatarListener{

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
	private static final String PARCELABLE_REQUEST2 = "PARCELABLE_REQUEST2";
	private static final String FRAGMENT_WUSERCOLLEAGUE_PROFILE = "fragment colleague";
	private static final String FRAGMENT_WUSER_PROFILE = "fragment wuser";
	private static ProgressDialog progressDialog; 
	private RequestManager mRequestManager;
	private Request r;
	private Request r2;
	private Boolean isFragmentLoading;

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
		Log.i("homewuser", "prima");
		if (getIntent().hasExtra("bundle") && savedInstanceState==null){
			Bundle bundle = getIntent().getExtras().getBundle("bundle");
			if (bundle != null){
				wuser = (WUser)bundle.getParcelable(Consts.WUSER);
				Log.i("homewuser", wuser.toString());
				proxy_string = (String)getIntent().getExtras().getString(Preferences.PROXYSERVER);
				userName_string = (String)getIntent().getExtras().getString(Preferences.USERNAME);
				passw_string = (String)getIntent().getExtras().getString(Preferences.PASSWORD);
				unlockScreenOrientation();
				loadServices();
				this.loadFriends();
			}	
		}

		if (savedInstanceState==null) {
			Fragment fragment = TimeLine_Fragment.newInstance(this.passw_string);
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.frag_ptr_list, fragment);
			fragmentTransaction.commit();
		}
	}


	public String getPassword(){
		return this.passw_string;
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArray(Consts.WSERVICES, wservice);
		outState.putParcelableArrayList(Consts.WUSERS, this.wuser_all);
		outState.putIntArray(Consts.WUSERS_NUMBERS, wUsersNumType_SuggFingFersHidd);
		outState.putParcelable(Consts.WUSER, wuser);
		outState.putString(Preferences.PROXYSERVER, this.proxy_string);
		outState.putString(Preferences.USERNAME, this.userName_string);
		outState.putString(Preferences.PASSWORD, this.passw_string);
		if (progressDialog != null && progressDialog.isShowing()) {
			// Dismiss the dialog, in order to avoid a memory leak
			StopProgressDialog();
			// Adds the status to the outState Bundle
			outState.putBoolean(DIALOG_SHOWN, true);
			outState.putParcelable(PARCELABLE_REQUEST, r);
			outState.putParcelable(PARCELABLE_REQUEST2, r2);
		} else
			outState.putBoolean(DIALOG_SHOWN, false);	
		outState.putParcelable(PARCELABLE_REQUEST, null);
		outState.putParcelable(PARCELABLE_REQUEST2, null);
	}


	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);


		if (savedInstanceState != null) {
			r = savedInstanceState.getParcelable(PARCELABLE_REQUEST);
			r2 = savedInstanceState.getParcelable(PARCELABLE_REQUEST2);

			// Show the dialog, if there has to be one
			if (savedInstanceState.getBoolean(DIALOG_SHOWN) && (mRequestManager.isRequestInProgress(r)||mRequestManager.isRequestInProgress(r2)))
				StartProgressDialog();

			Parcelable[] parcelableArray =	savedInstanceState.getParcelableArray(Consts.WSERVICES);
			if (parcelableArray != null) {
				wservice = Arrays.copyOf(parcelableArray, parcelableArray.length, WService[].class);

				wuser_all = savedInstanceState.getParcelableArrayList(Consts.WUSERS);
				wUsersNumType_SuggFingFersHidd = savedInstanceState.getIntArray(Consts.WUSERS_NUMBERS);
				wuser = savedInstanceState.getParcelable(Consts.WUSER);
				proxy_string = savedInstanceState.getString(Preferences.PROXYSERVER);
				userName_string = savedInstanceState.getString(Preferences.USERNAME);
				passw_string = savedInstanceState.getString(Preferences.PASSWORD);
				populateDrawerLeft();
				populateDrawerRight();
			}
		}
	}


	public   void StartProgressDialog(){
		if (progressDialog == null || !progressDialog.isShowing()){
			lockScreenOrientation();
			progressDialog = ProgressDialog.show(this, "Loading..", "Wait a moment please", true, false);
		}
	}

	public  void StopProgressDialog(){
		if (progressDialog != null && ((r == null || !mRequestManager.isRequestInProgress(r)) && (r2 == null || !mRequestManager.isRequestInProgress(r2))) && !isFragmentLoading){
			unlockScreenOrientation();
			progressDialog.dismiss();
		}
	}

	public void lockScreenOrientation(){
		ScreenUtility.lockScreenOrientation(this);
	}

	public void unlockScreenOrientation(){

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

	}


	@Override
	protected void onResume() {
		super.onResume();
		if (r != null && mRequestManager.isRequestInProgress(r)){
			StartProgressDialog();
			mRequestManager.addRequestListener(this, r);
		}else if (r != null) {
			mRequestManager.callListenerWithCachedData(this, r);
		}
		if (r2 != null && mRequestManager.isRequestInProgress(r2)){
			StartProgressDialog();
			mRequestManager.addRequestListener(this, r2);
		}
		else if (r2 != null) {
			mRequestManager.callListenerWithCachedData(this, r2);
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
		ServicesAdapter adapter = new ServicesAdapter(getBaseContext(), 0, wservice, wuser, this.proxy_string);
		mDrawerList_left.setAdapter(adapter);
		mDrawerList_left.setOnItemClickListener(new DrawerLeftItemClickListener());
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}


	private class DrawerLeftItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			// switch sul TYPE
			int type = mDrawerList_left.getAdapter().getItemViewType(position);
			switch(type){
			case ServicesAdapter.TYPE_AVATAR:
				openUserProfile(wuser);
				break;
			case ServicesAdapter.TYPE_SERVICE:
				WService wservice = (WService) mDrawerList_left.getAdapter().getItem(position-2);
				if (wservice.isRegistered()){
					getFeatures(wservice.getId());
				}else{
					if (wservice.isRequireOAuth()){
						loadOAuthData(wservice.getId(), wservice.getOAuthVersion());
					}else {
						TFSAuthDialog tfs_dialog = TFSAuthDialog.newInstance(wservice);
						//tfs_dialog.setCancelable(false);
						tfs_dialog.show(getFragmentManager(), "TFS Auth");
					}
				}
				break;
			case ServicesAdapter.TYPE_SETTING:
				int itemSetting = position - 3 - HomeActivity.this.wservice.length;
				Log.i("setting position", String.valueOf(itemSetting));
				switch(itemSetting){ //SETTINGS = { "Choose Avatar", "Change Password", "Exit"};
				case 0: //choose avatar
					loadAvailableAvatars();
					break;
				case 1://change password
					ChangePasswordDialog changeP_dialog = ChangePasswordDialog.newInstance(passw_string);
					changeP_dialog.show(getFragmentManager(), "Change Password");
					break;
				case 2: //exit
					HomeActivity.this.exitToLogin();
					break;

				}
				break;
			}
			mDrawerLayout.closeDrawer(mDrawerList_left);
		}
	}

	private void loadOAuthData(int serviceID, int oAuthVersion){
		if (isOnline()){
			r = SocialCDERequestFactory.getOAuthDataRequest();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.SERVICE_ID, String.valueOf(serviceID));
			r.put(Consts.OAUTH_VERSION, String.valueOf(oAuthVersion));
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
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
	}



	private class DrawerRightItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			int array_position=0;
			if (mDrawerList_right.getAdapter().getItemViewType(position) == ((UsersAdapter) mDrawerList_right.getAdapter()).getUserTypeID()){
				for(int i=0; i<position;i++){
					if (mDrawerList_right.getAdapter().getItemViewType(i) == ((UsersAdapter) mDrawerList_right.getAdapter()).getUserTypeID())
						array_position++;
				}
				loadColleagueProfile(wuser_all.get(array_position).getId());
			}
			mDrawerLayout.closeDrawer(mDrawerList_right);
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

		switch (item.getItemId()) {

		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList_left))	mDrawerLayout.closeDrawer(mDrawerList_left);
			else{if (mDrawerLayout.isDrawerOpen(mDrawerList_right))	mDrawerLayout.closeDrawer(mDrawerList_right);
			mDrawerLayout.openDrawer(mDrawerList_left); }
			break;

		case R.id.action_drawer:
			if (mDrawerLayout.isDrawerOpen(mDrawerList_right))	mDrawerLayout.closeDrawer(mDrawerList_right);
			else { if (mDrawerLayout.isDrawerOpen(mDrawerList_left))	 mDrawerLayout.closeDrawer(mDrawerList_left);
			mDrawerLayout.openDrawer(mDrawerList_right);}
			break;

		default:

		}
		return super.onOptionsItemSelected(item);
	}


	private void openUserProfile(WUser wuserToOpen){
		// create a new fragment and specify the planet to show based on position
		Fragment fragment = null;
		if (wuserToOpen.getId() == wuser.getId())	{
			fragment = WUserProfile_Fragment.newInstance(wuser, this.passw_string);
		}
		else	{
			fragment = WUserColleagueProfile_Fragment.newInstance(wuserToOpen, this.passw_string);
		}
		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if (wuserToOpen.getId() == wuser.getId()){
			fragmentTransaction.replace(R.id.frag_ptr_list, fragment, FRAGMENT_WUSER_PROFILE);
		}else{
			fragmentTransaction.replace(R.id.frag_ptr_list, fragment, FRAGMENT_WUSERCOLLEAGUE_PROFILE);
		}
		fragmentManager.popBackStack();
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		// Highlight the selected item, update the title, and close the drawer
		//getActionBar().setTitle("User Profile");
		mDrawerLayout.closeDrawer(mDrawerList_right);
	}



	private void loadColleagueProfile(int wuserID){
		if (isOnline()){
			r = SocialCDERequestFactory.getColleagueProfileRequest();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.COLLEAGUE_ID, String.valueOf(wuserID));
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);

		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}

	}

	private void loadServices(){
		if (isOnline()){
			r = SocialCDERequestFactory.getWServiceRequest();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}

	}


	private void loadFriends(){
		if (isOnline()){
			r2 = SocialCDERequestFactory.GetFriends();
			r2.put(Preferences.PROXYSERVER, this.proxy_string);
			r2.put(Preferences.USERNAME, this.userName_string);
			r2.put(Preferences.PASSWORD, this.passw_string);
			r2.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r2, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}

	}


	private void setFollow(Boolean followChecked, WUser wuser_profile) {
		if (isOnline()){
			r = SocialCDERequestFactory.setFollowed();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.BOOLEAN_FOLLOW, followChecked);
			r.put(Consts.COLLEAGUE_ID, wuser_profile.getId());
			r.setMemoryCacheEnabled(true);

			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}

	}

	@Override
	public void onRequestFinished(Request request, Bundle resultData) {
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
			populateDrawerLeft(); //posso popolare il drawer sinistro
			StopProgressDialog();
			break;


			case(Consts.REQUESTTYPE_RETRIEVEHIDESETTINGS):
				if (resultData.getBoolean(Consts.FOUND_HIDDEN_SETTINGS)){
					WHidden whidden = (WHidden) resultData.getParcelable(Consts.WHIDDEN);
					int user_id = resultData.getInt(Consts.USERID);
					StopProgressDialog();
					HideUnhideDialog hideUnhide_dialog = HideUnhideDialog.newInstance(whidden, user_id);
					//		hideUnhide_dialog.setCancelable(false);
					hideUnhide_dialog.show(getFragmentManager(), "Change Hide Settings");
				}else{
					StopProgressDialog();
					Toast.makeText(this, "Error retrieving settings. Try again."  , Toast.LENGTH_LONG).show();
				}

			break;


			case(Consts.REQUESTTYPE_SENDTFSPOST):
				if (resultData.getBoolean(Consts.SENT)){
					//va fatto il refresh della view..
					//Toast.makeText(this, "Updating Timeline", Toast.LENGTH_SHORT).show();
					FragmentManager fragmentManager = getSupportFragmentManager();
					TimeLine_Fragment fragment;
					fragment=(TimeLine_Fragment)fragmentManager.findFragmentById(R.id.frag_ptr_list);
					fragment.refreshFragment();
				}else{
					Toast.makeText(this, "An error occured."  , Toast.LENGTH_LONG).show();
				}
			StopProgressDialog();
			break;

			case(Consts.REQUESTTYPE_GET_AVAILABLE_AVATARS):
				StopProgressDialog();
			if (resultData.getBoolean(Consts.FOUND_AVATAR_IMAGES)){

				String[] uri = null;
				uri = resultData.getStringArray(Consts.URI);

				//apro la dialog 
				ChooseAvatarDialog chooseAvatar_dialog = ChooseAvatarDialog.newInstance(uri, wuser.getAvatar());
				chooseAvatar_dialog.show(getFragmentManager(), "choose avatar");

			}else{
				Toast.makeText(this, "Avatars Not Available."  , Toast.LENGTH_LONG).show();
			}
			break;



			case(Consts.REQUESTTYPE_RETRIEVEFEATURES):
				StopProgressDialog();
			if (resultData.getBoolean(Consts.FOUND_WFEATURES)){
				WFeature[] wfeature = null;
				int service_id = resultData.getInt(Consts.SERVICE_ID);
				Parcelable[] parcelableArray =	resultData.getParcelableArray(Consts.WFEATURES);
				if (parcelableArray != null) 
					wfeature = Arrays.copyOf(parcelableArray, parcelableArray.length, WFeature[].class);
				for (int i=0; i<wfeature.length; i++){
					Log.i("wfeature",wfeature[i].toString());
				}
				//qui carico un pannello per visualizzare le feature
				for (int i=0; i<wservice.length;i++){
					if (wservice[i].getId() == service_id){
						SetServiceFeaturesDialog features_dialog = SetServiceFeaturesDialog.newInstance(wfeature, wservice[i]);
						//		features_dialog.setCancelable(false);
						features_dialog.show(getFragmentManager(), "set features");
						break;
					}
				}
			}else{
				Toast.makeText(this, "No features found."  , Toast.LENGTH_LONG).show();
			}
			break;


			case(Consts.REQUESTTYPE_GETOAUTDATA):
				WOAuthData woauthdata =	resultData.getParcelable(Consts.OAUTH_DATA);
			final Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);

			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra(Consts.OAUTH_DATA, woauthdata);
			intent.putExtra(Consts.SERVICE_ID,resultData.getString(Consts.SERVICE_ID) );
			intent.putExtra(Consts.OAUTH_VERSION, resultData.getInt(Consts.OAUTH_VERSION) );
			StopProgressDialog();
			startActivityForResult(intent, Consts.WEBVIEW_REQUEST);				

			break;

			case(Consts.REQUESTTYPE_ALL_USERS):
				if (resultData.getBoolean(Consts.FOUND_WUSERS)){
					wuser_all = resultData.getParcelableArrayList(Consts.WUSERS);
					wUsersNumType_SuggFingFersHidd = resultData.getIntArray(Consts.WUSERS_NUMBERS);
				}
				else{
					wUsersNumType_SuggFingFersHidd = new int[0];
				}
			populateDrawerRight();
			StopProgressDialog();
			break;


			case(Consts.REQUESTTYPE_GET_COLLEAGUE_PROFILE):
				if (resultData.getBoolean(Consts.FOUND_WUSER)){
					WUser wuser_colleague = resultData.getParcelable(Consts.WUSER);
					openUserProfile(wuser_colleague);
				}else{
					Toast.makeText(this, "Error retrieving colleague profile."  , Toast.LENGTH_LONG).show();
					StopProgressDialog();
				}
			break;


			case(Consts.REQUESTTYPE_RECORD):
				StopProgressDialog();
			if (resultData.getBoolean(Consts.RECORDED)){
				Toast.makeText(this, "Service recorded."  , Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "Error recording service. Check the inserted values and try again."  , Toast.LENGTH_LONG).show();
			}
			int service_id_rec = resultData.getInt(Consts.SERVICE_ID);
			for (int i=0; i<wservice.length;i++){
				if (wservice[i].getId() == service_id_rec){
					wservice[i].setRegistered(true);
					ServicesAdapter adapter = new ServicesAdapter(getBaseContext(), 0, wservice, wuser, this.proxy_string);
					mDrawerList_left.setAdapter(adapter);
					break;
				}
			}
			getFeatures(service_id_rec);
			break;

			case(Consts.REQUESTTYPE_SET_FEATURES):
				StopProgressDialog();
			if (resultData.getBoolean(Consts.SETTED_FEATURES)){
				Toast.makeText(this, "Features updated."  , Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "Error occurred."  , Toast.LENGTH_LONG).show();
			}
			break;

			case(Consts.REQUESTTYPE_SET_AVATAR):
				StopProgressDialog();
			if (resultData.getBoolean(Consts.SETTED_AVATAR)){
				Toast.makeText(this, "Avatar updated."  , Toast.LENGTH_LONG).show();
				//è necessario aggiornare wuser e ricaricare il drawerleft
				wuser.setAvatar(resultData.getString(Consts.URI));
				populateDrawerLeft();
				//TODO da controllare che funzioni anche con la cache dell'imageloader attiva
			}else{
				Toast.makeText(this, "Error occurred."  , Toast.LENGTH_LONG).show();
			}
			break;

			case(Consts.REQUESTTYPE_UPDATE_HIDDEN_SETTINGS):
				if (resultData.getBoolean(Consts.HIDDEN_SETTINGS_UPDATED)){
					Toast.makeText(this, "Hide settings updated."  , Toast.LENGTH_LONG).show();
					//ricarica il drawer degli utenti
					loadFriends();
				}else{
					StopProgressDialog();
					Toast.makeText(this, "Error occurred."  , Toast.LENGTH_LONG).show();
				}
			break;

			case(Consts.REQUESTTYPE_CHANGE_PASSW):
				StopProgressDialog();
			if (resultData.getBoolean(Consts.PASSWORD_SETTED)){
				Toast.makeText(this, "Password changed."  , Toast.LENGTH_LONG).show();
				passw_string = resultData.getString(Consts.NEW_PASSWORD);
			}else{
				Toast.makeText(this, "Error occurred."  , Toast.LENGTH_LONG).show();
			}
			break;

			case(Consts.REQUESTTYPE_UNREG_SERVICE):
				int service_id = resultData.getInt(Consts.SERVICE_ID);
			for (int i=0; i<wservice.length;i++){
				if (wservice[i].getId() == service_id){
					wservice[i].setRegistered(false);
					ServicesAdapter adapter = new ServicesAdapter(getBaseContext(), 0, wservice, wuser, this.proxy_string);
					mDrawerList_left.setAdapter(adapter);
					break;
				}
			}
			StopProgressDialog();
			Toast.makeText(this, "Service unsubscribed."  , Toast.LENGTH_LONG).show();
			break;

			case(Consts.REQUESTTYPE_SET_FOLLOWED):
				//nel caso sia stato scelto di non seguirlo più allora ve chiesto se si vuole anche nascondere
				//dalla lista dei suggeriti
				loadFriends(); //valore settato, ricarica il drawer destro
			break;

			case(Consts.REQUESTTYPE_AUTHORIZE):
				int service_id1 = resultData.getInt(Consts.SERVICE_ID);
			for (int i=0; i<wservice.length;i++){
				if (wservice[i].getId() == service_id1){
					wservice[i].setRegistered(true);
					ServicesAdapter adapter = new ServicesAdapter(getBaseContext(), 0, wservice, wuser, this.proxy_string);
					mDrawerList_left.setAdapter(adapter);
					break;
				}
			}
			getFeatures(service_id1);
			//StopProgressDialog();
			break;
			}
		}
	}


	@Override
	public void onRequestConnectionError(Request request, int statusCode) {
		StopProgressDialog();
		switch(statusCode){
		case Error_consts.SETFOLLOWED_ERROR:
		{
			Toast.makeText(this, "Error setting user's status", Toast.LENGTH_SHORT).show();
			FragmentManager fragmentManager = getSupportFragmentManager();
			WUserColleagueProfile_Fragment fragment = (WUserColleagueProfile_Fragment) fragmentManager.findFragmentByTag(HomeActivity.FRAGMENT_WUSERCOLLEAGUE_PROFILE);
			fragment.changeCheckBoxState();
		}
		break;
		case Error_consts.SETFOLLOWED_ERROR * Error_consts.TIMEOUT_FACTOR:
		{ 
			Toast.makeText(this, "Error setting user's status. Try again in a few minutes.", Toast.LENGTH_SHORT).show();
			FragmentManager fragmentManager = getSupportFragmentManager();
			WUserColleagueProfile_Fragment fragment = (WUserColleagueProfile_Fragment) fragmentManager.findFragmentByTag(HomeActivity.FRAGMENT_WUSERCOLLEAGUE_PROFILE);
			fragment.changeCheckBoxState();
		}
		break;
		case Error_consts.ERROR_GET_FRIENDS:
			Toast.makeText(this, "Error retrieving users list. Exiting to login. ", Toast.LENGTH_SHORT).show();
			exitToLogin();
			break;
		case Error_consts.ERROR_GET_FRIENDS * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error retrieving users list. Try again in a few minutes. Exiting to login.", Toast.LENGTH_SHORT).show();
			exitToLogin();
			break;
		case Error_consts.ERROR_RETRIEVING_HIDDEN_SETTINGS:
			Toast.makeText(this, "Error retrieving Hide Settings. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_RETRIEVING_HIDDEN_SETTINGS * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error retrieving Hide Settings. Try again in a few minutes. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.POST_ERROR:
			Toast.makeText(this, "Error sending message. ", Toast.LENGTH_SHORT).show();
			//exitToLogin();
			break;
		case Error_consts.POST_ERROR * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error sending message. Try again in a few minutes.", Toast.LENGTH_SHORT).show();
			//exitToLogin();
			break;
		case Error_consts.RECORD_ERROR:
			Toast.makeText(this, "Error recording the service. ", Toast.LENGTH_SHORT).show();
			//exitToLogin();
			break;
		case Error_consts.RECORD_ERROR * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error recording the service. Try again in a few minutes.", Toast.LENGTH_SHORT).show();
			//	exitToLogin();
			break;
		case Error_consts.ERROR_RETRIEVING_SERVICES:
			Toast.makeText(this, "Error retrieving services.  Exiting to login.", Toast.LENGTH_SHORT).show();
			exitToLogin();
			break;
		case Error_consts.ERROR_RETRIEVING_SERVICES * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error retrieving services. Try again in a few minutes. Exiting to login.", Toast.LENGTH_SHORT).show();
			exitToLogin();
			break;
		case Error_consts.ERROR_RETRIVENG_GOLLEAGUE:
			Toast.makeText(this, "Error retrieving colleague profile. ", Toast.LENGTH_SHORT).show();

			break;
		case Error_consts.ERROR_RETRIVENG_GOLLEAGUE * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error retrieving colleague profile. Try again in a few minutes.", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_SETTINGPASSW:
			Toast.makeText(this, "Error setting password. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_SETTINGPASSW * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error setting password. Try again in a few minutes.", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_UPDATING_HIDDEN_SETTINGS:
			Toast.makeText(this, "Error updating Hide Settings. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_UPDATING_HIDDEN_SETTINGS * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error updating Hide Settings. Try again in a few minutes.", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_USERNAME_AVAILABLE:
			Toast.makeText(this, "Error retrieving username availability. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_USERNAME_AVAILABLE * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error retrieving username availability. Try again in a few minutes. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_GET_OAUTHDATA:
			Toast.makeText(this, "Error retrieving OAuth Data. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_GET_OAUTHDATA * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error retrieving OAuth Data. Try again in a few minutes. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_RETRIEVING_AVATARS:
			Toast.makeText(this, "Error retrieving Available Avatars. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_RETRIEVING_AVATARS * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error retrieving Available Avatars. Try again in a few minutes. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.AUTHORIZE_ERROR:
			Toast.makeText(this, "Error in Authorize operation. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.AUTHORIZE_ERROR * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error in Authorize operation. Try again in a few minutes. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_RETRIEVING_FEATURES:
			Toast.makeText(this, "Error in retrieving features. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_RETRIEVING_FEATURES * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error in retrieving features. Try again in a few minutes. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.SET_FEATURES_ERROR:
			Toast.makeText(this, "Error updating features. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.SET_FEATURES_ERROR * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error updating features. Try again in a few minutes. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.SET_AVATAR_ERROR:
			Toast.makeText(this, "Error setting new Avatar. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.SET_AVATAR_ERROR * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error setting new Avatar. Try again in a few minutes. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.UNREG_SERVICE_ERROR:
			Toast.makeText(this, "Error unsubscribing service. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.UNREG_SERVICE_ERROR * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error unsubscribing service. Try again in a few minutes. ", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	public void exitToLogin(){
		StopProgressDialog();
		//se ci sono fragment eliminali.. 
		if (mRequestManager.isRequestInProgress(r)) mRequestManager.removeRequestListener(this, r);
		if (mRequestManager.isRequestInProgress(r2)) mRequestManager.removeRequestListener(this, r2);
		ConfiguratedImageLoader.destroyIfImageLoader();
		Preferences.setFalseAutolog(this);
		Intent i = new Intent(HomeActivity.this, LoginActivity.class);
		startActivity(i);
		HomeActivity.this.finish();
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
		if (!mRequestManager.isRequestInProgress(r)) r = null;
		if (!mRequestManager.isRequestInProgress(r2)) r2 = null;
		mRequestManager.removeRequestListener(this);
		unlockScreenOrientation();


	}


	@Override
	public void onBackPressed() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.getBackStackEntryCount()>0){
			super.onBackPressed();
			return;
		}else{
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


	private boolean isOnline() {
		ConnectivityManager cm =     (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}


	@Override
	public void setFragmentLoading(Boolean isFragmentLoading) {
		this.isFragmentLoading = isFragmentLoading;  

	}

	@Override
	public void onProfileFragmentCheckBoxChanged(Boolean followChecked,	WUser wuser_profile) {
		setFollow( followChecked,  wuser_profile);

	}



	@Override
	public void openUserProfileFromFragment(WUser wuserToOpen) {
		if (wuserToOpen.getId() == wuser.getId()){
			openUserProfile(wuserToOpen);
		}else{
			loadColleagueProfile(wuserToOpen.getId());
		}

	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Consts.WEBVIEW_REQUEST) {

			if(resultCode == RESULT_OK){      
				String service_id = data.getStringExtra(Consts.SERVICE_ID);          
				String token = data.getStringExtra(Consts.ACCESS_TOKEN); 
				String secret = data.getStringExtra(Consts.ACCESS_SECRET);
				String pin = data.getStringExtra(Consts.VERIFIER_PIN);
				//	int oaut_version = data.getIntExtra(Consts.OAUTH_VERSION, -1);
				Authorize(service_id, token, pin, secret);

			}
			if (resultCode == RESULT_CANCELED) {    
				//Write your code if there's no result
			}
		}
	}




	private void Authorize(String service_id, String token, String verifier, String accessSecret) {
		if (isOnline()){
			r = SocialCDERequestFactory.authorize();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.SERVICE_ID, service_id);
			r.put(Consts.ACCESS_TOKEN, token);
			r.put(Consts.VERIFIER, verifier);
			r.put(Consts.ACCESS_SECRET, accessSecret);
			r.setMemoryCacheEnabled(true);

			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}

	}


	private void getFeatures(int service_id) {
		if (isOnline()){
			r = SocialCDERequestFactory.getFeatures();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.SERVICE_ID, service_id);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}


	public void saveFeaturesStatus(int service_id, String active_features) {
		if (isOnline()){
			r = SocialCDERequestFactory.setActiveFeatures();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.SERVICE_ID, service_id);
			r.put(Consts.ACTIVE_FEATURES, active_features);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}


	public void unregisterService(int service_id){
		if (isOnline()){
			r = SocialCDERequestFactory.unregisterService();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.SERVICE_ID, service_id);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}




	@Override
	public void recordService(String service_id, String username,
			String password, String domain) {
		if (isOnline()){
			r = SocialCDERequestFactory.recordService();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.SERVICE_ID, service_id);
			r.put(Consts.TFSUSERNAME, username);
			r.put(Consts.TFSPASSWORD, password);
			r.put(Consts.TFSDOMAIN, domain);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}


	public void sendTFSPost(String postTFS){
		if (isOnline()){
			r = SocialCDERequestFactory.sendTFSpost();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.POST_TFS, postTFS);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}




	@Override
	public void change_password(String new_password) {
		if (isOnline()){
			r = SocialCDERequestFactory.changePass();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.NEW_PASSWORD, new_password);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}

	}




	@Override
	public void loadUserHideSettings(int userId) {
		if (isOnline()){
			r = SocialCDERequestFactory.getHideSettings();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.USERID, userId);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}




	@Override
	public void setHideSettings(WHidden whidden, int userId) {
		if (isOnline()){
			r = SocialCDERequestFactory.setHideSettings();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.USERID, userId);
			r.put(Consts.WHIDDEN, whidden);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}

	private void loadAvailableAvatars(){
		if (isOnline()){
			r = SocialCDERequestFactory.getAvailableAvatars();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}




	@Override
	public void setAvatar(String uri) {
		if (isOnline()){
			r = SocialCDERequestFactory.setAvatar();
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_string);
			r.put(Preferences.PASSWORD, this.passw_string);
			r.put(Consts.URI, uri);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}


} 