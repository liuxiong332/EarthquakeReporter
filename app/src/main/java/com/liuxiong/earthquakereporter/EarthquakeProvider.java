package com.liuxiong.earthquakereporter;

import android.app.ExpandableListActivity;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.content.ContentValues;

import java.util.HashMap;

/**
 * Created by liuxi_000 on 2014/7/22.
 */
public class EarthquakeProvider extends ContentProvider {
    public static final Uri CONTENT_URI =
            Uri.parse("content://com.liuxiong.earthquakeprovider/earthquakes");

    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DETAILS = "details";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_LOCATION_LAT = "latitude";
    public static final String KEY_LOCATION_LAG = "longitude";
    public static final String KEY_MAGNITUDE = "magnitude";
    public static final String KEY_LINK = "link" ;

    private static final HashMap<String, String> SEARCH_PROJECTION_MAP;
    static {
        SEARCH_PROJECTION_MAP = new HashMap<String, String>();
        SEARCH_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, KEY_SUMMARY +
        " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        SEARCH_PROJECTION_MAP.put("_id", KEY_ID +
        " AS " + "_id");
    }
    EarthquakeDatabaseHelper databaseHelper;

    private static final int QUAKES = 1;
    private static final int QUAKE_ID = 2;
    private static final int SEARCH = 3;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.liuxiong.earthquakeprovider", "earthquakes", QUAKES);
        uriMatcher.addURI("com.liuxiong.earthquakeprovider", "earthquakes/#", QUAKE_ID);
        uriMatcher.addURI("com.liuxiong.earthquakeprovider",
                SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
        uriMatcher.addURI("com.liuxiong.earthquakeprovider",
                SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
        uriMatcher.addURI("com.liuxiong.earthquakeprovider",
                SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH);
        uriMatcher.addURI("com.liuxiong.earthquakeprovider",
                SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", SEARCH);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new EarthquakeDatabaseHelper(getContext(),
                EarthquakeDatabaseHelper.DATABASE_NAME, null,
                EarthquakeDatabaseHelper.DATABASE_VERSION);
        return false;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE);

        switch (uriMatcher.match(uri)) {
            case QUAKE_ID:
                builder.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            case SEARCH:
                if(uri.getPathSegments().size()>1) {
                    builder.appendWhere(KEY_SUMMARY + " LIKE \"%" +
                            uri.getPathSegments().get(1) + "%\"");
                }
                builder.setProjectionMap(SEARCH_PROJECTION_MAP);
                break;
        }
        Cursor c = builder.query(database, projection, selection, selectionArgs, null, null,
                sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        long rowId = database.insert(
                EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, "quake", values);
        if(rowId>0) {
            Uri insertUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(uri, null);
            return insertUri;
        }
        return null;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int count;
        switch(uriMatcher.match(uri)) {
            case QUAKES:
                count = database.update(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, values,
                        selection, selectionArgs);
                break;
            case QUAKE_ID:
                String segment = uri.getPathSegments().get(1);
                count = database.update(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, values,
                        KEY_ID + "=" + segment + (selection.isEmpty()?"":" AND ("
                        +selection + ")"), selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int count;
        switch(uriMatcher.match(uri)) {
            case QUAKES:
                count = database.delete(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE, selection, selectionArgs);
                break;
            case QUAKE_ID:
                String segment = uri.getPathSegments().get(1);
                count = database.delete(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE,
                        KEY_ID + "=" + segment+ (selection.isEmpty()?"": " AND ("
                        +selection + ")"), selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unsupported Uri");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case QUAKES:
                return "vnd.android.cursor.dir/earthquake";
            case QUAKE_ID:
                return "vnd.android.cursor.item/earthquake";
            case SEARCH:
                return SearchManager.SUGGEST_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unsupport URI: " + uri);
        }
    }

    private static final class EarthquakeDatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "earthquakes.db";
        private static final int DATABASE_VERSION = 1;
        private static final String EARTHQUAKE_TABLE = "earthquakes";

        private static final String DATABASE_CREATE = "create table " + EARTHQUAKE_TABLE
                + " (" + KEY_ID + " integer primary key autoincrement, "
                + KEY_DATE + " integer, "
                + KEY_DETAILS + " text, "
                + KEY_SUMMARY + " text, "
                + KEY_LOCATION_LAT + " float, "
                + KEY_LOCATION_LAG +  " float, "
                + KEY_MAGNITUDE + " float, "
                + KEY_LINK + " text);";

        public EarthquakeDatabaseHelper (Context context, String name,
                                         SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        @Override
        public void onCreate (SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }
        @Override
        public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + EARTHQUAKE_TABLE);
            onCreate(db);
        }
    }
}
