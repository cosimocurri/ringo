package com.example.ringo_star.data.database;

import androidx.room.RoomDatabase;
import androidx.room.Database;
import androidx.room.TypeConverters;

import com.example.ringo_star.data.Converter;
import com.example.ringo_star.data.dao.UserDAO;
import com.example.ringo_star.data.entity.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
@TypeConverters(Converter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
}