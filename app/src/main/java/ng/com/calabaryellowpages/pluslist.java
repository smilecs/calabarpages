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
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;

import ng.com.calabaryellowpages.Adapters.GalleryAdapter;
import ng.com.calabaryellowpages.Model.Category;
import ng.com.calabaryellowpages.Model.Review;
import ng.com.calabaryellowpages.util.Parse;
import ng.com.calabaryellowpages.util.volleySingleton;

public class pluslist extends AppCompatActivity{
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
    private AppBarLayout mAppBarLayout;
    RequestQueue queue;
    volleySingleton volleySin;
    RatingBar ratingBar;
    TextView baseScore;
    ArrayList<Review> reviewArrayList;
    CardView ReviewA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pluslist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        volleySin = ng.com.calabaryellowpages.util.volleySingleton.getsInstance();
        queue = volleySin.getmRequestQueue();
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        try{
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }catch (Exception e){
            e.printStackTrace();
        }
        setSupportActionBar(toolbar);

        col = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Typeface items = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Light.ttf");
        Typeface desc = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Thin.ttf");
        data = (Category) getIntent().getSerializableExtra("data");
        getSupportActionBar().setTitle(data.getTitle());
        reviewArrayList = new ArrayList<>();
        model = new ArrayList<>();
        imageView = (ImageView) findViewById(R.id.image);
        try{
            if(!data.getImage().isEmpty()){
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
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }

        imageView.setImageDrawable(getResources().getDrawable(R.drawable.yellowpages));
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
        description.setText(data.getDescription());
        //title = (TextView) findViewById(R.id.title);
        phone = (TextView) findViewById(R.id.contact);
        phone.setTypeface(items);
        phone.setText(data.getPhone());
        address = (TextView) findViewById(R.id.address);
        address.setTypeface(items);
        address.setText(data.getAddress());
        special = (TextView) findViewById(R.id.specialisation);
        special.setTypeface(items);
        special.setText(data.getSpecialisation());
        work_days = (TextView) findViewById(R.id.workingDays);
        work_days.setTypeface(items);
        work_days.setText(data.getWork_days());
        ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
        baseScore = (TextView) findViewById(R.id.summary);
        rv = (RecyclerView) findViewById(R.id.recycler);
        try{
            for(int i=0; i<data.getImages().length; i++){
                Log.d("pluslist", Integer.toString(data.getImages().length));
                Category mode = new Category();
                mode.setImage(data.getImages()[i]);
                Log.d("pluslist", data.getImages()[i]);
                Log.d("pluslist", mode.getImage().toString());
                model.add(mode);
            }
        }catch (Exception e){
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
      /* if(data.getImages().length > 0){
            ImageLoader imageLoader = volleySingleton.getsInstance().getImageLoader();
            imageLoader.get(data.getImages()[0], new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    imageView.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        }*/
        mAdapter = new GalleryAdapter(model);
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(mAdapter);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ng.com.calabaryellowpages.util.volleySingleton.URL + "/api/get_reviews?p=0&q=" + data.getSlug(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int base = 0;
                try{
                    JSONArray jsonArray = jsonObject.getJSONArray("Posts");
                    for(int i = 0; i < jsonArray.length(); i++){
                        Review review = new Review();
                        JSONObject json = jsonArray.getJSONObject(i);
                        review.setComment(json.getString("Comment"));
                        review.setName(json.getString("Name"));
                        review.setScore(json.getInt("Rating"));
                        base += review.getScore();
                        reviewArrayList.add(review);
                    }
                    ratingBar.setRating(base/jsonArray.length());
                    baseScore.setText(String.valueOf(jsonArray.length() + "Total Reivews"));
                }catch (Exception e){
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
}
