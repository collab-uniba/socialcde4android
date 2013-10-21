package it.uniba.socialcde4android.shared.library;

import android.os.Parcel;
import android.os.Parcelable;

public class WUser implements Parcelable{

	// / <summary>
	// / Identifier of the user.
	// / </summary>
	public int Id;

	// / <summary>
	// / Identification name of the user.
	// / </summary>
	public String Username;

	// / <summary>
	// / Email address of the user.
	// / </summary>
	public String Email;

	// / <summary>
	// / Image avatar of the user.
	// / </summary>
	public String Avatar;

	// / <summary>
	// / Number of statuses written by the user stored in the database.
	// / </summary>
	public int Statuses;

	// / <summary>
	// / Number of followings of the user.
	// / </summary>
	public int Followings;

	// / <summary>
	// / Number of followers of the user.
	// / </summary>
	public int Followers;

	// / <summary>
	// / True if current user follow this user, false otherwise.
	// / </summary>
	public boolean Followed;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		this.Id = id;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		this.Username = username;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		this.Email = email;
	}

	public String getAvatar() {
		return Avatar;
	}

	public void setAvatar(String avatar) {
		this.Avatar = avatar;
	}

	public int getStatuses() {
		return Statuses;
	}

	public void setStatuses(int statuses) {
		this.Statuses = statuses;
	}

	public int getFollowings() {
		return Followings;
	}

	public void setFollowings(int followings) {
		this.Followings = followings;
	}

	public int getFollowers() {
		return Followers;
	}

	public void setFollowers(int followers) {
		this.Followers = followers;
	}

	public boolean isFollowed() {
		return Followed;
	}

	public void setFollowed(boolean followed) {
		this.Followed = followed;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String toString(){
		String temp = "";
		temp+="Id: " + Id + " Username: " + Username + " Email: " + Email + " Avatar: " 
		+ Avatar + " Statuses: " + Statuses + " Followings: " + Followings + 
		" Followers: " + Followers  +
		" Followed: " + (Followed ? "true" : "false");
		
		return temp;
		
	}
	

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeInt(Id);
		out.writeString(Username);
		out.writeString(Email);
		out.writeString(Avatar);
		out.writeInt(Statuses);
		out.writeInt(Followings);
		out.writeInt(Followers);
		out.writeByte((byte) (Followed ? 1 : 0));
	}

	
	public static final Parcelable.Creator<WUser> CREATOR = new Parcelable.Creator<WUser>() {
		public WUser createFromParcel(Parcel in) {
			return new WUser(in);
		}

		public WUser[] newArray(int size) {
			return new WUser[size];
		}
	};
	
	
	/** recreate object from parcel */
	private WUser(Parcel in) {


		Id = in.readInt();
		Username = in. readString();
		Email = in. readString();
		Avatar = in. readString();
		Statuses = in. readInt();
		Followings = in. readInt();
		Followers = in. readInt();
		Followed = in.readByte() != 0;

		}

	public WUser() {
		// TODO Auto-generated constructor stub
	}
}
