package it.uniba.socialcde4android.adapters;

import java.util.ArrayList;
import java.util.Calendar;

import com.squareup.picasso.Picasso;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.shared.library.WPost;
import it.uniba.socialcde4android.shared.library.WUser;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TimeLineAdapter extends ArrayAdapter<WPost> {


	private static final int TYPE_MAX_COUNT = 3;
	private final int TYPE_MESSAGE = 0;
	private final int TYPE_LAST_ITEM = 1;
	private final int TYPE_NO_MORE_ITEMS = 2;
	private LayoutInflater infalInflater;
	private int num_post;
	private Boolean noMoreItems = false;	
	private Boolean clickable;
	private OnTimeLineAdapterListener frgmentListener;



	public TimeLineAdapter(Context context, int resource, ArrayList<WPost> mListWpostItems, Boolean noMoreItems, Boolean clickable, Fragment fragment) {
		super(context, resource, mListWpostItems);
		infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (mListWpostItems == null) num_post=0;
			else num_post = mListWpostItems.size();
		frgmentListener = (OnTimeLineAdapterListener) fragment;
		this.noMoreItems = noMoreItems;
		this.clickable = clickable;
	}

	public interface OnTimeLineAdapterListener {

		public  void openUserProfileFromActivity(WUser wuser);

	}

	public int getCount() {
		return num_post + 1 ;
	}


	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}


	@Override
	public int getItemViewType(int position) {
		if (noMoreItems && position == num_post){
			return this.TYPE_NO_MORE_ITEMS;
		}else if (position == num_post){
			return this.TYPE_LAST_ITEM;
		}	else return  this.TYPE_MESSAGE;
	}



	@Override
	public View getView(int position, View rowView, ViewGroup parent) 
	{
		ViewHolder view = null;
		int type = getItemViewType(position);

		switch (type) {
		case TYPE_MESSAGE:
			if (rowView == null)	{
				view = new ViewHolder();
				rowView = infalInflater.inflate(R.layout.timeline_item, parent,false);
				view.textViewUsername = (TextView) rowView.findViewById(R.id.textViewTIMELINEUsername);
				view.textViewAbout = (TextView) rowView.findViewById(R.id.textViewTIMELINEabout);
				view.textViewMessage = (TextView) rowView.findViewById(R.id.textViewTIMELINEmessage);
				view.imageviewUser = (ImageView) rowView.findViewById(R.id.imageViewTIMELINEuser); 
				rowView.setTag(view);
				rowView.setTag(view);
			}else {
				view = (ViewHolder) rowView.getTag();
			}

			final WPost wpost = (WPost) TimeLineAdapter.this.getItem(position);
			view.textViewUsername.setText(wpost.getUser().getUsername());
			if (clickable){


				view.textViewUsername.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						frgmentListener.openUserProfileFromActivity(wpost.getUser());
					}
				});  
			}
			view.textViewAbout.setText(getTimeElpased(wpost.getCreateAt(), wpost.getService().getName()));
			view.textViewMessage.setText(wpost.getMessage());
			Linkify.addLinks(view.textViewMessage, Linkify.ALL);
			String userImage_adress = wpost.getUser().getAvatar();
			if (clickable){
				view.imageviewUser.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						frgmentListener.openUserProfileFromActivity(wpost.getUser());
					}
				});  
			}
			if (userImage_adress != null){
				Picasso.with(this.getContext()).load(userImage_adress).into(view.imageviewUser);
			}

			break;

		case TYPE_LAST_ITEM:
			if (rowView == null) rowView = infalInflater.inflate(R.layout.progress_item, parent,false);
			break;
		case TYPE_NO_MORE_ITEMS:
			if (rowView == null) rowView = infalInflater.inflate(R.layout.nomore_item, parent,false);
			break;
		}
		return rowView;
	}


	//	protected void openProfile(WUser user) {
	//		// TODO Auto-generated method stub
	//		
	//	}


	protected static class ViewHolder{
		protected TextView textViewUsername;
		protected TextView textViewAbout ;
		protected TextView textViewMessage;
		protected ImageView imageviewUser ;
	}


	private String getTimeElpased(Calendar time, String service){
		Calendar nowDate = Calendar.getInstance();
		Calendar dateSelected = time;
		long millisDiff = nowDate.getTime().getTime()
				- dateSelected.getTime().getTime();

		int seconds = (int) (millisDiff / 1000 % 60);
		int minutes = (int) (millisDiff / 60000 % 60);
		int hours = (int) (millisDiff / 3600000 % 24);
		int days = (int) (millisDiff / 86400000);

		String result;

		if (days > 1 && days < 30) {
			result = "About " + days + " days ago from " + service;
		} else if (days > 30) {
			result = "More than one month ago from " + service;
		} else if (days == 1) {
			result = "About " + days + " day ago from "	+ service;
		} else {
			if (hours > 1) {
				result = "About " + hours	+ " hours ago from " + service;
			} else if (hours == 1) {
				result = "About " + hours + " hour ago from "	+ service;
			} else {
				if (minutes > 1) {
					result = "About " + minutes	+ " minutes ago from "	+ service;
				} else if (minutes == 1) {
					result = "About " + minutes	+ " minute ago from "+ service;
				} else {
					if (seconds > 1) {
						result = "About " + seconds	+ " seconds ago from "	+ service;
					} else if (seconds == 1) {
						result = "About " + seconds	+ " second ago from " + service;
					} else {
						result = "Few seconds ago from " + service;
					}
				}
			}
		}
		return result;
	}
}
