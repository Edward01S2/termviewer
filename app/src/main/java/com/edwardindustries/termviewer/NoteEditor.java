package com.edwardindustries.termviewer;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.Toast;

public class NoteEditor extends AppCompatActivity {
    private long courseId;
    private EditText editText;
    private Uri courseUri, noteUri;
    private String action, noteFilter, oText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("COURSE_ID")) {
            courseId = getIntent().getLongExtra("COURSE_ID", courseId);
        }
        else {
            Log.d("Note Editor", "Course Id not found");
        }

        editText = findViewById(R.id.tvNoteTextEdit);

        Intent intent = getIntent();
        courseUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_C);
        noteUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_N);
        courseId = intent.getLongExtra("COURSE_ID", courseId);

        if(noteUri == null) {
            action = Intent.ACTION_INSERT;
            setTitle("New Note");
        }
        else {
            action = Intent.ACTION_EDIT;
            setTitle("Edit Note");
            noteFilter = DBOpen.NOTE_ID + "=" + noteUri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(noteUri, DBOpen.ALL_COLUMNS_NOTES, noteFilter, null, null);
            cursor.moveToFirst();
            oText = cursor.getString(cursor.getColumnIndex(DBOpen.NOTE_TEXT));
            editText.setText(oText);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }
        if(action.equals(Intent.ACTION_INSERT)) {
            getMenuInflater().inflate(R.menu.menu_save, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(item.getItemId()) {
            case android.R.id.home:
                finishEdit();
                break;
            case R.id.action_delete:
                deleteNote();
                Intent intent = new Intent(this, NotesActivity.class);
                //intent.putExtra(Provider.CONTENT_ITEM_TYPE_C, courseUri);
                //intent.putExtra("COURSE_ID", courseId);
                startActivity(intent);
                break;
            case R.id.action_save:
                finishEdit();
                break;
        }

        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(Provider.CONTENT_URI_N, noteFilter, null);
        Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEdit() {
        String text = editText.getText().toString().trim();

        switch(action) {
            case Intent.ACTION_INSERT:
                if(text.length() == 0) {
                    setResult(RESULT_CANCELED);
                    //Log.d("TermEditor", "No new text");
                }
                else {
                    insertNote(text, courseId);
                    //Log.d("TermEditor", "Text inserted");
                }
                break;
            case Intent.ACTION_EDIT:
                if (text.length() == 0) {
                    deleteNote();
                }
                else if(oText.equals(text)) {
                    setResult(RESULT_CANCELED);
                }
                else {
                    updateNote(text);
                    setResult(RESULT_OK);
                }
        }
        finish();
    }

    private void updateNote(String text) {
        ContentValues values = new ContentValues();
        values.put(DBOpen.NOTE_TEXT, text);
        //values.put(DBOpen.COURSE_TERM_ID, termId);
        getContentResolver().update(Provider.CONTENT_URI_N, values, noteFilter, null);
        Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String text, long courseId) {
        ContentValues values = new ContentValues();
        values.put(DBOpen.NOTE_TEXT, text);
        values.put(DBOpen.NOTE_COURSE_ID, courseId);
        getContentResolver().insert(Provider.CONTENT_URI_N, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEdit();
        //Log.d("TermEditor", "Back pressed");
    }

}
