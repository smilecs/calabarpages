package ng.com.calabaryellowpages;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Adapters.Adapter;
import ng.com.calabaryellowpages.util.Application;
import ng.com.calabaryellowpages.util.EndlessRecyclerViewScrollListener;
import ng.com.calabaryellowpages.util.VolleySingleton;

public class Category extends AppCompatActivity {
    ArrayList<ng.com.calabaryellowpages.Model.Category> model;
    RecyclerView rv;
    Adapter mAdapter;
    LinearLayoutManager manager;
    VolleySingleton volleySingle;
    RequestQueue requestQueue;
    String slug, page, url, title;
    TextView txt;
    Context c;
    boolean load;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        c = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(title);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
              android.R.color.holo_green_light,
              android.R.color.holo_orange_light,
              android.R.color.holo_red_light
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh(slug, "1");
            }
        });
        slug = getIntent().getStringExtra("slug");
        page = "1";
        model = new ArrayList<>();
        MobileAds.initialize(getApplicationContext(), "a-app-pub-9472469694308804~6150882570");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        txt = (TextView) findViewById(R.id.error);
        volleySingle = VolleySingleton.getsInstance();
        requestQueue = volleySingle.getmRequestQueue();
        rv = (RecyclerView) findViewById(R.id.recycler);
        manager = new LinearLayoutManager(this);
        mAdapter = new Adapter(model, this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(mAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("Category", "page no" + Integer.toString(page) + "   " + Integer.toString(totalItemsCount));
                String pg = Integer.toString(page);
                if(load){
                    if (slug != null) {
                        Refresh(slug, pg);
                    }
                }

            }
        };

        rv.addOnScrollListener(scrollListener);

        if(slug != null){
            Refresh(slug, "");
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Application.logViewedContentEvent(title, slug);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.search){
            onSearchRequested();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchRequested() {

        return super.onSearchRequested();
    }


    private void Refresh(String query, String page){
        if(page.equals("1")){
            model.clear();
            mAdapter.notifyDataSetChanged();
            scrollListener.resetState();
        }
        txt.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, VolleySingleton.URL + "api/categories/"+ query +"?p="+page, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                swipeRefreshLayout.setRefreshing(false);
                try{
                    JSONObject json;
                    JSONArray jsonArray = jsonObject.getJSONArray("Posts");
                    load = jsonObject.getJSONObject("Page").getBoolean("Next");
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject jsons = jsonArray.getJSONObject(i);
                        json = jsons.getJSONObject("Listing");
                        ng.com.calabaryellowpages.Model.Category cat = new ng.com.calabaryellowpages.Model.Category();
                        cat.setListing(jsonArray.getJSONObject(i).getString("Type"));
                        cat.setType(json.getString("Plus"));
                        if(cat.getType().equals("true")){
                            cat.setImage(json.getString("Image"));
                                try{
                                    String[] tmp = new String[json.getJSONArray("Images").length()];
                                    Log.d("Special", json.getJSONArray("Images").toString());
                                    for(int k = 0; k < json.getJSONArray("Images").length(); k++){
                                        tmp[k] = json.getJSONArray("Images").getString(k);
                                        Log.d("special", tmp[k]);
                                    }
                                    cat.setImages(tmp);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                        }
                        if(!cat.getListing().equals("advert")){
                            cat.setTitle(json.getString("CompanyName"));
                            cat.setSlug(json.getString("Slug"));
                            cat.setAddress(json.getString("Address"));
                            cat.setSpecialisation(json.getString("Specialisation"));
                            try{
                                if(!json.getString("Reviews").isEmpty())
                                cat.setRating(json.getString("Reviews"));
                            }catch (JSONException je){
                                je.printStackTrace();
                            }
                            try{
                                cat.setDescription(json.getString("Description"));
                            }catch (JSONException je){
                                je.printStackTrace();
                            }
                            try{
                                cat.setPhone(json.getString("Hotline"));
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            try{
                                cat.setWork_days(json.getString("DHr"));
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            model.add(cat);
                        }


                    }
                    mAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                swipeRefreshLayout.setRefreshing(false);
                volleyError.printStackTrace();
                Toast.makeText(c, "network error", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}