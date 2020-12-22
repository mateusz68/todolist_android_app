package com.example.todolist.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todolist.HelpMethods;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository repository;
    private LiveData<List<Task>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTask();
    }

    public void insert(Task task){
        repository.insert(task);
    }

    public void update(Task task){
        repository.update(task);
    }

    public void delete(Task task){
        repository.delete(task);
    }

    public LiveData<List<Task>> getAllTasks(){
        return allTasks;
    }

    public LiveData<List<Task>> getArchiveTasks(){
        return repository.getArchiveTask();
    }

    public LiveData<List<Task>> getTodayTasks(){
        return repository.getTodayTask(HelpMethods.getCurrentDate());
    }

    public LiveData<List<Task>> getFutureTasks(){
        return repository.getFutureTask(HelpMethods.getCurrentDate());
    }

    public Task getTaskById(int taskId){
        return repository.getTaskById(taskId);
    }
}
