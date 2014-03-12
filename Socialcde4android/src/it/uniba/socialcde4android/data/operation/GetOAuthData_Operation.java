package it.uniba.socialcde4android.data.operation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import it.uniba.socialcde4android.config.Config;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.costants.Error_consts;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WOAuthData;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.google.gson.Gson;

public class GetOAuthData_Operation implements Operation{

	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {

		String username = request.getString(Preferences.USERNAME);
		String password = request.getString(Preferences.PASSWORD);
		String service = request.getString(Consts.SERVICE_ID);
		String host = request.getString(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
		WOAuthData woutAuthData = null;
		try {
			URL url = new URL(host + "/GetOAuth1Data");
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
					+ password + "\" , \"service\":\"" + service + "\" }");

			writer.close();
			out.close();
			int status = conn.getResponseCode();

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
				woutAuthData = gson.fromJson(result, WOAuthData.class);
			}else{
				throw new ConnectionException	("Error retrieving services", Error_consts.ERROR_RETRIEVING_SERVICES);		

			}

			conn.disconnect();

		} catch(java.net.SocketTimeoutException e) {
			throw new ConnectionException	("Error retrieving services", Error_consts.ERROR_GET_OAUTHDATA * Error_consts.TIMEOUT_FACTOR);		
		}  catch (Exception e) {
			throw new ConnectionException	("Error retrieving services", Error_consts.ERROR_GET_OAUTHDATA);		
		}
		
		Bundle bundle = new Bundle();
Log.i("woutdata",woutAuthData.toString());
		bundle.putParcelable(Consts.OAUTH_DATA, woutAuthData);
		bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_GETOAUTDATA);
		bundle.putString(Consts.SERVICE_ID, service);
		bundle.putInt(Consts.OAUTH_VERSION, Integer.valueOf(request.getString(Consts.OAUTH_VERSION)));

		
		return bundle;
	}

}
