package com.example.todolist.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.sql.Date;
import java.time.LocalTime;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class, Note.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ToDoDatabase extends RoomDatabase {

    public abstract ToDoDao toDoDao();

    private static volatile ToDoDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static ToDoDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (ToDoDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ToDoDatabase.class, "to_do_db")
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
                ToDoDao dao = INSTANCE.toDoDao();
                dao.deleteAllNotes();
                dao.deleteAllTasks();
                Task task = new Task("Test title", "test decription", new Date(2020,11,5));
                Task task1 = new Task("Test second", "test second decription", new Date(2020,12,5));
                task1.setDone(true);
                dao.insertTask(task);
                dao.insertTask(task1);
                Note note = new Note("Test title", "Test content", new Date(20,5,2));
                Note note1 = new Note("Test 2 title", "Test 23123", new Date(20,5,2));
                Note note2 = new Note("Test dvfgdfgf", "rsrgegeregegr", new Date(12,1,1));
                dao.insertNote(note);
                dao.insertNote(note1);
                dao.insertNote(note2);
            });
        }
    };

//    private static TaskDatabase instance;
//
//    public abstract TaskDao taskDao();
//
//    public static synchronized TaskDatabase getInstance(Context context){
//        if(instance == null){
//            instance = Room.databaseBuilder(context.getApplicationContext(),
//                    TaskDatabase.class, "task_database")
//                    .fallbackToDestructiveMigration()
//                    .build();
//        }
//        return instance;
//    }

}
