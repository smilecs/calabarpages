package ng.com.calabaryellowpages.Services;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import ng.com.calabaryellowpages.Model.Review;
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
        Review value = (Review) intent.getSerializableExtra("data");
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("Comment", value.getComment());
            jsonObject.put("Name", value.getName());
            jsonObject.put("SocialId", value.getID());
            jsonObject.put("Slug", value.getSlug());
            //jsonObject.put("ImageUrl", value.getImageUrl());
            jsonObject.put("Rating", value.getScore());
        }catch (Exception e){
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, volleySingleton.URL + "api/add_review", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        queue.add(jsonObjectRequest);

    }

}
