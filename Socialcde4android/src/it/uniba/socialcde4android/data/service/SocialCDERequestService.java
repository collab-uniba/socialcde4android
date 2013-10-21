/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package it.uniba.socialcde4android.data.service;



import it.uniba.socialcde4android.data.operation.ChangeInvitationWithPassword_Operation;
import it.uniba.socialcde4android.data.operation.GetFollowers_Operation;
import it.uniba.socialcde4android.data.operation.GetFollowings_Operation;
import it.uniba.socialcde4android.data.operation.GetHidden_Operation;
import it.uniba.socialcde4android.data.operation.GetSuggestedFriend_Operation;
import it.uniba.socialcde4android.data.operation.GetUser_Operation;
import it.uniba.socialcde4android.data.operation.IsUsernameAvailable_Operation;
import it.uniba.socialcde4android.data.operation.IsWebServiceRunning_Operation;
import it.uniba.socialcde4android.data.operation.RetrieveServices_Operation;
import it.uniba.socialcde4android.data.operation.SubscribeUser_Operation;
import it.uniba.socialcde4android.data.requestmanager.SocialCDERequestFactory;

import com.foxykeep.datadroid.service.RequestService;

import android.content.Intent;

/**
 * This class is called by the {@link SocialCDERequestManager} through the {@link Intent} system.
 *
 * @author Foxykeep, Francesco Ditrani
 */
public final class SocialCDERequestService extends RequestService {

	@Override
	protected int getMaximumNumberOfThreads() {
		return 3;
	}

	@Override
	public Operation getOperationForType(int requestType) {
		switch (requestType) {
		case SocialCDERequestFactory.WEBSERVER_AVAILABLE:
			return new IsWebServiceRunning_Operation();
		case SocialCDERequestFactory.RETREIVE_WSERVICES:
			return new RetrieveServices_Operation();
		case SocialCDERequestFactory.USERNAME_AVAILABLE:
			return new IsUsernameAvailable_Operation();
		case SocialCDERequestFactory.SUBSCRIBE_USER:
			return new SubscribeUser_Operation();
		case SocialCDERequestFactory.CHANGE_INVITATION_WITH_PASSWORD:
			return new ChangeInvitationWithPassword_Operation();
		case SocialCDERequestFactory.GET_USER:
			return new GetUser_Operation();
		case SocialCDERequestFactory.GET_SUGGESTED_FRIEND:
			return new GetSuggestedFriend_Operation();
		case SocialCDERequestFactory.GET_FOLLOWINGS:
			return new GetFollowings_Operation();
		case SocialCDERequestFactory.GET_FOLLOWERS:
			return new GetFollowers_Operation();
		case SocialCDERequestFactory.GET_HIDDEN:
			return new GetHidden_Operation();
		
		}
		
		return null;
	}
}
