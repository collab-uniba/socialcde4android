package it.uniba.socialcde4android.adapters;

import it.uniba.socialcde4android.R;
import it.uniba.socialcde4android.shared.library.WFeature;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class FeaturesAdapter extends ArrayAdapter<WFeature>{

	private LayoutInflater infalInflater;
	private Boolean[] feat_status;


	public FeaturesAdapter(Context context, int resource, WFeature[] wfeature) {
		super(context, resource, wfeature);
		// TODO Auto-generated constructor stub
		infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		feat_status = new Boolean[wfeature.length];
		for (int i=0; i< wfeature.length;i++){
			this.feat_status[i] = wfeature[i].isIsChosen();
		}
	}


	public Boolean[] getFeaturesStatus(){
		return feat_status;
	}
	
	@Override
	public View getView(int position, View rowView, ViewGroup parent) 
	{

		if (rowView == null){
			rowView = infalInflater.inflate(R.layout.item_checkbox_feature, null);
			WFeature wfeature = (WFeature) FeaturesAdapter.this.getItem(position);
			CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.checkBox1_feature);
			checkbox.setText(wfeature.getDescription());
			checkbox.setChecked(wfeature.isIsChosen());
			final int pos = position;
			checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				   @Override
				   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					   FeaturesAdapter.this.feat_status[pos] = isChecked;
				   }
				});
		}
		return rowView;
	}
}
