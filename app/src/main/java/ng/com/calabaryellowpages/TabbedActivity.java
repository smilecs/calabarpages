package ng.com.calabaryellowpages;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ng.com.calabaryellowpages.Fragment.MainCategory;
import ng.com.calabaryellowpages.Fragment.Special;
import ng.com.calabaryellowpages.util.DbUtility;
import ng.com.calabaryellowpages.util.VolleySingleton;

public class TabbedActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    DbUtility db;
    VolleySingleton volley;
    RequestQueue requestQueue;
    CoordinatorLayout view;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    String message;
    ShareLinkContent content;
    FloatingActionButton addButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseMessaging.getInstance().subscribeToTopic("update");
        preferences = getSharedPreferences("app", MODE_PRIVATE);
        editor = preferences.edit();
        printKeyHash(this);
        if(preferences.getBoolean("isnotlogged", true)){
            Intent intent = new Intent(this, intro.class);
            startActivity(intent);
            finish();
        }
        //app id
        addButton = (FloatingActionButton) findViewById(R.id.add);
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.calabaryellowpages));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        if(preferences.getBoolean("hasValue", false)){
            mAdView.setVisibility(View.GONE);
            //addButton.setVisibility(View.VISIBLE);
        }
        db = new DbUtility(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        view = (CoordinatorLayout) findViewById(R.id.main_content);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        volley = VolleySingleton.getsInstance();
        requestQueue = volley.getmRequestQueue();
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        new LoadData().execute();
        content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=ng.com.calabaryellowpages"))
                .setQuote("CalabarPages or CalabarYellowPages is an online business directory which lists and advertises businesses in Cross River State. Calabar, the capital of Cross River State is the first capital of Nigeria and the tourism capital of South Eastern Nigeria.If you are a resident or visitor,this app will guide you to products and services and will keep you updated with new listings and additions to the directory!")
                .setContentDescription("CalabarPages or CalabarYellowPages is an online business directory which lists and advertises businesses in Cross River State. Calabar, the capital of Cross River State is the first capital of Nigeria and the tourism capital of South Eastern Nigeria.If you are a resident or visitor,this app will guide you to products and services and will keep you updated with new listings and additions to the directory!")
                .setContentTitle("CalabarPages - Online directory of businesses in Cross River State")
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
       // MenuItem searchItem = menu.findItem(R.id.search);
        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        //searchView.setSearchableInfo();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share) {
            ShareDialog.show(this, content);
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

    /**
     * A placeholder fragment containing a simple view.
     */


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.d("tagged", Integer.toString(position));
            Special sp = null;
            MainCategory mn = new MainCategory();
            switch (position) {
                case 0:
                    return mn;
                case 1:
                    sp = Special.newInstance("api/pluslistings");
                    return sp;
                case 2:
                    sp = Special.newInstance("api/advert");
                    return sp;
            }
            return null;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Category";
                case 1:
                    return "PlusListings";
                case 2:
                    return "Advert";
            }
            return null;
        }
    }

    private class LoadData extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            Refresh();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT);

        }
    }

    private void Refresh(){
        Log.d("TabbedActivity", "refresh");
        JsonArrayRequest json = new JsonArrayRequest(VolleySingleton.URL + "api/categories", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if(jsonArray.length() > preferences.getInt("Number", 0)){
                    message = "New Categories Loaded";
                    db.Delete();
                    Log.d("TabbedActivity", "deleting");
                    for(int i=0; i < jsonArray.length(); i++){
                        ng.com.calabaryellowpages.Model.Category mode = new ng.com.calabaryellowpages.Model.Category();
                        try{
                            JSONObject tmpData = jsonArray.getJSONObject(i);
                            if(!tmpData.getString("Category").isEmpty()){
                                mode.setSlug(tmpData.getString("Slug"));
                                mode.setTitle(tmpData.getString("Category"));
                                final ng.com.calabaryellowpages.Model.Category md = mode;
                                Thread bk = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        db.addCategory(md);
                                    }
                                });
                                bk.start();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                message = "Refresh";
                editor.putInt("Number", jsonArray.length());
                editor.commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Snackbar.make(view, "Unable to Load new Categories check Connectivity", Snackbar.LENGTH_SHORT);
            }
        });
        requestQueue.add(json);
    }
    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                  PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
