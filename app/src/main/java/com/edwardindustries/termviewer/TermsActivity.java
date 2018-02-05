package com.edwardindustries.termviewer;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TermsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter cursorAdapter;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action = Intent.ACTION_INSERT;
                Intent intent = new Intent(TermsActivity.this, TermEditor.class);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        //insertTerm("New com.edwardindustries.termviewer.Term", "10/24/2017", "10/30/2018");
        String[] from = {DBOpen.TERM_NAME, DBOpen.TERM_START, DBOpen.TERM_END};
        int[] to = {R.id.tvTerms, R.id.tvStart, R.id.tvEnd};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.term_list_item, null, from, to, 0);

        ListView list = findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(TermsActivity.this, TermDetail.class);
                Uri uri = Uri.parse(Provider.CONTENT_URI + "/" + l);
                intent.putExtra(Provider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        restartLoader();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Provider.CONTENT_URI, null, null, null, null);
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
        if (reqCode == EDITOR_REQUEST_CODE && resCode == RESULT_OK) {
            Log.d("TermsActivity", "Request recieved");
            restartLoader();

        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

}
