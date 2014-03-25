package it.uniba.socialcde4android.dialogs;



import java.util.Arrays;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.adapters.UserAvatarsAdapter;
import it.uniba.socialcde4android.costants.Consts;


import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class ChooseAvatarDialog extends DialogFragment{

	private static final String ARG_AVATARURLS = "avatar URLs";
	private String[] avatarURLs;
	private OnChooseAvatarListener mListener;
	private GridView imagesGridView;

	public static ChooseAvatarDialog newInstance(String[] uri) {
		ChooseAvatarDialog dialog = new ChooseAvatarDialog();
		Bundle args = new Bundle();
		args.putStringArray(ARG_AVATARURLS, uri);
		dialog.setArguments(args);
		return dialog;
	}

	
	
	public ChooseAvatarDialog(){

	}

	public interface OnChooseAvatarListener{
		//
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog); 
		if (getArguments() != null) {
				avatarURLs = getArguments().getStringArray(ARG_AVATARURLS);
		}
		mListener = (OnChooseAvatarListener) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
		View v = null;

		v = inflater.inflate(R.layout.dialog_choose_avatar, container, false);

//		Button ok_button = (Button) v.findViewById(R.id.button2_dialog_OKGALLERY);
//		Button cancel_button = (Button) v.findViewById(R.id.button1_dialog_CANCELGALLERY);
		imagesGridView = (GridView) v.findViewById(R.id.gridViewGALLERY);
		

		UserAvatarsAdapter avatarAdapter = new UserAvatarsAdapter(getActivity(), android.R.layout.simple_list_item_1, avatarURLs);
		imagesGridView.setAdapter(avatarAdapter);
//		ok_button.setOnClickListener(new Button.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				ChooseAvatarDialog.this.dismiss();
//			}
//		});
//
//		cancel_button.setOnClickListener(new Button.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				ChooseAvatarDialog.this.dismiss();
//			}
//		});
		return v;
	}


}
