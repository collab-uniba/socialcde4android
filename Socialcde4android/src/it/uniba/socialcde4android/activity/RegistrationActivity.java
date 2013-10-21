package it.uniba.socialcde4android.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.dialogs.NoNetworkDialog;
import it.uniba.socialcde4android.data.requestmanager.SocialCDERequestFactory;
import it.uniba.socialcde4android.data.requestmanager.SocialCDERequestManager;
import it.uniba.socialcde4android.preferences.Preferences;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class RegistrationActivity extends Activity implements RequestListener {

	private EditText proxyEdit;
	private EditText mailEdit;
	private EditText invitationEdit;
	private EditText userEdit;
	private EditText passwEdit;
	private EditText passwConfEdit;
	private Button regButton;
	private String proxy_string;
	private String mail_string;
	private String invitationCode_string;
	private String userName_String;
	private String passw_string;
	private String passwConf_string;
	private RequestManager mRequestManager;
	private Request r;
	private static final String DIALOG_SHOWN = "DIALOG_SHOWN";
	private static final String PARCELABLE_REQUEST = "PARCELABLE_REQUEST";
	private static ProgressDialog progressDialog; 




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registration);

		proxyEdit = (EditText) findViewById(R.id.editTextRegProxy);
		mailEdit = (EditText) findViewById(R.id.editTextRegEmail);
		invitationEdit = (EditText) findViewById(R.id.editTextRecInvCode);
		userEdit = (EditText) findViewById(R.id.editTextRegName);
		passwEdit = (EditText) findViewById(R.id.editTextRegPassw);
		passwConfEdit = (EditText) findViewById(R.id.editTextRegConfPassw);
		regButton = (Button) findViewById(R.id.buttonSendRegistration);
		regButton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// 
				initializeRegistration();
			}
		});
		mRequestManager = SocialCDERequestManager.from(this);

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


	private void initializeRegistration() {
		// 
		proxy_string = proxyEdit.getText().toString().trim();
		mail_string = mailEdit.getText().toString().trim();
		invitationCode_string = invitationEdit.getText().toString().trim();
		userName_String = userEdit.getText().toString().trim();
		passw_string = passwEdit.getText().toString().trim();
		passwConf_string = passwConfEdit.getText().toString().trim();
		//controllo prima che tutti i campi siano stati riempiti
		if (proxy_string.equals("") || mail_string.equals("") || invitationCode_string.equals("") || 
				userName_String.equals("") || passw_string.equals("") || passwConf_string.equals("")){

			Toast.makeText(this, "Please compile all the fields.."  , Toast.LENGTH_SHORT).show();

		}else{
			//controllo che le due password coincidano
			if (!passw_string.equals(passwConf_string)){
				//mostra la dialog in cui dice che le password non coincidono
				Toast.makeText(this, "Please check your password; the confirmation entry does not match. "  , Toast.LENGTH_SHORT).show();
				passwEdit.setText("");
				passwConfEdit.setText("");
				passwEdit.setHint("Password");
				passwConfEdit.setHint("Confirm password");
				Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
				passwEdit.startAnimation(shake);
				passwConfEdit.startAnimation(shake);
			}else{

				//poi che la mail sia in formato corretto
				Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
				Matcher m = p.matcher(mail_string);
				if (!m.matches()){
					Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
					mailEdit.startAnimation(shake);
					Toast.makeText(this, "You have entered an invalid email. Please check your email address and try again."  , Toast.LENGTH_SHORT).show();

				}else{
					//la lunghezza minima della password e i caratteri ammessi(?)
					//in caso di campi tutti compilati correttamente


					//invio la richiesta di registrazione
					if (isOnline()){
						//posso chiamare il metodo per il login
						sendRegistration();
					}else{
						new NoNetworkDialog().show(getFragmentManager(), "alert");
					}
				}
			}
		}
	}



	private void sendRegistration(){
		r = SocialCDERequestFactory.subscribeUser();
		r.put(Consts.MAIL, this.mail_string);
		r.put(Preferences.PROXYSERVER, this.proxy_string);
		r.put(Preferences.USERNAME, this.userName_String);
		r.put(Preferences.PASSWORD, this.passw_string);
		r.setMemoryCacheEnabled(true);
		StartProgressDialog();
		mRequestManager.execute(r, this);		
	}

	
	private void changeInvitationCodeWithPassword(){
		r = SocialCDERequestFactory.changeInvWithPass();
		r.put(Consts.MAIL, this.mail_string);
		r.put(Preferences.PROXYSERVER, this.proxy_string);
		r.put(Preferences.USERNAME, this.userName_String);
		r.put(Consts.INVIT_CODE, this.invitationCode_string);
		r.put(Preferences.PASSWORD, this.passw_string);
		r.setMemoryCacheEnabled(true);
		StartProgressDialog();
		mRequestManager.execute(r, this);	
		
	}
	
	public   void StartProgressDialog(){
		if (progressDialog == null || !progressDialog.isShowing()){
			progressDialog = ProgressDialog.show(this, "Querying the server..", "Wait a moment please", true, false);
		}
	}


	public  void StopProgressDialog(){
		if (progressDialog != null){
			progressDialog.dismiss();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}

	@Override
	public void onRequestFinished(Request request, Bundle resultData) {
		// 
		StopProgressDialog();
		if (resultData != null){
			switch(resultData.getInt(Consts.REQUEST_TYPE)){


			case(Consts.REQUESTTYPE_SUBSCRIBEUSER):
				int status_serv = resultData.getInt(Consts.STATUS_WEBSERVICE);
			if (status_serv >= 200 && status_serv <= 299) {
				int response = resultData.getInt(Consts.SUBSCRIPTION_RESPONSE);
				switch(response){
				case -3:
					Toast.makeText(this, "Please compile all the fields correctly."  , Toast.LENGTH_SHORT).show();
					break;
				case -2:
					Toast.makeText(this, "Please enter a valid proxy."  , Toast.LENGTH_SHORT).show();
					Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
					proxyEdit.startAnimation(shake);
					break;
				case -1:
					Toast.makeText(this, "There's a problem. Check your connection and try again"  , Toast.LENGTH_SHORT).show();
					break;
				case 0:

					//chiama change password per l'iscrizione vera e propria..
					changeInvitationCodeWithPassword();
					break;
				case 1: // if e-mail address does not exist in the database
					Toast.makeText(this, "Please enter the email on which you recived the invite"  , Toast.LENGTH_SHORT).show();
					Animation shake1 = AnimationUtils.loadAnimation(this, R.anim.shake);
					mailEdit.startAnimation(shake1);
					break;
				case 2:
					Toast.makeText(this, "Please enter the invitation code that you recived in the invite"  , Toast.LENGTH_SHORT).show();
					Animation shake2 = AnimationUtils.loadAnimation(this, R.anim.shake);
					this.invitationEdit.startAnimation(shake2);
					break;
				case 3: // if username is already used by another user
					Toast.makeText(this, "The Username chosen is not available"  , Toast.LENGTH_SHORT).show();
					Animation shake3 = AnimationUtils.loadAnimation(this, R.anim.shake);
					userEdit.startAnimation(shake3);
					break;
				default:
					Toast.makeText(this, "Response not valid from the server"  , Toast.LENGTH_SHORT).show();
				}

			}else{
				Toast.makeText(this, "Please check the proxy address entered."  , Toast.LENGTH_SHORT).show();
				Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
				proxyEdit.startAnimation(shake);
			}
			break;
			
			
			
			
			case(Consts.REQUESTTYPE_CHANGE_INVIT_WITH_PASSW):
				int status_ch = resultData.getInt(Consts.STATUS_WEBSERVICE);
			if (status_ch >= 200 && status_ch <= 299) {
				if(resultData.getBoolean(Consts.PASSWORD_SETTED)){
					
					//dialog
					//TODO
					Toast.makeText(this, "Registration complete."  , Toast.LENGTH_SHORT).show();

				}else{
					Toast.makeText(this, "Registration failed; please contact the administrator"  , Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(this, "Please check the proxy address entered."  , Toast.LENGTH_SHORT).show();
				Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
				proxyEdit.startAnimation(shake);
			}
			
			
			}

		}else{
			Toast.makeText(this, "ERROR"  , Toast.LENGTH_SHORT).show();
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



}
