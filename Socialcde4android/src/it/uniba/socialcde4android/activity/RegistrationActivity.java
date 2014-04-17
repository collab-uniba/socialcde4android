package it.uniba.socialcde4android.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.costants.Error_consts;
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

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.view.Menu;

import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

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
		invitationCode_string = invitationEdit.getText().toString();
		userName_String = userEdit.getText().toString().trim();
		passw_string = passwEdit.getText().toString();
		passwConf_string = passwConfEdit.getText().toString();
		//controllo prima che tutti i campi siano stati riempiti

		Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher m = p.matcher(mail_string);

		if (proxy_string.equals("") || mail_string.equals("") || invitationCode_string.equals("") ||
				userName_String.equals("") || passw_string.equals("") || passwConf_string.equals("")){

			Toast.makeText(this, "Please compile all the fields.." , Toast.LENGTH_SHORT).show();

		}else if (!passw_string.equals(passwConf_string)){
			//controllo che le due password coincidano

			//mostra la dialog in cui dice che le password non coincidono
			Toast.makeText(this, "Please check your password; the confirmation entry does not match. " , Toast.LENGTH_SHORT).show();
			passwEdit.setText("");
			passwConfEdit.setText("");
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			passwEdit.startAnimation(shake);
			passwConfEdit.startAnimation(shake);
		}else if (!m.matches()){

			//poi che la mail sia in formato corretto
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			mailEdit.startAnimation(shake);
			Toast.makeText(this, "You have entered an invalid email. Please check your email address and try again." , Toast.LENGTH_SHORT).show();

		}else if (passwEdit.getText().toString().length()<6){
			//la lunghezza minima della password
			//in caso di campi tutti compilati correttamente
			Toast.makeText(this, "Please enter a password of at least six characters" , Toast.LENGTH_SHORT).show();
			passwEdit.setText("");
			passwConfEdit.setText("");
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			passwEdit.startAnimation(shake);
			passwConfEdit.startAnimation(shake);
		}else if (isOnline()){
			//posso chiamare il metodo per il login
			sendRegistration();
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}





	private void sendRegistration(){
		if (isOnline()){
			r = SocialCDERequestFactory.subscribeUser();
			r.put(Consts.MAIL, this.mail_string);
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_String);
			r.put(Consts.INVIT_CODE, this.invitationCode_string);
			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);	
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}


	private void changeInvitationCodeWithPassword(){
		if (isOnline()){
			r = SocialCDERequestFactory.changePass();
			r.put(Consts.MAIL, this.mail_string);
			r.put(Preferences.PROXYSERVER, this.proxy_string);
			r.put(Preferences.USERNAME, this.userName_String);

			r.put(Preferences.PASSWORD, this.invitationCode_string);
			r.put(Consts.NEW_PASSWORD, this.passw_string);

			r.setMemoryCacheEnabled(true);
			StartProgressDialog();
			mRequestManager.execute(r, this);	
		}else{
			new NoNetworkDialog().show(getFragmentManager(), "alert");
		}
	}


	public void StartProgressDialog(){
		if (progressDialog == null || !progressDialog.isShowing()){
			progressDialog = ProgressDialog.show(this, "Querying the server..", "Wait a moment please", true, false);
		}
	}


	public void StopProgressDialog(){
		if (progressDialog != null){
			progressDialog.dismiss();
		}
	}



	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

		if (resultData != null){
			switch(resultData.getInt(Consts.REQUEST_TYPE)){


			case(Consts.REQUESTTYPE_SUBSCRIBEUSER):
				int response = resultData.getInt(Consts.SUBSCRIPTION_RESPONSE);
			switch(response){
			//			case -3:
			//				StopProgressDialog();
			//				Toast.makeText(this, "Please compile all the fields correctly." , Toast.LENGTH_SHORT).show();
			//				
			//				break;
			//			case -2:
			//				StopProgressDialog();
			//				Toast.makeText(this, "Please enter a valid proxy." , Toast.LENGTH_SHORT).show();
			//				Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			//				proxyEdit.startAnimation(shake);
			//				break;
			case -1:
				StopProgressDialog();
				Toast.makeText(this, "There's a problem. Check your connection and try again" , Toast.LENGTH_SHORT).show();
				break;
			case 0:
				//TODO
				//chiama change password per l'iscrizione vera e propria..
				changeInvitationCodeWithPassword();
				break;
			case 1: // if e-mail address does not exist in the database
				StopProgressDialog();
				Toast.makeText(this, "Please enter the email on which you recived the invite" , Toast.LENGTH_SHORT).show();
				Animation shake1 = AnimationUtils.loadAnimation(this, R.anim.shake);
				mailEdit.startAnimation(shake1);
				break;
			case 2:
				StopProgressDialog();
				Toast.makeText(this, "Please enter the invitation code that you recived in the invite" , Toast.LENGTH_SHORT).show();
				Animation shake2 = AnimationUtils.loadAnimation(this, R.anim.shake);
				this.invitationEdit.startAnimation(shake2);
				break;
			case 3: // if username is already used by another user
				StopProgressDialog();
				Toast.makeText(this, "The Username chosen is not available" , Toast.LENGTH_SHORT).show();
				Animation shake3 = AnimationUtils.loadAnimation(this, R.anim.shake);
				userEdit.startAnimation(shake3);
				break;
			default:
				StopProgressDialog();
				Toast.makeText(this, "Response not valid from the server" , Toast.LENGTH_SHORT).show();
			}

			break;


			case(Consts.REQUESTTYPE_CHANGE_PASSW):
				StopProgressDialog();
			if(resultData.getBoolean(Consts.PASSWORD_SETTED)){
				//dialog
				//TODO
				Toast.makeText(this, "Registration complete." , Toast.LENGTH_SHORT).show();
				exitToLogin();
			}else{
				Toast.makeText(this, "Registration failed; please contact the administrator" , Toast.LENGTH_SHORT).show();
			}
			break;


			}	
		}else{
			Toast.makeText(this, "ERROR" , Toast.LENGTH_SHORT).show();
		}
	}



	@Override
	public void onRequestConnectionError(Request request, int statusCode) {
		StopProgressDialog();
		switch(statusCode){

		case Error_consts.ERROR_SUBSCRIBE_USER:
			Toast.makeText(this, "Error subscribing user. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_SUBSCRIBE_USER * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error subscribing user. Try again in a few minutes. ", Toast.LENGTH_SHORT).show();
			break;
		case Error_consts.ERROR_SETTINGPASSW:
			Toast.makeText(this, "Error setting password. ", Toast.LENGTH_SHORT).show();

			break;
		case Error_consts.ERROR_SETTINGPASSW * Error_consts.TIMEOUT_FACTOR:
			Toast.makeText(this, "Error setting password. Try again in a few minutes.", Toast.LENGTH_SHORT).show();
			break;
		}
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

	private void exitToLogin(){
		Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
		startActivity(i);
		RegistrationActivity.this.finish();
	}



}