package govph.rsis.releasingapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SeedDao {
    @Query("SELECT * FROM seed")
    LiveData<List<Seed>> getSeeds();

    @Query("SELECT * FROM seed where orderId =:orderId")
    LiveData<List<Seed>> getByOrderId(String orderId);

    @Query("SELECT count(*) FROM seed WHERE orderId =:orderId")
    int existing(String orderId);

    @Query("SELECT count(*) FROM seed WHERE orderId =:orderId AND isVerified = 0")
    int isNotVerified(String orderId);

    @Query("DELETE FROM seed WHERE orderId =:orderId")
    void deleteById(String orderId);

    @Insert
    void insertSeed(Seed seed);

    @Update
    void updateSeed(Seed seed);

    @Delete
    void deleteSeed(Seed seed);
}
