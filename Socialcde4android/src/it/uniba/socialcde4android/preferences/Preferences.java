package it.uniba.socialcde4android.preferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	
	private static final String USERINFO = "socialTFSuser_info";
	public static final String SAVEDPREF = "SavedPref";
	public static final String SAVEPASS = "Save_pass";
	public static final String AUTOLOG = "AUTO_log";
	public static final String USERNAME = "Username";
	public static final String PASSWORD = "Password";
	public static final String PROXYSERVER = "Proxyserver";


	public static void savePreferences(Activity activity, HashMap<String,String> preferences ){
		SharedPreferences settings = activity.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		Set<String> keySet = preferences.keySet();
		for (String key : keySet){
			editor.putString(key, preferences.get(key));
		}
		//se savepass == false allora salva una password vuota
//		if ((preferences.get(SAVEPASS)).equals(false)){
//			editor.putString(PASSWORD, "");
//		}
		editor.putString(SAVEDPREF,"true");
		editor.commit();
	}
	
	public static void setFalseAutolog(Activity activity){
		SharedPreferences settings = activity.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(AUTOLOG,"false");
		editor.commit();
	}
	
	public static void deletePreferences(Activity activity){
		SharedPreferences settings = activity.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(SAVEDPREF,"false");
		editor.commit();
	}
	
	public static HashMap<String, String> loadPreferences(Activity activity){
		SharedPreferences settings = activity.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		if (settings.getString(SAVEDPREF, "").equals("true")){
			HashMap<String, String> prefMap = new HashMap<String, String>();
			prefMap.put(AUTOLOG, settings.getString(AUTOLOG, ""));
			prefMap.put(USERNAME, settings.getString(USERNAME, ""));
			prefMap.put(PASSWORD, settings.getString(PASSWORD, ""));
			prefMap.put(PROXYSERVER, settings.getString(PROXYSERVER, ""));
			prefMap.put(SAVEPASS, settings.getString(SAVEPASS, ""));

			//non è necessario aggiungere 
			return prefMap;
		}else 	return null;
	}
	
	public static boolean isSavedPreferences(Activity activity){
		SharedPreferences settings = activity.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		if (settings.getString(SAVEDPREF, "").equals("true"))
			return true;
		else 
			return false;
	}
	
	public static String getSavedHost(Activity activity){
		SharedPreferences settings = activity.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
		return settings.getString(PROXYSERVER, "");
	}
	
}
