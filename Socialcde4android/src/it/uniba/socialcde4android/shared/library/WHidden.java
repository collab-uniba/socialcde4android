package it.uniba.socialcde4android.shared.library;

import android.os.Parcel;
import android.os.Parcelable;

public class WHidden implements Parcelable {

	public boolean Suggestions;

	// / <summary>
	// / Is hidden in dynamic timeline.
	// / </summary>

	public boolean Dynamic;

	// / <summary>
	// / Is hidden in interactive timeline.
	// / </summary>

	public boolean Interactive;

	public boolean isSuggestions() {
		return Suggestions;
	}

	public void setSuggestions(boolean suggestions) {
		Suggestions = suggestions;
	}

	public boolean isDynamic() {
		return Dynamic;
	}

	public void setDynamic(boolean dynamic) {
		Dynamic = dynamic;
	}

	public boolean isInteractive() {
		return Interactive;
	}

	public void setInteractive(boolean interactive) {
		Interactive = interactive;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeByte((byte) (Suggestions ? 1 : 0));
		out.writeByte((byte) (Dynamic ? 1 : 0));
		out.writeByte((byte) (Interactive ? 1 : 0));
	}

	public static final Parcelable.Creator<WHidden> CREATOR = new Parcelable.Creator<WHidden>() {
		public WHidden createFromParcel(Parcel in) {
			return new WHidden(in);
		}

		public WHidden[] newArray(int size) {
			return new WHidden[size];
		}
	};
	
	
	/** recreate object from parcel */
	private WHidden(Parcel in) {


		Suggestions = in.readByte() != 0;
		Dynamic = in.readByte() != 0;
		Interactive = in.readByte() != 0;
		}
	
	
	public WHidden() {
		// TODO Auto-generated constructor stub
	}

	public String toString(){
		String temp = "";
		temp+="Suggestions: " + (Suggestions ? "true" : "false") + " Dynamic: " 
		+ (Dynamic ? "true" : "false") + 	" Interactive: " + (Interactive ? "true" : "false");
		
		return temp;
		
	}
}
