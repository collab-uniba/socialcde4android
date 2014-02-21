package it.uniba.socialcde4android.shared.library;

import android.os.Parcel;
import android.os.Parcelable;

public class WOAuthData implements Parcelable{
	// / <summary>
	// / Link to authorization page of the service instance.
	// / </summary>
	public String AuthorizationLink;
	// / <summary>
	// / Access Token of the service instance.
	// / </summary>
	public String AccessToken;

	// / <summary>
	// / AccessSecret of the service instance.
	// / </summary>
	public String AccessSecret;

	public String getAuthorizationLink() {
		return AuthorizationLink;
	}

	public void setAuthorizationLink(String authorizationLink) {
		AuthorizationLink = authorizationLink;
	}

	public String getAccessToken() {
		return AccessToken;
	}

	public void setAccessToken(String accessToken) {
		AccessToken = accessToken;
	}

	public String getAccessSecret() {
		return AccessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		AccessSecret = accessSecret;
	}

	public String toString(){
		String temp = "";
		temp+="AuthorizationLink: " + AuthorizationLink 
				+ " AccessToken: " + AccessToken + " AccessSecret: " + AccessSecret ;
		
		return temp;
		
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(AuthorizationLink);
		out.writeString(AccessToken);
		out.writeString(AccessSecret);
	}
	
	public static final Parcelable.Creator<WOAuthData> CREATOR = new Parcelable.Creator<WOAuthData>() {
		public WOAuthData createFromParcel(Parcel in) {
			return new WOAuthData(in);
		}

		public WOAuthData[] newArray(int size) {
			return new WOAuthData[size];
		}
	};
	
	/** recreate object from parcel */
	private WOAuthData(Parcel in) {
		
		AuthorizationLink = in. readString();
		AccessToken = in. readString();
		AccessSecret = in. readString();
		
		}
	
	
}
