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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TermDetail extends AppCompatActivity {
    private static final int COURSE_LIST_ACTIVITY_CODE = 2002;
    private String action;
    private String termFilter;
    private String oName, oStart, oEnd, oID;
    TextView detailName, detailStart, detailEnd;
    private Uri termUri;
    private static final int EDITOR_REQUEST_CODE = 1001;
    private Long termId;
    private Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        termUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE);
        termId = Long.parseLong(termUri.getLastPathSegment());

        setTitle("Term Detail");
        detailName = findViewById(R.id.tvTermName);
        detailStart = findViewById(R.id.tvTermStart);
        detailEnd = findViewById(R.id.tvTermEnd);
        updateView();
        loadTerm();


    }

    private void loadTerm() {
        if (termUri == null) {
            setResult(RESULT_CANCELED);
            finish();
        }

        else {
            termId = Long.parseLong(termUri.getLastPathSegment());
            term = Term.getTerm(this, termId);

            detailName.setText(term.termName);
            detailStart.setText(term.termStart);
            detailEnd.setText(term.termEnd);
        }
    }

    private void updateView() {

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE);
        termFilter = DBOpen.TERM_ID + "=" + uri.getLastPathSegment();

        Cursor cursor = getContentResolver().query(uri, DBOpen.ALL_COLUMNS, termFilter, null, null);
        cursor.moveToFirst();
        oName = cursor.getString(cursor.getColumnIndex(DBOpen.TERM_NAME));
        oID = cursor.getString(cursor.getColumnIndex(DBOpen.TERM_ID));
        oStart = cursor.getString(cursor.getColumnIndex(DBOpen.TERM_START));
        oEnd = cursor.getString(cursor.getColumnIndex(DBOpen.TERM_END));

        detailName.setText(oName);
        detailStart.setText(oStart);
        detailEnd.setText(oEnd);
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
        int id = item.getItemId();

        switch(item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.action_edit:
                action = Intent.ACTION_EDIT;
                Intent intent = new Intent(TermDetail.this, TermEditor.class);
                Uri uri = Uri.parse(Provider.CONTENT_URI + "/" + oID);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE, uri);
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
            //Log.d("TermsActivity", "Request recieved");
            termUri = data.getParcelableExtra("TERM_URI");
            updateView();
        }
    }

    public void openClassList(View view) {
        Intent intent = new Intent(this, CoursesActivity.class);
        intent.putExtra(Provider.CONTENT_ITEM_TYPE, termUri);
        intent.putExtra("TERM_ID", termId);
        startActivityForResult(intent, COURSE_LIST_ACTIVITY_CODE);
    }

}
