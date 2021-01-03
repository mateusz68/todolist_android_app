package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleTasks extends AppCompatActivity {
    GoogleAccountCredential credential;
    com.google.api.services.tasks.Tasks service;
    final HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private String TASK_LIST = "@default";
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private static final String TAG = "GoogleTasks";
    GoogleTaskAdapter adapter;
    TaskList currentTaskList;
    private Task selectedTask;
    Menu menuTaskList;
    public static final int GOOGLE_ADD_TASK_REQUEST = 1;
    public static final int GOOGLE_EDIT_TASK_REQUEST = 2;
    private FloatingActionButton addTaskButton;

    @Override
    protected void onResume() {
        super.onResume();
        haveGooglePlayServices();
    }

    private void haveGooglePlayServices() {
        // check if there is already an account selected
        if (credential.getSelectedAccountName() == null) {
            // ask user to choose account
            Intent intent = new Intent(this, GoogleSignInAccountActivity.class);
            startActivity(intent);
        } else {
            // load calendars
            loadTasks();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_tasks);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        addTaskButton = findViewById(R.id.add_task_google_button);

        RecyclerView recyclerView = findViewById(R.id.google_task_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new GoogleTaskAdapter();
        recyclerView.setAdapter(adapter);

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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.LEFT) {
                    Task task = adapter.getTaskAt(viewHolder.getAdapterPosition());
                    new deleteTask().execute(task.getId(),currentTaskList.getId());
                    adapter.removeAt(viewHolder.getAdapterPosition());
                    Toast.makeText(GoogleTasks.this, "Note delete", Toast.LENGTH_SHORT).show();
                }else if(direction == ItemTouchHelper.RIGHT){
                    selectedTask = adapter.getTaskAt(viewHolder.getAdapterPosition());
                    new markTaskAsDone().execute();
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    adapter.removeAt(viewHolder.getAdapterPosition());
                }
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new GoogleTaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Log.d(TAG, "selected task" + task);
                Intent intent = new Intent(GoogleTasks.this, GoogleTaskDetails.class);
                intent.putExtra(GoogleTaskDetails.EXTRA_TASK_ID, task.getId());
                startActivityForResult(intent,GOOGLE_EDIT_TASK_REQUEST);
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoogleTasks.this, GoogleTaskDetails.class);
                startActivityForResult(intent, GOOGLE_ADD_TASK_REQUEST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.google_task_menu, menu);
        menuTaskList = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "menu" + item);
        Log.d(TAG, "listy" + item.getItemId());
        switch (item.getItemId()){
            case R.id.refresh_google_tasks:
                loadTasks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadTasks() {
        new getTasks().execute();
    }

    private class markTaskAsDone extends AsyncTask<Void,Void,Void>{
        Task selectedTaskFun;

        markTaskAsDone(){
            this.selectedTaskFun = selectedTask;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            selectedTaskFun.setStatus("completed");
            try {
                service.tasks().update(currentTaskList.getId(),selectedTask.getId(),selectedTask).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class deleteTask extends AsyncTask<String, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... strings) {
            String taskId = strings[0];
            String listId = strings[1];
            try {
                service.tasks().delete(listId,taskId).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private class getTasks extends AsyncTask<Void,Void,Void>{
        List<Task> tasks;
        TaskLists taskLists = new TaskLists();
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                tasks = service.tasks().list(TASK_LIST).execute().getItems();
                currentTaskList = service.tasklists().get(TASK_LIST).execute();
                taskLists = service.tasklists().list().execute();
                Log.d(TAG, "listy" + taskLists);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setTasks(tasks);
                    for (TaskList taskList: taskLists.getItems()) {
                        menuTaskList.add(0,Menu.NONE,0,taskList.getTitle());
                    }
                }
            });
        }
    }
}