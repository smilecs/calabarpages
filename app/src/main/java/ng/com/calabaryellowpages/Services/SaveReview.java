package ng.com.calabaryellowpages.Services;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.RequestQueue;

import ng.com.calabaryellowpages.util.volleySingleton;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SaveReview extends IntentService {
    volleySingleton volley;
    RequestQueue queue;
    public SaveReview() {
        super("SaveReview");
        volley = volleySingleton.getsInstance();
        queue = volley.getmRequestQueue();

    }


    @Override
    protected void onHandleIntent(Intent intent) {

    }

}
