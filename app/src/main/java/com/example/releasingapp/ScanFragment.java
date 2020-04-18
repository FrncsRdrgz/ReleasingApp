package com.example.releasingapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.Result;

public class ScanFragment extends Fragment {
    public static final String TAG = "Scan Fragment";
    private UserViewModel userViewModel;
    UserDatabase database;
    TextView textView,tvLogout,tvVersion;
    User user;
    Intent intent;
    FragmentManager fragmentManager;
    View view;
    Button scanBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scan,container,false);
        database = UserDatabase.getInstance(getActivity());
        user = database.userDao().isOnline();
        tvLogout = view.findViewById(R.id.tvLogout);
        textView = view.findViewById(R.id.textView);
        tvVersion = view.findViewById(R.id.tvVersion);
        scanBtn = view.findViewById(R.id.btnScanBarcode);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        textView.setText(user.getName());
        fragmentManager = getActivity().getSupportFragmentManager();

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
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
        return view;
    }

    public void logout() {
        intent = new Intent(getActivity(), SwitchAccountActivity.class);
        user.setStatus(false);
        userViewModel.update(user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void scanIntent() {
        intent = new Intent(getActivity(),ScannedBarcodeActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE,"ScanFragment");
        startActivity(intent);
    }
}
