package ng.com.calabaryellowpages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Adapters.ReviewAdapter;
import ng.com.calabaryellowpages.Fragment.ReviewFragment;
import ng.com.calabaryellowpages.Model.Review;
import ng.com.calabaryellowpages.Services.SaveReview;
import ng.com.calabaryellowpages.util.volleySingleton;

public class ReviewActivity extends AppCompatActivity implements ReviewFragment.CustomDialogInterface{
    RecyclerView rv;
    LinearLayoutManager manager;
    ReviewAdapter reviewAdapter;
    ArrayList<Review> model;
    SharedPreferences preferences;

    Context c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        c = this;
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
                if(preferences.getString("id", "no").equals("no")){
                    Intent intent = new Intent(view.getContext(), FacebookActivity.class);
                    startActivityForResult(intent, 1001);
                }
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void okButtonClicked(Review value) {
        value.setName(preferences.getString("name", "name"));
        value.setID(preferences.getString("id", "id"));
        Intent i = new Intent(c, SaveReview.class);
        i.putExtra("data", value);
        i.putExtra("query", getIntent().getStringExtra("Slug"));
        startService(i);

    }
}
