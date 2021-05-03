package govph.rsis.seedreleasingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Intent intent;

            switch (menuItem.getItemId()) {
                case R.id.nav_scan:
                    intent = new Intent(TransactionActivity.this,HomeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_list:
                    intent = new Intent(TransactionActivity.this,TransactionActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            return true;
        }
    };
}