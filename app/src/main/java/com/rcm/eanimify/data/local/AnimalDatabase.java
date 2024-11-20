package com.rcm.eanimify.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Animal.class}, version = 1)
public abstract class AnimalDatabase extends RoomDatabase {
    private static volatile AnimalDatabase INSTANCE;
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract AnimalDao animalDao(); // Define the DAO for Animal

    public static AnimalDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AnimalDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AnimalDatabase.class, "animal-database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }
}