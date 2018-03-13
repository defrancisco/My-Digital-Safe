package com.development.security.ciphernote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.development.security.ciphernote.model.DatabaseManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditNoteActivity extends AppCompatActivity {
    Context applicationContext;
    EditText userInput = null;
    SecurityManager securityManager;
    String fileName;
    String hashedFilename = "";
    DatabaseManager databaseManager;

    String noteValue = "";

    com.development.security.ciphernote.model.File file;

    boolean newNoteFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
        newNoteFlag = intent.getBooleanExtra("newNoteFlag", false);
        String jsonForFile = intent.getStringExtra("fileObject");

        Gson gson = new Gson();

        Type type = new TypeToken<com.development.security.ciphernote.model.File>() {
        }.getType();
        file = gson.fromJson(jsonForFile, type);

//        file = new com.development.security.ciphernote.model.File();

        securityManager = SecurityManager.getInstance();
        applicationContext = this.getBaseContext();
        databaseManager = new DatabaseManager(applicationContext);


//        file = databaseManager.getFileByName(fileName);

        hashedFilename = fileName;

        Log.d("help", "Filename: " + fileName + " hash: " + hashedFilename);

        setTitle(fileName);

        setContentView(R.layout.activity_landing);

        WebView browser;
        browser = (WebView) findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new WebAppInterface(this), "Android");
        browser.loadUrl("file:///android_asset/EditNotePage.html");

        setTitle("My Digital Safe");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.deleteButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                androidDelete();

//                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case DialogInterface.BUTTON_POSITIVE:
//                                //Yes button clicked
//                                androidDelete();
//                                break;
//
//                            case DialogInterface.BUTTON_NEGATIVE:
//                                //No button clicked
//                                break;
//                        }
//                    }
//                };
//                AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
//                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
//                        .setNegativeButton("No", dialogClickListener).show();
//
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Date date = new Date();


//                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            String dateString = sdf.format(date);
            Log.d("date", dateString);
            file.setAccessDate(dateString);
            long id = databaseManager.updateFile(file);
            Log.d("help", "Id: " + id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void androidDelete(){
        try {
            DatabaseManager databaseManager = new DatabaseManager(applicationContext);

            file.setFileName("redacted");
            file.setAccessDate("redacted");
            file.setData("redacted");
            databaseManager.updateFile(file);

            databaseManager.deleteFile(file);

            CharSequence failedAuthenticationString = getString(R.string.deleteFileSuccess);
            Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
            toast.show();

            Intent listIntent = new Intent(applicationContext, ListActivity.class);
            startActivity(listIntent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();

            CharSequence failedAuthenticationString = getString(R.string.deleteFileFailed);
            Toast toast = Toast.makeText(applicationContext, failedAuthenticationString, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void updateFile(String fileString) {
            Gson gson = new Gson();
            Type type = new TypeToken<com.development.security.ciphernote.model.File>() {
            }.getType();
            file = gson.fromJson(fileString, type);
            Log.d("help", "time");
        }

        @JavascriptInterface
        public String getFile() {
            Gson gson = new Gson();
            String fileJson = gson.toJson(file);
            return fileJson;
        }
    }


}
