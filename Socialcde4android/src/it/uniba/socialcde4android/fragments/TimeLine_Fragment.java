package it.uniba.socialcde4android.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import it.uniba.socialcde4android.R;

import it.uniba.socialcde4android.shared.library.WPost;
import it.uniba.socialcde4android.shared.library.WUser;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Use the
 * {@link TimeLine_Fragment#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
public class TimeLine_Fragment extends TimeLine_AbstractFragment {
	
	private static final String TAG = TimeLine_AbstractFragment.class.getSimpleName();


	private static final String EDIT_SHOWING = "edit_showing";

	private ImageButton buttonWriteMessage;
	private ImageButton buttonCancelEditText;
	private ImageButton sendTFSPost;
	private EditText editTextMessage;
	private LinearLayout editTextLayout;
	private Boolean isEditShowing = false;
	private static final String ARG_PASSWORD = "password";



	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * @param passw_string 
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment BlankFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static TimeLine_Fragment newInstance(String passw_string) {
		
		TimeLine_Fragment fragment = new TimeLine_Fragment();
		Bundle args = new Bundle();
		args.putString(ARG_PASSWORD, passw_string);
		fragment.setArguments(args);
		return fragment;
		
	}

	public TimeLine_Fragment() {
	//	super();
		// Required empty public constructor
	}

	
	@Override
	public String getTAG(){
		return TAG;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			password = getArguments().getString(ARG_PASSWORD);

		}
	}

	@Override
	public int getFragmentViewId() {
		return R.layout.fragment_home_time_line_;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView( inflater,  container, savedInstanceState);
		pullListView.setOnRefreshListener(TimeLine_Fragment.this);

		buttonWriteMessage = (ImageButton)view.findViewById(R.id.buttonEditTextHOMETIMELINE);
		buttonCancelEditText = (ImageButton)view.findViewById(R.id.imageButtonCancelEditTextTIMELINE);
		sendTFSPost = (ImageButton) view.findViewById(R.id.imageButton2);
		editTextMessage = (EditText) view.findViewById(R.id.editTextMessageHOMETIMELINE);
		editTextLayout = (LinearLayout) view.findViewById(R.id.layout_edittext_TIMELINE);
		editTextLayout.setVisibility(View.GONE);

		buttonWriteMessage.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				isEditShowing = true;
				//nascondi il tasto e visualizza l'edittext
				buttonWriteMessage.setVisibility(View.GONE);
				editTextLayout.setVisibility(View.VISIBLE);
				editTextMessage.requestFocus();
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(editTextMessage, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		buttonCancelEditText.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				editTextLayout.setVisibility(View.GONE);
				isEditShowing = false;
				buttonWriteMessage.setVisibility(View.VISIBLE);
				editTextMessage.setText("");
				editTextLayout.setVisibility(View.GONE);
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);		
			}
		});
		sendTFSPost.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!editTextMessage.getText().toString().equals("")){
					mListener.sendTFSPost(editTextMessage.getText().toString());
					editTextMessage.setText("");
				}
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null){
			isEditShowing = savedInstanceState.getBoolean(EDIT_SHOWING);
			if (mListWpostItems!=null && TimeLine_Fragment.this.getActivity()!=null){
				if (isEditShowing){
					buttonWriteMessage.setVisibility(View.GONE);
					editTextLayout.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean(EDIT_SHOWING, isEditShowing);
		//	((OnTimeLineFragmentInteractionListener)this.mListener).onHomeTimeLineFragmentEvent();


	}



	@Override
	public String getRequestType() {
		return "/GetHomeTimeline";
	}

	@Override
	public String getRequest(int datatype) {

		long since = 0;
		long to = 0 ;
		if (datatype == super.GET_MOREDATA_TYPE){
			to = (mListWpostItems.get(mListWpostItems.size()-1)).getId();
		}

		return "{ \"username\":\"" + username + "\", \"password\":\""
		+ password + "\" , \"since\":\"" + since + "\" , \"to\":\""
		+ to + "\"}";
	}

	@Override
	protected Boolean getClickable() {

		return true;
	}

	@Override
	public void openUserProfileFromActivity(WUser wuser) {
		mListener.openUserProfileFromFragment(wuser);
	}

	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	ArrayList<WPost> getWPosts() {
		// TODO Auto-generated method stub
		return mListWpostItems;
	}


}
