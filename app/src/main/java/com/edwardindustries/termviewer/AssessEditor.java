package com.edwardindustries.termviewer;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

public class AssessEditor extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private long courseId;
    private Uri courseUri, assessUri;
    private String action, assessFilter, status, courseFilter;
    private String oTitle, oType, oDate, oId;
    private EditText editTitle, editType, editDate;
    private String aId, aTitle, aDate, list[], assessTitle, key, cTitle;
    private Boolean cbDue, dueCheck;
    Calendar dateSel = Calendar.getInstance();
    CheckBox checkDue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assess_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("COURSE_ID")) {
            courseId = getIntent().getLongExtra("COURSE_ID", courseId);
            //Log.d("CoursesEditor", "Term Id = " + termId);
        }

        editTitle = findViewById(R.id.etEditATitle);
        editDate = findViewById(R.id.etEditADue);
        checkDue = findViewById(R.id.cbDueDate);

        Intent intent = getIntent();
        courseUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_C);
        assessUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_A);

        courseFilter = DBOpen.COURSE_ID + "=" + courseUri.getLastPathSegment();
        Cursor cursorC = getContentResolver().query(courseUri, DBOpen.ALL_COLUMNS_COURSES, courseFilter, null, null);
        cursorC.moveToFirst();
        cTitle = cursorC.getString(cursorC.getColumnIndex(DBOpen.COURSE_NAME));

        Spinner spinner = findViewById(R.id.spinAType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.assess_spin, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        if(assessUri == null) {
            action = Intent.ACTION_INSERT;
            setTitle("New Assessment");
        }
        else {
            action = Intent.ACTION_EDIT;
            setTitle("Edit Assessment");
            assessFilter = DBOpen.ASSESS_ID + "=" + assessUri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(assessUri, DBOpen.ALL_COLUMNS_ASSESS, assessFilter, null, null);
            cursor.moveToFirst();
            oId = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_ID));
            oTitle = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_TITLE));
            oType = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_TYPE));
            oDate = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_DUE));
            editTitle.setText(oTitle);
            //editType.setText(oType);
            editDate.setText(oDate);
            spinner.setSelection(getIndex(spinner, oType));
            editTitle.requestFocus();
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
                editDate.setText(sdf.format(dateSel.getTime()));

            }
        };

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AssessEditor.this, date, dateSel.get(Calendar.YEAR), dateSel.get(Calendar.MONTH), dateSel.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void loadSavedPreferences(String id) {
        SharedPreferences mPref = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        key = "Assess:" + id;

        cbDue = mPref.contains(key);
        checkDue.setChecked(cbDue);

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

    private void saveCB() {
        savePreferences("assessDue", checkDue.isChecked());
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
                deleteAssess();
                Intent intent = new Intent(this, AssessActivity.class);
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

    private void deleteAssess() {
        getContentResolver().delete(Provider.CONTENT_URI_A, assessFilter, null);
        Toast.makeText(this, "Assessment Deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();

        key = "Assess:" + oId;
        deletePref(key);
    }

    private void finishEdit() {
        String title = editTitle.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        Boolean due = checkDue.isChecked();

        switch(action) {
            case Intent.ACTION_INSERT:
                if(title.length() == 0) {
                    setResult(RESULT_CANCELED);
                    //Log.d("TermEditor", "No new text");
                }
                else {
                    insertAssess(title, date, courseId);
                    //Log.d("TermEditor", "Text inserted");
                }
                break;
            case Intent.ACTION_EDIT:
                if (title.length() == 0) {
                    deleteAssess();
                }
                else if(oTitle.equals(title) && (oDate.equals(date)) && (oType.equals(status)) && (cbDue.equals(due))) {
                    setResult(RESULT_CANCELED);
                }
                else {
                    updateAssess(title, date);
                    setResult(RESULT_OK);
                }
        }
        finish();
    }

    private void updateAssess(String title, String date) {
        ContentValues values = new ContentValues();
        values.put(DBOpen.ASSESS_TITLE, title);
        values.put(DBOpen.ASSESS_DUE, date);
        values.put(DBOpen.ASSESS_TYPE, status);
        getContentResolver().update(Provider.CONTENT_URI_A, values, assessFilter, null);
        Toast.makeText(this, "Assessment Updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);

        SharedPreferences mPref = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        Cursor cursor = getContentResolver().query(Provider.CONTENT_URI_A, DBOpen.ALL_COLUMNS_ASSESS, assessFilter, null, null);
        cursor.moveToFirst();
        aId = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_ID));
        aTitle = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_TITLE));
        aDate = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_DUE));
        key = "Assess:" + aId;
        cbDue = mPref.contains(key);
        deletePref(key);

        if(checkDue.isChecked()) {

            list = new String[]{aTitle, aDate, cTitle};
            List<String> stringList = new ArrayList<String>(Arrays.asList(list));
            Gson gson = new Gson();
            String json = gson.toJson(stringList);

            savePreferences("Assess:" + aId, json);
        }

    }

    private void insertAssess(String title, String date, long courseId) {
        ContentValues values = new ContentValues();
        values.put(DBOpen.ASSESS_TITLE, title);
        values.put(DBOpen.ASSESS_DUE, date);
        values.put(DBOpen.ASSESS_TYPE, status);
        values.put(DBOpen.ASSESS_COURSE_ID, courseId);
        getContentResolver().insert(Provider.CONTENT_URI_A, values);
        setResult(RESULT_OK);

        if(checkDue.isChecked()) {
            assessTitle = DBOpen.ASSESS_TITLE + "= \"" + title + "\"";
            Cursor cursor = getContentResolver().query(Provider.CONTENT_URI_A, DBOpen.ALL_COLUMNS_ASSESS, assessTitle, null, null);
            cursor.moveToFirst();
            aId = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_ID));
            aTitle = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_TITLE));
            aDate = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_DUE));

            list = new String [] {aTitle, aDate, cTitle};
            List<String> stringList = new ArrayList<String>(Arrays.asList(list));
            Gson gson = new Gson();
            String json = gson.toJson(stringList);

            savePreferences("Assess:" + aId, json);
        }
    }

    @Override
    public void onBackPressed() {
        finishEdit();
        //Log.d("TermEditor", "Back pressed");
    }

}
