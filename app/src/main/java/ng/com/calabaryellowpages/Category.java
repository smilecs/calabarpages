package ng.com.calabaryellowpages;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import ng.com.calabaryellowpages.util.EndlessRecyclerViewScrollListener;
import ng.com.calabaryellowpages.util.volleySingleton;

public class Category extends AppCompatActivity {
    ArrayList<ng.com.calabaryellowpages.Model.Category> model;
    RecyclerView rv;
    Adapter mAdapter;
    LinearLayoutManager manager;
    volleySingleton volleySingle;
    RequestQueue requestQueue;
    String slug, page, url;
    TextView txt;
    Context c;
    Boolean load;
    ProgressBar bar;
    private EndlessRecyclerViewScrollListener scrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        c = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        slug = getIntent().getStringExtra("slug");
        page = "1";
        model = new ArrayList<>();
        MobileAds.initialize(getApplicationContext(), "a-app-pub-9472469694308804~6150882570");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        txt = (TextView) findViewById(R.id.error);
        volleySingle = volleySingleton.getsInstance();
        requestQueue = volleySingle.getmRequestQueue();
        bar = (ProgressBar) findViewById(R.id.progress);
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
                    } else {
                        //url = getIntent().getStringExtra("url");
                        Refresh2(url, pg);
                    }
                }

            }
        };

        rv.addOnScrollListener(scrollListener);

        if(slug != null){
            Refresh(slug, "");
        }else{
            url = getIntent().getStringExtra("url");
            Refresh2(getIntent().getStringExtra("url"), page);
        }


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
        Log.d("Category", "Refresh" + " " + Integer.toString(model.size()));
        if(page.equals("1")){
            Log.d("Category", "clean");
            model.clear();
            mAdapter.notifyDataSetChanged();
            scrollListener.resetState();
        }
        txt.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, volleySingleton.URL + "api/categories/"+ query +"?p="+page, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    bar.setVisibility(View.GONE);
                    JSONObject json;
                    JSONArray jsonArray = jsonObject.getJSONArray("Posts");
                    load = jsonObject.getJSONObject("Page").getBoolean("Next");
                    for(int i=0; i<jsonArray.length(); i++){
                        Log.d("category", jsonArray.toString());
                        JSONObject jsons = jsonArray.getJSONObject(i);
                        json = jsons.getJSONObject("Listing");
                       // Log.d("Category", jsons.toString());
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
                        if(!cat.getType().equals("advert")){
                            cat.setTitle(json.getString("CompanyName"));
                            cat.setSlug(json.getString("Slug"));
                            cat.setAddress(json.getString("Address"));
                            cat.setSpecialisation(json.getString("Specialisation"));
                            try{
                                cat.setRating((float) json.getLong("rating"));
                            }catch (JSONException je){
                                je.printStackTrace();
                            }
                            try{
                                cat.setTotal(json.getInt("TotalReview"));
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

                        }
                        model.add(cat);
                        mAdapter.notifyDataSetChanged();

                    }

                    Log.d("Category", "check" + " " + model.size());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                bar.setVisibility(View.GONE);
                Toast.makeText(c, "network error", Toast.LENGTH_LONG);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void Refresh2(final String url, String page){
        Log.d("Category", "Refresh" + " " + Integer.toString(model.size()));
        if(page.equals("1")){
            model.clear();
            mAdapter.notifyDataSetChanged();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, volleySingleton.URL + url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    bar.setVisibility(View.GONE);
                    JSONObject json;
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    for(int i=0; i<jsonArray.length(); i++){
                        json = jsonArray.getJSONObject(i);
                        ng.com.calabaryellowpages.Model.Category cat = new ng.com.calabaryellowpages.Model.Category();
                        cat.setType(json.getString("Type"));
                        if(cat.getType().equals("advert") || cat.getType().equals("true")){
                            cat.setImage(json.getString("Image"));
                            try{
                                String[] tmp = {};
                                for(int k = 0; k <json.getJSONArray("Images").length(); k++){
                                    tmp[k] = json.getJSONArray("Images").getString(k);
                                }
                                cat.setImages(tmp);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        if(!cat.getType().equals("advert")){
                            cat.setTitle(json.getString("CompanyName"));
                            cat.setSlug(json.getString("Slug"));
                            cat.setAddress(json.getString("Address"));
                            cat.setSpecialisation(json.getString("Specialisation"));
                            try{
                                cat.setRating((float) json.getLong("rating"));
                            }catch (JSONException je){
                                je.printStackTrace();
                            }
                            try{
                                cat.setTotal(json.getInt("TotalReview"));
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
                                cat.setWork_days(json.getString("Dhr"));
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            try{
                                cat.setDescription(json.getString("About"));
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            try{
                                cat.setWeb(json.getString("Website"));
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }

                        }
                        model.add(cat);
                        mAdapter.notifyDataSetChanged();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}