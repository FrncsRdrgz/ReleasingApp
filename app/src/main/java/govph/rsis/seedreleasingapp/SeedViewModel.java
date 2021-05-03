package govph.rsis.seedreleasingapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SeedViewModel extends AndroidViewModel {
    private SeedRepository repository;
    private UserDatabase database;
    private LiveData<List<Seed>> getSeeds;
    private LiveData<List<Seed>> getReleasedOrders;
    private LiveData<List<Seed>> getByOrderId;
    private int isNotVerified;
    public String orderId;

    public SeedViewModel(@NonNull Application application) {
        super(application);
        database = UserDatabase.getInstance(application);
        repository = new SeedRepository(application);
        getSeeds = repository.getSeeds();
        getReleasedOrders = repository.getReleasedOrders();

        getByOrderId = database.seedDao().getByOrderId(orderId);
        isNotVerified = database.seedDao().isNotVerified(orderId);
    }

    public void insert(Seed seed) {repository.insert(seed);}
    public void update(Seed seed) {repository.update(seed);}
    public void delete(Seed seed) {repository.delete(seed);}

    public LiveData<List<Seed>> getSeeds() {return getSeeds;}
    public LiveData<List<Seed>> getReleasedOrders() {return getReleasedOrders;}
    public LiveData<List<Seed>> getByOrderId(String orderId){
        return getByOrderId = database.seedDao().getByOrderId(orderId);
    }
    public int isNotVerified(String orderId){
        return isNotVerified = database.seedDao().isNotVerified(orderId);
    }

    public void deleteById(String orderId){
        database.seedDao().deleteById(orderId);
    }

    public void isReleased(String order_id) { repository.isReleased(order_id);}
}
