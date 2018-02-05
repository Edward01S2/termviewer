package com.edwardindustries.termviewer;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by edward on 12/17/17.
 */

public class Term {
    public long termId;
    public String termName;
    public String termStart;
    public String termEnd;


    public static Term getTerm(Context context, long id) {
        Cursor cursor = context.getContentResolver().query(Provider.CONTENT_URI, DBOpen.ALL_COLUMNS, DBOpen.TERM_ID + "=" + id, null, null);

        cursor.moveToFirst();
        String termName = cursor.getString(cursor.getColumnIndex(DBOpen.TERM_NAME));
        String termStartDate = cursor.getString(cursor.getColumnIndex(DBOpen.TERM_START));
        String termEndDate = cursor.getString(cursor.getColumnIndex(DBOpen.TERM_END));

        Term t = new Term();
        t.termId = id;
        t.termName = termName;
        t.termStart = termStartDate;
        t.termEnd = termEndDate;

        return t;
    }
}