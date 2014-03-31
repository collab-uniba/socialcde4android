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

import it.uniba.socialcde4android.config.Config;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.costants.Error_consts;
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
		String old_password = request.getString(Preferences.PASSWORD);
		String new_password = request.getString(Consts.NEW_PASSWORD);
		int status = 0;
		String result = "";
		Boolean changed = false;
		try {
			URL url = new URL(host + "/ChangePassword");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(Config.CONN_TIMEOUT_MS);
			conn.setReadTimeout(Config.READ_TIMEOUT_MS);
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
					+ "\", \"oldPassword\":\"" + old_password
					+ "\" , \"newPassword\":\"" + new_password + "\"}");

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
				throw new ConnectionException("Error setting new password",Error_consts.ERROR_SETTINGPASSW);

			}

			conn.disconnect();
		}catch(java.net.SocketTimeoutException e) {
			status = Consts.TIMEOUT_STATUS;
			throw new ConnectionException("Error setting new password",Error_consts.ERROR_SETTINGPASSW * Error_consts.TIMEOUT_FACTOR);
		} catch (Exception e) {
			throw new ConnectionException("Error setting new password",Error_consts.ERROR_SETTINGPASSW);
		}

		if (result.equals("true")) {

			changed = true;

		} else {
			changed = false;
		}
				
		
		Bundle bundle = new Bundle();
		bundle.putBoolean(Consts.PASSWORD_SETTED, changed);
		bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_CHANGE_PASSW);
		bundle.putString(Consts.NEW_PASSWORD, new_password);
		return bundle;

	}

}
