package it.uniba.socialcde4android.data.operation;

import it.uniba.socialcde4android.config.Config;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.costants.Error_consts;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WFeature;
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

public class GetHideSettings_Operation implements Operation{
	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {


		String username = request.getString(Preferences.USERNAME);
		String password = request.getString(Preferences.PASSWORD);
		String host = request.getString(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
		int user_id = request.getInt(Consts.USERID);
		int status =0;


		WHidden whidden = null;


		try {
			URL url = new URL(host + "/GetUserHideSettings");
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
					+ user_id + "\"}");

			writer.close();
			out.close();
			status = conn.getResponseCode();

			if (status >= 200 && status <= 299) {
				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());
				BufferedReader br = new BufferedReader(in);
				String output;
				String result = "";
				while ((output = br.readLine()) != null) {
					result += output;

				}
				br.close();

				Gson gson = new Gson();
				whidden = gson.fromJson(result, WHidden.class);
			}else{
				throw new ConnectionException	("Error retrieving hidden settings", Error_consts.ERROR_RETRIEVING_HIDDEN_SETTINGS);		

			}

			conn.disconnect();
		} catch(java.net.SocketTimeoutException e) {
			throw new ConnectionException	("Error retrieving hidden settings", Error_consts.ERROR_RETRIEVING_HIDDEN_SETTINGS * Error_consts.TIMEOUT_FACTOR);		
		}  catch (Exception e) {

			throw new ConnectionException	("Error retrieving hidden settings", Error_consts.ERROR_RETRIEVING_HIDDEN_SETTINGS);		
		}

		Bundle bundle = new Bundle();

		if (whidden != null){
			bundle.putParcelable(Consts.WHIDDEN, whidden);
			bundle.putBoolean(Consts.FOUND_HIDDEN_SETTINGS, true);
			bundle.putInt(Consts.USERID, user_id);
			//bundle.putInt(Consts.SERVICE_ID, service_id);
		}else{
			bundle.putBoolean(Consts.FOUND_HIDDEN_SETTINGS, false);
		}
		bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_RETRIEVEHIDESETTINGS);
		return bundle;
	}
}
