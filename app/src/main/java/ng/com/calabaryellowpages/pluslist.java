package ng.com.calabaryellowpages;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ng.com.calabaryellowpages.Adapters.GalleryAdapter;
import ng.com.calabaryellowpages.Model.Category;
import ng.com.calabaryellowpages.Model.Review;
import ng.com.calabaryellowpages.util.Parse;
import ng.com.calabaryellowpages.util.volleySingleton;

import com.facebook.ads.*;

public class pluslist extends AppCompatActivity {
    ArrayList<ng.com.calabaryellowpages.Model.Category> model;
    RecyclerView rv;
    RecyclerView.LayoutManager manager;
    GalleryAdapter mAdapter;
    ng.com.calabaryellowpages.Model.Category data;
    volleySingleton volleySingleton;
    CollapsingToolbarLayout col;
    FloatingActionButton callButton;
    ImageView imageView;
    TextView special, title, phone, address, work_days, web, description;
    RequestQueue queue;
    volleySingleton volleySin;
    RatingBar ratingBar;
    TextView baseScore;
    ArrayList<Review> reviewArrayList;
    CardView ReviewA, mainCard;
    String query = "";
    private NativeAd nativeAd;
    private AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pluslist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        volleySin = ng.com.calabaryellowpages.util.volleySingleton.getsInstance();
        queue = volleySin.getmRequestQueue();
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mainCard = (CardView) findViewById(R.id.mainCard);
        setSupportActionBar(toolbar);

        col = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Typeface items = Typeface.createFromAsset(getAssets(),
              "fonts/RobotoCondensed-Light.ttf");
        Typeface desc = Typeface.createFromAsset(getAssets(),
              "fonts/Roboto-Thin.ttf");
        data = (Category) getIntent().getSerializableExtra("data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(data.getTitle());
        reviewArrayList = new ArrayList<>();
        model = new ArrayList<>();
        imageView = (ImageView) findViewById(R.id.image);
        try {
            if (!data.getImage().isEmpty()) {
                ImageLoader imageLoader = volleySingleton.getsInstance().getImageLoader();
                imageLoader.get(data.getImage(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        imageView.setImageBitmap(imageContainer.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        Typeface robot = Typeface.createFromAsset(getAssets(),
              "fonts/Roboto-Thin.ttf");
        Typeface addressType = Typeface.createFromAsset(getAssets(), "fonts/Roboto-LightItalic.ttf");
        Typeface regular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface robotBold = Typeface.createFromAsset(getAssets(),
              "fonts/Roboto-Black.ttf");
        callButton = (FloatingActionButton) findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + Parse.Parse(data.getPhone())[0]));
                startActivity(callIntent);
            }
        });
        web = (TextView) findViewById(R.id.website);
        web.setText(data.getWeb());
        description = (TextView) findViewById(R.id.description);
        description.setTypeface(desc);
        try {
            if (!data.getDescription().isEmpty()) {
                description.setText(data.getDescription());
            }
        } catch (Exception e) {

        }
        phone = (TextView) findViewById(R.id.contact);
        phone.setTypeface(robot);
        phone.setText(data.getPhone());
        address = (TextView) findViewById(R.id.address);
        address.setTypeface(addressType);
        address.setText(data.getAddress());
        special = (TextView) findViewById(R.id.specialisation);
        special.setTypeface(robotBold);
        special.setText(data.getSpecialisation());
        work_days = (TextView) findViewById(R.id.workingDays);
        work_days.setTypeface(robot);
        work_days.setText(data.getWork_days());
        ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
        baseScore = (TextView) findViewById(R.id.summary);
        rv = (RecyclerView) findViewById(R.id.recycler);
        try {
            for (int i = 0; i < data.getImages().length; i++) {
                Log.d("pluslist", Integer.toString(data.getImages().length));
                Category mode = new Category();
                mode.setImage(data.getImages()[i]);
                Log.d("pluslist", data.getImages()[i]);
                Log.d("pluslist", mode.getImage().toString());
                model.add(mode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReviewA = (CardView) findViewById(R.id.content_review);
        ReviewA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ReviewActivity.class);
                intent.putExtra("model", reviewArrayList);
                intent.putExtra("slug", data.getSlug());
                startActivity(intent);
            }
        });

        mAdapter = new GalleryAdapter(model);
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(mAdapter);
        try {
            query = URLEncoder.encode(data.getSlug(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadReviews();
        showNativeAd();
    }

    private void showNativeAd() {
        nativeAd = new NativeAd(this, "1035586836549797_1183764911731988");
        nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (nativeAd != null) {
                    nativeAd.unregisterView();
                }

                // Add the Ad view into the ad container.
                LinearLayout nativeAdContainer = (LinearLayout) findViewById(R.id.native_ad_container);
                LayoutInflater inflater = LayoutInflater.from(pluslist.this);
                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                View adView = inflater.inflate(R.layout.ad_layout, nativeAdContainer, false);
                nativeAdContainer.addView(adView);

                // Create native UI using the ad metadata.
                ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
                TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
                MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
                TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
                TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
                Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdTitle());
                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdBody.setText(nativeAd.getAdBody());
                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

                // Download and display the ad icon.
                NativeAd.Image adIcon = nativeAd.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                // Download and display the cover image.
                nativeAdMedia.setNativeAd(nativeAd);

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = (LinearLayout) findViewById(R.id.ad_choices_container);
                AdChoicesView adChoicesView = new AdChoicesView(pluslist.this, nativeAd, true);
                adChoicesContainer.addView(adChoicesView);

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);
                nativeAd.registerViewForInteraction(nativeAdContainer, clickableViews);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        // Request an ad
        nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
    }

    private void loadReviews() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ng.com.calabaryellowpages.util.volleySingleton.URL + "/api/get_reviews?p=1&q=" + query, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int base = 0;
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Review review = new Review();
                        JSONObject json = jsonArray.getJSONObject(i);
                        review.setComment(json.getString("Comment"));
                        review.setName(json.getString("Name"));
                        review.setScore(json.getInt("Rating"));
                        base += review.getScore();
                        reviewArrayList.add(review);
                    }
                    ratingBar.setRating(base / jsonArray.length());
                    baseScore.setText(getString(R.string.reviews, String.valueOf(jsonArray.length())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReviews();
    }
}
