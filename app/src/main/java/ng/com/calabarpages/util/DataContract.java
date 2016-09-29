package ng.com.calabarpages.util;

import android.provider.BaseColumns;

/**
 * Created by SMILECS on 9/13/16.
 */
public class DataContract {
    public DataContract(){}
    public static abstract class Data implements BaseColumns {
        public static final String TABLE_NAME = "Category";
        public static final String SLUG = "Slug";
        public static final String CATEGORY = "CATEGORY";
        public static final String TYPE = "tamka";
    }
}
