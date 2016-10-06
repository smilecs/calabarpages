package ng.com.calabarpages;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import ng.com.calabarpages.util.DbUtility;
import ng.com.calabarpages.util.volleySingleton;

public class tabbed extends AppCompatActivity {

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
    volleySingleton volley;
    RequestQueue requestQueue;
    CoordinatorLayout view;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        db = new DbUtility(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        view = (CoordinatorLayout) findViewById(R.id.main_content);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        volley = volleySingleton.getsInstance();
        requestQueue = volley.getmRequestQueue();
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        preferences = getSharedPreferences("app", MODE_PRIVATE);
        editor = preferences.edit();
        new LoadData().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
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
            Main_Category mn = new Main_Category();
            switch (position) {
                case 0:
                    return mn;
                case 1:
                    sp = Special.newInstance("api/falseview");
                    return sp;
                case 2:
                    sp = Special.newInstance("advert");
                    return sp;
            }
            return null;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
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
        Log.d("tabbed", "refresh");
        JsonArrayRequest json = new JsonArrayRequest(volleySingleton.URL + "api/getcat", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if(jsonArray.length() > preferences.getInt("Number", 0)){
                    message = "New Categories Loaded";
                    db.Delete();
                    Log.d("tabbed", "deleting");
                    for(int i=0; i < jsonArray.length(); i++){
                        ng.com.calabarpages.Model.Category mode = new ng.com.calabarpages.Model.Category();
                        try{
                            JSONObject tmpData = jsonArray.getJSONObject(i);
                            mode.setSlug(tmpData.getString("Slug"));
                            mode.setTitle(tmpData.getString("Category"));
                            final ng.com.calabarpages.Model.Category md = mode;
                            Thread bk = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("tabbed", "runnable");
                                    db.addCategory(md);
                                }
                            });
                            bk.start();
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
}
