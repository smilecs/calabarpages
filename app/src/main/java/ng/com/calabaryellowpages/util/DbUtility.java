package ng.com.calabaryellowpages.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Model.Category;

/**
 * Created by SMILECS on 9/14/16.
 */
public class DbUtility {
    String  selection = DataContract.Data.CATEGORY + "=?";
    String [] projection = null;
    Dbhelper helper;
    String TAG = "DbUtility";
    SQLiteDatabase db;
    SQLiteDatabase Rdb;
    public DbUtility(Context c){
        helper = new Dbhelper(c);
        db = helper.getWritableDatabase();
        Rdb = helper.getReadableDatabase();
    }
    public void addCategory(Category model){
        ContentValues values = new ContentValues();
        values.put(DataContract.Data.SLUG, model.getSlug());
        values.put(DataContract.Data.CATEGORY, model.getTitle());
        values.put(DataContract.Data.TYPE, "1");
        //byte[] im = model.getImage().getBytes();
        db.insert(DataContract.Data.TABLE_NAME, null, values);

    }

    public ArrayList<Category> readData(){
        Log.d("Dbutility", "here");
        String [] selectionAgs = {};
        ArrayList<Category> list = new ArrayList<>();
        Cursor cursor = Rdb.query(DataContract.Data.TABLE_NAME, projection, null, selectionAgs, DataContract.Data.CATEGORY, null,  null);
        if(cursor != null  && cursor.moveToFirst()) {
            do {
                Category model = new Category();
                try {
                    model.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DataContract.Data.CATEGORY)));
                    model.setSlug(cursor.getString(cursor.getColumnIndexOrThrow(DataContract.Data.SLUG)));
                    list.add(model);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int  Delete(){
        if(!readData().isEmpty()){
            db.delete(DataContract.Data.TABLE_NAME, DataContract.Data.TYPE + "= ?", new String[] {"1"});
            return 1;
        }
        return 0;

    }
}
