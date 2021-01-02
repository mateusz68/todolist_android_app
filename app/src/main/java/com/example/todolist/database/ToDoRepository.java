package com.example.todolist.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

public class ToDoRepository {
    private ToDoDao toDoDao;
    private LiveData<List<Task>> allTask;
    private LiveData<List<Note>> allNotes;

    public ToDoRepository(Application application){
        ToDoDatabase taskDatabase = ToDoDatabase.getDatabase(application);
        toDoDao = taskDatabase.toDoDao();
        allTask = toDoDao.getAllTasks();
        allNotes = toDoDao.getAllNotes();
    }

    public void insertNote(Note note){
        ToDoDatabase.databaseWriterExecutor.execute(() ->{
            toDoDao.insertNote(note);
        });
    }

    public void insertTask(Task task){
        ToDoDatabase.databaseWriterExecutor.execute(() ->{
            toDoDao.insertTask(task);
        });
    }

    public void updateTask(Task task){
        ToDoDatabase.databaseWriterExecutor.execute(() ->{
            toDoDao.updateTask(task);
        });
    }

    public void updateNote(Note note){
        ToDoDatabase.databaseWriterExecutor.execute(() ->{
            toDoDao.updateNote(note);
        });
    }

    public void deleteTask(Task task){
        ToDoDatabase.databaseWriterExecutor.execute(() ->{
            toDoDao.deleteTask(task);
        });
    }

    public void deleteNote(Note note){
        ToDoDatabase.databaseWriterExecutor.execute(() ->{
            toDoDao.deleteNote(note);
        });
    }

    public LiveData<List<Task>> getAllTask(){
        return allTask;
    }

    public LiveData<List<Task>> getArchiveTask(){
        return toDoDao.getArchiveTasks();
    }

    public LiveData<List<Task>> getFutureTask(Date today){
        return toDoDao.getFutureTasks(today);
    }

    public LiveData<List<Task>> getTodayTask(Date today){
        return toDoDao.getTodayTasks(today);
    }

    public Task getTaskById(int taskId){
        return toDoDao.getTaskById(taskId);
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    public Note getNoteById(int noteId){
        return toDoDao.getNoteById(noteId);
    }
}
