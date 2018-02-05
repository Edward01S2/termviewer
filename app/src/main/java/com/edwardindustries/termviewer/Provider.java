package com.edwardindustries.termviewer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by edward on 12/7/17.
 */

public class Provider extends ContentProvider {

    private static final String AUTHORITY = "com.edwardindustries.termviewer.provider";
    private static final String BASE_PATH = "terms";
    private static final String BASE_PATH_C = "courses";
    private static final String BASE_PATH_M = "mentors";
    private static final String BASE_PATH_N = "notes";
    private static final String BASE_PATH_A = "assess";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final Uri CONTENT_URI_C = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_C);
    public static final Uri CONTENT_URI_M = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_M);
    public static final Uri CONTENT_URI_N = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_N);
    public static final Uri CONTENT_URI_A = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_A);

    private static final int TERMS = 1;
    private static final int TERMS_ID = 2;
    private static final int COURSES = 3;
    private static final int COURSE_ID = 4;
    private static final int MENTORS = 5;
    private static final int MENTOR_ID = 6;
    private static final int NOTES = 7;
    private static final int NOTE_ID = 8;
    private static final int ASSESS = 9;
    private static final int ASSESS_ID = 10;


    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Term";
    public static final String CONTENT_ITEM_TYPE_C = "Course";
    public static final String CONTENT_ITEM_TYPE_M = "Mentor";
    public static final String CONTENT_ITEM_TYPE_N = "Note";
    public static final String CONTENT_ITEM_TYPE_A = "Assess";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, TERMS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TERMS_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH_C, COURSES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH_C + "/#", COURSE_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH_M, MENTORS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH_M + "/#", MENTOR_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH_N, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH_N + "/#", NOTE_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH_A, ASSESS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH_A + "/#", ASSESS_ID);


    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        DBOpen helper = new DBOpen(getContext());
        db = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query( Uri uri, String[] strings,  String s, String[] strings1,  String s1) {
        String x = "";
        String z = "";
        String[] y = {""};

        switch(uriMatcher.match(uri)) {
            case TERMS:
                x = DBOpen.TABLE_TERMS;
                y = DBOpen.ALL_COLUMNS;
                z = DBOpen.TERM_START + " ASC";
                break;

            case TERMS_ID:
                s = DBOpen.TERM_ID + "=" + uri.getLastPathSegment();
                x = DBOpen.TABLE_TERMS;
                y = DBOpen.ALL_COLUMNS;
                z = DBOpen.TERM_START + " ASC";
                break;

            case COURSES:
                x = DBOpen.TABLE_COURSES;
                y = DBOpen.ALL_COLUMNS_COURSES;
                z = DBOpen.COURSE_CODE + " ASC";
                break;

            case COURSE_ID:
                s = DBOpen.COURSE_ID + "=" + uri.getLastPathSegment();
                x = DBOpen.TABLE_COURSES;
                y = DBOpen.ALL_COLUMNS_COURSES;
                z = DBOpen.COURSE_CODE + " ASC";
                break;

            case MENTORS:
                x = DBOpen.TABLE_MENTORS;
                y = DBOpen.ALL_COLUMNS_MENTORS;
                z = DBOpen.MENTOR_NAME + " ASC";
                break;

            case MENTOR_ID:
                s = DBOpen.MENTOR_ID + "=" + uri.getLastPathSegment();
                x = DBOpen.TABLE_MENTORS;
                y = DBOpen.ALL_COLUMNS_MENTORS;
                z = DBOpen.MENTOR_NAME + " ASC";
                break;

            case NOTES:
                x = DBOpen.TABLE_NOTES;
                y = DBOpen.ALL_COLUMNS_NOTES;
                z = DBOpen.NOTE_ID + " ASC";
                break;

            case NOTE_ID:
                s = DBOpen.NOTE_ID + "=" + uri.getLastPathSegment();
                x = DBOpen.TABLE_NOTES;
                y = DBOpen.ALL_COLUMNS_NOTES;
                z = DBOpen.NOTE_ID + " ASC";
                break;

            case ASSESS:
                x = DBOpen.TABLE_ASSESS;
                y = DBOpen.ALL_COLUMNS_ASSESS;
                z = DBOpen.ASSESS_TITLE + " ASC";
                break;

            case ASSESS_ID:
                s = DBOpen.ASSESS_ID + "=" + uri.getLastPathSegment();
                x = DBOpen.TABLE_ASSESS;
                y = DBOpen.ALL_COLUMNS_ASSESS;
                z = DBOpen.ASSESS_TITLE + " ASC";
                break;
        }

        return db.query(x, y, s, null, null, null, z);
    }

/*    public Cursor getCourses() {
        return db.query(DBOpen.TABLE_COURSES, DBOpen.ALL_COLUMNS_COURSES, DBOpen.TERM_ID, null, null, null, null);
    }*/

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri _uri = null;
        switch(uriMatcher.match(uri)) {
            case TERMS:
                long id = db.insert(DBOpen.TABLE_TERMS, null, contentValues);
                _uri =  Uri.parse(BASE_PATH + "/" + id);
                break;

            case COURSES:
                long idC = db.insert(DBOpen.TABLE_COURSES, null, contentValues);
                _uri = Uri.parse(BASE_PATH + "/" + idC);
                break;

            case MENTORS:
                long idM = db.insert(DBOpen.TABLE_MENTORS, null, contentValues);
                _uri = Uri.parse(BASE_PATH_M + "/" + idM);
                break;

            case NOTES:
                long idN = db.insert(DBOpen.TABLE_NOTES, null, contentValues);
                _uri = Uri.parse(BASE_PATH_N + "/" + idN);
                break;

            case ASSESS:
                long idA = db.insert(DBOpen.TABLE_ASSESS, null, contentValues);
                _uri = Uri.parse(BASE_PATH_A + "/" + idA);
                break;

            default: throw new SQLException("Failed to insert row into " + uri);
        }
        return _uri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        String x = "";
        switch(uriMatcher.match(uri)) {
            case TERMS:
                x = DBOpen.TABLE_TERMS;
                break;

            case COURSES:
               x = DBOpen.TABLE_COURSES;
               break;

            case MENTORS:
                x = DBOpen.TABLE_MENTORS;
                break;

            case NOTES:
                x = DBOpen.TABLE_NOTES;
                break;

            case ASSESS:
                x = DBOpen.TABLE_ASSESS;
                break;
        }
        return db.delete(x, s, strings);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        String x = "";
        switch(uriMatcher.match(uri)) {
            case TERMS:
                x = DBOpen.TABLE_TERMS;
                break;

            case COURSES:
                x = DBOpen.TABLE_COURSES;
                break;

            case MENTORS:
                x = DBOpen.TABLE_MENTORS;
                break;

            case NOTES:
                x = DBOpen.TABLE_NOTES;
                break;

            case ASSESS:
                x = DBOpen.TABLE_ASSESS;
                break;
        }
        return db.update(x, contentValues, s, strings);
    }
}

