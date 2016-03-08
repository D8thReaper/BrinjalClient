package exun.cli.in.brinjal.helper;

/**
 * Created by n00b on 3/6/2016.
 */
public class AppConstants {

    // Table names
    private static final String TABLE_LOGIN = "login";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_SUB_CATEGORIES = "subCategories";



    // Location constants start
    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME =
            "exun.cli.in.brinjal.helper";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static String RESULT_LAT = "LATITUDE";

    public static String RESULT_LONG = "LONGITUDE";

    public static String RESULT_LOCALITY = "LOCALITY";

    public static String RESULT_CITY = "CITY";
    // Location constants end



    // Store list URL
    public static final String URL_STORE_LIST = "http://android.brinjal.me/user/v1/places/";

    // Categories url
    public static String URL_CATEGORIES = "http://android.brinjal.me/user/v1/categories";

    // Sub categories url
    public static String URL_SUB_CATEGORIES = "http://android.brinjal.me/user/v1/sub_categories";

    // Login url
    public static String URL_LOGIN = "http://android.brinjal.me/user/v2/login";

    // Register url
    public static String URL_REGISTER = "http://android.brinjal.me/user/v2/register";

    // Store details
    public static String URL_STORES = "http://android.brinjal.me/user/v1/place_details/";
}
