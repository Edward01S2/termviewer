package com.edwardindustries.termviewer;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by edward on 12/19/17.
 */

public class Course {
    public long courseId;
    public String courseCode;
    public String courseName;
    public String courseStart;
    public String courseEnd;


    public static Course getCourse(Context context, long id) {
        Cursor cursor = context.getContentResolver().query(Provider.CONTENT_URI_C, DBOpen.ALL_COLUMNS_COURSES, DBOpen.COURSE_ID + "=" + id, null, null);

        cursor.moveToFirst();
        String courseCode = cursor.getString(cursor.getColumnIndex(DBOpen.COURSE_CODE));
        String courseName = cursor.getString(cursor.getColumnIndex(DBOpen.COURSE_NAME));
        String courseStartDate = cursor.getString(cursor.getColumnIndex(DBOpen.COURSE_START));
        String courseEndDate = cursor.getString(cursor.getColumnIndex(DBOpen.COURSE_END));

        Course t = new Course();
        t.courseId = id;
        t.courseCode = courseCode;
        t.courseName = courseName;
        t.courseStart = courseStartDate;
        t.courseEnd = courseEndDate;

        return t;
    }
}
