package ng.com.calabaryellowpages.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Adapters.Adapter;
import ng.com.calabaryellowpages.R;
import ng.com.calabaryellowpages.util.EndlessRecyclerViewScrollListener;
import ng.com.calabaryellowpages.util.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Special#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Special extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<ng.com.calabaryellowpages.Model.Category> model;
    RecyclerView rv;
    Adapter mAdapter;
    LinearLayoutManager manager;
    VolleySingleton volleySingle;
    Button ref;
    boolean load;
    RequestQueue requestQueue;
    String slug, page, url;
    SwipeRefreshLayout swipeRefreshLayout;
    private EndlessRecyclerViewScrollListener scrollListener;
    // TODO: Rename and change types of parameters
    private String mParam1;


    public Special() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Special.
     */
    // TODO: Rename and change types and number of parameters
    public static Special newInstance(String param1) {
        Special fragment = new Special();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ArrayList<>();
        volleySingle = VolleySingleton.getsInstance();
        requestQueue = volleySingle.getmRequestQueue();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_category, container, false);
        ref = (Button) v.findViewById(R.id.button);
        rv = (RecyclerView) v.findViewById(R.id.recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
              android.R.color.holo_green_light,
              android.R.color.holo_orange_light,
              android.R.color.holo_red_light
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh(mParam1, "1");
            }
        });
        manager = new LinearLayoutManager(getContext());
        mAdapter = new Adapter(model, getContext());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(mAdapter);
        Refresh(mParam1, "1");
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Refresh(mParam1, "1");
            }
        });
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
        return v;
    }

    private void Refresh(final String url, String page){
        ref.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        if(page.equals("1")){
            model.clear();
            mAdapter.notifyDataSetChanged();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, VolleySingleton.URL + url + "?p=" + page , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d("url", VolleySingleton.URL + url);
                    JSONObject json;
                    JSONArray jsonArray = jsonObject.getJSONArray("Posts");
                    load = jsonObject.getJSONObject("Page").getBoolean("Next");
                    for(int i=0; i<jsonArray.length(); i++){
                        json = jsonArray.getJSONObject(i);
                        json = json.getJSONObject("Listing");
                        //Log.d("data", json.toString());
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
                                cat.setPhone(json.getString("Hotline"));
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            try{
                                cat.setWork_days(json.getString("DHr"));
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
                            try{
                                if(!json.getString("Reviews").isEmpty())
                                    cat.setRating(json.getString("Reviews"));
                            }catch (JSONException je){
                                je.printStackTrace();
                            }

                        }
                        model.add(cat);
                        mAdapter.notifyDataSetChanged();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    ref.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                swipeRefreshLayout.setRefreshing(false);
                volleyError.printStackTrace();
                ref.setVisibility(View.VISIBLE);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}
