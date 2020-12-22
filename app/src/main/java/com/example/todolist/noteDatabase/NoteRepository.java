package com.example.todolist.noteDatabase;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava3.core.Single;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application){
        NoteDatabase noteDatabase = NoteDatabase.getDatabase(application);
        noteDao = noteDatabase.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    public void insert(Note note){
        NoteDatabase.databaseWriterExecutor.execute(() ->{
            noteDao.insert(note);
        });
    }

    public void update(Note note){
        NoteDatabase.databaseWriterExecutor.execute(() ->{
            noteDao.update(note);
        });
    }

    public void delete(Note note){
        NoteDatabase.databaseWriterExecutor.execute(() ->{
            noteDao.delete(note);
        });
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    public Note getNoteById(int noteId){
       return noteDao.getNoteById(noteId);
    }

    
}
