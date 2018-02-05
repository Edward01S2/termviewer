package com.edwardindustries.termviewer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CourseDetail extends AppCompatActivity {

    private String action;
    private String courseFilter;
    private String oName, oStart, oEnd, oID, oCode, oStatus;
    TextView detailCode, detailName, detailStart, detailEnd, detailStatus;
    private static final int EDITOR_REQUEST_CODE = 1001;
    private static final int MENTOR_LIST_ACTIVITY_CODE = 2002;
    private static final int NOTE_LIST_ACTIVITY_CODE = 3003;
    private static final int ASSESS_LIST_ACTIVITY_CODE = 4004;
    private long termId, courseId;
    private Uri termUri, courseUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Course Detail");
        detailName = findViewById(R.id.tvCName);
        detailCode = findViewById(R.id.tvCCode);
        detailStart = findViewById(R.id.tvCourseStart);
        detailEnd = findViewById(R.id.tvCourseEnd);
        detailStatus = findViewById(R.id.tvCourseStatus);

        Intent intent = getIntent();
        termId = intent.getLongExtra("TERM_ID", termId);
        termUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE);
        courseUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_C);

        updateView();

    }

    private void updateView() {

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_C);
        courseFilter = DBOpen.COURSE_ID + "=" + uri.getLastPathSegment();

        Cursor cursorC = getContentResolver().query(uri, DBOpen.ALL_COLUMNS_COURSES, courseFilter, null, null);
        cursorC.moveToFirst();
        oName = cursorC.getString(cursorC.getColumnIndex(DBOpen.COURSE_NAME));
        oID = cursorC.getString(cursorC.getColumnIndex(DBOpen.COURSE_ID));
        oCode = cursorC.getString(cursorC.getColumnIndex(DBOpen.COURSE_CODE));
        oStart = cursorC.getString(cursorC.getColumnIndex(DBOpen.COURSE_START));
        oEnd = cursorC.getString(cursorC.getColumnIndex(DBOpen.COURSE_END));
        oStatus = cursorC.getString(cursorC.getColumnIndex(DBOpen.COURSE_STATUS));

        detailName.setText(oName);
        detailCode.setText(oCode);
        detailStart.setText(oStart);
        detailEnd.setText(oEnd);
        detailStatus.setText(oStatus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_term, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.action_edit:
                action = Intent.ACTION_EDIT;
                Intent intent = new Intent(CourseDetail.this, CourseEditor.class);
                Uri uri = Uri.parse(Provider.CONTENT_URI_C + "/" + oID);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE_C, uri);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE, termUri);
                intent.putExtra("TERM_ID", termId);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (reqCode == EDITOR_REQUEST_CODE && resCode == RESULT_OK) {
            //termUri = data.getParcelableExtra("TERM_URI");
            if (data != null) {
                courseUri = data.getParcelableExtra("COURSE_URI");
                updateView();
            } else {
                finish();
            }
            //Log.d("TermsActivity", "Request recieved");
            //termUri = data.getParcelableExtra("TERM_URI");
        }
    }

    public void openMentorList(View view) {
        //Toast.makeText(this, "Mentor Clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(CourseDetail.this, MentorActivity.class);
        intent.putExtra(Provider.CONTENT_ITEM_TYPE_C, courseUri);
        intent.putExtra("COURSE_ID", courseId);
        startActivityForResult(intent, MENTOR_LIST_ACTIVITY_CODE);
    }

    public void openNoteList(View view) {
        //Toast.makeText(this, "Mentor Clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(CourseDetail.this, NotesActivity.class);
        intent.putExtra(Provider.CONTENT_ITEM_TYPE_C, courseUri);
        intent.putExtra("COURSE_ID", courseId);
        startActivityForResult(intent, NOTE_LIST_ACTIVITY_CODE);
    }

    public void openAssessList(View view) {
        //Toast.makeText(this, "Mentor Clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(CourseDetail.this, AssessActivity.class);
        intent.putExtra(Provider.CONTENT_ITEM_TYPE_C, courseUri);
        intent.putExtra("COURSE_ID", courseId);
        startActivityForResult(intent, ASSESS_LIST_ACTIVITY_CODE);
    }

}
