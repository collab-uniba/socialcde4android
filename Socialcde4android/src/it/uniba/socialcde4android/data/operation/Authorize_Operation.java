package it.uniba.socialcde4android.data.operation;

import it.uniba.socialcde4android.config.Config;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.costants.Error_consts;
import it.uniba.socialcde4android.preferences.Preferences;

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

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;

public class Authorize_Operation implements Operation {

	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {
		String username = request.getString(Preferences.USERNAME);
		String password = request.getString(Preferences.PASSWORD);
		String host = request.getString(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
		String service = request.getString(Consts.SERVICE_ID);
		String accessToken = request.getString(Consts.ACCESS_TOKEN);
		String verifier = request.getString(Consts.VERIFIER);
		String accessSecret = request.getString(Consts.ACCESS_SECRET);
		String result = "";
		try {
			URL url = new URL(host + "/Authorize");
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
					+ password + "\" , \"service\":\"" + service
					+ "\", \"verifier\":\"" + verifier
					+ "\", \"accessToken\": \"" + accessToken
					+ "\", \"accessSecret\":\"" + accessSecret + "\"}");
			writer.close();
			out.close();
			int status = conn.getResponseCode();

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
				throw new ConnectionException("Error ",Error_consts.AUTHORIZE_ERROR);

			}

			conn.disconnect();
		} catch(java.net.SocketTimeoutException e) {
			
			throw new ConnectionException("Error ",Error_consts.AUTHORIZE_ERROR * Error_consts.TIMEOUT_FACTOR);
		}  catch (Exception e) {
			throw new ConnectionException("Error ",Error_consts.AUTHORIZE_ERROR);
		}
		Bundle bundle = new Bundle();

		if (result.equals("true")) {
			bundle.putBoolean(Consts.AUTHORIZED, true);
			bundle.putInt(Consts.SERVICE_ID,Integer.valueOf(service));
			bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_AUTHORIZE);
			return bundle;	
		} else {
			throw new ConnectionException("Error ",Error_consts.AUTHORIZE_ERROR);
		}
	}

}
