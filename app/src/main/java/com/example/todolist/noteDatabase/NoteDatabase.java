package com.example.todolist.noteDatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.todolist.database.Converters;
import com.example.todolist.database.TaskDatabase;

import java.sql.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class NoteDatabase extends RoomDatabase {

    public abstract NoteDao noteDao();

    private static volatile NoteDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static NoteDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (NoteDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NoteDatabase.class, "note_db")
                            .addCallback(sRoomDatabasecallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabasecallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriterExecutor.execute(() ->{
                NoteDao dao = INSTANCE.noteDao();
                dao.deleteAll();
                Note note = new Note("Test title", "Test content", new Date(20,5,2));
                Note note1 = new Note("Test 2 title", "Test 23123", new Date(20,5,2));
                Note note2 = new Note("Test dvfgdfgf", "rsrgegeregegr", new Date(12,1,1));
                dao.insert(note);
                dao.insert(note1);
                dao.insert(note2);
            });
        }
    };
}
