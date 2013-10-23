package it.uniba.socialcde4android.activity;


import java.util.HashMap;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.dialogs.NoNetworkDialog;
import it.uniba.socialcde4android.data.requestmanager.SocialCDERequestFactory;
import it.uniba.socialcde4android.data.requestmanager.SocialCDERequestManager;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WService;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;



public class LoginActivity extends Activity implements RequestListener {

	private EditText proxyEdit;
	private EditText userNameEdit;
	private EditText passwEdit;
	private CheckBox autoLogCheck;
	private CheckBox savePasswCheck;
	private Button loginButton;
	private String proxy_string;
	private String userName_string;
	private String passw_string;
	private boolean doubleBackToExitPressedOnce = false;
	private static final String DIALOG_SHOWN = "DIALOG_SHOWN";
	private static final String PARCELABLE_REQUEST = "PARCELABLE_REQUEST";
	private static ProgressDialog progressDialog; 
	private RequestManager mRequestManager;
	private Request r;
	private Button signButton;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Preferences.setFalseAutolog(this);
		setContentView(R.layout.activity_login);
		proxyEdit = (EditText)findViewById(R.id.editTextRegProxy);
		userNameEdit = (EditText)findViewById(R.id.editTextRegName);
		passwEdit = (EditText)findViewById(R.id.editTextloginpassw);
		loginButton = (Button)findViewById(R.id.buttonSendRegistration);
		autoLogCheck = (CheckBox)findViewById(R.id.checkBoxAutoLog);
		savePasswCheck = (CheckBox)findViewById(R.id.checkBoxSavePassw);
		signButton = (Button) findViewById(R.id.buttonSignup);
		signButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
				startActivity(i);
			}
		});

		loginButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				initializeLogin();
			}
		});
		autoLogCheck.setOnClickListener(new CheckBox.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (autoLogCheck.isChecked()){
					savePasswCheck.setChecked(true);
				}
			}

		});
		savePasswCheck.setOnClickListener(new CheckBox.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if (!savePasswCheck.isChecked()){
					autoLogCheck.setChecked(false);
				}
			}

		});
		mRequestManager = SocialCDERequestManager.from(this);
		loadPreferences();	

	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
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


	private void loadPreferences(){
		if (Preferences.isSavedPreferences(this)){
			//load delle preferenze

			HashMap<String, String> prefMap =  Preferences.loadPreferences(this);
			String tempProxy = prefMap.get(Preferences.PROXYSERVER);
			proxyEdit.setText(tempProxy);
			String tempUser = prefMap.get(Preferences.USERNAME);
			userNameEdit.setText(tempUser);

			String passBool = prefMap.get(Preferences.SAVEPASS);
			if (passBool.equals("true")){
				savePasswCheck.setChecked(true);
				String tempPassw = prefMap.get(Preferences.PASSWORD);
				passwEdit.setText(tempPassw);
				//a questo punto controllo autolog
				String autoLog = prefMap.get(Preferences.AUTOLOG);
				if (autoLog.equals("true")){
					autoLogCheck.setChecked(true);
					if (isOnline()){
						//posso chiamare il metodo per il login
						proxy_string = tempProxy;
						userName_string = tempUser;
						passw_string = tempPassw;
						login();
					}else{
						new NoNetworkDialog().show(getFragmentManager(), "alert");
					}
				}
			}
		}
	}

	private void initializeLogin(){

		//proxyWrapper = new ProxyWrapper();
		proxy_string = proxyEdit.getText().toString().trim();
		userName_string = userNameEdit.getText().toString().trim();
		passw_string = passwEdit.getText().toString().trim();
		if (proxy_string.equals("") || userName_string.equals("") ||  passw_string.equals("") ){
			Toast.makeText(this, "Please compile all the fields.."  , Toast.LENGTH_SHORT).show();
		}else{
			//prima controllo che ci sia la rete attiva e da lì chiamo il resto
			if (isOnline()){
				//posso chiamare il metodo per il login
				verifyServerAndLogin();
			}else{
				new NoNetworkDialog().show(getFragmentManager(), "alert");
			}
		}
	}


	private void verifyServerAndLogin(){
		//controllo che il server sia online
		r = SocialCDERequestFactory.isWebServRunRequest();
		r.put(Preferences.PROXYSERVER, this.proxy_string);
		r.setMemoryCacheEnabled(true);
		StartProgressDialog();
		mRequestManager.execute(r, this);	
	}

	private void login(){
		r = SocialCDERequestFactory.getWUser();
		r.put(Preferences.PROXYSERVER, this.proxy_string);
		r.put(Preferences.USERNAME, this.userName_string);
		r.put(Preferences.PASSWORD, this.passw_string);
		r.setMemoryCacheEnabled(true);
		StartProgressDialog();
		mRequestManager.execute(r, this);
	}



	private void savePreferences(){
		//devo controllare le checkbox per salvare le impostazioni
		//ed eventualmente i dati personali 
		boolean autolog = autoLogCheck.isChecked();
		boolean savepass = savePasswCheck.isChecked();
		HashMap<String, String> preferencesMap = new HashMap<String, String>();
		preferencesMap.put(Preferences.AUTOLOG, autolog ? "true" : "false");
		preferencesMap.put(Preferences.SAVEPASS, savepass ? "true" : "false");
		preferencesMap.put(Preferences.USERNAME, userName_string);
		preferencesMap.put(Preferences.PROXYSERVER, proxy_string);
		if (savepass){
			preferencesMap.put(Preferences.PASSWORD, passw_string);
		}
		Preferences.savePreferences(this, preferencesMap);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
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


	@Override
	public void onRequestFinished(Request request, Bundle resultData) {

		if (resultData != null){
			switch(resultData.getInt(Consts.REQUEST_TYPE)){




			case(Consts.REQUESTTYPE_GETUSER):

				if (resultData.getBoolean(Consts.FOUND_WUSER)){//OK
					savePreferences();
					Intent i = new Intent(LoginActivity.this, HomeActivity.class);
					i.putExtra("bundle", resultData);
					i.putExtra(Preferences.PROXYSERVER,this.proxy_string);
					i.putExtra(Preferences.USERNAME,this.userName_string);
					i.putExtra(Preferences.PASSWORD,this.passw_string);
					startActivity(i);
					LoginActivity.this.finish();
				}else{
					Toast.makeText(this, "Please check username and password."  , Toast.LENGTH_SHORT).show();
					Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
					this.userNameEdit.startAnimation(shake);
					this.passwEdit.startAnimation(shake);
				}


			StopProgressDialog();
			break;




			case(Consts.REQUESTTYPE_WEBSERVICEVAILABLE):

				Boolean online = resultData.getBoolean(Consts.WEBSERVICE_AVAILABLE);
			if (online){
				login();
			}else{
				Toast.makeText(this, "Please check the proxy address entered. The web service seems uavailable"  , Toast.LENGTH_SHORT).show();
				Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
				this.proxyEdit.startAnimation(shake);
			}

			StopProgressDialog();
			break;

			}
		}
	}



	@Override
	public void onRequestConnectionError(Request request, int statusCode) {
		StopProgressDialog();
		Toast.makeText(this, "Connection error, status: "+statusCode, Toast.LENGTH_SHORT).show();
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



}
