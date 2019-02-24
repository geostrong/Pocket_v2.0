package com.pocketwallet.pocket;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.github.omadahealth.lollipin.lib.managers.LockManager;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends Activity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 200;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.splash_screen);
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {

            //Check for logged in before

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    next();
                }
            }, 1000);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            next();
                        }
                    }, 1000);
                } else {
                    finish();
                }
                return;
            }

        }
    }

    public void next () {
        if (!preferences.getBoolean("isLoggedIn", false)) {
            LockManager.getInstance().getAppLock().disable();
            //Go to sign up page
            Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        } else {
            //GO TO LOG IN PAGE (THAT HAS DETAILS SAVED);
            Intent intent = new Intent(SplashActivity.this, LoginActivity_Logged.class);
            startActivity(intent);
            finish();
        }
    }
}
