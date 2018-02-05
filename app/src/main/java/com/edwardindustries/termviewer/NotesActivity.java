package com.edwardindustries.termviewer;

import android.app.LoaderManager;
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

public class NotesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EDITOR_REQUEST_CODE = 1001;
    private Uri courseUri;
    private long courseId;
    private CursorAdapter cursorAdapter;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action = Intent.ACTION_INSERT;
                Intent intent = new Intent(NotesActivity.this, NoteEditor.class);
                intent.putExtra("COURSE_ID", courseId);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        courseUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_C);
        courseId = intent.getLongExtra("COURSE_ID", courseId);
        //Log.d("MentorActivity", "Course = " + courseUri);

        //String[] from = {DBOpen.NOTE_TEXT};
        //int[] to = {android.R.id.text1};
        //cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);

        cursorAdapter = new NotesAdapter(this, null, 0);

        ListView list = findViewById(R.id.lvNotes);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NotesActivity.this, NoteDetail.class);
                Uri uri = Uri.parse(Provider.CONTENT_URI_N + "/" + l);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE_N, uri);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE_C, courseUri);
                intent.putExtra("COURSE_ID", courseId);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        loadCourse();
        restartLoader();
    }

    private void loadCourse() {
        if(courseUri == null) {
            setResult(RESULT_CANCELED);
            finish();
        }
        else {
            //course = Course.getCourse(this, courseId);
            setTitle("Course Notes");
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
        return new CursorLoader(this, Provider.CONTENT_URI_N, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        loadCourse();
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }




}

