package it.uniba.socialcde4android.shared.library;

import android.os.Parcel;
import android.os.Parcelable;

public class WFeature implements Parcelable{

	public String Name;

	// / <summary>
	// / Description of the Feature.
	// / </summary>

	public String Description;

	// / <summary>
	// / True if the current user have chosed the feature, false otherwise.
	// / </summary>

	public boolean IsChosen;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public boolean isIsChosen() {
		return IsChosen;
	}

	public void setIsChosen(boolean isChosen) {
		IsChosen = isChosen;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(Name);
		out.writeString(Description);
		out.writeByte((byte) (IsChosen ? 1 : 0));
	}
	
	
	public static final Parcelable.Creator<WFeature> CREATOR = new Parcelable.Creator<WFeature>() {
		public WFeature createFromParcel(Parcel in) {
			return new WFeature(in);
		}

		public WFeature[] newArray(int size) {
			return new WFeature[size];
		}
	};
	

	/** recreate object from parcel */
	private WFeature(Parcel in) {


		Name = in.readString();
		Description = in.readString();
		IsChosen = in.readByte() != 0;
		}
	
	public String toString(){
		String temp = "";
		temp+="Name: " + Name + " Description: " + Description + 
		" IsChosen: " + (IsChosen ? "true" : "false");
		
		return temp;
		
	}
}
