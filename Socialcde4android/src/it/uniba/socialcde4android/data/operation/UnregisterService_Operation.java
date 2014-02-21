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
import android.content.Context;
import android.os.Bundle;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;

public class UnregisterService_Operation implements Operation {

	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {


		String username = request.getString(Preferences.USERNAME);
		String password = request.getString(Preferences.PASSWORD);
		int service_id = request.getInt(Consts.SERVICE_ID);
		String host = request.getString(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
		int status =0;
		String result = "";

		try {
			URL url = new URL(host + "/DeleteRegistredService");
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
					+ password + "\" , \"service\":\"" + service_id + "\" }");

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
				throw new ConnectionException("Error ",Error_consts.UNREG_SERVICE_ERROR);

			}

			conn.disconnect();
		} catch(java.net.SocketTimeoutException e) {

			throw new ConnectionException("Error ",Error_consts.UNREG_SERVICE_ERROR * Error_consts.TIMEOUT_FACTOR);
		}  catch (Exception e) {
			throw new ConnectionException("Error ",Error_consts.UNREG_SERVICE_ERROR);
		}

		Bundle bundle = new Bundle();

		if (result.equals("true")) {
			bundle.putBoolean(Consts.UNREGISTERED_SERVICE, true);
			bundle.putInt(Consts.SERVICE_ID, service_id);
			bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_UNREG_SERVICE);
			return bundle;		
		} else {
			throw new ConnectionException("Error ",Error_consts.UNREG_SERVICE_ERROR);	

		}



	}

}
