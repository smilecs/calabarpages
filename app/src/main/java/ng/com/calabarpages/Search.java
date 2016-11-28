package ng.com.calabarpages;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import ng.com.calabarpages.Adapters.Adapter;
import ng.com.calabarpages.util.EndlessRecyclerViewScrollListener;
import ng.com.calabarpages.util.volleySingleton;

public class Search extends AppCompatActivity {
    ArrayList<ng.com.calabarpages.Model.Category> model;
    Adapter mAdapter;
    RecyclerView rv;
    LinearLayoutManager manager;
    volleySingleton volleySingle;
    RequestQueue requestQueue;
    TextView networkError;
    ProgressBar bar;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        Intent intent = getIntent();
        bar = (ProgressBar) findViewById(R.id.progress);
        rv = (RecyclerView) findViewById(R.id.rv);
        networkError = (TextView) findViewById(R.id.network);
        manager = new LinearLayoutManager(this);
        model = new ArrayList<>();
        mAdapter = new Adapter(model, this);
        volleySingle = volleySingleton.getsInstance();
        requestQueue = volleySingle.getmRequestQueue();
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(mAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            getSupportActionBar().setTitle(query.toUpperCase());
            performSearch(query, "1");
        }

        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d("Category", "page no" + Integer.toString(page));
                String pg = Integer.toString(page);
                    if(totalItemsCount > 49){
                        performSearch(query, pg);
                    }

            }
        });

    }

    private void performSearch(String query, String page){
        if(page.equals("1")){
            model.clear();
            mAdapter.notifyDataSetChanged();
        }
        bar.setVisibility(View.VISIBLE);
        networkError.setVisibility(View.GONE);
        query = query.trim();
        Uri uri = Uri.parse(query);
        try{
            query = URLEncoder.encode(query, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, volleySingleton.URL + "api/result?page=" + page + "&q=" +query , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    bar.setVisibility(View.GONE);
                    networkError.setVisibility(View.GONE);
                    JSONObject json;
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    for(int i=0; i<jsonArray.length(); i++){
                        json = jsonArray.getJSONObject(i);
                        ng.com.calabarpages.Model.Category cat = new ng.com.calabarpages.Model.Category();
                        cat.setType(json.getString("Plus"));
                        if(cat.getType().equals("true")){
                            cat.setImage(json.getString("Image"));
                        }
                            cat.setTitle(json.getString("CompanyName"));
                            cat.setSlug(json.getString("Slug"));
                            cat.setAddress(json.getString("Address"));
                            cat.setSpecialisation(json.getString("Specialisation"));
                            try{
                                cat.setPhone(json.getString("Hotline"));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            try{
                                Log.d("Search", json.getString("Dhr"));
                                cat.setWork_days(json.getString("Dhr"));
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        model.add(cat);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    networkError.setVisibility(View.VISIBLE);
                    bar.setVisibility(View.GONE);

                }
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                networkError.setVisibility(View.VISIBLE);
                bar.setVisibility(View.GONE);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
