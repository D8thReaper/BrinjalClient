package exun.cli.in.brinjal.helper;

/**
 * Created by n00b on 3/6/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_LOGIN = "AndroidConstants";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_IS_CATEGORIES = "isCategories";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_LOCALITY = "locality";
    private static final String KEY_CITY = "city";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_LOGIN, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setCategories(boolean isCategories) {

        editor.putBoolean(KEY_IS_CATEGORIES, isCategories);

        // commit changes
        editor.commit();

        Log.d(TAG, "Categories have been saved");
    }

    public void setLat(Double lat){

        editor.putLong(KEY_LAT, Double.doubleToRawLongBits(lat));

        // commit changes
        editor.commit();
    }

    public void setLongi(Double longi){

        editor.putLong(KEY_LONG, Double.doubleToRawLongBits(longi));

        // commit changes
        editor.commit();
    }

    public void setLocality(String locality){

        editor.putString(KEY_LOCALITY, locality);

        // commit changes
        editor.commit();
    }

    public void setCity(String city){

        editor.putString(KEY_CITY, city);

        // commit changes
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public boolean isCategories(){
        return pref.getBoolean(KEY_IS_CATEGORIES, false);
    }

    public Double getLat() {
        return Double.longBitsToDouble(pref.getLong(KEY_LAT, 0));
    }

    public Double getLongi() {
        return Double.longBitsToDouble(pref.getLong(KEY_LONG,0));
    }

    public String getLocality() {
        return pref.getString(KEY_LOCALITY, null);
    }

    public String getCity() {
        return pref.getString(KEY_CITY, null);
    }
}
