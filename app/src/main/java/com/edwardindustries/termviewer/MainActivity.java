package com.edwardindustries.termviewer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int TERM_CODE = 1001;
    private static final int COURSE_CODE = 1002;
    private static final int ASSESS_CODE = 1003;
    public static final String PREFS_NAME = "myPrefs";
    private String value, curDate, keyType;
    private ArrayList<String> msg;
    private StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Clear Shared Preferences
       /*SharedPreferences mPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        editor.clear();
        editor.commit();*/

        //Get Current Date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        curDate = df.format(c.getTime());


        //Loop through preferences
        SharedPreferences mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Map<String, ?> keys = mPrefs.getAll();
        msg = new ArrayList<String>();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            //Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
            Gson gson = new Gson();
            String json = entry.getValue().toString();

            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> arraylist = gson.fromJson(json, type);



            if ((arraylist.get(1)).equals(curDate)) {
                String key = entry.getKey().toString();
                String[] keySep = key.split(":");
                //Log.d("Key Type", keySep[0]);
                switch(keySep[0]) {
                    case "CourseE":
                        keyType = "- " + arraylist.get(0).toUpperCase() + " course ending today for " + arraylist.get(2).toUpperCase();
                        break;
                    case "Assess":
                        keyType = "- " + arraylist.get(0).toUpperCase() + " assessment due today for " + arraylist.get(2).toUpperCase() + " course";
                        break;
                    case "CourseS":
                        keyType = "- " + arraylist.get(0).toUpperCase() + " course starting today for " + arraylist.get(2).toUpperCase();
                        break;
                }

                msg.add(keyType);

                //Log.d("Pref", keyType);
            }

        }
        sb = new StringBuilder();
        for(String s : msg) {
            sb.append(s);
            sb.append("\n");
            sb.append("\n");
        }

        //Create alert dialog values

        if(!msg.isEmpty()) {

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Upcoming Alerts")
                .setMessage(sb.toString())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }
    }

    public void toTerms(View view) {
        Intent intent = new Intent(this, TermsActivity.class);
        startActivityForResult(intent, TERM_CODE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_delete_all:
                deleteAll();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert Data management code here
                            getContentResolver().delete(Provider.CONTENT_URI, null, null);

                            Toast.makeText(MainActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }


    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {

    }

}
