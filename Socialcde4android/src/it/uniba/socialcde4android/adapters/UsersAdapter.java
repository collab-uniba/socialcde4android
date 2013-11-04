package it.uniba.socialcde4android.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.adapters.TimeLineAdapter.ViewHolder;
import it.uniba.socialcde4android.shared.library.WUser;

import com.squareup.picasso.Picasso;

public class UsersAdapter extends ArrayAdapter<WUser>{


	private final int TYPE_MAX_COUNT = 6;
	private final int TYPE_USER = 0;
	private final int TYPE_TITLE_SUGG = 1;
	private final int TYPE_TITLE_FLLING = 4;
	private final int TYPE_TITLE_FLLERS = 5;
	private final int TYPE_TITLE_HIDDEN = 3;
	private final int TYPE_NOUSER = 2;
	private LayoutInflater infalInflater;
	private int length_suggested;
	private int length_following;
	private int length_followers;
	private int length_hidden;
	private ArrayList<Integer> type_list = null;
	private int add_positions = 0;
	private Boolean[] added = new Boolean[5];






	public UsersAdapter(Context context, int resource,ArrayList<WUser> allwusers,	int[] wUsersNumType_SuggFingFersHidd ) {
		super(context, resource, allwusers);

		for (int j=0;j<added.length;j++){
			added[j]=false;
		}
		//	this.context = context;
		length_suggested = wUsersNumType_SuggFingFersHidd[0];
		length_following = wUsersNumType_SuggFingFersHidd[1];
		length_followers = wUsersNumType_SuggFingFersHidd[2];
		length_hidden = wUsersNumType_SuggFingFersHidd[3];

		//posso creare un array per memorizare i tipi
		type_list = new ArrayList<Integer>();
		type_list.add(this.TYPE_TITLE_SUGG);
		if (length_suggested == 0) 	type_list.add(this.TYPE_NOUSER);
		else {
			for (int i=0; i < length_suggested;i++){
				type_list.add(this.TYPE_USER);
			}
		}
		type_list.add(this.TYPE_TITLE_FLLING);
		if (length_following == 0) 	type_list.add(this.TYPE_NOUSER);
		else {
			for (int i=0; i < length_following;i++){
				type_list.add(this.TYPE_USER);
			}
		}
		type_list.add(this.TYPE_TITLE_FLLERS);
		if (length_followers == 0) 	type_list.add(this.TYPE_NOUSER);
		else {
			for (int i=0; i < length_followers;i++){
				type_list.add(this.TYPE_USER);
			}
		}
		type_list.add(this.TYPE_TITLE_HIDDEN);
		if (length_hidden == 0) 	type_list.add(this.TYPE_NOUSER);
		else {
			for (int i=0; i < length_hidden;i++){
				type_list.add(this.TYPE_USER);
			}
		}

		infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getUserTypeID(){
		return this.TYPE_USER;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}
	
	
	public int getArrayPosition(int position){
		int pos=position;
		for (int j=0;j<position;j++){
			if (!type_list.get(j).equals(this.TYPE_USER)){
				pos--;
			}
		}
		return pos;
	}
	
	
	@Override
	public int getItemViewType(int position) {

		return type_list.get(position);
	}

	public int getCount() {
		return type_list.size();
	}


	@Override
	public View getView(int position, View rowView, ViewGroup parent) 
	{
		ViewHolder view = null;
		int type = getItemViewType(position);

		switch (type) {

		case TYPE_TITLE_SUGG:
			rowView = infalInflater.inflate(R.layout.drawer_services_title, null);
			TextView title = (TextView) rowView.findViewById(R.id.textViewdrawerTileServices);
			title.setText("Suggestions");
			break;
		case TYPE_TITLE_FLLING:		
			rowView = infalInflater.inflate(R.layout.drawer_services_title, null);
			TextView title_fing = (TextView) rowView.findViewById(R.id.textViewdrawerTileServices);
			title_fing.setText("Followings");
			break;
		case TYPE_TITLE_FLLERS:		
			rowView = infalInflater.inflate(R.layout.drawer_services_title, null);
			TextView title_fillers = (TextView) rowView.findViewById(R.id.textViewdrawerTileServices);
			title_fillers.setText("Followers");
			break;
		case TYPE_TITLE_HIDDEN:		
			rowView = infalInflater.inflate(R.layout.drawer_services_title, null);
			TextView title_hidden = (TextView) rowView.findViewById(R.id.textViewdrawerTileServices);
			title_hidden.setText("Hidden");
			break;

		case TYPE_NOUSER:		
			rowView = infalInflater.inflate(R.layout.drawer_no_users_item, null);
			break;

		case TYPE_USER:	

			if (rowView == null)	{
				view = new ViewHolder();
				rowView = infalInflater.inflate(R.layout.drawer_user_tem,parent, false);
				view.textViewUser = (TextView) rowView.findViewById(R.id.textViewdrawerUSER);
				view.imageviewUser = (ImageView) rowView.findViewById(R.id.imageViewdrawerUSER); 
				rowView.setTag(view);
			}else {
				view = (ViewHolder) rowView.getTag();
			}
			
			WUser wuser = (WUser) UsersAdapter.this.getItem(getArrayPosition(position));
			view.textViewUser.setText(wuser.getUsername());
			String avatar_address = wuser.getAvatar();
			if (avatar_address != null){
				Picasso.with(this.getContext()).load(avatar_address).into(view.imageviewUser);
			}

			break;

		}


		return rowView;
	}

	protected static class ViewHolder{
		protected TextView textViewUser;
		protected ImageView imageviewUser;
	}
}
