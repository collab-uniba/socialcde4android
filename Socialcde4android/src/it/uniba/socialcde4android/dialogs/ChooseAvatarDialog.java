package it.uniba.socialcde4android.dialogs;



import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.adapters.UserAvatarsAdapter;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class ChooseAvatarDialog extends DialogFragment{

	private static final String ARG_AVATARURLS = "avatar URLs";
	private static final String ARG_OLD_AVATARURLS = "old avatar URLs";

	private String[] avatarURLs;
	private String old_uri;
	private OnChooseAvatarListener mListener;
	private GridView imagesGridView;

	public static ChooseAvatarDialog newInstance(String[] uri, String old_uri) {
		ChooseAvatarDialog dialog = new ChooseAvatarDialog();
		Bundle args = new Bundle();
		args.putStringArray(ARG_AVATARURLS, uri);
		args.putString(ARG_OLD_AVATARURLS, old_uri);
		dialog.setArguments(args);
		return dialog;
	}

	
	
	public ChooseAvatarDialog(){

	}

	public interface OnChooseAvatarListener{
		public void setAvatar(String uri);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog); 
		if (getArguments() != null) {
				avatarURLs = getArguments().getStringArray(ARG_AVATARURLS);
				old_uri= getArguments().getString(ARG_OLD_AVATARURLS);
		}
		mListener = (OnChooseAvatarListener) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
		View v = null;

		v = inflater.inflate(R.layout.dialog_choose_avatar, container, false);

		imagesGridView = (GridView) v.findViewById(R.id.gridViewGALLERY);
		UserAvatarsAdapter avatarAdapter = new UserAvatarsAdapter(getActivity(), android.R.layout.simple_list_item_1, avatarURLs);
		imagesGridView.setAdapter(avatarAdapter);
		imagesGridView.setOnItemClickListener(new GridView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				String uri = (String) imagesGridView.getAdapter().getItem(position);
				//adesso richiamo dall'activity il metodo per settare il nuovo indirizzo
				if (!uri.equals(old_uri)){
				mListener.setAvatar(uri);
				}else{
					Toast.makeText(ChooseAvatarDialog.this.getActivity(), "No Avatar change is required."  , Toast.LENGTH_SHORT).show();
				}
				ChooseAvatarDialog.this.dismiss();
			}

			
		});

		return v;
	}


}
