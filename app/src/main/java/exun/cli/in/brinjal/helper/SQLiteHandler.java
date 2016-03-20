package exun.cli.in.brinjal.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "bookfoxi_brinjal_merchant";

    // Table names
    private static final String TABLE_LOGIN = "login";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_SUB_CATEGORIES = "subCategories";

    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";

    // Login Table Columns names
    private static final String KEY_EMAIL = "email";
    private static final String KEY_HAS_BOOKMARKS = "hasbookmarks";
    private static final String KEY_HAS_LOC = "hasloc";

    // Sub Categories Column names
    private static final String KEY_PARENT_ID = "parent_id";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_HAS_BOOKMARKS + " INTEGER,"
                + KEY_HAS_LOC + " INTEGER"
                + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT" + ")";
        db.execSQL(CREATE_CATEGORY);

        String CREATE_SUB_CATEGORY = "CREATE TABLE " + TABLE_SUB_CATEGORIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PARENT_ID + " INTEGER" + ")";
        db.execSQL(CREATE_SUB_CATEGORY);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(int uId,String name, String email, int hasBookmark, int hasLocation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,uId);
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_HAS_BOOKMARKS,hasBookmark); // Bookmarks
        values.put(KEY_HAS_LOC,hasLocation); // Location

        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Storing user details in database
     * */
    public void addCategories(int uId,String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,uId);
        values.put(KEY_NAME, name); // Name

        // Inserting Row
        long id = db.insert(TABLE_CATEGORIES, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New category inserted into sqlite: " + id);
    }

    /**
     * Storing user details in database
     * */
    public void addSubCategory(int uId,String name, int parentID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,uId);
        values.put(KEY_NAME, name); // Name
        values.put(KEY_PARENT_ID, parentID); // ParentID

        // Inserting Row
        long id = db.insert(TABLE_SUB_CATEGORIES, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New sub category inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("_id", cursor.getString(0));
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("hasbookmarks", cursor.getString(3));
            user.put("hasloc", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting category data from database
     * */
    public HashMap<String, String> getCategories() {
        HashMap<String, String> categories = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            categories.put("_id", cursor.getString(0));
            categories.put("name", cursor.getString(1));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching category from Sqlite: " + categories.toString());

        return categories;
    }

    /**
     * Getting sub category data from database
     * */
    public HashMap<String, String> getSubCategories() {
        HashMap<String, String> subCategories = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_SUB_CATEGORIES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            subCategories.put("_id", cursor.getString(0));
            subCategories.put("name", cursor.getString(1));
            subCategories.put("parent_id", cursor.getString(2));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching sub-category from Sqlite: " + subCategories.toString());

        return subCategories;
    }

    public Cursor fetchGroup() {
        String query = "SELECT * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        return cursor;
    }

    public Cursor fetchChildren(String where) {
        String query = "SELECT * FROM " + TABLE_SUB_CATEGORIES + " WHERE " + where;
        Log.d(TAG,query);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        return cursor;
    }

    /**
     * Getting row count of given table
     * */
    public int getRowCount(String TABLE) {
        String countQuery = "SELECT  * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re create database Delete all tables and create them again
     * */
    public void delete(String Table) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(Table, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public Cursor fetchChildrenAll() {
        String query = "SELECT * FROM " + TABLE_SUB_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        return cursor;
    }

    public Cursor fetchGroupByName(String where) {
        String query = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + where;
        Log.d(TAG,query);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        return cursor;
    }
}

