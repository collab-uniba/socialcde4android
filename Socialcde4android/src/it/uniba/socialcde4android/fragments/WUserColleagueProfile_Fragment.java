package it.uniba.socialcde4android.fragments;

import com.squareup.picasso.Picasso;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.dialogs.ChangePasswordDialog;
import it.uniba.socialcde4android.dialogs.HideUnhideDialog;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WHidden;
import it.uniba.socialcde4android.shared.library.WUser;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


public class WUserColleagueProfile_Fragment extends TimeLine_AbstractFragment{

	private static final String ARG_WUSER = "wuser";
	private WUser wuser;
	private ImageView userImage;
	private TextView userNameText;
	private TextView postsText;
	private TextView followersText;
	private TextView followingText;
	private CheckBox followCheckBox;
	private Button hideButton;
	private OnProfileFragmentInteractionListener mListenerWUser;

	@Override
	public int getFragmentViewId() {
		return R.layout.fragment_wusercolleague_profile;
	}

	public static WUserColleagueProfile_Fragment newInstance(WUser wuser) {
		WUserColleagueProfile_Fragment fragment = new WUserColleagueProfile_Fragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_WUSER, wuser);
		//	args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public WUserColleagueProfile_Fragment() {
		//super();
		// Required empty public constructor
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			wuser = getArguments().getParcelable(ARG_WUSER);
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = super.onCreateView(inflater, container, savedInstanceState);

		userImage = (ImageView) view.findViewById(R.id.imageViewFrament);
		userNameText = (TextView) view.findViewById(R.id.textViewUsernameFragment);
		postsText = (TextView) view.findViewById(R.id.textViewPostsFragment);
		followersText = (TextView) view.findViewById(R.id.textViewFollowersFragment);
		followingText = (TextView) view.findViewById(R.id.textViewFollowingFragment);
		followCheckBox = (CheckBox) view.findViewById(R.id.checkBoxFollowFragment);
		hideButton = (Button) view.findViewById(R.id.buttonHideUnide);


		String avatar_address = wuser.getAvatar();
		
		hideButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	mListenerWUser.loadUserHideSettings(wuser.getId());
		    }
		});
		
		
		if (avatar_address != null){
			Picasso.with(this.getActivity()).load(avatar_address).into(userImage);
		}
		userNameText.setText(wuser.getUsername());
		postsText.setText("Posts: "+wuser.getStatuses());
		followersText.setText("Followers: "+wuser.getFollowers());
		followingText.setText("Following: "+wuser.getFollowings());
		followCheckBox.setChecked(wuser.isFollowed());
		followCheckBox.setOnClickListener(new CheckBox.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				onFollowCheckBoxClicked(followCheckBox.isChecked());
			}
		});
		
		return view;
	}

	public void changeCheckBoxState(){
		followCheckBox.setChecked(!followCheckBox.isChecked()) ;
	}

	public void onFollowCheckBoxClicked(Boolean checked) {
		if (mListenerWUser != null) {
			mListenerWUser.onProfileFragmentCheckBoxChanged(checked, wuser);
		}
	}

	public interface OnProfileFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onProfileFragmentCheckBoxChanged(Boolean followChecked, WUser wuser_profile);
		
		public void loadUserHideSettings(int userId);
	}

//	public void loadDialogHideUnhide(WHidden widden){
//		//richiamare la dialog impostando i checkbox con i valori di hidden
//		HideUnhideDialog hideUnhide_dialog = HideUnhideDialog.newInstance(widden);
//		hideUnhide_dialog.show(getFragmentManager(), "Change Hide Settings");
//	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListenerWUser = (OnProfileFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnProfileFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListenerWUser = null;
	}


	@Override
	public void onPause() {
		super.onPause();
		if (getDataTask != null ) 	getDataTask.cancel(true);
		//if (getMoreDataTask != null )getMoreDataTask.cancel(true);
	}

	@Override
	public String getRequestType() {
		return "/GetUserTimeline";
	}

	@Override
	public String getRequest(int datatype) {
		long since = 0;
		long to = 0 ;
		if (datatype == super.GET_MOREDATA_TYPE)
			to = (mListWpostItems.get(mListWpostItems.size()-1)).getId();
		return "{ \"username\":\"" + username + "\", \"password\":\""
		+ password + "\" , \"ownerName\":\"" + wuser.getUsername()
		+ "\" , \"since\":\"" + since + "\" , \"to\":\"" + to
		+ "\"}";
	}

	@Override
	protected Boolean getClickable() {
		return false;
	}

	@Override
	public void openUserProfileFromActivity(WUser wuser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Fragment getFragment() {
		return this;
	}



}