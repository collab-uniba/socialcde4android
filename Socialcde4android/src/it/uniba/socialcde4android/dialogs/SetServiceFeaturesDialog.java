package it.uniba.socialcde4android.dialogs;

import java.util.Arrays;

import com.nostra13.universalimageloader.core.ImageLoader;


import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.adapters.ConfiguratedImageLoader;
import it.uniba.socialcde4android.adapters.FeaturesAdapter;
import it.uniba.socialcde4android.preferences.Preferences;
import it.uniba.socialcde4android.shared.library.WFeature;
import it.uniba.socialcde4android.shared.library.WService;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SetServiceFeaturesDialog extends DialogFragment{

	private static final String ARG_WFEAT = "arg wfeatures";
	private static final String ARG_WSERV = "arg wservice";
	private WFeature[] wfeature;
	private WService wservice;
	private ListView listview;
	private FeaturesAdapter adapter;
	private OnFeaturesDialogInteractionListener mListener;
	private ImageLoader imageloader;

	
	public static SetServiceFeaturesDialog newInstance(WFeature[] wfeature, WService wService) {
		SetServiceFeaturesDialog dialog = new SetServiceFeaturesDialog();
		Bundle args = new Bundle();
		args.putParcelableArray(ARG_WFEAT, wfeature);
		args.putParcelable(ARG_WSERV, wService);
		dialog.setArguments(args);
		return dialog;
	}

	public SetServiceFeaturesDialog(){
		
	}
	
	public interface OnFeaturesDialogInteractionListener{
		public void saveFeaturesStatus(int service_id, String active_features);
		
		public void unregisterService(int service_id);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog); 
		if (getArguments() != null) {
			wservice = getArguments().getParcelable(ARG_WSERV);
			Parcelable[] parcelableArray =	getArguments().getParcelableArray(ARG_WFEAT);
			if (parcelableArray != null) 
				wfeature = Arrays.copyOf(parcelableArray, parcelableArray.length, WFeature[].class);
		}
		mListener = (OnFeaturesDialogInteractionListener) getActivity();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
		imageloader = ConfiguratedImageLoader.getImageLoader((Activity)mListener);
        View v = inflater.inflate(R.layout.dialog_set_service_features, container, false);
        //getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        ImageView imageviewService = (ImageView) v.findViewById(R.id.imageView1_logoFEATURE); 
		imageloader.displayImage(Preferences.getSavedHost(getActivity())+wservice.getImage(), imageviewService);
        TextView title = (TextView) v.findViewById(R.id.textView1_title_FEATURES);
        title.setText(wservice.getName()+" features");
		listview = (ListView) v.findViewById(R.id.listViewCheckBoxFEATURES2);
        adapter = new FeaturesAdapter(getActivity(), android.R.layout.simple_list_item_1,  wfeature);
        listview.setAdapter(adapter);
        Button save_button = (Button) v.findViewById(R.id.button2_dialog_features_SAVE);
    	Button unsub_button = (Button) v.findViewById(R.id.button1_dialog_features_UNSUBSCRIBE);
    	
    	save_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean[] adapterFeatStatus = adapter.getFeaturesStatus();
				for(int i=0; i<wfeature.length;i++){
					if (wfeature[i].isIsChosen() != adapterFeatStatus[i]){
						//invia la richiesta di salvataggio..
						updateFeatures(adapterFeatStatus);
						break;
					}
				}
				SetServiceFeaturesDialog.this.dismiss();
			}
		});
    	
    	unsub_button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				mListener.unregisterService(wservice.getId());
				SetServiceFeaturesDialog.this.dismiss();
			}
		});
        return v;
    }

	
	protected void updateFeatures(Boolean[] adapterFeatStatus) {
		int count = 0;
		String features = "[";
		for (int i=0; i<adapterFeatStatus.length;i++){
			
			if (adapterFeatStatus[i] == true){
				if (count == 0){
					features += " \"" + wfeature[i].getName() + "\"";
					count++;
				}else{
					features += " , \"" + wfeature[i].getName() + "\"";
					
				}
			}
		}
		features += "]";
		mListener.saveFeaturesStatus(wservice.getId(), features);
	}
	
	
}
	

