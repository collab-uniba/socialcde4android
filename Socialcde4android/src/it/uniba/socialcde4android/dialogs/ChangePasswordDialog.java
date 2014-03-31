package it.uniba.socialcde4android.dialogs;



import it.uniba.socialcde4android.R;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordDialog extends DialogFragment{

	private static final String ARG_PASSW = "old passw";
	private String old_password;
	private OnChangePasswordListener mListener;
	private EditText old_password_edit;
	private EditText new_password_edit;
	private EditText repeat_password_edit;
	
	public static ChangePasswordDialog newInstance( String password) {
		ChangePasswordDialog dialog = new ChangePasswordDialog();
		Bundle args = new Bundle();
		args.putString(ARG_PASSW, password);
		dialog.setArguments(args);
		return dialog;
	}

	public ChangePasswordDialog(){
		
	}
	
	public interface OnChangePasswordListener{
		//metodo per cambiare la password
		public void change_password(String new_password);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog); 
		if (getArguments() != null) {
			old_password = getArguments().getString(ARG_PASSW);
		}
		mListener = (OnChangePasswordListener) getActivity();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
		View v = null;
	
		v = inflater.inflate(R.layout.dialog_change_password, container, false);
			
        //getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
		old_password_edit = (EditText)  v.findViewById(R.id.editTextdialogOLDPASSWORD);
		new_password_edit = (EditText)  v.findViewById(R.id.editTextDialogNEWPassword);
		repeat_password_edit = (EditText)  v.findViewById(R.id.editTextdialogRepeatPASSWORD);
		old_password_edit.requestFocus();

        Button ok_button = (Button) v.findViewById(R.id.button2_dialog_OKCHANGEPASS);
    	Button cancel_button = (Button) v.findViewById(R.id.button1_dialog_CANCELCHANGEPASS);
    	
    	ok_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				//controlla che la oldpassword sia giusta
				//poi controlla che le nuove password siano uguali tra loro 
				if (old_password_edit.getText().toString().equals("") || new_password_edit.getText().toString().equals("") || repeat_password_edit.getText().toString().equals("") ){
					Toast.makeText(ChangePasswordDialog.this.getActivity(), "Please compile all the fields."  , Toast.LENGTH_SHORT).show();
				}else if (!old_password_edit.getText().toString().equals(old_password)){
					Toast.makeText(ChangePasswordDialog.this.getActivity(), "Please check your old password."  , Toast.LENGTH_SHORT).show();
					Animation shake = AnimationUtils.loadAnimation(ChangePasswordDialog.this.getActivity(), R.anim.shake);
					old_password_edit.startAnimation(shake);
					old_password_edit.setText("");
				}else if (!new_password_edit.getText().toString().equals(repeat_password_edit.getText().toString())){
					Toast.makeText(ChangePasswordDialog.this.getActivity(), "Passwords do not match."  , Toast.LENGTH_SHORT).show();
					Animation shake = AnimationUtils.loadAnimation(ChangePasswordDialog.this.getActivity(), R.anim.shake);
					new_password_edit.startAnimation(shake);
					repeat_password_edit.startAnimation(shake);
					new_password_edit.setText("");
					repeat_password_edit.setText("");
				}else{
					//può cambiare la password
					mListener.change_password(new_password_edit.getText().toString());
					ChangePasswordDialog.this.dismiss();
				}
			}
		});
    	
    	cancel_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				ChangePasswordDialog.this.dismiss();
			}
		});
        return v;
    }

		
}
