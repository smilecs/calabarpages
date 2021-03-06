package ng.com.calabaryellowpages.Fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Adapters.CategoryAdapter;
import ng.com.calabaryellowpages.Model.Category;
import ng.com.calabaryellowpages.R;
import ng.com.calabaryellowpages.util.Application;
import ng.com.calabaryellowpages.util.DbUtility;
import ng.com.calabaryellowpages.util.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainCategory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainCategory extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView rv;
    CategoryAdapter mAdapter;
    ArrayList<Category> model;
    VolleySingleton volley;
    RequestQueue requestQueue;
    Button ref;
    ProgressBar bar;
    public String TAG = "MainCategory";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MainCategory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainCategory.
     */
    // TODO: Rename and change types and number of parameters
    public static MainCategory newInstance(String param1, String param2) {
        MainCategory fragment = new MainCategory();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    DbUtility dbUtility;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "created");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main, container, false);
        volley = VolleySingleton.getsInstance();
        model = new ArrayList<>();
        bar = (ProgressBar) v.findViewById(R.id.progress);
        rv = (RecyclerView) v.findViewById(R.id.recycler);
        ref = (Button) v.findViewById(R.id.button);
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        //editor.putString("name", object.getString("Name"));
        Log.d(TAG, getContext().getSharedPreferences("app", Context.MODE_PRIVATE).getString("name", "empty"));
        mAdapter = new CategoryAdapter(model, getContext());
        rv.setAdapter(mAdapter);
        requestQueue = volley.getmRequestQueue();
        dbUtility = new DbUtility(getContext());
        new LoadData().execute();
        //Refresh();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Application.logViewedContentEvent("Categories", "categories");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class LoadData extends AsyncTask< ArrayList<Category>, ArrayList<Category>, ArrayList<Category> > {
        @Override
        protected ArrayList<Category> doInBackground(ArrayList<Category>... params) {

            return dbUtility.readData();
        }

        @Override
        protected void onPostExecute(ArrayList<Category> categories) {
            ref.setVisibility(View.GONE);
            if(categories.isEmpty()){
                Log.d("MainCategory", "Refresh");
                Refresh();
            }
            else {
                bar.setVisibility(View.GONE);
                mAdapter = new CategoryAdapter(categories, getContext());
                rv.setAdapter(mAdapter);
                Log.d("MainCategory", String.valueOf(categories.size()));
            }

        }
    }


    private void Refresh(){
        JsonArrayRequest json = new JsonArrayRequest(VolleySingleton.URL + "api/getcat", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                bar.setVisibility(View.GONE);
                for(int i=0; i < jsonArray.length(); i++){
                    Category mode = new Category();
                    try{
                        JSONObject tmpData = jsonArray.getJSONObject(i);
                        mode.setSlug(tmpData.getString("Slug"));
                        mode.setTitle(tmpData.getString("Category"));
                        model.add(mode);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                bar.setVisibility(View.GONE);
                ref.setVisibility(View.VISIBLE);
            }
        });
        requestQueue.add(json);
    }

    public void load(){
        new LoadData().execute();
    }


}
