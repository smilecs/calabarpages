package ng.com.calabaryellowpages;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Adapters.GalleryAdapter;
import ng.com.calabaryellowpages.Model.Category;
import ng.com.calabaryellowpages.util.Parse;
import ng.com.calabaryellowpages.util.volleySingleton;

public class pluslist extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{
    ArrayList<ng.com.calabaryellowpages.Model.Category> model;
    RecyclerView rv;
    RecyclerView.LayoutManager manager;
    GalleryAdapter mAdapter;
    ng.com.calabaryellowpages.Model.Category data;
    volleySingleton volleySingleton;
    RequestQueue requestQueue;
    CollapsingToolbarLayout col;
    TextView title1, title2;
    LinearLayout mTitleContainer;
    FloatingActionButton callButton;
    de.hdodenhof.circleimageview.CircleImageView rounded;
    ImageView imageView;
    TextView special, title, phone, address, work_days, web, description;
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;
    private AppBarLayout mAppBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pluslist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(this);
        try{
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }catch (Exception e){
            e.printStackTrace();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        col = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Typeface items = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Light.ttf");
        Typeface desc = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Thin.ttf");
        data = (Category) getIntent().getSerializableExtra("data");
        title1 = (TextView) findViewById(R.id.title1);
        title2 = (TextView) findViewById(R.id.title2);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        startAlphaAnimation(title2, 0, View.INVISIBLE);
        rounded = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.roundimage);
        model = new ArrayList<>();
        imageView = (ImageView) findViewById(R.id.image);
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
        title1.setText(data.getTitle());
        title2.setText(data.getTitle());
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
       if(data.getImages().length > 0){
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
        }
        mAdapter = new GalleryAdapter(model);
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(mAdapter);
        ImageLoader imageLoader = volleySingleton.getsInstance().getImageLoader();
        imageLoader.get(data.getImage(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                //imageView.setImageBitmap(imageContainer.getBitmap());
                rounded.setImageBitmap(imageContainer.getBitmap());

            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }



    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(title2, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(title2, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}
