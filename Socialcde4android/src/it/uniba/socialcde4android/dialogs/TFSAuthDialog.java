package it.uniba.socialcde4android.dialogs;


import com.squareup.picasso.Picasso;

import it.uniba.socialcde4android.R;

import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WService;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TFSAuthDialog extends DialogFragment{

	private static final String ARG_WSERV = "arg wservice";
	private WService wservice;
	private OnTFSAuthInteractionListener mListener;
	private EditText username;
	private EditText password;
	private EditText domain = null;
	
	public static TFSAuthDialog newInstance( WService wService) {
		TFSAuthDialog dialog = new TFSAuthDialog();
		Bundle args = new Bundle();
		args.putParcelable(ARG_WSERV, wService);
		dialog.setArguments(args);
		return dialog;
	}

	public TFSAuthDialog(){
		
	}
	
	public interface OnTFSAuthInteractionListener{

		public void recordService(String service_id, String username, String password, String domain);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog); 
		if (getArguments() != null) {
			wservice = getArguments().getParcelable(ARG_WSERV);
		}
		mListener = (OnTFSAuthInteractionListener) getActivity();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
		View v = null;
		if (wservice.isRequireTFSDomain()){
			v = inflater.inflate(R.layout.dialog_service_domain, container, false);
			domain = (EditText)  v.findViewById(R.id.editTextdialogDomain);
		}else{
			v = inflater.inflate(R.layout.dialog_service, container, false);
		}
        //getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        ImageView imageviewService = (ImageView) v.findViewById(R.id.imageView1_service_LOGO); 
		Picasso.with(v.getContext()).load(Preferences.getSavedHost(getActivity())+wservice.getImage()).into(imageviewService);

		//imageviewService.setImageResource(getActivity().getResources().getIdentifier("it.uniba.socialcde4android:drawable/"+wservice.getImage().replace("/Images/", "").replace(".png", ""),null,null));
        TextView title = (TextView) v.findViewById(R.id.textView1_title_service_SERVICE);
        username = (EditText)  v.findViewById(R.id.editTextdialogName);
        password = (EditText)  v.findViewById(R.id.editTextDialogPassword);
        username.requestFocus();
        title.setText(wservice.getName());
        Button ok_button = (Button) v.findViewById(R.id.button2_dialog_service_OK);
    	Button cancel_button = (Button) v.findViewById(R.id.button1_dialog_service_CANCEL);
    	
    	ok_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.recordService(String.valueOf(wservice.getId()), username.getText().toString(), password.getText().toString(), (domain == null) ? null : domain.getText().toString());
				TFSAuthDialog.this.dismiss();
			}
		});
    	
    	cancel_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				TFSAuthDialog.this.dismiss();
			}
		});
        return v;
    }

		
}
