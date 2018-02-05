package com.edwardindustries.termviewer;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NoteDetail extends AppCompatActivity {
    private TextView noteText;
    private long courseId;
    private Uri courseUri, noteUri;
    private String noteFilter, oText, oId, action;
    private static final int EDITOR_REQUEST_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Note Detail");
        noteText = findViewById(R.id.tvNoteTextDetail);

        Intent intent = getIntent();
        courseId = intent.getLongExtra("COURSE_ID", courseId);
        courseUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_C);
        noteUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_N);

        updateView();

    }

    protected void updateView() {

        noteFilter = DBOpen.NOTE_ID + "=" + noteUri.getLastPathSegment();

        Cursor cursor = getContentResolver().query(noteUri, DBOpen.ALL_COLUMNS_NOTES, noteFilter, null, null);
        cursor.moveToFirst();

        oText = cursor.getString(cursor.getColumnIndex(DBOpen.NOTE_TEXT));
        oId = cursor.getString(cursor.getColumnIndex(DBOpen.NOTE_ID));
        noteText.setText(oText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_email, menu);
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
                Intent intent = new Intent(NoteDetail.this, NoteEditor.class);
                Uri uri = Uri.parse(Provider.CONTENT_URI_N + "/" + oId);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE_N, uri);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE_C, courseUri);
                intent.putExtra("COURSE_ID", courseId);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
                break;
            case R.id.action_email:
                composeEmail(oText);
                //Toast.makeText(this, email, Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }


    public void composeEmail(String oText) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        String uriText = "mailto:" + "?subject" + Uri.encode("Course Note") + "&body=" + Uri.encode(oText);
        Uri uri = Uri.parse(uriText);
        intent.setData(uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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
        }
    }


}
