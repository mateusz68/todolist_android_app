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

@Database(entities = {Task.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class TaskDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static volatile TaskDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static TaskDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (TaskDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TaskDatabase.class, "task_db")
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
                TaskDao dao = INSTANCE.taskDao();
                dao.deleteAll();
                Task task = new Task("Test title", "test decription", new Date(2020,11,5));
                Task task1 = new Task("Test second", "test second decription", new Date(2020,12,5));
                task1.setDone(true);
                dao.insert(task);
                dao.insert(task1);
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
