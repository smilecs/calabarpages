package ng.com.calabarpages;

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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ng.com.calabarpages.Adapters.Adapter;
import ng.com.calabarpages.util.EndlessRecyclerViewScrollListener;
import ng.com.calabarpages.util.volleySingleton;

public class Category extends AppCompatActivity {
    ArrayList<ng.com.calabarpages.Model.Category> model;
    RecyclerView rv;
    Adapter mAdapter;
    LinearLayoutManager manager;
    volleySingleton volleySingle;
    RequestQueue requestQueue;
    String slug, page, url;
    ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        slug = getIntent().getStringExtra("slug");
        page = "1";
        model = new ArrayList<>();
        volleySingle = volleySingleton.getsInstance();
        requestQueue = volleySingle.getmRequestQueue();
        bar = (ProgressBar) findViewById(R.id.progress);
        rv = (RecyclerView) findViewById(R.id.recycler);
        manager = new LinearLayoutManager(this);
        mAdapter = new Adapter(model, this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(mAdapter);
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d("Category", "page no" + Integer.toString(page) + "   " + Integer.toString(totalItemsCount));
                String pg = Integer.toString(page);
                if(totalItemsCount > 49) {
                    if (slug != null) {
                        Refresh(slug, pg);
                    } else {
                        //url = getIntent().getStringExtra("url");
                        Refresh2(url, pg);
                    }
                }

            }
        });
        if(slug != null){
            Refresh(slug, page);
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
            model.clear();
            mAdapter.notifyDataSetChanged();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, volleySingleton.URL + "api/newview?page=" + page + "&q=" + query, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    bar.setVisibility(View.GONE);
                    JSONObject json;
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    for(int i=0; i<jsonArray.length(); i++){
                        json = jsonArray.getJSONObject(i);
                        ng.com.calabarpages.Model.Category cat = new ng.com.calabarpages.Model.Category();
                        cat.setType(json.getString("Type"));
                        if(cat.getType().equals("advert") || cat.getType().equals("true")){
                            cat.setImage(json.getString("Image"));
                        }
                        if(!cat.getType().equals("advert")){
                            cat.setTitle(json.getString("CompanyName"));
                            cat.setSlug(json.getString("Slug"));
                            cat.setAddress(json.getString("Address"));
                            cat.setSpecialisation(json.getString("Specialisation"));
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

                        }
                        model.add(cat);
                        mAdapter.notifyDataSetChanged();

                    }
                    Log.d("Category", "Refresh" + " " + Integer.toString(model.size()));
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

    private void Refresh2(final String url, String page){
        Log.d("Category", "Refresh" + " " + Integer.toString(model.size()));
        if(page.equals("1")){
            model.clear();
            mAdapter.notifyDataSetChanged();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, volleySingleton.URL + url + "?page=" + page , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    bar.setVisibility(View.GONE);
                    JSONObject json;
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    for(int i=0; i<jsonArray.length(); i++){
                        json = jsonArray.getJSONObject(i);
                        ng.com.calabarpages.Model.Category cat = new ng.com.calabarpages.Model.Category();
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