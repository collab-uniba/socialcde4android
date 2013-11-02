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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
//import com.handmark.pulltorefresh.library.PullTltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.adapters.TimeLineAdapter;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.JsonDateDeserializer;
import it.uniba.socialcde4android.shared.library.WPost;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link WUserColleagueProfile_Fragment.OnFragmentInteractionListener} interface to
 * handle interaction events. Use the {@link WUserColleagueProfile_Fragment#newInstance}
 * factory method to create an instance of this fragment.
 * 
 */
public abstract class TimeLine_AbstractFragment extends Fragment implements  OnRefreshListener<ListView> {


	private static final String TAG = TimeLine_AbstractFragment.class.getSimpleName();

	private static final String WPOST_ARRAY = "wpost array";
	protected static final int GET_DATA_TYPE  = 0;
	protected static final int GET_MOREDATA_TYPE  = 1;
	protected PullToRefreshListView pullListView;
	ListView listView;
	private TimeLineAdapter mAdapter;
	private Boolean loading = false;
	protected ArrayList<WPost> mListWpostItems = null;
	protected OnHomeTimeLineFragmentInteractionListener mListener;
	public boolean noMoreMessages = false;
	private final String NO_MORE_MESSAGES = "no more messages";
	protected GetDataTask getDataTask ;
	protected GetMoreDataTask getMoreDataTask ;
	protected Map<String,String> preferences; 

	//private Boolean noMorePosts_status = false;



	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * @return A new instance of fragment WUserProfileFragment.
	 */
	//	public static TimeLine_Fragment newInstance() {
	//		TimeLine_Fragment fragment = new TimeLine_Fragment();
	//		//Bundle args = new Bundle();
	//		//args.putParcelable(ARG_WUSER, wuser);
	//		//	args.putString(ARG_PARAM2, param2);
	//		//fragment.setArguments(args);
	//	//	fragment.setRetainInstance(true);
	//		return fragment;
	//	}

	public String getTAG(){
		return TAG;
	}

	public TimeLine_AbstractFragment() {
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

		pullListView = (PullToRefreshListView) view.findViewById(R.id.listView1);
		listView = pullListView.getRefreshableView();
		pullListView.setOnRefreshListener(TimeLine_AbstractFragment.this);
		return view;
	}

	public abstract int getFragmentViewId();

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

				if(!TimeLine_AbstractFragment.this.loading && !noMoreMessages){
					TimeLine_AbstractFragment.this.loading = true;
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
		if (mListWpostItems!=null && TimeLine_AbstractFragment.this.getActivity()!=null){
			Context context = TimeLine_AbstractFragment.this.getActivity();
			mAdapter = new TimeLineAdapter(context, android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages);
			listView.setAdapter(mAdapter);
			setListViewListener();
			pullListView.onRefreshComplete();
		}else{
			if (savedInstanceState == null){
				TimeLine_AbstractFragment.this.loading = true;
				mListener.setFragmentLoading(loading);
				mListener.StartProgressDialog();
				Log.i("astraaaaaaaaaaact","savedinstace == null");
				getDataTask = new GetDataTask();
				getDataTask.execute();
			}
			else {
				noMoreMessages = savedInstanceState.getBoolean(NO_MORE_MESSAGES);
				mListWpostItems = savedInstanceState.getParcelableArrayList(WPOST_ARRAY);
				if (mListWpostItems!=null && TimeLine_AbstractFragment.this.getActivity()!=null){
					Context context = TimeLine_AbstractFragment.this.getActivity();
					mAdapter = new TimeLineAdapter(context, android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages);
					listView.setAdapter(mAdapter);
					setListViewListener();
					pullListView.onRefreshComplete();


				}else{
					TimeLine_AbstractFragment.this.loading = true;
					mListener.setFragmentLoading(loading);
					mListener.StartProgressDialog();
					Log.i("astraaaaaaaaaaact","savedinstace not null ma list o activity null");

					getDataTask = 	new GetDataTask();
					getDataTask.execute();
				}
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putParcelableArrayList(WPOST_ARRAY, mListWpostItems);
		savedInstanceState.putBoolean(NO_MORE_MESSAGES, noMoreMessages);
		if (getDataTask != null ) {
			getDataTask.cancel(true);
			pullListView.onRefreshComplete();
		}
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


	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		TimeLine_AbstractFragment.this.loading = true;
		getDataTask = new GetDataTask();
		getDataTask.execute();

	}



	class GetDataTask extends AsyncTask<Void, Void, WPost[]> {

		@Override
		protected WPost[] doInBackground(Void... params) {
			// Simulates a background job.
			preferences = Preferences.loadPreferences(getActivity());
			String host = preferences.get(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
			WPost[] wpost;
			wpost = new WPost[2];

			try {
				URL url = new URL(host + getRequestType());

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
				writer.write(getRequest(GET_DATA_TYPE));

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
				//controlla che non ci sia gi� una lista aggiornata
				if (mListWpostItems != null && (mListWpostItems.get(0).getId() == wposts[0].getId())){
					pullListView.onRefreshComplete();
				}else{
					mListWpostItems = new ArrayList<WPost>( Arrays.asList(wposts));
					noMoreMessages = false;
					mAdapter = new TimeLineAdapter(TimeLine_AbstractFragment.this.getActivity(), android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages);
					listView.setAdapter(mAdapter);
					setListViewListener();
					// Call onRefreshComplete when the list has been refreshed.
					pullListView.onRefreshComplete();
				}
			}else{
				showError();
				pullListView.onRefreshComplete();
			}
			TimeLine_AbstractFragment.this.loading = false;
			mListener.setFragmentLoading(TimeLine_AbstractFragment.this.loading);
			mListener.StopProgressDialog();
		}
	}



	class GetMoreDataTask extends AsyncTask<Void, Void, WPost[]> {

		//boolean error = false;

		@Override
		protected WPost[] doInBackground(Void... params) {
			// Simulates a background job.
			preferences = Preferences.loadPreferences(getActivity());
			String host = preferences.get(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";

			WPost[] wpost;
			wpost = new WPost[2];

			try {
				URL url = new URL(host + getRequestType());

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
				writer.write(getRequest(GET_MOREDATA_TYPE));

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
				listViewState = TimeLine_AbstractFragment.this.listView.onSaveInstanceState();
				mAdapter.notifyDataSetChanged();
				listView.onRestoreInstanceState(listViewState);
				pullListView.onRefreshComplete();
			}else{//wpost==0
				if (noMoreMessages){
					//� necessario cambiare l'ultimo elemento per comunicare l'assenza di altri post
					listViewState = TimeLine_AbstractFragment.this.listView.onSaveInstanceState();
					mAdapter = new TimeLineAdapter(TimeLine_AbstractFragment.this.getActivity(), android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages);
					listView.setAdapter(mAdapter);
					listView.onRestoreInstanceState(listViewState);
					pullListView.onRefreshComplete();
				}else {
					showError();
				}

			}
			TimeLine_AbstractFragment.this.loading = false;
			mListener.setFragmentLoading(loading);
		}
	}


	public void showError(){

		Toast.makeText(TimeLine_AbstractFragment.this.getActivity(), "Connection error.", Toast.LENGTH_SHORT).show();

	}

	public abstract String getRequest(int dataType);

	public abstract String getRequestType();

}
