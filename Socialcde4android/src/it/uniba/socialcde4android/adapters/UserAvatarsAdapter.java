package it.uniba.socialcde4android.adapters;

import it.uniba.socialcde4android.R;



import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class UserAvatarsAdapter extends ArrayAdapter<String>{

	private LayoutInflater infalInflater;
	private DisplayImageOptions options;
	private ImageLoader imageloader;

	
	public UserAvatarsAdapter(Context context, int resource, String[] avatarURLs) {
		super(context, resource, avatarURLs);
		infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
	            .cacheOnDisc(true).showImageOnLoading(R.drawable.user_default).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
        .defaultDisplayImageOptions(options)
        .build();
		imageloader = ImageLoader.getInstance();
		imageloader.init(config);
	}



	@Override
	public View getView(int position, View view, ViewGroup parent) 
	{

		if (view == null){
			view = infalInflater.inflate(R.layout.avatar_image_item, null);
			String uri = UserAvatarsAdapter.this.getItem(position);
			ImageView imageviewAvatar = (ImageView) view.findViewById(R.id.pictureAVATARIMAGES); 
//			if (uri != null){
//				Picasso.with(this.getContext()).load(uri).into(imageviewAvatar);
//			}
			imageloader.displayImage(uri, imageviewAvatar);
		}
			return view;
		
	}
}
