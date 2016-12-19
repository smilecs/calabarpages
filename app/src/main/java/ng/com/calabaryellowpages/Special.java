package ng.com.calabaryellowpages;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import org.json.JSONObject;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Adapters.Adapter;
import ng.com.calabaryellowpages.util.volleySingleton;

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
    volleySingleton volleySingle;
    Button ref;
    RequestQueue requestQueue;
    String slug, page, url;
    ProgressBar bar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


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
        volleySingle = volleySingleton.getsInstance();
        requestQueue = volleySingle.getmRequestQueue();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_category, container, false);
        bar = (ProgressBar) v.findViewById(R.id.progress);
        ref = (Button) v.findViewById(R.id.button);
        rv = (RecyclerView) v.findViewById(R.id.recycler);
        manager = new LinearLayoutManager(getContext());
        mAdapter = new Adapter(model, getContext());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(mAdapter);
        Refresh2(mParam1, "1");
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Refresh2(mParam1, "1");
            }
        });
        return v;
    }

    private void Refresh2(final String url, String page){
        ref.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
        Log.d("Category", "Refresh" + " " + Integer.toString(model.size()));
        if(page.equals("1")){
            model.clear();
            mAdapter.notifyDataSetChanged();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, volleySingleton.URL + url + "?p=" + page , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    bar.setVisibility(View.GONE);
                    JSONObject json;
                    JSONArray jsonArray = jsonObject.getJSONArray("Posts");
                    for(int i=0; i<jsonArray.length(); i++){
                        json = jsonArray.getJSONObject(i);
                        json = json.getJSONObject("Listing");
                        ng.com.calabaryellowpages.Model.Category cat = new ng.com.calabaryellowpages.Model.Category();
                        cat.setType(json.getString("Plus"));
                        if(cat.getType().equals("advert") || cat.getType().equals("true")){
                            cat.setImage(json.getString("Image"));
                            try{
                                String[] tmp = new String[json.getJSONArray("Images").length()];
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

                        }
                        model.add(cat);
                        mAdapter.notifyDataSetChanged();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    bar.setVisibility(View.GONE);
                    ref.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                bar.setVisibility(View.GONE);
                ref.setVisibility(View.VISIBLE);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}
