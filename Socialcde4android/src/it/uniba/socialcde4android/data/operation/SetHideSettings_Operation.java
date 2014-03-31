package it.uniba.socialcde4android.data.operation;

import it.uniba.socialcde4android.config.Config;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.costants.Error_consts;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WHidden;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.Bundle;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.google.gson.Gson;

public class SetHideSettings_Operation implements Operation{
	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {


		String username = request.getString(Preferences.USERNAME);
		String password = request.getString(Preferences.PASSWORD);
		String host = request.getString(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
		int user_id = request.getInt(Consts.USERID);
		WHidden whidden = (WHidden) request.getParcelable(Consts.WHIDDEN);
		
		int status =0;
		String result = "";

		try {
			URL url = new URL(host + "/UpdateHiddenUser");
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
			writer.write("{ \"username\":\"" + username + "\", \"password\":\""
					+ password + "\" , \"userId\":\""
					+ user_id + "\", \"suggestions\":\"" + whidden.isSuggestions()
					+ "\" , \"dynamic\":\"" + whidden.isDynamic()
					+ "\" , \"interactive\":\"" + whidden.isInteractive() + "\" }");

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
				throw new ConnectionException	("Error updating hidden settings", Error_consts.ERROR_UPDATING_HIDDEN_SETTINGS);		

			}

			conn.disconnect();
		} catch(java.net.SocketTimeoutException e) {
			throw new ConnectionException	("Error updating hidden settings", Error_consts.ERROR_UPDATING_HIDDEN_SETTINGS * Error_consts.TIMEOUT_FACTOR);		
		}  catch (Exception e) {

			throw new ConnectionException	("Error updating hidden settings", Error_consts.ERROR_UPDATING_HIDDEN_SETTINGS);		
		}

		Bundle bundle = new Bundle();

		if (result.equals("true")) {
			bundle.putBoolean(Consts.HIDDEN_SETTINGS_UPDATED, true);
		} else {
			bundle.putBoolean(Consts.HIDDEN_SETTINGS_UPDATED, false);
		}
		bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_UPDATE_HIDDEN_SETTINGS);
		return bundle;	
	}
}
