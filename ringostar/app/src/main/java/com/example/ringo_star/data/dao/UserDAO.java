package com.example.ringo_star.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ringo_star.data.entity.User;

@Dao
public interface UserDAO {
    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM User LIMIT 1")
    User getUser();

    @Query("DELETE FROM User")
    void delete();
}