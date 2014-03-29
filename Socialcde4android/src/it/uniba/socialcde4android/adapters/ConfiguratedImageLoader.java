package it.uniba.socialcde4android.adapters;

import it.uniba.socialcde4android.R;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ConfiguratedImageLoader extends ImageLoader {
	
	private static ImageLoader imageloader;
	
	private static void  newIstance(Context context){
		
		imageloader = ImageLoader.getInstance();
		if (!imageloader.isInited()){
			DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
		            .showImageOnLoading(R.drawable.user_default).build();
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
	        .defaultDisplayImageOptions(options).build();
			imageloader.init(config);
		}
		
	}
	
	public static ImageLoader getImageLoader(Context context){
		ConfiguratedImageLoader.newIstance(context);
		return imageloader;
	}
	
	public static void destroyIfImageLoader() {
		if (imageloader != null && imageloader.isInited()){
			imageloader.destroy();
		}
	}
	
	private ConfiguratedImageLoader(){
		
	}

	
}
