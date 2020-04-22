package com.learnateso.learn_ateso.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import android.database.Cursor;

import com.learnateso.learn_ateso.models.User;

/**
 * Created by BE on 2/3/2018.
 */

@Dao
public interface UsersDao {

    /*
    insert user into db
     */
    @Insert
    void insertUser(User user);

    @Query("Delete from user")
    void deleteUser();

    @Query("SELECT * from user")
    //HashMap<String, String> getUserDetails();
    Cursor getUserDetails();
}
