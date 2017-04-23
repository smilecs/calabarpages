package ng.com.calabaryellowpages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Adapters.ReviewAdapter;
import ng.com.calabaryellowpages.Fragment.ReviewFragment;
import ng.com.calabaryellowpages.Model.Review;
import ng.com.calabaryellowpages.Services.SaveReview;
import ng.com.calabaryellowpages.util.EndlessRecyclerViewScrollListener;
import ng.com.calabaryellowpages.util.volleySingleton;

public class ReviewActivity extends AppCompatActivity implements ReviewFragment.CustomDialogInterface{
    RecyclerView rv;
    LinearLayoutManager manager;
    ReviewAdapter reviewAdapter;
    ArrayList<Review> model;
    SharedPreferences preferences;
    Context c;
    volleySingleton volleySingleton;
    RequestQueue queue;
    private EndlessRecyclerViewScrollListener scrollListener;
    boolean load;
    String slug;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        volleySingleton = ng.com.calabaryellowpages.util.volleySingleton.getsInstance();
        queue = volleySingleton.getmRequestQueue();
        c = this;
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
              android.R.color.holo_green_light,
              android.R.color.holo_orange_light,
              android.R.color.holo_red_light
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadReviews(slug, "1");
            }
        });
        slug = getIntent().getStringExtra("slug");
        preferences = getSharedPreferences("app", MODE_PRIVATE);
        model = (ArrayList<Review>) getIntent().getSerializableExtra("model");
        reviewAdapter = new ReviewAdapter(model);
        rv = (RecyclerView) findViewById(R.id.recycler);
        manager = new LinearLayoutManager(this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(reviewAdapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("value", String.valueOf(preferences.getBoolean("isnotlogged", true)));
                if(!preferences.getBoolean("hasValue", false)){
                    Intent intent = new Intent(view.getContext(), FacebookActivity.class);
                    intent.putExtra("review", true);
                    startActivityForResult(intent, 1001);
                    finish();
                    return;
                }
                new ReviewFragment().show(getSupportFragmentManager(), "Review");
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("Review", "page no" + Integer.toString(page) + "   " + Integer.toString(totalItemsCount));
                if(load){
                    swipeRefreshLayout.setRefreshing(true);
                    loadReviews(slug, String.valueOf(page +1));
                }

            }
        };
    }

    @Override
    public void okButtonClicked(Review value) {
        Log.d("trial", "ffff");
        value.setName(preferences.getString("name", "name"));
        value.setID(preferences.getString("id", "id"));
        value.setSlug(getIntent().getStringExtra("slug"));
        swipeRefreshLayout.setRefreshing(true);
        loadReviews(slug, "1");
        Intent i = new Intent(c, SaveReview.class);
        i.putExtra("data", value);
        startService(i);

    }

    private void loadReviews(String query, String page) {
        if(page.equals("1")){
            model.clear();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ng.com.calabaryellowpages.util.volleySingleton.URL + "/api/get_reviews?p="+ page+"&q=" + query, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                swipeRefreshLayout.setRefreshing(false);
                int base = 0;
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    load = jsonObject.getJSONObject("Page").getBoolean("Next");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Review review = new Review();
                        JSONObject json = jsonArray.getJSONObject(i);
                        review.setComment(json.getString("Comment"));
                        review.setName(json.getString("Name"));
                        review.setScore(json.getInt("Rating"));
                        base += review.getScore();
                        model.add(review);
                    }
                    reviewAdapter.notifyDataSetChanged();
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
        swipeRefreshLayout.setRefreshing(true);
        loadReviews(slug, "1");
    }
}
