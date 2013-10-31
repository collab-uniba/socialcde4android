package it.uniba.socialcde4android.utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.Surface;
import android.view.WindowManager;

public class ScreenUtility {
	
	public static void lockScreenOrientation(Activity activity){
		 int orientation = activity.getRequestedOrientation();
		    int rotation = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
		    switch (rotation) {
		    case Surface.ROTATION_0:
		        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		        break;
		    case Surface.ROTATION_90:
		        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		        break;
		    case Surface.ROTATION_180:
		        orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
		        break;
		    default:
		        orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
		        break;
		    }

		    activity.setRequestedOrientation(orientation);
	}

}
