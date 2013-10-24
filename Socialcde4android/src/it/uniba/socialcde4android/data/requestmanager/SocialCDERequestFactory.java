/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package it.uniba.socialcde4android.data.requestmanager;

import android.os.Parcel;

import com.foxykeep.datadroid.requestmanager.Request;

/**
 * Class used to create the {@link Request}s.
 *
 * @author Foxykeep, Francesco Pontillo
 */
public final class SocialCDERequestFactory {

    // Request types
    public static final int RETREIVE_WSERVICES = 0;
    public static final int WEBSERVER_AVAILABLE = 1;
    public static final int USERNAME_AVAILABLE = 2;
    public static final int SUBSCRIBE_USER = 3;
	public static final int CHANGE_INVITATION_WITH_PASSWORD = 4;
	public static final int GET_USER = 5;
	public static final int GET_FRIENDS = 6;
	public static final int GET_COLLEAGUE = 7;
	public static final int SET_FOLLOWED = 8;





    // Response data
    public static final String BUNDLE_USER = "it.uniba.socialcde4android.extra.USER";
    public static final String BUNDLE_DESC = "it.uniba.socialcde4android.extra.DESC";
	

    private SocialCDERequestFactory() {
        // no public constructor
    }

    /**
     * Create the send report request.
     * @return The request.
     */
    public static Request getWServiceRequest() {
        Request request = new Request(RETREIVE_WSERVICES);
        return request;
    }
    
    /**
     * Creates the get report request.
     * @return The request.
     */
    public static Request isWebServRunRequest() {
        Request request = new Request(WEBSERVER_AVAILABLE);
        return request;
    }

	public static Request isUsernameAvailable() {
		Request request = new Request(USERNAME_AVAILABLE);		
		return request;
	}

	public static Request subscribeUser() {
		Request request = new Request(SUBSCRIBE_USER);		
		return request;
	}

	public static Request changeInvWithPass() {
		Request request = new Request(CHANGE_INVITATION_WITH_PASSWORD);		
		return request;
	}

	public static Request getWUser() {
		Request request = new Request(GET_USER);		
		return request;
	}



	public static Request GetFriends() {
		Request request = new Request(GET_FRIENDS);		
		return request;
	}

	public static Request getColleagueProfileRequest() {
		Request request = new Request(GET_COLLEAGUE);		
		return request;
	}

	public static Request setFollowed() {
		Request request = new Request(SET_FOLLOWED);		
		return request;
	}

	

}
