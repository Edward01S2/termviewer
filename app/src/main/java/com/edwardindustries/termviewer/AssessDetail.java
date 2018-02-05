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

public class AssessDetail extends AppCompatActivity {
    private long courseId;
    private Uri courseUri, assessUri;
    private String assessFilter, action;
    private String oText, oType, oDate, oId;
    private static final int EDITOR_REQUEST_CODE = 1001;
    private TextView assessTitle, assessType, assessDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assess_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Assessment Detail");
        assessTitle = findViewById(R.id.tvAssessDetailTitle);
        assessType = findViewById(R.id.tvAssessDetailType);
        assessDate = findViewById(R.id.tvAssessDetailDate);

        Intent intent = getIntent();
        courseId = intent.getLongExtra("COURSE_ID", courseId);
        courseUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_C);
        assessUri = intent.getParcelableExtra(Provider.CONTENT_ITEM_TYPE_A);

        updateView();
    }

    private void updateView() {

        assessFilter = DBOpen.ASSESS_ID + "=" + assessUri.getLastPathSegment();

        Cursor cursor = getContentResolver().query(assessUri, DBOpen.ALL_COLUMNS_ASSESS, assessFilter, null, null);
        cursor.moveToFirst();

        oId = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_ID));
        oText = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_TITLE));
        oType = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_TYPE));
        oDate = cursor.getString(cursor.getColumnIndex(DBOpen.ASSESS_DUE));
        Log.d("AssessDetail", oText);

        assessTitle.setText(oText);
        assessType.setText(oType);
        assessDate.setText(oDate);
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
                Intent intent = new Intent(AssessDetail.this, AssessEditor.class);
                Uri uri = Uri.parse(Provider.CONTENT_URI_A + "/" + oId);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE_A, uri);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE_C, courseUri);
                intent.putExtra("COURSE_ID", courseId);
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
        }
    }

}
