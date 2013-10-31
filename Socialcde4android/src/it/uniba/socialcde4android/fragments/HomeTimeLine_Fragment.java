package it.uniba.socialcde4android.fragments;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.adapters.TimeLineAdapter;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.JsonDateDeserializer;
import it.uniba.socialcde4android.shared.library.WPost;
import it.uniba.socialcde4android.utility.ScreenUtility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
//import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link WUserProfileFragment.OnFragmentInteractionListener} interface to
 * handle interaction events. Use the {@link WUserProfileFragment#newInstance}
 * factory method to create an instance of this fragment.
 * 
 */
public class HomeTimeLine_Fragment extends Fragment implements  OnRefreshListener<ListView> {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

	private static final String TAG = HomeTimeLine_Fragment.class.getSimpleName();

	private static final String EDIT_SHOWING = "edit_showing";
	private static final String WPOST_ARRAY = "wpost array";

	private PullToRefreshListView pullListView;
	ListView listView;
	private TimeLineAdapter mAdapter;
	private Boolean loading = false;
	private ArrayList<WPost> mListWpostItems = null;
	private OnHomeTimeLineFragmentInteractionListener mListener;
	public boolean noMoreMessages = false;

	private final String NO_MORE_MESSAGES = "no more messages";
	private ImageButton buttonWriteMessage;
	private ImageButton buttonCancelEditText;
	private EditText editTextMessage;
	private LinearLayout editTextLayout;
	private Boolean isEditShowing = false;
	private GetDataTask getDataTask ;
	private GetMoreDataTask getMoreDataTask ;

	//private Boolean noMorePosts_status = false;



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
	public static HomeTimeLine_Fragment newInstance() {
		HomeTimeLine_Fragment fragment = new HomeTimeLine_Fragment();
		//Bundle args = new Bundle();
		//args.putParcelable(ARG_WUSER, wuser);
		//	args.putString(ARG_PARAM2, param2);
		//fragment.setArguments(args);
	//	fragment.setRetainInstance(true);
		return fragment;
	}

	public String getTAG(){
		return TAG;
	}

	public HomeTimeLine_Fragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(getFragmentViewId(), container,	false);
		buttonWriteMessage = (ImageButton)view.findViewById(R.id.buttonEditTextHOMETIMELINE);
		buttonCancelEditText = (ImageButton)view.findViewById(R.id.imageButtonCancelEditTextTIMELINE);
		editTextMessage = (EditText) view.findViewById(R.id.editTextMessageHOMETIMELINE);
		pullListView = (PullToRefreshListView) view.findViewById(R.id.listView1);
		editTextLayout = (LinearLayout) view.findViewById(R.id.layout_edittext_TIMELINE);
		editTextLayout.setVisibility(View.GONE);

		buttonWriteMessage.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				isEditShowing = true;
				//nascondi il tasto e visualizza l'edittext
				buttonWriteMessage.setVisibility(View.GONE);
				editTextLayout.setVisibility(View.VISIBLE);
			}
		});
		buttonCancelEditText.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				//nascondi il tasto e visualizza l'edittext
				isEditShowing = false;
				buttonWriteMessage.setVisibility(View.VISIBLE);
				editTextMessage.setText("");
				editTextLayout.setVisibility(View.GONE);
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);		
			}
		});
		listView = pullListView.getRefreshableView();
		pullListView.setOnRefreshListener(HomeTimeLine_Fragment.this);
		return view;
	}

	private int getFragmentViewId() {
		return R.layout.fragment_home_time_line_;
	}

	private void setListViewListener(){
		listView.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				switch(view.getId()) {
				case android.R.id.list:     

					final int lastItem = firstVisibleItem + visibleItemCount ;
					if(lastItem >= totalItemCount-1) {
						onLastItemVIsible();
					}
				}
			}

			private void onLastItemVIsible() {

				if(!HomeTimeLine_Fragment.this.loading && !noMoreMessages){
					HomeTimeLine_Fragment.this.loading = true;
					 getMoreDataTask =	new GetMoreDataTask();
					 getMoreDataTask.execute() ;
				}		
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}
		});
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null){
			HomeTimeLine_Fragment.this.loading = true;
			mListener.setFragmentLoading(loading);
			mListener.StartProgressDialog();
			
			getDataTask = new GetDataTask();
			getDataTask.execute();
		}
		else {
			noMoreMessages = savedInstanceState.getBoolean(NO_MORE_MESSAGES);
			mListWpostItems = savedInstanceState.getParcelableArrayList(WPOST_ARRAY);
			isEditShowing = savedInstanceState.getBoolean(EDIT_SHOWING);
			if (mListWpostItems!=null && HomeTimeLine_Fragment.this.getActivity()!=null){
				Context context = HomeTimeLine_Fragment.this.getActivity();
				mAdapter = new TimeLineAdapter(context, android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages);
				listView.setAdapter(mAdapter);
				setListViewListener();
				pullListView.onRefreshComplete();
				if (isEditShowing){
					buttonWriteMessage.setVisibility(View.GONE);
					editTextLayout.setVisibility(View.VISIBLE);
				}

			}else{
				HomeTimeLine_Fragment.this.loading = true;
				mListener.setFragmentLoading(loading);
				mListener.StartProgressDialog();
				getDataTask = 	new GetDataTask();
				getDataTask.execute();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putParcelableArrayList(this.WPOST_ARRAY, mListWpostItems);
		savedInstanceState.putBoolean(NO_MORE_MESSAGES, noMoreMessages);
		savedInstanceState.putBoolean(EDIT_SHOWING, isEditShowing);
		if (getDataTask != null ) getDataTask.cancel(true);
		if (getMoreDataTask != null )getMoreDataTask.cancel(true);
	}




	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnHomeTimeLineFragmentInteractionListener) activity;
			mListener.setFragmentLoading(loading);
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
	public interface OnHomeTimeLineFragmentInteractionListener {

		public void onHomeTimeLineFragmentEvent();

		public   void StartProgressDialog();

		public  void StopProgressDialog();

		public void setFragmentLoading(Boolean isFragmentLoading);


	}


	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		HomeTimeLine_Fragment.this.loading = true;
		 getDataTask = new GetDataTask();
		getDataTask.execute();
		
	}



	private class GetDataTask extends AsyncTask<Void, Void, WPost[]> {

		@Override
		protected WPost[] doInBackground(Void... params) {
			// Simulates a background job.
			Map<String,String> preferences = Preferences.loadPreferences(getActivity());
			String username = preferences.get(Preferences.USERNAME);
			String password = preferences.get(Preferences.PASSWORD);
			String host = preferences.get(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
			long since = 0;
			long to = 0;
			WPost[] wpost;
			wpost = new WPost[2];

			try {
				URL url = new URL(host + "/GetHomeTimeline");

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(20000);
				conn.setReadTimeout(25000);
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setAllowUserInteraction(false);
				conn.setRequestProperty("Content-Type", "application/json");

				// Create the form content
				OutputStream out = conn.getOutputStream();
				Writer writer = new OutputStreamWriter(out, "UTF-8");
				writer.write("{ \"username\":\"" + username + "\", \"password\":\""
						+ password + "\" , \"since\":\"" + since + "\" , \"to\":\""
						+ to + "\"}");

				writer.close();
				out.close();
				int status = conn.getResponseCode();

				if (status >= 200 && status <= 299) {
					InputStreamReader in = new InputStreamReader(
							conn.getInputStream(), "UTF-8");
					BufferedReader br = new BufferedReader(in);
					String output;
					String result = "";
					while ((output = br.readLine()) != null) {
						result += output;

					}
					br.close();
					int count = 0;
					if (result.equals("[]")) {
						wpost = new WPost[0];
					} else {
						String haystack = result;
						char needle = '{';
						for (int i = 0; i < haystack.length(); i++) {
							if (haystack.charAt(i) == needle) 
								count++;	}
						if (count == 0) count += 1;

						wpost = new WPost[count/3];
						Gson gson = new GsonBuilder().registerTypeAdapter(
								Calendar.class, new JsonDateDeserializer()).create();
						wpost = gson.fromJson(result, WPost[].class);
					}
				} else {
					wpost = new WPost[0];
				}

				conn.disconnect();
			} catch(java.net.SocketTimeoutException e) {
				e.printStackTrace();
				wpost = new WPost[0];
			} catch (Exception e) {
				e.printStackTrace();
				wpost = new WPost[0];
			}
			return wpost;
		}

		@Override
		protected void onPostExecute(WPost[] wposts) {
			super.onPostExecute(wposts);

			if (wposts.length>0){
				mListWpostItems = new ArrayList<WPost>( Arrays.asList(wposts));
				noMoreMessages = false;
				mAdapter = new TimeLineAdapter(HomeTimeLine_Fragment.this.getActivity(), android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages);
				listView.setAdapter(mAdapter);
				setListViewListener();
				// Call onRefreshComplete when the list has been refreshed.
				pullListView.onRefreshComplete();

			}else{
				showError();
				pullListView.onRefreshComplete();
			}
			HomeTimeLine_Fragment.this.loading = false;
			mListener.setFragmentLoading(HomeTimeLine_Fragment.this.loading);
			mListener.StopProgressDialog();
		}
	}



	private class GetMoreDataTask extends AsyncTask<Void, Void, WPost[]> {

		boolean error = false;
		
		@Override
		protected WPost[] doInBackground(Void... params) {
			// Simulates a background job.
			Map<String,String> preferences = Preferences.loadPreferences(getActivity());
			String username = preferences.get(Preferences.USERNAME);
			String password = preferences.get(Preferences.PASSWORD);
			String host = preferences.get(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
			long since = 0;
			long to = (mListWpostItems.get(mListWpostItems.size()-1)).getId();
			WPost[] wpost;
			wpost = new WPost[2];

			try {
				URL url = new URL(host + "/GetHomeTimeline");

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(20000);
				conn.setReadTimeout(25000);
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setAllowUserInteraction(false);
				conn.setRequestProperty("Content-Type", "application/json");

				// Create the form content
				OutputStream out = conn.getOutputStream();
				Writer writer = new OutputStreamWriter(out, "UTF-8");
				writer.write("{ \"username\":\"" + username + "\", \"password\":\""
						+ password + "\" , \"since\":\"" + since + "\" , \"to\":\""
						+ to + "\"}");

				writer.close();
				out.close();
				int status = conn.getResponseCode();

				if (status >= 200 && status <= 299) {
					InputStreamReader in = new InputStreamReader(
							conn.getInputStream(), "UTF-8");
					BufferedReader br = new BufferedReader(in);
					String output;
					String result = "";
					while ((output = br.readLine()) != null) {
						result += output;
					}
					br.close();
					int count = 0;
					if (result.equals("[]")) {
						wpost = new WPost[0];
						noMoreMessages=true;
					} else {
						String haystack = result;
						char needle = '{';
						for (int i = 0; i < haystack.length(); i++) {
							if (haystack.charAt(i) == needle) 
								count++;	}
						if (count == 0) count += 1;

						wpost = new WPost[count/3];
						Gson gson = new GsonBuilder().registerTypeAdapter(
								Calendar.class, new JsonDateDeserializer()).create();
						wpost = gson.fromJson(result, WPost[].class);
					}
				} else if (status == 400){
					wpost = new WPost[0];
					noMoreMessages=true;
				}else{
					wpost = new WPost[0];
				}

				conn.disconnect();
			} catch(java.net.SocketTimeoutException e) {
				e.printStackTrace();
				wpost = new WPost[0];
			} catch (Exception e) {
				e.printStackTrace();
				wpost = new WPost[0];
			}
			return wpost;
		}

		@Override
		protected void onPostExecute(WPost[] wposts) {
			super.onPostExecute(wposts);
			Parcelable listViewState;
			if (wposts.length>0){
				for (int j=0; j< wposts.length; j++)
					mListWpostItems.add(wposts[j]);
				listViewState = HomeTimeLine_Fragment.this.listView.onSaveInstanceState();
				mAdapter.notifyDataSetChanged();
				listView.onRestoreInstanceState(listViewState);
				pullListView.onRefreshComplete();
			}else{//wpost==0
				if (noMoreMessages){
					//è necessario cambiare l'ultimo elemento per comunicare l'assenza di altri post
					listViewState = HomeTimeLine_Fragment.this.listView.onSaveInstanceState();
					mAdapter = new TimeLineAdapter(HomeTimeLine_Fragment.this.getActivity(), android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages);
					listView.setAdapter(mAdapter);
					listView.onRestoreInstanceState(listViewState);
					pullListView.onRefreshComplete();
				}else {
					showError();
				}

			}
			HomeTimeLine_Fragment.this.loading = false;
			mListener.setFragmentLoading(loading);
		}
	}


	public void showError(){

		Toast.makeText(HomeTimeLine_Fragment.this.getActivity(), "Connection error.", Toast.LENGTH_SHORT).show();

	}

}
