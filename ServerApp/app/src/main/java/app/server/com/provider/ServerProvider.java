package app.server.com.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by gordon on 9/9/16.
 */
public class ServerProvider extends ContentProvider {

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "gordon_database";
    static final String TABLE_NAME = "names";
    static final int DATABASE_VERSION = 1;
    public static final String KEY_FIELD_NAME = "name";
    static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_FIELD_NAME + " TEXT NOT NULL);";

    public static final String AUTHORITY = "app.server.com.provider.ServerProvider";
    public static final String URL_SUFFIX = TABLE_NAME;
    public static final String SCHEMA = "content://";
    public static final String URL = SCHEMA + AUTHORITY + "/" + URL_SUFFIX;
    public static final Uri CONTENT_URI = Uri.parse(URL);
    public static final String DEFAULT_TYPE = "vnd.android.cursor.dir";
    public static final int URI_MATCH_SUCCESS_CODE = 1;
    public static final UriMatcher uriMatcher;

    private Context context;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, URL_SUFFIX, URI_MATCH_SUCCESS_CODE);
        uriMatcher.addURI(AUTHORITY, URL_SUFFIX + "/*", URI_MATCH_SUCCESS_CODE);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case URI_MATCH_SUCCESS_CODE:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        context.getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_MATCH_SUCCESS_CODE:
                return DEFAULT_TYPE + "/" + URL_SUFFIX;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long rowID = db.insert(TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            context.getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public boolean onCreate() {
        context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case URI_MATCH_SUCCESS_CODE:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = KEY_FIELD_NAME;
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(context.getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case URI_MATCH_SUCCESS_CODE:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        context.getContentResolver().notifyChange(uri, null);
        return count;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
