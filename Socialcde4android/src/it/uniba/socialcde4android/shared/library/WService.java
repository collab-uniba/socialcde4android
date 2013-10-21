package it.uniba.socialcde4android.shared.library;

import android.os.Parcel;
import android.os.Parcelable;

public class WService  implements Parcelable{
	// / <summary>
	// / Identifier of the service.
	// / </summary>
	public int Id;

	// / <summary>
	// / Name of the service.
	// / </summary>
	public String Name;

	// / <summary>
	// / Host of the service.
	// / </summary>
	public String Host;

	// / <summary>
	// / Service to the base of the service.
	// / </summary>
	public String BaseService;

	// / <summary>
	// / Image logo of the service.
	// / </summary>
	public String Image;

	// / <summary>
	// / True if the current user is registered to the service. False otherwise.
	// / </summary>
	public boolean Registered;

	// / <summary>
	// / True if the service require OAuth procedure, false otherwise.
	// / </summary>
	public boolean RequireOAuth;

	// / <summary>
	// / Version of OAuth procedure required.
	// / </summary>
	public int OAuthVersion;

	// / <summary>
	// / True if the service require TFS authetication procedure, false
	// otherwise.
	// / </summary>
	public boolean RequireTFSAuthentication;

	// / <summary>
	// / True if the TFS authetication procedure require domain, false
	// otherwise.
	// / </summary>
	public boolean RequireTFSDomain;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getHost() {
		return Host;
	}

	public void setHost(String host) {
		Host = host;
	}

	public String getBaseService() {
		return BaseService;
	}

	public void setBaseService(String baseService) {
		BaseService = baseService;
	}

	public String getImage() {
		return Image;
	}

	public void setImage(String image) {
		Image = image;
	}

	public boolean isRegistered() {
		return Registered;
	}

	public void setRegistered(boolean registered) {
		Registered = registered;
	}

	public boolean isRequireOAuth() {
		return RequireOAuth;
	}

	public void setRequireOAuth(boolean requireOAuth) {
		RequireOAuth = requireOAuth;
	}

	public int getOAuthVersion() {
		return OAuthVersion;
	}

	public void setOAuthVersion(int oAuthVersion) {
		OAuthVersion = oAuthVersion;
	}

	public boolean isRequireTFSAuthentication() {
		return RequireTFSAuthentication;
	}

	public void setRequireTFSAuthentication(boolean requireTFSAuthentication) {
		RequireTFSAuthentication = requireTFSAuthentication;
	}

	public boolean isRequireTFSDomain() {
		return RequireTFSDomain;
	}

	public void setRequireTFSDomain(boolean requireTFSDomain) {
		RequireTFSDomain = requireTFSDomain;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String toString(){
		String temp = "";
		temp+="Id: " + Id + " Name: " + Name + " Host: " + Host + " BaseService: " 
		+ BaseService + " Image: " + Image + " Registered: " +(Registered ? "true" : "false") + 
		" RequireOAuth: " + (RequireOAuth ? "true" : "false") + " OAuthVersion: " + OAuthVersion +
		" RequireTFSAuthentication: " + (RequireTFSAuthentication ? "true" : "false") +
		" RequireTFSDomain: " + (RequireTFSDomain ? "true" : "false");
		
		return temp;
		
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeInt(Id);
		out.writeString(Name);
		out.writeString(Host);
		out.writeString(BaseService);
		out.writeString(Image);
		out.writeByte((byte) (Registered ? 1 : 0));
		out.writeByte((byte) (RequireOAuth ? 1 : 0));
		out.writeInt(OAuthVersion);
		out.writeByte((byte) (RequireTFSAuthentication ? 1 : 0));
		out.writeByte((byte) (RequireTFSDomain ? 1 : 0));
	}

	public static final Parcelable.Creator<WService> CREATOR = new Parcelable.Creator<WService>() {
		public WService createFromParcel(Parcel in) {
			return new WService(in);
		}

		public WService[] newArray(int size) {
			return new WService[size];
		}
	};
	
	
	/** recreate object from parcel */
	private WService(Parcel in) {
		Id = in.readInt();
		Name = in. readString();
		Host = in. readString();
		BaseService = in. readString();
		Image = in. readString();
		Registered = in.readByte() != 0;
		RequireOAuth = in.readByte() != 0;
		OAuthVersion = in.readInt();
		RequireTFSAuthentication = in.readByte() != 0;
		RequireTFSDomain = in.readByte() != 0;

		}
}
