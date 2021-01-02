package com.example.todolist.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todolist.HelpMethods;

import java.util.List;

public class ToDoViewModel extends AndroidViewModel {
    private ToDoRepository repository;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Note>> allNotes;

    public ToDoViewModel(@NonNull Application application) {
        super(application);
        repository = new ToDoRepository(application);
        allTasks = repository.getAllTask();
        allNotes = repository.getAllNotes();
    }

    public void insertTask(Task task){
        repository.insertTask(task);
    }

    public void insertNote(Note note){
        repository.insertNote(note);
    }

    public void updateTask(Task task){
        repository.updateTask(task);
    }

    public void updateNote(Note note){
        repository.updateNote(note);
    }

    public void deleteTask(Task task){
        repository.deleteTask(task);
    }

    public void deleteNote(Note note){
        repository.deleteNote(note);
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

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    public Note getNoteById(int noteId){
        return repository.getNoteById(noteId);
    }
}
