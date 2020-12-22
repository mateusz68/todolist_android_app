package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.todolist.database.Task;
import com.example.todolist.database.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TaskViewModel taskViewModel;
    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;
    public Task selectedTask;

    public TaskListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskListFragment newInstance(String param1, String param2) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_list, container,false);

        FloatingActionButton buttonAddTask = view.findViewById(R.id.add_task_button);
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
                startActivityForResult(intent, ADD_TASK_REQUEST);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        final TaskAdapter adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        LiveData<List<Task>> taskLiveData;
        if(mParam1 == null){
            mParam1 = "0";
        }
        switch (mParam1){
            case "0":
                taskLiveData = taskViewModel.getTodayTasks();
                break;
            case "1":
                taskLiveData = taskViewModel.getFutureTasks();
                break;
            case "2":
                taskLiveData = taskViewModel.getArchiveTasks();
                break;
            default:
                taskLiveData = taskViewModel.getTodayTasks();
        }
        taskLiveData.observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
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
                    Toast.makeText(getContext(), "Note delet", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                }else if(direction == ItemTouchHelper.RIGHT){
                    Task tempTask = adapter.getTaskAt(viewHolder.getAdapterPosition());
                    tempTask.setDone(!tempTask.getDone());
                    taskViewModel.update(tempTask);
                    Toast.makeText(getContext(), "Task Done", Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
                intent.putExtra(AddEditTaskActivity.EXTRA_ID, task.getId());
                intent.putExtra(AddEditTaskActivity.EXTRA_TITLE, task.getTitle());
                intent.putExtra(AddEditTaskActivity.EXTRA_DESCRIPTION, task.getDescription());
                intent.putExtra(AddEditTaskActivity.EXTRA_DATE, HelpMethods.formatDate(task.getDate()));
                selectedTask = task;
                startActivityForResult(intent, EDIT_TASK_REQUEST);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditTaskActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditTaskActivity.EXTRA_DESCRIPTION);
            String taskDate = data.getStringExtra(AddEditTaskActivity.EXTRA_DATE);
            Date taskD = HelpMethods.parseDate(taskDate);

            if(taskD == null){
                taskD = HelpMethods.getCurrentDate();
            }
            Task task = new Task(title, description, taskD);
            taskViewModel.insert(task);
            Toast.makeText(getContext(),"Note saved", Toast.LENGTH_SHORT).show();
        }else if(requestCode == EDIT_TASK_REQUEST && resultCode == RESULT_OK){
            int id = data.getIntExtra(AddEditTaskActivity.EXTRA_ID, -1);
            if(id == -1){
                Toast.makeText(getContext(), "Note can't be update", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getContext(),"Task update", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }
}