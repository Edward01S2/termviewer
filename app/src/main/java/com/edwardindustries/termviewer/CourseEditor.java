package com.edwardindustries.termviewer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CourseEditor extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Calendar dateSel = Calendar.getInstance();
    EditText edittext, edittext2, editCode, editName;
    private String action, status, termFilter, tName;
    private String courseFilter, key, key2, courseName, cId;
    private String oCode, oName, oStart, oEnd, oStatus, oId, list[];
    private long termId;
    private Uri termUri;
    CheckBox checkStart, checkEnd;
    Boolean cbStart, cbEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("TERM_ID")) {
            termId = getIntent().getLongExtra("TERM_ID", termId);
            Log.d("CoursesEditor", "Term Id = " + termId);
        }
        else {
            Log.d("CoursesEditor", "Term Id not found ");
        }


        editCode = findViewById(R.id.etEditCCode);
        editName = findViewById(R.id.etEditCName);
        edittext = findViewById(R.id.etEditCStart);
        edittext2 = findViewById(R.id.etEditCEnd);
        checkStart = findViewById(R.id.cbStartDate);
        checkEnd = findViewById(R.id.cbEndDate);

        Intent intent = getIntent();
        termUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE);
        Uri uri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_C);

        termFilter = DBOpen.TERM_ID + "=" + termUri.getLastPathSegment();
        Cursor cursorT = getContentResolver().query(termUri, DBOpen.ALL_COLUMNS, termFilter, null, null);
        cursorT.moveToFirst();
        tName = cursorT.getString(cursorT.getColumnIndex(DBOpen.TERM_NAME));

        //Set array to spinner
        Spinner spinner = findViewById(R.id.spinStatus);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_spin, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        if(uri == null) {
            action = Intent.ACTION_INSERT;
            //termId = null;
            setTitle("New Course");
        }
        else {
            action = Intent.ACTION_EDIT;
            setTitle("Edit Course");
            courseFilter = DBOpen.COURSE_ID + "=" + uri.getLastPathSegment();
            //termId = getIntent().getLongExtra("TERM_ID", termId);
            Cursor cursor = getContentResolver().query(uri, DBOpen.ALL_COLUMNS_COURSES, courseFilter, null, null);
            cursor.moveToFirst();
            oId = cursor.getString(cursor.getColumnIndex(DBOpen.COURSE_ID));
            oCode = cursor.getString(cursor.getColumnIndex(DBOpen.COURSE_CODE));
            oName = cursor.getString(cursor.getColumnIndex(DBOpen.COURSE_NAME));
            oStart = cursor.getString(cursor.getColumnIndex(DBOpen.COURSE_START));
            oEnd = cursor.getString(cursor.getColumnIndex(DBOpen.COURSE_END));
            oStatus = cursor.getString(cursor.getColumnIndex(DBOpen.COURSE_STATUS));
            editCode.setText(oCode);
            editName.setText(oName);
            edittext.setText(oStart);
            edittext2.setText(oEnd);
            spinner.setSelection(getIndex(spinner, oStatus));
            editCode.requestFocus();
            loadSavedPreferences(oId);

        }

        //Date Pickers
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                dateSel.set(Calendar.YEAR, i);
                dateSel.set(Calendar.MONTH, i1);
                dateSel.set(Calendar.DAY_OF_MONTH, i2);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                edittext.setText(sdf.format(dateSel.getTime()));

            }
        };

        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                dateSel.set(Calendar.YEAR, i);
                dateSel.set(Calendar.MONTH, i1);
                dateSel.set(Calendar.DAY_OF_MONTH, i2);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                edittext2.setText(sdf.format(dateSel.getTime()));

            }
        };


        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CourseEditor.this, date, dateSel.get(Calendar.YEAR), dateSel.get(Calendar.MONTH), dateSel.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        edittext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CourseEditor.this, date2, dateSel.get(Calendar.YEAR), dateSel.get(Calendar.MONTH), dateSel.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void loadSavedPreferences(String id) {
        SharedPreferences mPref = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        key = "CourseS:" + id;
        key2 = "CourseE:" + id;

        cbStart = mPref.contains(key);
        cbEnd = mPref.contains(key2);
        checkStart.setChecked(cbStart);
        checkEnd.setChecked(cbEnd);

    }

    private void deletePref(String key) {
        SharedPreferences mPref = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        editor.remove(key);
        editor.apply();
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences mPref = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void savePreferences(String key, String value) {
        SharedPreferences mPref = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        ((TextView)parent.getChildAt(0)).setTextSize(20);
        status = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), "Selected: " + status, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private int getIndex(Spinner spinner, String string) {
        int index = 0;
        for(int i = 0; i < spinner.getCount(); i++) {
            if(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(string)) {
                index = i;
                break;
            }
        }
        return index;
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
                deleteCourse();
                Intent intent = new Intent(this, CoursesActivity.class);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE, termUri);
                intent.putExtra("TERM_ID", termId);
                startActivity(intent);
                break;
            case R.id.action_save:
                finishEdit();
                break;
        }

        return true;
    }

    private void deleteCourse() {
        getContentResolver().delete(Provider.CONTENT_URI_C, courseFilter, null);
        Toast.makeText(this, "Course Deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();

        key = "CourseE:" + oId;
        key2 = "CourseS:" + oId;
        deletePref(key);
        deletePref(key2);
    }


    private void finishEdit() {
        String code = editCode.getText().toString().trim();
        String name = editName.getText().toString().trim();
        String start = edittext.getText().toString().trim();
        String end = edittext2.getText().toString().trim();
        Boolean cbS = checkStart.isChecked();
        Boolean cbE = checkEnd.isChecked();

        switch(action) {
            case Intent.ACTION_INSERT:
                if(code.length() == 0) {
                    setResult(RESULT_CANCELED);
                    //Log.d("TermEditor", "No new text");
                }
                else {
                    insertCourse(code, name, start, end, termId);
                    //Log.d("TermEditor", "Text inserted");
                }
                break;
            case Intent.ACTION_EDIT:
                if (code.length() == 0) {
                    deleteCourse();
                }
                else if(oCode.equals(code) && (oName.equals(name)) && (oStart.equals(start)) && (oEnd.equals(end)) && (oStatus.equals(status)) && (cbStart.equals(cbS)) && (cbEnd.equals(cbE))) {
                    setResult(RESULT_CANCELED);
                }
                else {
                    updateCourse(code, name, start, end);
                    setResult(RESULT_OK);
                }
        }
        finish();
    }

    private void updateCourse(String code, String name, String start, String end) {
        ContentValues values = new ContentValues();
        values.put(DBOpen.COURSE_CODE, code);
        values.put(DBOpen.COURSE_NAME, name);
        values.put(DBOpen.COURSE_START, start);
        values.put(DBOpen.COURSE_END, end);
        values.put(DBOpen.COURSE_STATUS, status);
        //values.put(DBOpen.COURSE_TERM_ID, termId);
        getContentResolver().update(Provider.CONTENT_URI_C, values, courseFilter, null);
        Toast.makeText(this, "Course updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);

        SharedPreferences mPref = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        key = "CourseS:" + oId;
        key2 = "CourseE:" + oId;
        cbStart = mPref.contains(key);
        cbEnd = mPref.contains(key2);
        deletePref(key);
        deletePref(key2);

        if(checkStart.isChecked()) {

            list = new String[]{name, start, tName};
            List<String> stringList = new ArrayList<String>(Arrays.asList(list));
            Gson gson = new Gson();
            String json = gson.toJson(stringList);

            savePreferences("CourseS:" + oId, json);
        }
        if(checkEnd.isChecked()) {

            list = new String[]{name, end, tName};
            List<String> stringList = new ArrayList<String>(Arrays.asList(list));
            Gson gson = new Gson();
            String json = gson.toJson(stringList);

            savePreferences("CourseE:" + oId, json);
        }
    }

    private void insertCourse(String code, String name, String start, String end, long termId) {

        ContentValues values = new ContentValues();
        values.put(DBOpen.COURSE_CODE, code);
        values.put(DBOpen.COURSE_NAME, name);
        values.put(DBOpen.COURSE_START, start);
        values.put(DBOpen.COURSE_END, end);
        values.put(DBOpen.COURSE_STATUS, status);
        values.put(DBOpen.COURSE_TERM_ID, termId);
        getContentResolver().insert(Provider.CONTENT_URI_C, values);
        setResult(RESULT_OK);

        courseName = DBOpen.COURSE_NAME + "= \"" + name + "\"";
        Cursor cursor = getContentResolver().query(Provider.CONTENT_URI_C, DBOpen.ALL_COLUMNS_COURSES, courseName, null, null);
        cursor.moveToFirst();
        cId = cursor.getString(cursor.getColumnIndex(DBOpen.COURSE_ID));

        if(checkStart.isChecked()) {

            list = new String[]{name, start, tName};
            List<String> stringList = new ArrayList<String>(Arrays.asList(list));
            Gson gson = new Gson();
            String json = gson.toJson(stringList);

            savePreferences("CourseS:" + cId, json);
        }
        if(checkEnd.isChecked()) {

            list = new String[]{name, end, tName};
            List<String> stringList = new ArrayList<String>(Arrays.asList(list));
            Gson gson = new Gson();
            String json = gson.toJson(stringList);

            savePreferences("CourseE:" + cId, json);
        }
    }

    @Override
    public void onBackPressed() {
        finishEdit();
        //Log.d("TermEditor", "Back pressed");
    }
}
