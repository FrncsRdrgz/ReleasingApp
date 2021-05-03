package govph.rsis.seedreleasingapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {User.class,Seed.class}, version = 3, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    private static final String DB_NAME = "user_db";
    private static UserDatabase instance;

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER  TABLE seed ADD COLUMN compare_quantity TEXT");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE seed ADD COLUMN isReleased TEXT");
        }
    };

    public static synchronized UserDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class,DB_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build();
        }
        return instance;
    }

    public abstract UserDao userDao();

    public abstract SeedDao seedDao();
}
