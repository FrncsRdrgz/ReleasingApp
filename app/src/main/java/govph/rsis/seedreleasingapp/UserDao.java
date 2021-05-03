package govph.rsis.seedreleasingapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

/*    @Query("SELECT fullName FROM user WHERE idNo !=:idNo ")
    String[] getAll(String idNo);*/

    @Query("SELECT * FROM user WHERE status = 0")
    LiveData<List<User>> getInactiveUsers();

    @Query("select * from user")
    List<User> checkDB();

    @Query("select * from user where idNo =:idNo")
    int isExisting(String idNo);

    @Query("SELECT * from user where status = 1 ")
    User isOnline();

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
