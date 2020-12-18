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
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("Select * From task_table Order By date")
    LiveData<List<Task>> getAllTasks();

    @Query("Delete From task_table")
    void deleteAll();

    @Query("Select * From task_table Where done=1 Order By date DESC")
    LiveData<List<Task>> getArchiveTasks();

    @Query("Select * From task_table Where done=0 AND date > :today Order By date ASC")
    LiveData<List<Task>> getFutureTasks(Date today);

    @Query("Select * From task_table Where done=0 AND date <= :today Order By date DESC")
    LiveData<List<Task>> getTodayTasks(Date today);
}
