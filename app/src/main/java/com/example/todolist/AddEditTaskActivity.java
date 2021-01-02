package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todolist.database.Task;
import com.example.todolist.database.TaskViewModel;
import com.example.todolist.noteDatabase.NoteViewModel;

import java.util.Calendar;
import java.util.Date;

public class AddEditTaskActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.todolist.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.todolist.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.todolist.EXTRA_DESCRIPTION";
    public static final String EXTRA_DATE = "com.example.todolist.EXTRA_DATE";
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextDate;
    DatePickerDialog.OnDateSetListener setListener;
    private Task selectedTask = null;
    private TaskViewModel taskViewModel;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextDate = findViewById(R.id.task_date);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddEditTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day+"-"+month+"-"+year;
                        editTextDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();

            }
        });
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        Intent intent = getIntent();

        if(intent.hasExtra(EXTRA_ID)){
            setTitle(getString(R.string.edit_task_title));
            id = intent.getIntExtra(EXTRA_ID,-1);
            if(id != -1){
                new getTask().execute();
            }
        }else{
            setTitle(getString(R.string.add_task_title));
        }

    }

    private void saveTask(){
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String taskDate = editTextDate.getText().toString();
        Date taskD = HelpMethods.parseDate(taskDate);
        if(taskD == null){
            taskD = HelpMethods.getCurrentDate();
        }

        // Sprawdzam czy pola nie są puste
        if(title.trim().isEmpty()){
            Toast.makeText(this,getString(R.string.error_empty_title),Toast.LENGTH_SHORT).show();
            return;
        }
        if(description.trim().isEmpty()){
            Toast.makeText(this,getString(R.string.error_empty_description_task),Toast.LENGTH_SHORT).show();
            return;
        }

        if(id != -1 && selectedTask != null){
            selectedTask.setTitle(title);
            selectedTask.setDescription(description);
            selectedTask.setDate(taskD);
            taskViewModel.update(selectedTask);
        }else {
            Task task = new Task(title, description,taskD);
            taskViewModel.insert(task);
        }

        setResult(RESULT_OK, new Intent());
        finish();
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

    private class getTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids){
            selectedTask = taskViewModel.getTaskById(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(selectedTask != null){
                editTextTitle.setText(selectedTask.getTitle());
                editTextDescription.setText(selectedTask.getDescription());
                editTextDate.setText(HelpMethods.formatDate(selectedTask.getDate()));
            }

        }
    }
}