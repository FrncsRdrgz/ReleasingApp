package com.example.releasingapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;
    private LiveData<List<User>> allInactiveUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);

        repository = new UserRepository(application);
        allInactiveUsers = repository.getAllInactiveUsers();
    }

    public void insert(User user) {repository.insert(user);}
    public void update(User user) {repository.update(user);}
    public void delete(User user) {repository.delete(user);}

    public LiveData<List<User>> getAllInactiveUsers() {return allInactiveUsers;}

}
