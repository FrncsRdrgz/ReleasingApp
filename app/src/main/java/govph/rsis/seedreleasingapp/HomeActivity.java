package govph.rsis.seedreleasingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";
    private UserViewModel userViewModel;
    UserDatabase database;
    TextView staticName,staticVersion;
    User user;
    Intent intent;
    FragmentManager fragmentManager;
    View view;
    Button scanBtn,backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Log.e(TAG, "onCreate: "+DebugDB.getAddressLog() );
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        database = UserDatabase.getInstance(this);
        user = database.userDao().isOnline();
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        staticName = findViewById(R.id.staticName);
        staticVersion = findViewById(R.id.staticVersion);
        staticName.setText("User: "+user.getName());

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            staticVersion.setText("Version: "+ pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        /*
        tvLogout = (TextView) findViewById(R.id.tvLogout);
        textView = (TextView) findViewById(R.id.textView);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        scanBtn = (Button) findViewById(R.id.btnScanBarcode);
        backBtn = (Button) findViewById(R.id.homeBackBtn);

        textView.setText(user.getName());

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            tvVersion.setText("Version: "+ pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanIntent();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Intent intent;
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_scan:
                    selectedFragment = new ScanFragment();
                    /*intent = new Intent(HomeActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();*/
                    break;
                case R.id.nav_list:
                    selectedFragment = new TransactionFragment();
                    /*intent = new Intent(HomeActivity.this,TransactionActivity.class);
                    startActivity(intent);*/
                    break;
                case R.id.nav_logout:
                    //selectedFragment = null;
                    intent = new Intent(HomeActivity.this, SwitchAccountActivity.class);
                    user.setStatus(false);
                    userViewModel.update(user);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    //break;
            }
            if(selectedFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

            }
            return true;
        }
    };

    public void logout() {

    }

    public void scanIntent() {
        intent = new Intent(this,ScannedBarcodeActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE,"ScanFragment");
        startActivity(intent);
    }
}
