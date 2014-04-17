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
 * @author Foxykeep, Francesco Ditrani
 */
public final class SocialCDERequestFactory {

    // Request types
    public static final int RETREIVE_WSERVICES = 0;
    public static final int WEBSERVER_AVAILABLE = 1;
    public static final int USERNAME_AVAILABLE = 2;
    public static final int SUBSCRIBE_USER = 3;
	public static final int CHANGE_PASSWORD = 4;
	public static final int GET_USER = 5;
	public static final int GET_FRIENDS = 6;
	public static final int GET_COLLEAGUE = 7;
	public static final int SET_FOLLOWED = 8;
	public static final int GET_OAUTHDATA = 9;
	public static final int AUTHORIZE = 10;
	public static final int GET_FEATURES = 11;
	public static final int SET_FEATURES = 12;
	public static final int UNREG_SERVICE = 13;
	public static final int RECORD_SERVICE = 14;
	public static final int SEND_TFSPOST = 15;
	public static final int GET_HIDE_SETTINGS = 16;
	public static final int SET_HIDE_SETTINGS = 17;
	public static final int GET_AVAILABLE_AVATARS = 18;
	public static final int SET_AVATAR = 19;
	public static final int GET_WPOSTS = 20;


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

	public static Request changePass() {
		Request request = new Request(CHANGE_PASSWORD);		
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

	public static Request getOAuthDataRequest() {
		Request request = new Request(GET_OAUTHDATA);		
		return request;
	}

	public static Request authorize() {
		Request request = new Request(AUTHORIZE);		
		return request;
	}

	public static Request getFeatures() {
		Request request = new Request(GET_FEATURES);		
		return request;
	}

	public static Request setActiveFeatures() {
		Request request = new Request(SET_FEATURES);		
		return request;
	}

	public static Request unregisterService() {
		Request request = new Request(UNREG_SERVICE);		
		return request;
	}

	public static Request recordService() {
		Request request = new Request(RECORD_SERVICE);		
		return request;
	}

	public static Request sendTFSpost() {
		Request request = new Request(SEND_TFSPOST);		
		return request;
	}

	public static Request getHideSettings() {
		Request request = new Request(GET_HIDE_SETTINGS);		
		return request;
	}

	public static Request setHideSettings() {
		Request request = new Request(SET_HIDE_SETTINGS);		
		return request;
	}

	public static Request getAvailableAvatars() {
		Request request = new Request(GET_AVAILABLE_AVATARS);		
		return request;
	}

	public static Request setAvatar() {
		Request request = new Request(SET_AVATAR);		
		return request;
	}

	public static Request getWPosts() {
		Request request = new Request(GET_WPOSTS);		
		return request;
	}

	

}
