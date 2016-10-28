package ng.com.calabarpages.util;

/**
 * Created by SMILECS on 10/28/16.
 */

public class Parse {

    public static String[] Parse(String string){
        string = string.replaceAll(",", " ");
        string = string.replaceAll("  ", " ");
        return string.split(" ");
    }
}
