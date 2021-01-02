package com.example.todolist.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface ToDoDao {

    @Insert
    void insertTask(Task task);

    @Insert
    void insertNote(Note note);

    @Update
    void updateTask(Task task);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteTask(Task task);

    @Delete
    void deleteNote(Note note);

    @Query("Select * From task_table Order By date")
    LiveData<List<Task>> getAllTasks();

    @Query("Delete From task_table")
    void deleteAllTasks();

    @Query("Select * From task_table Where done=1 Order By date DESC")
    LiveData<List<Task>> getArchiveTasks();

    @Query("Select * From task_table Where done=0 AND date > :today Order By date ASC")
    LiveData<List<Task>> getFutureTasks(Date today);

    @Query("Select * From task_table Where done=0 AND date <= :today Order By date DESC")
    LiveData<List<Task>> getTodayTasks(Date today);

    @Query("SELECT * FROM task_table WHERE id = :taskId")
    Task getTaskById(int taskId);

    @Query("Select * From note_table Order By id")
    LiveData<List<Note>> getAllNotes();

    @Query("Delete From note_table")
    void deleteAllNotes();

    @Query("SELECT * FROM note_table WHERE id = :noteId")
    Note getNoteById(int noteId);
}
