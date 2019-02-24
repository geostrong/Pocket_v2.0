package com.pocketwallet.pocket;

import android.app.Application;

import com.github.omadahealth.lollipin.lib.managers.LockManager;

public class CustomApplication extends Application {

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();

        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomPinActivity.class);
        lockManager.getAppLock().setLogoId(R.drawable.ic_baseline_lock_24px);
        lockManager.getAppLock().setFingerprintAuthEnabled(false);
        lockManager.getAppLock().setShouldShowForgot(false);
    }
}
