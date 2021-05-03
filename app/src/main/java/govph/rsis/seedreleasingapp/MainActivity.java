package govph.rsis.seedreleasingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String EXTRA_MESSAGE = "com.example.releasingapp.MESSAGE";
    private static int SPLASH_TIME_OUT = 2000;
    Intent homeIntent;
    UserDatabase database;
    List<User> checkDb;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = UserDatabase.getInstance(this);
        checkDb = database.userDao().checkDB();
        TextView textView1 = findViewById(R.id.textView1);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            textView1.setText("ReleasingApp V"+ pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        user = database.userDao().isOnline();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(checkDb != null && checkDb.isEmpty()){
                    homeIntent = new Intent(MainActivity.this, LoginActivity.class);
                }
                else{
                    if(user != null){
                        homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                    }
                    else {
                        homeIntent = new Intent(MainActivity.this, SwitchAccountActivity.class);
                    }
                }
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

}
