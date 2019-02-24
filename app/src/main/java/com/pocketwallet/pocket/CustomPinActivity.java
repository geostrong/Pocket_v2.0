package com.pocketwallet.pocket;

import android.app.Activity;
import android.content.Intent;

import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;

import java.util.Arrays;
import java.util.List;

public class CustomPinActivity extends AppLockActivity {
    @Override
    public void showForgotDialog() {

    }

    @Override
    public void onPinFailure(int attempts) {
        if (attempts > 3) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
    }

    @Override
    public void onPinSuccess(int attempts) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
    }

    @Override
    public List<Integer> getBackableTypes() {
        return Arrays.asList(AppLock.ENABLE_PINLOCK, AppLock.CHANGE_PIN,
                AppLock.DISABLE_PINLOCK, AppLock.UNLOCK_PIN);
    }
}
