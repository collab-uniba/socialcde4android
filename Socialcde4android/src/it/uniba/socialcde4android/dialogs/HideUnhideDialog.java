package it.uniba.socialcde4android.dialogs;

import java.util.Arrays;


import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.adapters.FeaturesAdapter;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WFeature;
import it.uniba.socialcde4android.shared.library.WHidden;
import it.uniba.socialcde4android.shared.library.WService;
import it.uniba.socialcde4android.shared.library.WUser;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HideUnhideDialog extends DialogFragment{

	private static final String ARG_WHIDDEN = "arg whidden";
	private static final String ARG_USERID = "arg user id";
	private WHidden whidden;
	private int user_id;
	private OnHideHunideListener mListener;
	private CheckBox suggestions;
	private CheckBox iteration;
	private CheckBox interactive;
	
	
	public static HideUnhideDialog newInstance(WHidden whidden, int user_id) {
		HideUnhideDialog dialog = new HideUnhideDialog();
		Bundle args = new Bundle();
		args.putParcelable(ARG_WHIDDEN, whidden);
		args.putInt(ARG_USERID, user_id);
		dialog.setArguments(args);
		return dialog;
	}

	public HideUnhideDialog(){
		
	}
	
	public interface OnHideHunideListener{
		public void setHideSettings(WHidden whidden, int user_id);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog); 
		if (getArguments() != null) {
			whidden = getArguments().getParcelable(ARG_WHIDDEN);
			user_id = getArguments().getInt(ARG_USERID);
		}
		mListener = (OnHideHunideListener) getActivity();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_hide_unhide, container, false);
      
        Button save_button = (Button) v.findViewById(R.id.button2_dialog_OKHIDEUNHIDE);
    	Button cancel_button = (Button) v.findViewById(R.id.button1_dialog_CANCELHIDEUNIDE);
    	suggestions = (CheckBox) v.findViewById(R.id.checkBox1SUGGESTIONS);
    	iteration = (CheckBox) v.findViewById(R.id.checkBox3ITERATION);
    	interactive = (CheckBox) v.findViewById(R.id.checkBox2INTERACTIVE);
    	
    	suggestions.setChecked(whidden.isSuggestions());
    	iteration.setChecked(whidden.isDynamic());
    	interactive.setChecked(whidden.isInteractive());
    	
    	save_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//se è cambiato almeno un valore allora chiama il metodo per salvare i nuovi valori
				if (!(suggestions.isChecked() == whidden.isSuggestions() && iteration.isChecked() == whidden.isDynamic() 
						&& interactive.isChecked() == whidden.isInteractive())){
					WHidden newWhidden = new WHidden();
					newWhidden.setSuggestions(suggestions.isChecked());
					newWhidden.setDynamic(iteration.isChecked());
					newWhidden.setInteractive(interactive.isChecked());
					mListener.setHideSettings(newWhidden, user_id);
				}
				HideUnhideDialog.this.dismiss();
			}
		});
    	
    	cancel_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				HideUnhideDialog.this.dismiss();
			}
		});
        return v;
    }


	

	
	
}
	

