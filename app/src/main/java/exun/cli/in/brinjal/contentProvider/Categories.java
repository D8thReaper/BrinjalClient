package exun.cli.in.brinjal.contentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import exun.cli.in.brinjal.helper.AppConstants;
import exun.cli.in.brinjal.helper.SQLiteHandler;

/**
 * Created by n00b on 3/9/2016.
 */
public class Categories extends ContentProvider {

    public static final String PROVIDER_NAME = "exun.cli.in.brinjal.contentProvider.categories";

    /** A uri to do operations on sub_categories table. A content provider is identified by its uri */
    public static final Uri CONTENT_URI_SUB_CATEGORIES = Uri.parse("content://" + PROVIDER_NAME + "/" + AppConstants.TABLE_SUB_CATEGORIES);

    /** A uri to do operations on sub_categories table. A content provider is identified by its uri */
    public static final Uri CONTENT_URI_SUB_CATEGORIES_ALL = Uri.parse("content://" + PROVIDER_NAME + "/" + AppConstants.TABLE_SUB_CATEGORIES + "all");

    /** A uri to do operations on categories table. A content provider is identified by its uri */
    public static final Uri CONTENT_URI_CATEGORIES = Uri.parse("content://" + PROVIDER_NAME + "/" + AppConstants.TABLE_CATEGORIES);

    /** Constants to identify the requested operation */
    private static final int CATEGORIES = 1;
    private static final int SUB_CATEGORIES = 2;
    private static final int SUB_CATEGORIES_ALL = 3;

    private static final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "categories", CATEGORIES);
        uriMatcher.addURI(PROVIDER_NAME, "subCategories", SUB_CATEGORIES);
        uriMatcher.addURI(PROVIDER_NAME, "subCategoriesall", SUB_CATEGORIES_ALL);
    }

    /** This content provider does the database operations by this object */
    SQLiteHandler mDB;

    @Override
    public boolean onCreate() {
        mDB = new SQLiteHandler(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if(uriMatcher.match(uri)==CATEGORIES){
            return mDB.fetchGroup();
        }else if (uriMatcher.match(uri)==SUB_CATEGORIES){
            Log.d("ContentProvider",selection);
            return mDB.fetchChildren(selection);
        }
        else if (uriMatcher.match(uri)==SUB_CATEGORIES_ALL){
            return mDB.fetchChildrenAll();
        }
        else{
            return null;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
