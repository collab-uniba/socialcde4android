package it.uniba.socialcde4android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.shared.library.WService;
import it.uniba.socialcde4android.shared.library.WUser;

import com.squareup.picasso.Picasso;

public class ServicesAdapter extends ArrayAdapter<WService>{

	private Context context;
 	private int num_sevice;
	private final int TYPE_MAX_COUNT = 3;
	private final int TYPE_AVATAR = 0;
	private final int TYPE_TITLE = 1;
	private final int TYPE_SERVICE = 2;
	private LayoutInflater infalInflater;
	private String name;
	private String avatar_address;
	private int post;
	private int following;
	private int followers;

	
	


	public ServicesAdapter(Context context, int resource, WService[] wservice, WUser wuser ) {
		super(context, resource, wservice);

		this.context = context;
		num_sevice = wservice.length;
		name = wuser.getUsername();
		avatar_address = wuser.getAvatar();
		post = wuser.getStatuses();
		following = wuser.getFollowings();
		followers = wuser.getFollowers();

		infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return num_sevice+2;
	}
	
	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) return TYPE_AVATAR;
		else if (position == 1) return TYPE_TITLE;
		else return TYPE_SERVICE;
	}

	

	
	@Override
	public View getView(int position, View rowView, ViewGroup parent) 
	{
		
		int type = getItemViewType(position);
		
			switch (type) {
			case TYPE_AVATAR:
				if (rowView == null) 	rowView = infalInflater.inflate(R.layout.drawer_avatar_item, null);
				TextView textViewAvatar = (TextView) rowView.findViewById(R.id.textViewdrawerAVATAR);
				textViewAvatar.setText(name);
				ImageView imageviewAvatar = (ImageView) rowView.findViewById(R.id.imageViewdrawerAVATAR); 
				if (avatar_address != null){
					Picasso.with(this.getContext()).load(avatar_address).into(imageviewAvatar);
				}
				TextView textViewAvatarPost = (TextView) rowView.findViewById(R.id.textViewAvatarPost);
				textViewAvatarPost.setText("Posts: "+post);
				TextView textViewAvatarFollowing = (TextView) rowView.findViewById(R.id.textViewAvatarFollowing);
				textViewAvatarFollowing.setText("Following: "+following);
				TextView textViewAvatarFollowers = (TextView) rowView.findViewById(R.id.textViewAvatarFollwers);
				textViewAvatarFollowers.setText("Followers: "+followers);
				break;

			case TYPE_TITLE:
				if (rowView == null) 	rowView = infalInflater.inflate(R.layout.drawer_services_title, null);
				break;

			case TYPE_SERVICE:
				WService wservice = (WService) ServicesAdapter.this.getItem(position-2);
				if (rowView == null) 	rowView = infalInflater.inflate(R.layout.drawer_item, null);
				TextView textViewService = (TextView) rowView.findViewById(R.id.textViewdrawer);
				textViewService.setText(wservice.getName());
				ImageView imageviewStatusDot = (ImageView) rowView.findViewById(R.id.imageViewStausDot); 
				if (wservice.isRegistered()){
					imageviewStatusDot.setImageResource(getContext().getResources().getIdentifier("it.uniba.socialcde4android:drawable/"+"greendot",null,null));
				}else{
					imageviewStatusDot.setImageResource(getContext().getResources().getIdentifier("it.uniba.socialcde4android:drawable/"+"greydot",null,null));
				}
				ImageView imageviewService = (ImageView) rowView.findViewById(R.id.imageViewdrawer); 
				imageviewService.setImageResource(getContext().getResources().getIdentifier("it.uniba.socialcde4android:drawable/"+wservice.getImage().replace("/Images/", "").replace(".png", ""),null,null));
				break;

			}

		

	
		return rowView;
	}
}
