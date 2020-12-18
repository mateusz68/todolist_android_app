package com.example.todolist.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> allTask;

    public TaskRepository(Application application){
        TaskDatabase taskDatabase = TaskDatabase.getDatabase(application);
        taskDao = taskDatabase.taskDao();
        allTask = taskDao.getAllTasks();
    }

    public void insert(Task task){
        TaskDatabase.databaseWriterExecutor.execute(() ->{
            taskDao.insert(task);
        });
    }

    public void update(Task task){
        TaskDatabase.databaseWriterExecutor.execute(() ->{
            taskDao.update(task);
        });
    }

    public void delete(Task task){
        TaskDatabase.databaseWriterExecutor.execute(() ->{
            taskDao.delete(task);
        });
    }

    public LiveData<List<Task>> getAllTask(){
        return allTask;
    }

    public LiveData<List<Task>> getArchiveTask(){
        return taskDao.getArchiveTasks();
    }

    public LiveData<List<Task>> getFutureTask(Date today){
        return taskDao.getFutureTasks(today);
    }

    public LiveData<List<Task>> getTodayTask(Date today){
        return taskDao.getTodayTasks(today);
    }
}
