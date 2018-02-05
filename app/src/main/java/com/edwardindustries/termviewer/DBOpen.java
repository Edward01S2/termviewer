package com.edwardindustries.termviewer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by edward on 12/7/17.
 */

public class DBOpen extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "terms.db";
    private static final int DATABASE_VERSION = 11;

    public static final String TABLE_TERMS = "terms";
    public static final String TERM_ID = "_id";
    public static final String TERM_NAME = "termName";
    public static final String TERM_START = "termStart";
    public static final String TERM_END = "termEnd";

    public static final String TABLE_COURSES = "courses";
    public static final String COURSE_ID = "_id";
    public static final String COURSE_CODE = "courseCode";
    public static final String COURSE_NAME = "courseName";
    public static final String COURSE_START = "courseStart";
    public static final String COURSE_END = "courseEnd";
    public static final String COURSE_DESC = "courseDesc";
    public static final String COURSE_STATUS = "courseStatus";
    public static final String COURSE_TERM_ID = "termId";

    public static final String TABLE_MENTORS = "mentors";
    public static final String MENTOR_ID = "_id";
    public static final String MENTOR_NAME = "mentorName";
    public static final String MENTOR_PHONE = "mentorPhone";
    public static final String MENTOR_EMAIL = "mentorEmail";
    public static final String MENTOR_COURSE_ID = "courseId";

    public static final String TABLE_NOTES = "notes";
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TEXT = "noteText";
    public static final String NOTE_COURSE_ID = "courseId";

    public static final String TABLE_ASSESS = "assess";
    public static final String ASSESS_ID = "_id";
    public static final String ASSESS_TYPE = "assessType";
    public static final String ASSESS_TITLE = "assessTitle";
    public static final String ASSESS_DUE = "assessDue";
    public static final String ASSESS_COURSE_ID = "courseId";

    public static final String[] ALL_COLUMNS =
            {TERM_ID, TERM_NAME, TERM_START, TERM_END};

    public static final String[] ALL_COLUMNS_COURSES = {COURSE_ID, COURSE_CODE, COURSE_NAME, COURSE_START, COURSE_END, COURSE_STATUS, COURSE_DESC};

    public static final String[] ALL_COLUMNS_MENTORS = {MENTOR_ID, MENTOR_NAME, MENTOR_PHONE, MENTOR_EMAIL};

    public static final String[] ALL_COLUMNS_NOTES = {NOTE_ID, NOTE_TEXT};

    public static final String[] ALL_COLUMNS_ASSESS = {ASSESS_ID, ASSESS_TYPE, ASSESS_TITLE, ASSESS_DUE};

    private static final String TABLE_CREATE = "" +
            "CREATE TABLE " + TABLE_TERMS + " (" +
            TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TERM_NAME + " TEXT, " +
            TERM_START + " TEXT, " +
            TERM_END + " TEXT" +
            ")";

    private static final String TABLE_CREATE_COURSE =
            "CREATE TABLE " + TABLE_COURSES + " (" +
            COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COURSE_CODE + " TEXT, " +
            COURSE_NAME + " TEXT, " +
            COURSE_START + " TEXT, " +
            COURSE_END + " TEXT, " +
            COURSE_DESC + " TEXT, " +
            COURSE_STATUS + " TEXT, " +
            COURSE_TERM_ID + " INTEGER, " +
            "FOREIGN KEY(" + COURSE_TERM_ID + ") REFERENCES " + TABLE_TERMS + "(" + TERM_ID + ")" +
            ")";

    private static final String TABLE_CREATE_MENTOR =
            "CREATE TABLE " + TABLE_MENTORS + " (" +
            MENTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MENTOR_NAME + " TEXT, " +
            MENTOR_PHONE + " TEXT, " +
            MENTOR_EMAIL + " TEXT, " +
            MENTOR_COURSE_ID + " INTEGER, " +
            "FOREIGN KEY(" + MENTOR_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COURSE_ID + ")" +
            ")";

    private static final String TABLE_CREATE_NOTE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
            NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NOTE_TEXT + " TEXT, " +
            NOTE_COURSE_ID + " INTEGER, " +
            "FOREIGN KEY(" + NOTE_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COURSE_ID + ")" +
            ")";

    private static final String TABLE_CREATE_ASSESS =
            "CREATE TABLE " + TABLE_ASSESS + " (" +
            ASSESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ASSESS_TITLE + " TEXT, " +
            ASSESS_DUE + " TEXT, " +
            ASSESS_TYPE + " TEXT, " +
            ASSESS_COURSE_ID + " INTEGER, " +
            "FOREIGN KEY(" + ASSESS_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COURSE_ID + ")" +
            ")";

    public DBOpen(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_CREATE_COURSE);
        db.execSQL(TABLE_CREATE_MENTOR);
        db.execSQL(TABLE_CREATE_NOTE);
        db.execSQL(TABLE_CREATE_ASSESS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESS);
        onCreate(db);
    }
}
