package it.uniba.socialcde4android.data.operation;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import it.uniba.socialcde4android.config.Config;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.costants.Error_consts;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WUser;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
//import com.google.gson.Gson;
import com.google.gson.Gson;

public class GetFriends_Operation implements Operation {

	//private static final String TAG = RetrieveServices_Operation.class.getSimpleName();

	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {

		String userName = request.getString(Preferences.USERNAME);
		String password = request.getString(Preferences.PASSWORD);
		String host = request.getString(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
		int status =0;

		WUser[] wuser_sugg = new WUser[2];

		try {
			URL url = new URL(host + "/GetSuggestedFriends");
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
			writer.write("{ \"username\":\"" + userName + "\", \"password\":\""
					+ password + "\"}");

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
				wuser_sugg = new WUser[countOccurrences(result, '{')];
				wuser_sugg = gson.fromJson(result, WUser[].class);
			}else {
				throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS);
			}
			conn.disconnect();
		} catch(java.net.SocketTimeoutException e) {
			throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS);
		}catch (Exception e) {
			throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS);
		}


		WUser[] wuser_following = new WUser[2];
		try {
			URL url = new URL(host + "/GetFollowings");
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
			writer.write("{ \"username\":\"" + userName + "\", \"password\":\""
					+ password + "\"}");
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
				wuser_following = new WUser[countOccurrences(result, '{')];
				wuser_following = gson.fromJson(result, WUser[].class);
			}else{
				throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS);

			}

			conn.disconnect();
		}catch(java.net.SocketTimeoutException e) {
			throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS);
		} catch (Exception e) {
			throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS);

		}


		WUser[] wuser_followers = new WUser[2];

		try {
			URL url = new URL(host + "/GetFollowers");
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
			writer.write("{ \"username\":\"" + userName + "\", \"password\":\""
					+ password + "\"}");

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
				wuser_followers = new WUser[countOccurrences(result, '{')];
				wuser_followers = gson.fromJson(result, WUser[].class);
			}else{
				throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS);
			}
			conn.disconnect();
		} catch(java.net.SocketTimeoutException e) {
			throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS);

		} catch (Exception e) {
			throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS);
		}


		WUser[] wuser_hidden = new WUser[2];

		try {
			URL url = new URL(host + "/GetHiddenUsers");
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
			writer.write("{ \"username\":\"" + userName + "\", \"password\":\""
					+ password + "\"}");

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
				wuser_hidden = new WUser[countOccurrences(result, '{')];
				wuser_hidden = gson.fromJson(result, WUser[].class);
			}else{
				throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS);
			}

			conn.disconnect();
		} catch(java.net.SocketTimeoutException e) {
			throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS * Error_consts.TIMEOUT_FACTOR);

		} catch (Exception e) {
			throw new ConnectionException("Error retrieving suggested users",Error_consts.ERROR_GET_FRIENDS);

		}


		int[] wUsersNumType_SuggFingFersHidd = new int[4];
		if (wuser_sugg == null ) wUsersNumType_SuggFingFersHidd[0] = 0; else wUsersNumType_SuggFingFersHidd[0] = wuser_sugg.length;
		if (wuser_following == null ) wUsersNumType_SuggFingFersHidd[1] = 0; else wUsersNumType_SuggFingFersHidd[1] = wuser_following.length;
		if (wuser_followers == null ) wUsersNumType_SuggFingFersHidd[2] = 0; else wUsersNumType_SuggFingFersHidd[2] = wuser_followers.length;
		if (wuser_hidden == null ) wUsersNumType_SuggFingFersHidd[3] = 0; else wUsersNumType_SuggFingFersHidd[3] = wuser_hidden.length;

		ArrayList<WUser> allwusers = new ArrayList<WUser>(); 
		for (int i=0; i<wUsersNumType_SuggFingFersHidd[0];i++ ){	allwusers.add(wuser_sugg[i]);	}
		for (int i=0; i<wUsersNumType_SuggFingFersHidd[1];i++ ){	allwusers.add(wuser_following[i]);	}
		for (int i=0; i<wUsersNumType_SuggFingFersHidd[2];i++ ){	allwusers.add(wuser_followers[i]);	}
		for (int i=0; i<wUsersNumType_SuggFingFersHidd[3];i++ ){	allwusers.add(wuser_hidden[i]);  }

		Bundle bundle = new Bundle();

		if (allwusers != null && allwusers.size()>0){
			bundle.putParcelableArrayList(Consts.WUSERS,allwusers );
			bundle.putIntArray(Consts.WUSERS_NUMBERS, wUsersNumType_SuggFingFersHidd);
			bundle.putBoolean(Consts.FOUND_WUSERS, true);
		}else{
			bundle.putBoolean(Consts.FOUND_WUSERS, false);
		}
		bundle.putInt(Consts.REQUEST_TYPE, Consts.REQUESTTYPE_ALL_USERS);

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
