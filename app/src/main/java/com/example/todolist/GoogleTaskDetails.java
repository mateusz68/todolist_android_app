package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class GoogleTaskDetails extends AppCompatActivity {
    public static final String EXTRA_TASK_ID = "com.example.todolist.EXTRA_TASK_ID";
    public static final String EXTRA_LIST_ID = "com.example.todolist.EXTRA_LIST_ID";

    GoogleAccountCredential credential;
    com.google.api.services.tasks.Tasks service;
    final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private String TASK_LIST = "@default";
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private static final String TAG = "GoogleTasksDetails";


    private EditText googleTaskTitleField;
    private EditText googleTaskDescriptionField;
    private Button googleTaskDateButton;
    private String taskId;
    private String listId;
    private Task selectedTask;
    private Date taskDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_task_details);

        googleTaskTitleField = findViewById(R.id.google_task_title);
        googleTaskDescriptionField = findViewById(R.id.google_task_description);
        googleTaskDateButton = findViewById(R.id.google_task_date);
        googleTaskDateButton.setEnabled(true);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct == null) {
            finish();
            Intent intent = new Intent(this, GoogleSignInAccountActivity.class);
            startActivity(intent);
        }
        Log.d(TAG, "acct " + acct);

        credential =
                GoogleAccountCredential.usingOAuth2(this, Collections.singleton(TasksScopes.TASKS));
//        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
//        credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        credential.setSelectedAccountName(acct.getAccount().name);
        Log.d(TAG, "credentical " + credential);
//        Log.d(TAG, "user " + settings.getString(PREF_ACCOUNT_NAME,null));
        // Tasks client
        service =
                new com.google.api.services.tasks.Tasks.Builder(httpTransport, jsonFactory, credential)
                        .setApplicationName("Google-TasksAndroidSample/1.0").build();
        Log.d(TAG, "service " + service);


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        Intent intent = getIntent();
//        if(intent.hasExtra(EXTRA_TASK_ID) && intent.hasExtra(EXTRA_LIST_ID)){
//            setTitle(getString(R.string.edit_task_title));
//            taskId = intent.getStringExtra(EXTRA_TASK_ID);
//            listId = intent.getStringExtra(EXTRA_LIST_ID);
//            if(taskId != null && listId != null){
//                new getSelectedTask().execute();
//            }
//        }else{
//            setTitle(getString(R.string.add_task_title));
//        }
        if(intent.hasExtra(EXTRA_TASK_ID) ){
            setTitle(getString(R.string.edit_task_title));
            taskId = intent.getStringExtra(EXTRA_TASK_ID);
            Log.d(TAG, "id" + taskId);
            if(taskId != null){
                new getSelectedTask().execute();
            }
        }else{
            setTitle(getString(R.string.add_task_title));
            googleTaskDateButton.setText("Select Date");
        }

        googleTaskDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog();
            }
        });
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
                saveTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDateTimeDialog(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");
                googleTaskDateButton.setText(simpleDateFormat.format(calendar.getTime()));
                taskDate = calendar.getTime();
            }
        };
        new DatePickerDialog(GoogleTaskDetails.this, dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveTask(){
//        String title = googleTaskTitleField.getText().toString();
//        String description = googleTaskDescriptionField.getText().toString();
//
//        if(title.trim().isEmpty()){
//            Toast.makeText(this,getString(R.string.error_empty_title),Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(description.trim().isEmpty()){
//            Toast.makeText(this,getString(R.string.error_empty_description_task),Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(taskId != null && selectedTask != null) {
//
//        }else {
//            Task task = new Task();
//            task.setTitle(title);
//            task.setNotes(description);
//            if(taskDate != null){
//                DateTime dateTime = new DateTime(taskDate);
//                task.setDue(dateTime);
//            }
//            try {
//                service.tasks().insert(TASK_LIST,task).execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            finish();
//        }
        String title = googleTaskTitleField.getText().toString();
        String description = googleTaskDescriptionField.getText().toString();

        if(title.trim().isEmpty()){
            Toast.makeText(this,getString(R.string.error_empty_title),Toast.LENGTH_SHORT).show();
            return;
        }
        new SaveTask().execute();
    }

    private class SaveTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            String title = googleTaskTitleField.getText().toString();
            String description = googleTaskDescriptionField.getText().toString();

            if(taskId != null && selectedTask != null) {
                selectedTask.setTitle(title);
                if(!description.isEmpty()){
                    selectedTask.setNotes(description);
                }
                if(taskDate != null){
                    DateTime dateTime = new DateTime(taskDate);
                    selectedTask.setDue(dateTime);
                }
                try {
                    service.tasks().update(TASK_LIST,selectedTask.getId(),selectedTask).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Task task = new Task();
                task.setTitle(title);
                if(!description.isEmpty()){
                    task.setNotes(description);
                }
                if(taskDate != null){
                    DateTime dateTime = new DateTime(taskDate);
                    task.setDue(dateTime);
                }
                try {
                    service.tasks().insert(TASK_LIST,task).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }
    }

    private class getSelectedTask extends AsyncTask<Void,Void,Void>{
        private Task taskClass;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                taskClass = service.tasks().get(TASK_LIST, taskId).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            selectedTask = taskClass;
            googleTaskTitleField.setText(selectedTask.getTitle());
            googleTaskDescriptionField.setText(selectedTask.getNotes());
            if(selectedTask.getDue() != null) {
                taskDate = HelpMethods.formatRFCDate(selectedTask.getDue().toStringRfc3339());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");
                googleTaskDateButton.setText(simpleDateFormat.format(taskDate.getTime()));
            }else{
                googleTaskDateButton.setText("Select Date");
            }

        }
    }
}