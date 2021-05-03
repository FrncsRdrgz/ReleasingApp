package govph.rsis.seedreleasingapp;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SeedRepository {
    SeedDao seedDao;
    private int  isReleased;
    private LiveData<List<Seed>> getSeeds;
    private LiveData<List<Seed>> getReleasedOrders;

    public SeedRepository(Application application) {
        UserDatabase database = UserDatabase.getInstance(application);
        seedDao = database.seedDao();
        getSeeds = seedDao.getSeeds();
        getReleasedOrders = seedDao.getReleasedOrders();
    }

    public void insert(Seed seed) { new SeedRepository.InsertSeedAsyncTask(seedDao).execute(seed); }

    public void update(Seed seed) {
        new SeedRepository.UpdateSeedAsyncTask(seedDao).execute(seed);
    }

    public void delete(Seed seed) {
        new SeedRepository.DeleteSeedAsyncTask(seedDao).execute(seed);
    }

    public void isReleased(String order_id){  seedDao.isReleased(order_id);}
    public LiveData<List<Seed>> getSeeds() {return getSeeds;}
    public LiveData<List<Seed>> getReleasedOrders() {return getReleasedOrders;}

    private static class InsertSeedAsyncTask extends AsyncTask<Seed, Void, Void> {
        private SeedDao seedDao;

        private InsertSeedAsyncTask(SeedDao seedDao) {
            this.seedDao = seedDao;
        }

        @Override
        protected Void doInBackground(Seed... seeds) {
            seedDao.insertSeed(seeds[0]);
            return null;
        }
    }

    private static class UpdateSeedAsyncTask extends AsyncTask<Seed, Void, Void>{
        private SeedDao seedDao;

        private UpdateSeedAsyncTask(SeedDao seedDao) {
            this.seedDao = seedDao;
        }

        @Override
        protected Void doInBackground(Seed... seeds) {
            seedDao.updateSeed(seeds[0]);
            return null;
        }
    }

    private static class DeleteSeedAsyncTask extends AsyncTask<Seed, Void, Void>{
        private SeedDao seedDao;

        private DeleteSeedAsyncTask(SeedDao seedDao) {
            this.seedDao = seedDao;
        }

        @Override
        protected Void doInBackground(Seed... seeds) {
            seedDao.deleteSeed(seeds[0]);
            return null;
        }
    }
}
