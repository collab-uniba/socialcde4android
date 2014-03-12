/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package it.uniba.socialcde4android.data.service;



import it.uniba.socialcde4android.data.operation.Authorize_Operation;
import it.uniba.socialcde4android.data.operation.ChangePasswordWithPassword_Operation;
import it.uniba.socialcde4android.data.operation.GetColleagueProfile_Operation;
import it.uniba.socialcde4android.data.operation.GetFeatures_Operation;
import it.uniba.socialcde4android.data.operation.GetFriends_Operation;
import it.uniba.socialcde4android.data.operation.GetOAuthData_Operation;
import it.uniba.socialcde4android.data.operation.GetUser_Operation;
import it.uniba.socialcde4android.data.operation.IsUsernameAvailable_Operation;
import it.uniba.socialcde4android.data.operation.IsWebServiceRunning_Operation;
import it.uniba.socialcde4android.data.operation.RecordService_Operation;
import it.uniba.socialcde4android.data.operation.RetrieveServices_Operation;
import it.uniba.socialcde4android.data.operation.SetActiveFeatures_Operation;
import it.uniba.socialcde4android.data.operation.SetFollowed_Operation;
import it.uniba.socialcde4android.data.operation.SubscribeUser_Operation;
import it.uniba.socialcde4android.data.operation.UnregisterService_Operation;
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
			return new ChangePasswordWithPassword_Operation();
		case SocialCDERequestFactory.GET_USER:
			return new GetUser_Operation();
		case SocialCDERequestFactory.GET_FRIENDS:
			return new GetFriends_Operation();
		case SocialCDERequestFactory.GET_COLLEAGUE:
			return new GetColleagueProfile_Operation();
		case SocialCDERequestFactory.SET_FOLLOWED:
			return new SetFollowed_Operation();
		case SocialCDERequestFactory.GET_OAUTHDATA:
			return new GetOAuthData_Operation();
		case SocialCDERequestFactory.AUTHORIZE:
			return new Authorize_Operation();
		case SocialCDERequestFactory.GET_FEATURES:
			return new GetFeatures_Operation();
		case SocialCDERequestFactory.SET_FEATURES:
			return new SetActiveFeatures_Operation();
		case SocialCDERequestFactory.UNREG_SERVICE:
			return new UnregisterService_Operation();
		case SocialCDERequestFactory.RECORD_SERVICE:
			return new RecordService_Operation();
		}
		
		
		return null;
	}
}
