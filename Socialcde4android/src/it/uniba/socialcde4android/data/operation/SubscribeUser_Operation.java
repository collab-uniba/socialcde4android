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
import android.util.Log;
import it.uniba.socialcde4android.config.Config;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.costants.Error_consts;
import it.uniba.socialcde4android.preferences.Preferences;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
//import com.google.gson.Gson;

public class SubscribeUser_Operation implements Operation {

	//private static final String TAG = RetrieveServices_Operation.class.getSimpleName();

	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {
		String mail = request.getString(Consts.MAIL);

		String userName = request.getString(Preferences.USERNAME);
		String password = request.getString(Consts.INVIT_CODE);
		String host = request.getString(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
		int status = 0;
		int response = -1;
		String result = "";
		try {
			URL url = new URL(host + "/SubscribeUser");
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
			writer.write("{ \"email\":\"" + mail + "\" ,\"username\":\""
					+ userName + "\", \"password\":\"" + password + "\"}");
			Log.i("stauscode registration " , "{ \"email\":\"" + mail + "\" ,\"username\":\""
					+ userName + "\", \"password\":\"" + password + "\"}");

			writer.close();
			out.close();
			status = conn.getResponseCode();
Log.i("stauscode registration " , String.valueOf(status));
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
				throw new ConnectionException("Connection error",Error_consts.ERROR_SUBSCRIBE_USER);
			}
			conn.disconnect();
		}catch(java.net.SocketTimeoutException e) {
			throw new ConnectionException("Connection error",Error_consts.ERROR_SUBSCRIBE_USER * Error_consts.TIMEOUT_FACTOR);

		} catch (Exception e) {
			throw new ConnectionException("Connection error",Error_consts.ERROR_SUBSCRIBE_USER);
		}
		response = Integer.parseInt(result);
		Bundle bundle = new Bundle();
		bundle.putInt(Consts.SUBSCRIPTION_RESPONSE, response);
		bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_SUBSCRIBEUSER);
		return bundle;

	}



}
