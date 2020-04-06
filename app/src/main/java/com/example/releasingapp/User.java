package com.example.releasingapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uId")
    public int uId;

    @ColumnInfo(name = "idNo")
    public String idNo;

    @ColumnInfo(name = "fullName")
    public String fullName;



    @ColumnInfo(name = "status")
    public int status;

    public User(String idNo, String fullName,int status) {
        this.idNo = idNo;
        this.fullName = fullName;
        this.status = status;
    }

    public String getName () {
        return fullName;
    }

    public  String getIdNo(){
        return idNo;
    }

    public int getStatus(){
        return status;
    }
}
