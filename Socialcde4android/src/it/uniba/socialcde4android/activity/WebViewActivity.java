package it.uniba.socialcde4android.activity;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.R.layout;
import it.uniba.socialcde4android.R.menu;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WOAuthData;
import it.uniba.socialcde4android.shared.library.WUser;
import it.uniba.socialcde4android.utility.ScreenUtility;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewActivity extends Activity {

	private static ProgressDialog progressDialog; 
	private WOAuthData woauthdata = null;
	private String service_id = "";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.activity_web_view);

		if (getIntent().hasExtra(Consts.OAUTH_DATA) && savedInstanceState==null){
			woauthdata = getIntent().getParcelableExtra(Consts.OAUTH_DATA);
			service_id = getIntent().getStringExtra(Consts.SERVICE_ID);
		}
		final WebView webView = new WebView(this);
		final ViewGroup viewGroup = (ViewGroup)findViewById(R.id.webview_root);
		viewGroup.addView(webView);

		// Open keyboard when focusing on Twitter login form.
		webView.requestFocus(View.FOCUS_DOWN);
		webView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					break;
				}
				return false;
			}
		});

		webView.setWebViewClient(new CustomWebViewClient());

		try {
			webView.loadUrl(woauthdata.getAuthorizationLink());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private class CustomWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			StartProgressDialog();
		}

		@Override
		public void onPageFinished(WebView view, final String url) {
			StopProgressDialog();
		}

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
			if ( url.contains("#access_token=")  && woauthdata.getAuthorizationLink().contains("facebook")){
				String token =  url.toString().substring(url.indexOf("#access_token=")+14, url.indexOf("&expires"));
			}else{
				if(url.contains("user_denied#")){
					finish();
				}else{
					view.loadUrl(url);
				}
			}
			return true;
		}  
	}

	public   void StartProgressDialog(){
		if (progressDialog == null || !progressDialog.isShowing()){
			lockScreenOrientation();
			progressDialog = ProgressDialog.show(this, "Loading..", "Wait a moment please", true, true);
		}
	}

	public  void StopProgressDialog(){
		if (progressDialog != null ){
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}

}