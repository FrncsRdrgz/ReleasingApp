package com.example.releasingapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface UserDao {

/*    @Query("SELECT fullName FROM user WHERE idNo !=:idNo ")
    String[] getAll(String idNo);*/

    @Query("SELECT * FROM user WHERE status = 0")
    List<User> userAll();

    @Query("select * from user")
    List<User> fetchAll();

    @Query("SELECT fullName FROM user WHERE idNo =:idNo LIMIT 1")
    String getUserName(String idNo);

    @Query("select * from user where idNo =:idNo")
    int isExisting(String idNo);

    @Query("SELECT idNo from user where status = 1 ")
    String isOnline();

    @Query("UPDATE user SET status = 0 WHERE status = 1")
    int updateStatus();

    @Query("UPDATE user SET status = 1 WHERE idNo =:idNo")
    void updateStatusOnline(String idNo);

    @Query("UPDATE user SET status= 0 WHERE idNo =:idNo")
    void logout(String idNo);

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}
