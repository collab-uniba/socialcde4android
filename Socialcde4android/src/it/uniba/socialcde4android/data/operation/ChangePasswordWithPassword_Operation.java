package it.uniba.socialcde4android.data.operation;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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


public class ChangePasswordWithPassword_Operation implements Operation {

	//private static final String TAG = RetrieveServices_Operation.class.getSimpleName();

	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {
		String host = request.getString(Preferences.PROXYSERVER)+ "/SocialTFSProxy.svc";;
		String username = request.getString(Preferences.USERNAME);
		String invitCode = request.getString(Consts.INVIT_CODE);
		String password = request.getString(Preferences.PASSWORD);
		int status = 0;
		String result = "";
		Boolean changed = false;
		try {
			URL url = new URL(host + "/ChangePassword");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(20000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setRequestProperty("Content-Type", "application/json");

			// Create the form content
			OutputStream out = conn.getOutputStream();
			Writer writer = new OutputStreamWriter(out, "UTF-8");
			writer.write("{ \"username\":\"" + username
					+ "\", \"oldPassword\":\"" + invitCode
					+ "\" , \"newPassword\":\"" + password + "\"}");

			writer.close();
			out.close();
			status = conn.getResponseCode();

			if (status >= 200 && status <= 299) {
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());
				BufferedReader br = new BufferedReader(in);
				String output;

				while ((output = br.readLine()) != null) {
					result += output;

				}
				br.close();

			}else{
				throw new ConnectionException("Error setting new password",status);

			}

			conn.disconnect();
		}catch(java.net.SocketTimeoutException e) {
			status = Consts.TIMEOUT_STATUS;
			throw new ConnectionException("Error setting new password",status);
		} catch (Exception e) {
			throw new ConnectionException("Error setting new password",status);
		}

		if (result.equals("true")) {

			changed = true;

		} else {
			changed = false;
		}
				
		
		Bundle bundle = new Bundle();
		bundle.putBoolean(Consts.PASSWORD_SETTED, changed);
		bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_CHANGE_INVIT_WITH_PASSW);
		return bundle;

	}

}
