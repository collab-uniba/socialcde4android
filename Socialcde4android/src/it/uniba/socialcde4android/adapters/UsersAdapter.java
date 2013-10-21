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






	public UsersAdapter(Context context, int resource,ArrayList<WUser> allwusers,	int[] wuLgts_SuFngFrsHid ) {
		super(context, resource, allwusers);

	//	this.context = context;
		length_suggested = wuLgts_SuFngFrsHid[0];
		length_following = wuLgts_SuFngFrsHid[1];
		length_followers = wuLgts_SuFngFrsHid[2];
		length_hidden = wuLgts_SuFngFrsHid[3];

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


	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
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
		int type = getItemViewType(position);
		if (rowView == null) {
			switch (type) {

			case TYPE_TITLE_SUGG:
				add_positions++;
				rowView = infalInflater.inflate(R.layout.drawer_services_title, null);
				TextView title = (TextView) rowView.findViewById(R.id.textViewdrawerTileServices);
				title.setText("Suggestions");
				break;
			case TYPE_TITLE_FLLING:
				add_positions++;
				rowView = infalInflater.inflate(R.layout.drawer_services_title, null);
				TextView title_fing = (TextView) rowView.findViewById(R.id.textViewdrawerTileServices);
				title_fing.setText("Followings");
				break;
			case TYPE_TITLE_FLLERS:
				add_positions++;
				rowView = infalInflater.inflate(R.layout.drawer_services_title, null);
				TextView title_fillers = (TextView) rowView.findViewById(R.id.textViewdrawerTileServices);
				title_fillers.setText("Followers");
				break;
			case TYPE_TITLE_HIDDEN:
				add_positions++;
				rowView = infalInflater.inflate(R.layout.drawer_services_title, null);
				TextView title_hidden = (TextView) rowView.findViewById(R.id.textViewdrawerTileServices);
				title_hidden.setText("Hidden");
				break;

			case TYPE_NOUSER:
				add_positions++;
				rowView = infalInflater.inflate(R.layout.drawer_no_users_item, null);

				break;

			case TYPE_USER:
				WUser wuser = (WUser) UsersAdapter.this.getItem(position-add_positions);
				rowView = infalInflater.inflate(R.layout.drawer_user_tem, null);
				TextView textViewUser = (TextView) rowView.findViewById(R.id.textViewdrawerUSER);
				textViewUser.setText(wuser.getUsername());
				ImageView imageviewUser = (ImageView) rowView.findViewById(R.id.imageViewdrawerUSER); 
				String avatar_address = wuser.getAvatar();
				if (avatar_address != null){
					Picasso.with(this.getContext()).load(avatar_address).into(imageviewUser);
				}
				//devo calcolare la posizione dell'utente..
				
				break;

			}

		}
		return rowView;
	}
}
