package it.uniba.socialcde4android.fragments;

import com.squareup.picasso.Picasso;
import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WUser;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


public class WUserProfile_Fragment extends TimeLine_AbstractFragment{

	private static final String ARG_WUSER = "wuser";
	private WUser wuser;
	private ImageView userImage;
	private TextView userNameText;
	private TextView postsText;
	private TextView followersText;
	private TextView followingText;
	//private CheckBox followCheckBox;

	@Override
	public int getFragmentViewId() {
		return R.layout.fragment_wuser_profile;
	}

	public static WUserProfile_Fragment newInstance(WUser wuser) {
		WUserProfile_Fragment fragment = new WUserProfile_Fragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_WUSER, wuser);
		//	args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public WUserProfile_Fragment() {
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

		userImage = (ImageView) view.findViewById(R.id.imageViewFrament2);
		userNameText = (TextView) view.findViewById(R.id.textViewUsernameFragment2);
		postsText = (TextView) view.findViewById(R.id.textViewPostsFragment2);
		followersText = (TextView) view.findViewById(R.id.textViewFollowersFragment2);
		followingText = (TextView) view.findViewById(R.id.textViewFollowingFragment2);
		


		String avatar_address = wuser.getAvatar();
		if (avatar_address != null){
			Picasso.with(this.getActivity()).load(avatar_address).into(userImage);
		}
		userNameText.setText(wuser.getUsername());
		postsText.setText("Posts: "+wuser.getStatuses());
		followersText.setText("Followers: "+wuser.getFollowers());
		followingText.setText("Following: "+wuser.getFollowings());
		
		return view;
	}

	

	@Override
	public void onPause() {
		super.onPause();
		if (getDataTask != null ) 	getDataTask.cancel(true);
	//	if (getMoreDataTask != null )getMoreDataTask.cancel(true);
	}

	@Override
	public String getRequestType() {
		return "/GetUserTimeline";
	}

	@Override
	public String getRequest(int datatype) {
		String username = preferences.get(Preferences.USERNAME);
		String password = preferences.get(Preferences.PASSWORD);
		long since = 0;
		long to = 0 ;
		if (datatype == super.GET_MOREDATA_TYPE)
			to = (mListWpostItems.get(mListWpostItems.size()-1)).getId();
		return "{ \"username\":\"" + username + "\", \"password\":\""
		+ password + "\" , \"ownerName\":\"" + wuser.getUsername()
		+ "\" , \"since\":\"" + since + "\" , \"to\":\"" + to
		+ "\"}";
	}



}