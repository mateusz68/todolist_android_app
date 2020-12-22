package com.example.todolist.noteDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface NoteDao {
    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("Select * From note_table Order By id")
    LiveData<List<Note>> getAllNotes();

    @Query("Delete From note_table")
    void deleteAll();

    @Query("SELECT * FROM note_table WHERE id = :noteId")
    Note getNoteById(int noteId);

    @Query("SELECT * FROM note_table WHERE id = :noteId")
    Note getNoteBy(int noteId);
}
