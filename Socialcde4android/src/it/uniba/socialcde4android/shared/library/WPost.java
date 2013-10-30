package it.uniba.socialcde4android.shared.library;

import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class WPost implements Parcelable{
	// / <summary>
	// / Identifier of the post.
	// / </summary>
	public long Id;

	// / <summary>
	// / Name of the author of the post.
	// / </summary>
	public WUser User;

	// / <summary>
	// / Name of the service.
	// / </summary>
	public WService Service;

	// / <summary>
	// / Message of the post.
	// / </summary>
	public String Message;

	// / <summary>
	// / Creation date of the post.
	// / </summary>
	public Calendar CreateAt;

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public WUser getUser() {
		return User;
	}

	public void setUser(WUser user) {
		User = user;
	}

	public WService getService() {
		return Service;
	}

	public void setService(WService service) {
		Service = service;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public Calendar getCreateAt() {
		return CreateAt;
	}

	public void setCreateAt(Calendar createAt) {
		CreateAt = createAt;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(User, flags);
		out.writeParcelable(Service, flags);
		out.writeString(Message);
		out.writeSerializable(CreateAt);
	}
	

	public static final Parcelable.Creator<WPost> CREATOR = new Parcelable.Creator<WPost>() {
		public WPost createFromParcel(Parcel in) {
			return new WPost(in);
		}

		public WPost[] newArray(int size) {
			return new WPost[size];
		}
	};
	
	
	/** recreate object from parcel */
	private WPost(Parcel in) {
		User = in.readParcelable(getClass().getClassLoader());
		Service = in.readParcelable(getClass().getClassLoader());
		Message = in.readString();
		CreateAt = (Calendar) in.readSerializable();
		}
	
	public WPost(){
		// TODO Auto-generated constructor stub
	}
}
