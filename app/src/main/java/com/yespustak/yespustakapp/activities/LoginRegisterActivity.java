package com.yespustak.yespustakapp.activities;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.fragments.BaseFragment;
import com.yespustak.yespustakapp.utils.PermissionHelper;
import com.yespustak.yespustakapp.utils.utils;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginRegisterActivity extends AppCompatActivity {
    private static final String TAG = "LoginRegisterActivity";
    private final String[] permissions = {Manifest.permission.READ_PHONE_STATE};
    Toolbar toolbar;
    PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        setupViews();
        setupPermission();
    }

    private void setupViews() {
        //setup actionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the NavController for your NavHostFragment
        NavController navController = Navigation.findNavController(this, R.id.login_register_nav_host_fragment);
        // Set up the ActionBar to stay in sync with the NavController
        NavigationUI.setupActionBarWithNavController(this, navController);
        getSupportActionBar().setTitle(null);
    }

    private void setupPermission() {
        permissionHelper = new PermissionHelper(this, permissions, 100);
        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "onPermissionGranted() called");
            }

            @Override
            public void onIndividualPermissionGranted(String[] grantedPermission) {
                Log.d(TAG, "onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(",", grantedPermission) + "]");
            }

            @Override
            public void onPermissionDenied() {
                utils.showAlert(LoginRegisterActivity.this, null, "Permission required.", SweetAlertDialog.ERROR_TYPE, sweetAlertDialog -> finish(), false);
                Log.d(TAG, "onPermissionDenied() called");
            }

            @Override
            public void onPermissionDeniedBySystem() {
                Log.d(TAG, "onPermissionDeniedBySystem() called");
            }
        });
    }

    //Enable back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.login_register_nav_host_fragment);
        List<Fragment> fragmentList = navHostFragment.getChildFragmentManager().getFragments();

        //send onBackPressed to all back stack fragments
        boolean handled = false;
        for (Fragment f : fragmentList) {
            if (f instanceof BaseFragment) {
                handled = ((BaseFragment) f).onBackPressed();

                if (handled) {
                    break;
                }
            }
        }

        if (!handled) {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper != null) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}