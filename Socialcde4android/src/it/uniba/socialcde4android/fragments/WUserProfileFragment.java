package it.uniba.socialcde4android.fragments;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.picasso.Picasso;

import it.uniba.socialcde4android.R;

import it.uniba.socialcde4android.shared.library.WUser;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link WUserProfileFragment.OnFragmentInteractionListener} interface to
 * handle interaction events. Use the {@link WUserProfileFragment#newInstance}
 * factory method to create an instance of this fragment.
 * 
 */
public class WUserProfileFragment extends Fragment implements OnRefreshListener<ListView> {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_WUSER = "wuser";
	private ImageView userImage;
	private TextView userNameText;
	private TextView postsText;
	private TextView followersText;
	private TextView followingText;
	private CheckBox followCheckBox;
	private PullToRefreshListView listView;
	private ArrayList<String> mListItems;
	private ArrayAdapter<String> mAdapter;
	private static final String TAG = WUserProfileFragment.class.getSimpleName();

	
	// TODO: Rename and change types of parameters
	private WUser wuser;
	//private String mParam2;

	private OnProfileFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment WUserProfileFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static WUserProfileFragment newInstance(WUser wuser) {
		WUserProfileFragment fragment = new WUserProfileFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_WUSER, wuser);
	//	args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public String getTAG(){
		return TAG;
	}
	
	public WUserProfileFragment() {
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
		View view = inflater.inflate(R.layout.fragment_wuser_profile, container,	false);
		
		userImage = (ImageView) view.findViewById(R.id.imageViewFrament);
		userNameText = (TextView) view.findViewById(R.id.textViewUsernameFragment);
		postsText = (TextView) view.findViewById(R.id.textViewPostsFragment);
		followersText = (TextView) view.findViewById(R.id.textViewFollowersFragment);
		followingText = (TextView) view.findViewById(R.id.textViewFollowingFragment);
		followCheckBox = (CheckBox) view.findViewById(R.id.checkBoxFollowFragment);
		listView = (PullToRefreshListView) view.findViewById(R.id.listView1);
		mListItems = new ArrayList<String>(Arrays.asList(mStrings));
		
		mAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, mListItems);
		listView.setAdapter(mAdapter);
		listView.setOnRefreshListener(this);
		
		String avatar_address = wuser.getAvatar();
		if (avatar_address != null){
			Picasso.with(this.getActivity()).load(avatar_address).into(userImage);
		}
		userNameText.setText(wuser.getUsername());
		postsText.setText("Posts: "+wuser.getStatuses());
		followersText.setText("Followers: "+wuser.getFollowers());
		followingText.setText("Following: "+wuser.getFollowings());
		followCheckBox.setChecked(wuser.Followed);
		followCheckBox.setOnClickListener(new CheckBox.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				onFollowCheckBoxClicked(followCheckBox.isChecked());
			}

		});
		
		
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onFollowCheckBoxClicked(Boolean checked) {
		if (mListener != null) {
			mListener.onProfileFragmentCheckBoxChanged(checked, wuser);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnProfileFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnProfileFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onProfileFragmentCheckBoxChanged(Boolean followChecked, WUser wuser_profile);
	}

	
	private String[] mStrings = { "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			"Allgauer Emmentaler", "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			"Allgauer Emmentaler" };

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		new GetDataTask().execute();
		
	}
	
	
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {
			mListItems.add(0, "Added after refresh...");
			mAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			listView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
}
