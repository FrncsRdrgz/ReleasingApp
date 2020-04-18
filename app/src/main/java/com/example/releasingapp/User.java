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
    public boolean status;

    public User(String idNo, String fullName,boolean status) {
        this.idNo = idNo;
        this.fullName = fullName;
        this.status = status;
    }


    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getName () { return fullName; }
    public void setIdNo(String idNo) { this.idNo = idNo; }
    public  String getIdNo(){
        return idNo;
    }
    public void setStatus(boolean status) { this.status = status; }
    public boolean getStatus(){
        return status;
    }
}
