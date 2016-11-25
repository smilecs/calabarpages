package ng.com.calabarpages.util;

import android.util.Log;

/**
 * Created by SMILECS on 10/28/16.
 */

public class Parse {

    public static String[] Parse(String string){
        string = string.replaceAll(",", " ");
        string = string.replaceAll("  ", " ");
        Log.d("Parse", string);
        return string.split(" ");
    }
}
