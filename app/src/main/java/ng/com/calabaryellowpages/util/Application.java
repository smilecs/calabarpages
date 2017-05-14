package ng.com.calabaryellowpages.util;

import android.content.Context;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by SMILECS on 4/19/16.
 */
public class Application extends android.app.Application{
    private static Application sInstance;
    private static AppEventsLogger logR;


    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        sInstance = this;
    }
    public static Application getsInstance(){
        return sInstance;
    }
    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
    public static AppEventsLogger logger(){
        if(logR == null){
            logR = AppEventsLogger.newLogger(getsInstance());
        }
        return logR;
    }
    public static void logViewedContentEvent (String contentType, String contentId) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        logger().logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params);
    }

    public static void logRatedEvent (String contentType, String contentId, int maxRatingValue, double ratingGiven) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putInt(AppEventsConstants.EVENT_PARAM_MAX_RATING_VALUE, maxRatingValue);
        logger().logEvent(AppEventsConstants.EVENT_NAME_RATED, ratingGiven, params);
    }

    public static void logCompletedRegistrationEvent (String registrationMethod) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, registrationMethod);
        logger().logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, params);
    }
    public static void logCategorySelectedEvent (String categoryName) {
        Bundle params = new Bundle();
        params.putString("categoryName", categoryName);
        logger().logEvent("CategorySelected", params);
    }

    public static void logListingsSelectedEvent (String listingName, String listingsSlug) {
        Bundle params = new Bundle();
        params.putString("listingName", listingName);
        params.putString("listingsSlug", listingsSlug);
        logger().logEvent("ListingsSelected", params);
    }
}
