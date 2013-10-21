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

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
//import com.google.gson.Gson;
import com.google.gson.Gson;

public class RetrieveServices_Operation implements Operation {

	//private static final String TAG = RetrieveServices_Operation.class.getSimpleName();

	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {

		String userName = request.getString(Preferences.USERNAME);
		String password = request.getString(Preferences.PASSWORD);
		String host = request.getString(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
		int status =0;
		WService[] wservice = null;
		try {
			URL url = new URL(host + "/GetServices");
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
			writer.write("{ \"username\":\"" + userName + "\", \"password\":\""
					+ password + "\"}");

			writer.close();
			out.close();
			status = conn.getResponseCode();
			Log.i("operatioooooooooooooooooooon", String.valueOf(status));
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

				wservice = new WService[countOccurrences(result, '{')];
				Gson gson = new Gson();
				wservice = gson.fromJson(result, WService[].class);
			}

			conn.disconnect();
		} catch(java.net.SocketTimeoutException e) {
			
			wservice = null;
			
		} catch (Exception e) {
			
			wservice = null;
		}

		Bundle bundle = new Bundle();
		bundle.putInt(Consts.STAUS_WSERVICESREQUEST, status);

		if (wservice != null && wservice.length>0){
			bundle.putParcelableArray(Consts.WSERVICES, wservice);
			bundle.putBoolean(Consts.FOUND_WSERVICES, true);
		}else{
			bundle.putBoolean(Consts.FOUND_WSERVICES, false);
		}
		bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_RETRIEVESERVICES);
		return bundle;

	}


	private static int countOccurrences(String haystack, char needle) {
		int count = 0;
		for (int i = 0; i < haystack.length(); i++) {
			if (haystack.charAt(i) == needle) {
				count++;
			}
		}

		if (count == 0) {
			count += 1;
		}

		return count;
	}

}
