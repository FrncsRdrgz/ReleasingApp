package govph.rsis.releasingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import java.util.ArrayList;

public class SwitchAccountActivity extends AppCompatActivity{

    public static final String EXTRA_MESSAGE = "com.example.releasingapp.MESSAGE";
    private static final String TAG = "SwitchAccountActivity";
    private UserDatabase database;
    private Intent intent;

    TextView tvVersion;
    ArrayList<User> users;
    UsersAdapter adapter;
    RecyclerView rvUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_switch_account);
        tvVersion = findViewById(R.id.tvVersion);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVersion.setText("Version: "+ pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void selectUser(View view) {
        intent = new Intent(this, SelectUserActivity.class);
        startActivity(intent);
    }

    public void addNewUser(View view) {
        intent = new Intent(this, ScannedBarcodeActivity.class);
        intent.putExtra(EXTRA_MESSAGE,"LoginActivity");
        startActivity(intent);
    }
}
