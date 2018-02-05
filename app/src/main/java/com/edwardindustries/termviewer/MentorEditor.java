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

public class MentorEditor extends AppCompatActivity {
    private long courseId;
    EditText editName, editPhone, editEmail;
    private Uri courseUri, mentorUri;
    private String action, mentorFilter;
    private String oName, oPhone, oEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("COURSE_ID")) {
            courseId = getIntent().getLongExtra("COURSE_ID", courseId);
            //Log.d("CoursesEditor", "Term Id = " + termId);
        }

        editName = findViewById(R.id.etEditMentorName);
        editPhone = findViewById(R.id.etEditMentorPhone);
        editEmail = findViewById(R.id.etEditMentorEmail);

        Intent intent = getIntent();
        courseUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_C);
        mentorUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_M);

        if(mentorUri == null) {
            action = Intent.ACTION_INSERT;
            setTitle("New Mentor");
        }
        else {
            action = Intent.ACTION_EDIT;
            setTitle("Edit Mentor");
            mentorFilter = DBOpen.MENTOR_ID + "=" + mentorUri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(mentorUri, DBOpen.ALL_COLUMNS_MENTORS, mentorFilter, null, null);
            cursor.moveToFirst();
            oName = cursor.getString(cursor.getColumnIndex(DBOpen.MENTOR_NAME));
            oPhone = cursor.getString(cursor.getColumnIndex(DBOpen.MENTOR_PHONE));
            oEmail = cursor.getString(cursor.getColumnIndex(DBOpen.MENTOR_EMAIL));
            editName.setText(oName);
            editPhone.setText(oPhone);
            editEmail.setText(oEmail);
            editName.requestFocus();

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
                deleteMentor();
                Intent intent = new Intent(this, MentorActivity.class);
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

    private void deleteMentor() {
        getContentResolver().delete(Provider.CONTENT_URI_M, mentorFilter, null);
        Toast.makeText(this, "Mentor Deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEdit() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        switch(action) {
            case Intent.ACTION_INSERT:
                if(name.length() == 0) {
                    setResult(RESULT_CANCELED);
                    //Log.d("TermEditor", "No new text");
                }
                else {
                    insertMentor(name, phone, email, courseId);
                    //Log.d("TermEditor", "Text inserted");
                }
                break;
            case Intent.ACTION_EDIT:
                if (name.length() == 0) {
                    deleteMentor();
                }
                else if(oName.equals(name) && (oPhone.equals(phone)) && (oEmail.equals(email))) {
                    setResult(RESULT_CANCELED);
                }
                else {
                    updateMentor(name, phone, email);
                    setResult(RESULT_OK);
                }
        }
        finish();
    }

    private void updateMentor(String name, String phone, String email) {
        ContentValues values = new ContentValues();
        values.put(DBOpen.MENTOR_NAME, name);
        values.put(DBOpen.MENTOR_PHONE, phone);
        values.put(DBOpen.MENTOR_EMAIL, email);
        getContentResolver().update(Provider.CONTENT_URI_M, values, mentorFilter, null);
        Toast.makeText(this, "Mentor Updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertMentor(String name, String phone, String email, long courseId) {
        ContentValues values = new ContentValues();
        values.put(DBOpen.MENTOR_NAME, name);
        values.put(DBOpen.MENTOR_PHONE, phone);
        values.put(DBOpen.MENTOR_EMAIL, email);
        values.put(DBOpen.MENTOR_COURSE_ID, courseId);
        getContentResolver().insert(Provider.CONTENT_URI_M, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEdit();
        //Log.d("TermEditor", "Back pressed");
    }

}
