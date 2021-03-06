package ng.com.calabaryellowpages.util;

/**
 * Created by smile on 1/12/16.
 */

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by smilecs on 6/3/15.
 */
public class VolleySingleton {
    private static VolleySingleton sInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader imageLoader;
    //public static final String URL = "http://192.168.43.225:8080/";
    public static final String URL = "https://calabarpages.com/";

    private VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(Application.getAppContext());
        imageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);


            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

    }



    public static VolleySingleton getsInstance(){
        if(sInstance == null){
            sInstance = new VolleySingleton();
        }
        return sInstance;
    }
    public RequestQueue getmRequestQueue(){
        return mRequestQueue;
    }
    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
