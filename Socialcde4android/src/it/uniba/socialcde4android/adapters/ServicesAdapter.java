package it.uniba.socialcde4android.adapters;

import java.util.Locale;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.shared.library.WService;
import it.uniba.socialcde4android.shared.library.WUser;


public class ServicesAdapter extends ArrayAdapter<WService>{

	private Context context;
 	private int num_service;
	private static final int TYPE_MAX_COUNT = 5;
	public static final int TYPE_AVATAR = 0;
	public static final int TYPE_TITLE = 1;
	public static final int TYPE_SERVICE = 2;
	public static final int TYPE_TITLE_SETTING = 3;
	public static final int TYPE_SETTING = 4;

	private LayoutInflater infalInflater;
	private String name;
	private String avatar_address;
	private int post;
	private int following;
	private int followers;
	private String proxy;
	private static final String[] SETTINGS = { "Choose Avatar", "Change Password", "Exit"};
	//save an image without spaces in the name and all lowercase
	private ImageLoader imageloader;



	public ServicesAdapter(Context context, int resource, WService[] wservice, WUser wuser, String proxy_string ) {
		super(context, resource, wservice);

		this.context = context;
		num_service = wservice.length;
		name = wuser.getUsername();
		avatar_address = wuser.getAvatar();
		post = wuser.getStatuses();
		following = wuser.getFollowings();
		followers = wuser.getFollowers();
		this.proxy = proxy_string;

		infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageloader = ConfiguratedImageLoader.getImageLoader(context);

	}

	public int getCount() {
		return num_service+3+SETTINGS.length;
	}
	
	
	
	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	
	
//	public int getServiceTypeID(){
//		return TYPE_SERVICE;
//	}
	
	@Override
	public int getItemViewType(int position) {
		if (position == 0) return TYPE_AVATAR;
		else if (position == 1) return TYPE_TITLE;
		else if (position <= 1+this.num_service)return TYPE_SERVICE;
		else if (position == 2+this.num_service ) return this.TYPE_TITLE_SETTING;
		else return this.TYPE_SETTING;
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
					imageloader.displayImage(avatar_address, imageviewAvatar);

				}
				TextView textViewAvatarPost = (TextView) rowView.findViewById(R.id.textViewAvatarPost);
				textViewAvatarPost.setText("Posts: "+post);
				TextView textViewAvatarFollowing = (TextView) rowView.findViewById(R.id.textViewAvatarFollowing);
				textViewAvatarFollowing.setText("Following: "+following);
				TextView textViewAvatarFollowers = (TextView) rowView.findViewById(R.id.textViewAvatarFollwers);
				textViewAvatarFollowers.setText("Followers: "+followers);
				break;

//			case TYPE_LOGOUT:
//				if (rowView == null) 	rowView = infalInflater.inflate(R.layout.logout_item, null);
//				break;
				

			case TYPE_TITLE_SETTING:
				if (rowView == null) 	rowView = infalInflater.inflate(R.layout.drawer_settings_title, null);
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
				imageloader.displayImage(proxy+wservice.getImage(), imageviewService);


				break;
			case TYPE_TITLE:
				if (rowView == null) 	rowView = infalInflater.inflate(R.layout.drawer_services_title, null);
				break;
				
			case TYPE_SETTING:
				String setting_name = SETTINGS[position - 3 - this.num_service];
				if (rowView == null) 	rowView = infalInflater.inflate(R.layout.drawer_settings_item, null);

				TextView textViewSettings = (TextView) rowView.findViewById(R.id.textViewdrawerSetting);
				textViewSettings.setText(setting_name);
				
				ImageView imageviewSettins = (ImageView) rowView.findViewById(R.id.imageViewdrawerSetting); 
				String imagename = setting_name.toLowerCase(Locale.ENGLISH).replaceAll("\\s","");
				Log.i("adapter",imagename);
				imageviewSettins.setImageResource(getContext().getResources()
						.getIdentifier("it.uniba.socialcde4android:drawable/"+imagename,null,null));

			}

		

	
		return rowView;
	}
}
