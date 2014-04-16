package it.uniba.socialcde4android.fragments;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.adapters.ConfiguratedImageLoader;
import it.uniba.socialcde4android.adapters.TimeLineAdapter;
import it.uniba.socialcde4android.adapters.TimeLineAdapter.OnTimeLineAdapterListener;
import it.uniba.socialcde4android.costants.Consts;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WPost;
import it.uniba.socialcde4android.shared.library.WUser;
import android.app.Activity;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.Toast;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link WUserColleagueProfile_Fragment.OnFragmentInteractionListener} interface to
 * handle interaction events. Use the {@link WUserColleagueProfile_Fragment#newInstance}
 * factory method to create an instance of this fragment.
 * 
 */
public abstract class TimeLine_AbstractFragment extends Fragment implements  OnRefreshListener<ListView> , OnTimeLineAdapterListener{



	private static final String WPOST_ARRAY = "wpost array";
	protected static final int GET_DATA_TYPE  = 0;
	protected static final int GET_MOREDATA_TYPE  = 1;
	protected PullToRefreshListView pullListView;
	protected ListView listView;
	protected TimeLineAdapter mAdapter;
	protected ArrayList<WPost> mListWpostItems = null;
	protected OnGenericTimeLineFragmentInteractionListener mListener;
	public  boolean noMoreMessages = false;
	private final static String NO_MORE_MESSAGES = "no more messages";
	protected String username = "";
	protected String password = "";
	protected String host = "";
	private  boolean data_error = false;
	protected ImageLoader imageloader;
	protected ProgressBar progress;


	abstract String getTAG();
	
	abstract ArrayList<WPost> getWPosts();

	public TimeLine_AbstractFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("onCreate","onCreate");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		imageloader = ConfiguratedImageLoader.getImageLoader((Activity)mListener);
		Log.i("onCreateView","onCreateView");
		//se come background metto un loading.. e poi lo rendo invisibile una volta caircato
		View view = inflater.inflate(getFragmentViewId(), container,	false);
		progress = (ProgressBar)view.findViewById(R.id.progressBar3);
		progress.setVisibility(View.VISIBLE);
		pullListView = (PullToRefreshListView) view.findViewById(R.id.listViewCheckBoxFEATURES);
		listView = pullListView.getRefreshableView();
		return view;
	}

	public abstract int getFragmentViewId();

	private void setListViewListener(){
		listView.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					checkLastItemInView(view);
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					break;
				}
			}
		});
	}

	private void checkLastItemInView(AbsListView   view){
		int count = view.getCount(); // visible views count
		int lastVisibleItemPosition = view.getLastVisiblePosition();

		if (lastVisibleItemPosition >= count-2){
			if( !noMoreMessages){
				Log.i("inside listview before getdata listener","lastitem: "+lastVisibleItemPosition+ "count-1: "+String.valueOf(count-1));
				getDataTask(GET_MOREDATA_TYPE) ;
			}	
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("onResume",getTag());

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		Log.i("onActivityCreated",getTag());
		if (username.equals("")){
			Map<String,String> preferences = Preferences.loadPreferences((Activity)mListener);
			username = preferences.get(Preferences.USERNAME);
			host = preferences.get(Preferences.PROXYSERVER) + "/SocialTFSProxy.svc";
		}
		if (savedInstanceState != null){
			String tag = savedInstanceState.getString(Consts.TAG);
			Log.i("onActivityCreated",tag);

			mListWpostItems = savedInstanceState.getParcelableArrayList(WPOST_ARRAY);
			if (mListWpostItems!=null){
				
				noMoreMessages = savedInstanceState.getBoolean(NO_MORE_MESSAGES);
				password = savedInstanceState.getString(Preferences.PASSWORD);
				Context context = (Activity)mListener;
				mAdapter = new TimeLineAdapter(context, android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages, getClickable(), getFragment());
				listView.setAdapter(mAdapter);
				setListViewListener();
				pullListView.onRefreshComplete();
				progress.setVisibility(View.GONE);

			}else{
				getDataTask(GET_DATA_TYPE);
			}
		}else{
			getDataTask(GET_DATA_TYPE);
		}
	}




	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		Log.i("onSaveInstanceState","onSaveInstanceState");

		savedInstanceState.putParcelableArrayList(WPOST_ARRAY, getWPosts());
		savedInstanceState.putBoolean(NO_MORE_MESSAGES, noMoreMessages);
		savedInstanceState.putString(Preferences.PASSWORD, password);
		savedInstanceState.putString(Consts.TAG, getTag());
	}


	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("onAttach","onAttach");

		try {
			mListener = (OnGenericTimeLineFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}


	protected abstract Boolean getClickable();

	@Override
	public void onDetach() {
		super.onDetach();
		Log.i("onDetach","onDetach");

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
	public interface OnGenericTimeLineFragmentInteractionListener {


		public void exitToLogin();

		public void openUserProfileFromFragment(WUser wuser);

		public void sendTFSPost(String post);

		public void loadData(Integer type_request, String request, String requestType);

	}


	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		getDataTask(GET_DATA_TYPE);

	}


	public void refreshFragment(){
		getDataTask(GET_DATA_TYPE);
	}


	public void getDataTask(Integer type_request){
		mListener.loadData(type_request , getRequest(type_request), getRequestType());
	}



	//@Override
	public void onPostExecute(Bundle result_data) {
		Parcelable[] parcelableArray =	result_data.getParcelableArray(Consts.WPOSTS);
		WPost[] wposts = null;
		if (parcelableArray != null) 
			wposts = Arrays.copyOf(parcelableArray, parcelableArray.length, WPost[].class);
		//bisogna considerare se wpost == null
		if (wposts != null){

		int type_request = result_data.getInt(Consts.TYPE_REQUEST);
		data_error = result_data.getBoolean(Consts.DATA_ERROR);
		noMoreMessages = result_data.getBoolean(Consts.NO_MORE_MESSAGES);
		switch(type_request){
		case GET_DATA_TYPE:
			if (wposts.length>0){
				mListWpostItems = new ArrayList<WPost>( Arrays.asList(wposts));
				if (wposts.length<15)
					noMoreMessages = true;
				else
					noMoreMessages = false;

				mAdapter = new TimeLineAdapter((Activity)mListener, android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages, getClickable(), getFragment());
				listView.setAdapter(mAdapter);
				setListViewListener();
				// Call onRefreshComplete when the list has been refreshed.
				pullListView.onRefreshComplete();
			}else{
				if (data_error){
					Log.i("abstractfragment","error in get data type");
					showErrorAndExit();
					data_error = false;
				}else{
					noMoreMessages=true;
					mAdapter = new TimeLineAdapter((Activity)mListener, android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages, getClickable(), getFragment());
					listView.setAdapter(mAdapter);
					setListViewListener();
					pullListView.onRefreshComplete();
				}
			}
			break;

		case GET_MOREDATA_TYPE:
			Parcelable listViewState = null;
			if (wposts.length>0){
				for (int j=0; j< wposts.length; j++)
					mListWpostItems.add(wposts[j]);
				listViewState = TimeLine_AbstractFragment.this.listView.onSaveInstanceState();

				mAdapter = new TimeLineAdapter((Activity)mListener, android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages, getClickable(),getFragment());
				listView.setAdapter(mAdapter);
				//setListViewListener();
				listView.onRestoreInstanceState(listViewState);
				pullListView.onRefreshComplete();
			}else{//wpost==0
				if (noMoreMessages){
					//è necessario cambiare l'ultimo elemento per comunicare l'assenza di altri post
					listViewState = TimeLine_AbstractFragment.this.listView.onSaveInstanceState();
					mAdapter = new TimeLineAdapter((Activity)mListener, android.R.layout.simple_list_item_1, mListWpostItems, noMoreMessages, getClickable(),getFragment());
					listView.setAdapter(mAdapter);
					listView.onRestoreInstanceState(listViewState);
					pullListView.onRefreshComplete();
				}else {
					if (data_error){
						Log.i("abstractfragment","error in get data type");
						showErrorAndExit();
						data_error = false;
					}else{

					}
				}

			}
			break;
		}
		progress.setVisibility(View.GONE);
		}else{
			showErrorAndExit();
		}
	}




	public void showErrorAndExit(){

		Toast.makeText((Activity)mListener, "Connection error.", Toast.LENGTH_SHORT).show();
		mListener.exitToLogin();

	}



	public abstract String getRequest(int dataType);

	public abstract String getRequestType();

	public abstract void openUserProfileFromActivity(WUser wuser);

	public abstract Fragment getFragment();

}
