/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package it.uniba.socialcde4android.data.requestmanager;

import it.uniba.socialcde4android.data.service.SocialCDERequestService;

import com.foxykeep.datadroid.requestmanager.RequestManager;

import android.content.Context;

/**
 * This class is used as a proxy to call the Service. It provides easy-to-use methods to call the
 * service and manages the Intent creation. It also assures that a request will not be sent again if
 * an exactly identical one is already in progress.
 *
 * @author Foxykeep, Francesco Ditrani
 */
public final class SocialCDERequestManager extends RequestManager {

    // Singleton management
    private static SocialCDERequestManager sInstance;

    public synchronized static SocialCDERequestManager from(Context context) {
        if (sInstance == null) {
            sInstance = new SocialCDERequestManager(context);
        }

        return sInstance;
    }

    
    public synchronized static SocialCDERequestManager newIstancefrom(Context context) {
         return new SocialCDERequestManager(context);
    }
    
    
    private SocialCDERequestManager(Context context) {
        super(context, SocialCDERequestService.class);
    }
}
