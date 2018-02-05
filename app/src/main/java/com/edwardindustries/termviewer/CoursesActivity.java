package com.edwardindustries.termviewer;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CoursesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter cursorAdapterC;
    private String action;
    private long termId, courseId;
    private Uri termUri;
    private Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        termUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE);
        termId = getIntent().getLongExtra("TERM_ID", termId);
        Log.d("CoursesActivity", "Term Id = " + termId);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                action = Intent.ACTION_INSERT;
                Intent intent = new Intent(CoursesActivity.this, CourseEditor.class);
                intent.putExtra("TERM_ID", termId);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE, termUri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        //insertTerm("New com.edwardindustries.termviewer.Term", "10/24/2017", "10/30/2018");
        String[] from = {DBOpen.COURSE_CODE, DBOpen.COURSE_NAME};
        int[] to = {R.id.tvCourseCode, R.id.tvCourseName};
        cursorAdapterC = new SimpleCursorAdapter(this, R.layout.course_list_item, null, from, to, 0);

        ListView list = findViewById(R.id.courselist);
        list.setAdapter(cursorAdapterC);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CoursesActivity.this, CourseDetail.class);
                Uri courseUri = Uri.parse(Provider.CONTENT_URI_C + "/" + l);
                //courseId = intent.getLongExtra("COURSE_ID", courseId);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE_C, courseUri);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE, termUri);
                //intent.putExtra("COURSE_ID", courseId);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        loadTerm();
        restartLoader();
    }

    private void loadTerm() {
        if(termUri == null) {
            setResult(RESULT_CANCELED);
            finish();
        }
        else {
            term = Term.getTerm(this, termId);
            setTitle("Courses");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Provider.CONTENT_URI_C, DBOpen.ALL_COLUMNS_COURSES, DBOpen.COURSE_TERM_ID + " = " + this.termId, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapterC.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapterC.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        loadTerm();
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

}


