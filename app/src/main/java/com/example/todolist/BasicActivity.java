package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;

import com.example.todolist.database.Task;
import com.example.todolist.database.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import android.widget.Toast;

import java.util.Date;
import java.util.List;

public class BasicActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    public Task selectedTask;
    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        FloatingActionButton buttonAddTask = findViewById(R.id.add_task_button);
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BasicActivity.this, AddEditTaskActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final TaskAdapter adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.setTasks(tasks);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.LEFT) {
                    taskViewModel.delete(adapter.getTaskAt(viewHolder.getAdapterPosition()));
                    Toast.makeText(BasicActivity.this, "Note delet", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                }else if(direction == ItemTouchHelper.RIGHT){
                    Task tempTask = adapter.getTaskAt(viewHolder.getAdapterPosition());
                    tempTask.setDone(!tempTask.getDone());
                    taskViewModel.update(tempTask);
                    Toast.makeText(BasicActivity.this, "Task Done", Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Intent intent = new Intent(BasicActivity.this, AddEditTaskActivity.class);
                intent.putExtra(AddEditTaskActivity.EXTRA_ID, task.getId());
                intent.putExtra(AddEditTaskActivity.EXTRA_TITLE, task.getTitle());
                intent.putExtra(AddEditTaskActivity.EXTRA_DESCRIPTION, task.getDescription());
                intent.putExtra(AddEditTaskActivity.EXTRA_DATE, HelpMethods.formatDate(task.getDate()));
                selectedTask = task;
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditTaskActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditTaskActivity.EXTRA_DESCRIPTION);
            String taskDate = data.getStringExtra(AddEditTaskActivity.EXTRA_DATE);
            Date taskD = HelpMethods.parseDate(taskDate);

            if(taskD == null){
               taskD = HelpMethods.getCurrentDate();
            }
            Task task = new Task(title, description, taskD);
            taskViewModel.insert(task);
            Toast.makeText(this,"Note saved", Toast.LENGTH_SHORT).show();
        }else if(requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddEditTaskActivity.EXTRA_ID, -1);
            if(id == -1){
                Toast.makeText(this, "Note can't be update", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddEditTaskActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditTaskActivity.EXTRA_DESCRIPTION);
            String taskDate = data.getStringExtra(AddEditTaskActivity.EXTRA_DATE);
            Date taskD = HelpMethods.parseDate(taskDate);
            if(taskD == null){
                taskD = HelpMethods.getCurrentDate();
            }
            selectedTask.setTitle(title);
            selectedTask.setDescription(description);
            selectedTask.setDate(taskD);
            taskViewModel.update(selectedTask);
            selectedTask = null;

            Toast.makeText(this,"Task update", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }
}