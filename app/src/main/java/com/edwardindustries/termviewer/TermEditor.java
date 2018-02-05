package com.edwardindustries.termviewer;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TermEditor extends AppCompatActivity{
    Calendar dateSel = Calendar.getInstance();
    EditText edittext, edittext2, editor;
    private String action;
    private long termId;
    private String termFilter;
    private String oName, oStart, oEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editor = findViewById(R.id.editText);

        edittext = findViewById(R.id.editText2);
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TermEditor.this, date, dateSel.get(Calendar.YEAR), dateSel.get(Calendar.MONTH), dateSel.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edittext2 = findViewById(R.id.editText3);
        edittext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TermEditor.this, date2, dateSel.get(Calendar.YEAR), dateSel.get(Calendar.MONTH), dateSel.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE);
        termId = intent.getLongExtra("TERM_ID", termId);
        if(uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle("New Term");
        }
        else {
            action = Intent.ACTION_EDIT;
            termFilter = DBOpen.TERM_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri, DBOpen.ALL_COLUMNS, termFilter, null, null);
            cursor.moveToFirst();
            oName = cursor.getString(cursor.getColumnIndex(DBOpen.TERM_NAME));
            oStart = cursor.getString(cursor.getColumnIndex(DBOpen.TERM_START));
            oEnd = cursor.getString(cursor.getColumnIndex(DBOpen.TERM_END));
            setTitle(oName);
            editor.setText(oName);
            edittext.setText(oStart);
            edittext2.setText(oEnd);

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
                deleteTerm();
                //Skip over com.edwardindustries.termviewer.Term Detail if com.edwardindustries.termviewer.Term is deleted
                Intent intent = new Intent(this, TermsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_save:
                finishEdit();
                break;
        }

        return true;
    }

    private void deleteTerm() {
        long classCount = getClassCount(TermEditor.this);
        if(classCount == 0) {
            getContentResolver().delete(Provider.CONTENT_URI, termFilter, null);
            Toast.makeText(this, R.string.term_deleted, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
        else {
            Toast.makeText(this, R.string.del_term_toast, Toast.LENGTH_LONG).show();
        }
    }

    public long getClassCount(Context context) {
        Cursor cursor = context.getContentResolver().query(Provider.CONTENT_URI_C, DBOpen.ALL_COLUMNS_COURSES, DBOpen.COURSE_TERM_ID+ "=" + this.termId, null, null );
        int numRows = cursor.getCount();
        return numRows;
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            dateSel.set(Calendar.YEAR, i);
            dateSel.set(Calendar.MONTH, i1);
            dateSel.set(Calendar.DAY_OF_MONTH, i2);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            edittext.setText(sdf.format(dateSel.getTime()));

        }
    };

    DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            dateSel.set(Calendar.YEAR, i);
            dateSel.set(Calendar.MONTH, i1);
            dateSel.set(Calendar.DAY_OF_MONTH, i2);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            edittext2.setText(sdf.format(dateSel.getTime()));

        }
    };

    private void finishEdit() {
        String newText = editor.getText().toString().trim();
        String start = edittext.getText().toString().trim();
        String end = edittext2.getText().toString().trim();

        switch(action) {
            case Intent.ACTION_INSERT:
                if(newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                    Log.d("TermEditor", "No new text");
                }
                else {
                    insertTerm(newText, start, end);
                    Log.d("TermEditor", "Text inserted");
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteTerm();
                }
                else if(oName.equals(newText) && (oStart.equals(start)) && (oEnd.equals(end))) {
                    setResult(RESULT_CANCELED);
                }
                else {
                    updateTerm(newText, start, end);
                    setResult(RESULT_OK);
                }
        }
        finish();
    }

    private void updateTerm(String newText, String start, String end) {
        ContentValues values = new ContentValues();
        values.put(DBOpen.TERM_NAME, newText);
        values.put(DBOpen.TERM_START, start);
        values.put(DBOpen.TERM_END, end);
        getContentResolver().update(Provider.CONTENT_URI, values, termFilter, null);
        Toast.makeText(this, "com.edwardindustries.termviewer.Term updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertTerm(String newText, String start, String end) {
        ContentValues values = new ContentValues();
        values.put(DBOpen.TERM_NAME, newText);
        values.put(DBOpen.TERM_START, start);
        values.put(DBOpen.TERM_END, end);
        getContentResolver().insert(Provider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEdit();
        //Log.d("TermEditor", "Back pressed");
    }

}
