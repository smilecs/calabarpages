package ng.com.calabarpages;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import ng.com.calabarpages.Adapters.GalleryAdapter;
import ng.com.calabarpages.Model.Category;
import ng.com.calabarpages.util.volleySingleton;

public class pluslist extends AppCompatActivity {
    ArrayList<ng.com.calabarpages.Model.Category> model;
    RecyclerView rv;
    RecyclerView.LayoutManager manager;
    GalleryAdapter mAdapter;
    ng.com.calabarpages.Model.Category data;
    volleySingleton volleySingleton;
    RequestQueue requestQueue;
    CollapsingToolbarLayout col;
    ImageView imageView;
    TextView special, title, phone, address, work_days, web, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pluslist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        col = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Typeface items = Typeface.createFromAsset(getAssets(),
                "fonts/RobotoCondensed-Light.ttf");
        Typeface desc = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Thin.ttf");
        data = (Category) getIntent().getSerializableExtra("data");
        getSupportActionBar().setTitle(data.getTitle());
        model = new ArrayList<>();
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.yellowpages));
        web = (TextView) findViewById(R.id.website);
        web.setText(data.getWeb());
        description = (TextView) findViewById(R.id.description);
        description.setTypeface(desc);
        description.setText(data.getDescription());
        //title = (TextView) findViewById(R.id.title);
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
        mAdapter = new GalleryAdapter(model);
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(mAdapter);
        ImageLoader imageLoader = volleySingleton.getsInstance().getImageLoader();
        imageLoader.get(data.getImage(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                Bitmap bit = imageContainer.getBitmap();
                imageView.setImageBitmap(bit);
                /*Palette.from(bit).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        applyPalette(palette);

                    }
                });*/
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

    private void applyPalette(Palette palette){
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);
        col.setContentScrimColor(palette.getMutedColor(primary));
        col.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        supportStartPostponedEnterTransition();
    }

}
