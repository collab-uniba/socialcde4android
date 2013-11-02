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

import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WService;
import it.uniba.socialcde4android.shared.library.WUser;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
//import com.google.gson.Gson;
import com.google.gson.Gson;

public class SetFollowed_Operation implements Operation {

	private static final String TAG = RetrieveServices_Operation.class.getSimpleName();

	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {

		String username = request.getString(Preferences.USERNAME);
		String password = request.getString(Preferences.PASSWORD);
		String host = request.getString(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
		int colleague_id = request.getInt(Consts.COLLEAGUE_ID);
		Boolean follow = request.getBoolean(Consts.BOOLEAN_FOLLOW);
		int status =0;
		String result = "";


		try {

			if (colleague_id != -1) {
				URL url = new URL(host + (follow? "/Follow" : "/UnFollow"));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(20000);
				conn.setReadTimeout(25000);
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
						+ "\", \"password\":\"" + password
						+ "\" , \"followId\":\"" + colleague_id + "\"}");

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
					throw new ConnectionException("Error ",Consts.SETFOLLOWED_ERROR);

				}

				conn.disconnect();
			}
		}catch(java.net.SocketTimeoutException e) {
			status = Consts.TIMEOUT_STATUS;
			throw new ConnectionException("Error ",Consts.SETFOLLOWED_ERROR);
		}  catch (Exception e) {
			throw new ConnectionException("Error ",Consts.SETFOLLOWED_ERROR);
		}

		if (colleague_id == -1) {
			throw new ConnectionException("Error ",Consts.SETFOLLOWED_ERROR);
		}

		if (result.equals("true")) {
			Bundle bundle = new Bundle();
			bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_SET_FOLLOWED);
			return bundle;		
		} else {
			throw new ConnectionException("Error ",Consts.SETFOLLOWED_ERROR);
		}

	}

}
