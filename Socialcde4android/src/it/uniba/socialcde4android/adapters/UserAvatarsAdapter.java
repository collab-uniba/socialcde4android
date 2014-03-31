package it.uniba.socialcde4android.adapters;

import it.uniba.socialcde4android.R;



import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class UserAvatarsAdapter extends ArrayAdapter<String>{

	private LayoutInflater infalInflater;

	private ImageLoader imageloader;

	
	public UserAvatarsAdapter(Context context, int resource, String[] avatarURLs) {
		super(context, resource, avatarURLs);
		infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageloader = ConfiguratedImageLoader.getImageLoader(context);
	}



	@Override
	public View getView(int position, View view, ViewGroup parent) 
	{

		if (view == null){
			view = infalInflater.inflate(R.layout.avatar_image_item, null);
			String uri = UserAvatarsAdapter.this.getItem(position);
			ImageView imageviewAvatar = (ImageView) view.findViewById(R.id.pictureAVATARIMAGES); 
			imageloader.displayImage(uri, imageviewAvatar);
		}
			return view;
		
	}
}
