package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;

import java.util.Collections;

public class GoogleTaskDetails extends AppCompatActivity {
    public static final String EXTRA_TASK_ID = "com.example.todolist.EXTRA_TASK_ID";
    public static final String EXTRA_LIST_ID = "com.example.todolist.EXTRA_LIST_ID";

    GoogleAccountCredential credential;
    com.google.api.services.tasks.Tasks service;
    final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private String TASK_LIST = "@default";
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private static final String TAG = "GoogleTasks";

    private EditText googleTaskTitleField;
    private EditText googleTaskDescriptionField;
    private Button googleTaskDateButton;
    private ListView googleTaskSubtaskList;
    private String taskId;
    private String listId;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_task_details);

        googleTaskTitleField = findViewById(R.id.google_task_title);
        googleTaskDescriptionField = findViewById(R.id.google_task_description);
        googleTaskDateButton = findViewById(R.id.google_task_date);
        googleTaskSubtaskList = findViewById(R.id.google_subtask_list);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct == null) {
            finish();
            Intent intent = new Intent(this, GoogleSignInAccountActivity.class);
            startActivity(intent);
        }

        credential =
                GoogleAccountCredential.usingOAuth2(this, Collections.singleton(TasksScopes.TASKS));
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        // Tasks client
        service =
                new com.google.api.services.tasks.Tasks.Builder(httpTransport, jsonFactory, credential)
                        .setApplicationName("Google-TasksAndroidSample/1.0").build();

        //https://stackoverflow.com/questions/45634946/how-to-pass-googleapiclient-to-another-all-activity-for-google-play-games

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_TASK_ID) && intent.hasExtra(EXTRA_LIST_ID)){
            setTitle(getString(R.string.edit_task_title));
            taskId = intent.getStringExtra(EXTRA_TASK_ID);
            listId = intent.getStringExtra(EXTRA_LIST_ID);
            if(taskId != null && listId != null){
                new getSelectedTask().execute();
            }
        }else{
            setTitle(getString(R.string.add_task_title));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_task:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class getSelectedTask extends AsyncTask<Void,Void,Void>{
        private Task taskClass;
        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}