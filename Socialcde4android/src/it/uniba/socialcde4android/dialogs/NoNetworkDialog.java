package it.uniba.socialcde4android.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.provider.Settings;

public class NoNetworkDialog extends DialogFragment {
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    	AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setMessage("Internet Connection Required..");
		builder.setCancelable(false);
		builder.setPositiveButton("Open Settings",new OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				//ACTION_WIRELESS_SETTINGS
				//ACTION_NETWORK_OPERATOR_SETTINGS
				Intent intent=new Intent(Settings.ACTION_SETTINGS);
				startActivity(intent);
				dialog.cancel();
			}
		})
		.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		//builder.show();
        // Create the AlertDialog object and return it
        return builder.create();
    }
}