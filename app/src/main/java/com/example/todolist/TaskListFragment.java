package com.example.todolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListFragment extends Fragment {
    public static final String ARG_PARAM1 = "param1";
    private int mParam1;

    private TaskViewModel taskViewModel;
    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;

    public TaskListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment TaskListFragment.
     */
    public static TaskListFragment newInstance(int param1) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
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
        switch (mParam1){
            case 0:
                taskLiveData = taskViewModel.getTodayTasks();
                break;
            case 1:
                taskLiveData = taskViewModel.getFutureTasks();
                break;
            case 2:
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
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle(getString(R.string.delete_task_title));
                    alert.setMessage(getString(R.string.delete_task_message));
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            taskViewModel.delete(adapter.getTaskAt(viewHolder.getAdapterPosition()));
                            Toast.makeText(getContext(), getString(R.string.task_delete_info), Toast.LENGTH_SHORT).show();
                            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        }
                    });
                    alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // close dialog
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }else if(direction == ItemTouchHelper.RIGHT){
                    Task tempTask = adapter.getTaskAt(viewHolder.getAdapterPosition());
                    tempTask.setDone(!tempTask.getDone());
                    taskViewModel.update(tempTask);
                    Toast.makeText(getContext(), getString(R.string.task_done_info), Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
                intent.putExtra(AddEditTaskActivity.EXTRA_ID, task.getId());
                startActivityForResult(intent, EDIT_TASK_REQUEST);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK){
            Toast.makeText(getContext(), getString(R.string.task_save_info), Toast.LENGTH_SHORT).show();
        }else if(requestCode == ADD_TASK_REQUEST && resultCode != RESULT_OK){
            Toast.makeText(getContext(), getString(R.string.task_save_error_info), Toast.LENGTH_SHORT).show();

        }else if(requestCode == EDIT_TASK_REQUEST && resultCode == RESULT_OK){
            Toast.makeText(getContext(), getString(R.string.task_update_info), Toast.LENGTH_SHORT).show();
        }else if(requestCode == EDIT_TASK_REQUEST && resultCode != RESULT_OK) {
            Toast.makeText(getContext(), getString(R.string.task_update_error_info), Toast.LENGTH_SHORT).show();
        }
    }
}