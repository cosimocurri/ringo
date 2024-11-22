package com.example.ringo_star.data;

import android.content.Context;

import androidx.room.Room;

import com.example.ringo_star.data.database.AppDatabase;

public class DatabaseClient {
    private static DatabaseClient instance;
    private final AppDatabase database;

    private DatabaseClient(Context context) {
        database = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "database").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if(instance == null)
            instance = new DatabaseClient(context);

        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}