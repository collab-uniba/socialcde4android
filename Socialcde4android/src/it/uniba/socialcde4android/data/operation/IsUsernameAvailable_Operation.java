package it.uniba.socialcde4android.data.operation;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.Bundle;

import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.preferences.Preferences;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;


public class IsUsernameAvailable_Operation implements Operation {

	//private static final String TAG = RetrieveServices_Operation.class.getSimpleName();

	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {
		String host = request.getString(Preferences.PROXYSERVER)+ "/SocialTFSProxy.svc";;
		String username = request.getString(Preferences.USERNAME);
		String output = "";
		Boolean available = false;
		int status = 0;
		try {

			URL url = new URL(host + "/IsAvailable?username=" + username);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(20000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			status = conn.getResponseCode();

			if (status >= 200 && status <= 299) {
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());
				BufferedReader br = new BufferedReader(in);
				output = br.readLine();

			}else{
				throw new ConnectionException("Connection error",status);

			}
		}catch(java.net.SocketTimeoutException e) {
			
			throw new ConnectionException("Connection error",status);
			
		} catch (Exception e) {

			throw new ConnectionException("Connection error",status);
		}

		if (output.equals("true")) {

			available = true;


		} else {

			available = false;

		}
		
		Bundle bundle = new Bundle();
		bundle.putBoolean(Consts.USERNAME_AVAILABLE, available);
		bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_USERNAMEVAILABLE);

		return bundle;
		

	}

}
