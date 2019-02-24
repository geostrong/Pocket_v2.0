package com.pocketwallet.pocket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.LockManager;

import org.apache.commons.io.FileUtils;

public class MoreFragment extends Fragment {

        private static final int REQUEST_CODE_ENABLE = 11;

        private SharedPreferences userPreferences;
        private boolean useFingerprint;
        private boolean shakeToExit;
        private boolean enablePIN;
        Switch pinSwitch;
        private SharedPreferences.Editor editor;
        private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, null);
        ((MainActivity)getActivity()).getSupportActionBar().show();
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Settings");
        ((MainActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        ((MainActivity)getActivity()).getSupportActionBar().setElevation(4);

        //Retrieve bundle information
        Bundle extras = new Bundle();
        extras = getArguments();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        userPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor =  userPreferences.edit();
        useFingerprint = userPreferences.getBoolean("useFingerprint", false);
        shakeToExit = userPreferences.getBoolean("ShakeToExit",false);
        //enablePIN = userPreferences.getBoolean("EnablePIN", false);

        Button changepPINBtn = (Button) view.findViewById(R.id.changePin);
        changepPINBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (LockManager.getInstance().getAppLock().isPasscodeSet()) {
                    Intent intent = new Intent(getActivity(), CustomPinActivity.class);
                    intent.putExtra(AppLock.EXTRA_TYPE, AppLock.CHANGE_PIN);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "PIN hasn't been set yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button changepasswordBtn = (Button) view.findViewById(R.id.changePassword);
        changepasswordBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), ChangePasswordActivity.class);
                newIntent.putExtra("userId",userId);
                startActivity(newIntent);
            }
        });

        Button changeDailyLimitBtn = (Button) view.findViewById(R.id.changeDailyLimit);
        changeDailyLimitBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), ChangeDailyLimit.class);
                newIntent.putExtra("userId",userId);
                startActivity(newIntent);
            }
        });

        Button changeTransactionLimitBtn = (Button) view.findViewById(R.id.changeTransactionLimit);
        changeTransactionLimitBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), ChangeTransactionLimit.class);
                newIntent.putExtra("userId",userId);
                startActivity(newIntent);
            }
        });

        Button logoutBtn = (Button) view.findViewById(R.id.logoutButton);
        logoutBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                cleanData();
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
                getActivity().finish();
            }
        });


        final Switch bioSwitch = (Switch) view.findViewById(R.id.biometricLogin);
        bioSwitch.setChecked(useFingerprint);
        bioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(getContext());
                    if (!fingerprintManagerCompat.isHardwareDetected()) {
                        System.out.println("Device does not have fingerprint scanner");

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Fingerprint scanner not found")
                                .setMessage("Device do not have a fingerprint scanner")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        bioSwitch.setChecked(false);
                    } else {
                        if (!fingerprintManagerCompat.hasEnrolledFingerprints()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Fingerprint not found")
                                    .setMessage("No fingerprint registered in device. Please setup fingerprint in your device first")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                            // User hasn't enrolled any fingerprints to authenticate with
                            System.out.println("Devices does not have enrolled fingerprints");
                            bioSwitch.setChecked(false);
                        } else {
                            editor.putBoolean("useFingerprint", true);
                            editor.commit();
                        }
                    }


                } else {
                    editor.putBoolean("useFingerprint", false);
                    editor.commit();
                }
            }
        });

        pinSwitch = (Switch) view.findViewById(R.id.enablePin);
        pinSwitch.setChecked(LockManager.getInstance().isAppLockEnabled());
        pinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LockManager.getInstance().getAppLock().enable();

                    if (!LockManager.getInstance().getAppLock().isPasscodeSet()) {
                        Intent intent = new Intent(getActivity(), CustomPinActivity.class);
                        intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                        startActivityForResult(intent, REQUEST_CODE_ENABLE);
                    }

                    editor.putBoolean("usePIN", true);
                    editor.commit();
                } else {
                    //LockManager.getInstance().disableAppLock();
                    LockManager.getInstance().getAppLock().disable();
                    editor.putBoolean("usePIN", false);
                    editor.commit();
                }
            }
        });

        LockManager.getInstance().isAppLockEnabled();

        Switch shakeToExitSwitch = (Switch) view.findViewById(R.id.shakeToQuit);
        shakeToExitSwitch.setChecked(shakeToExit);
        shakeToExitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("ShakeToExit", true);
                    editor.commit();
                } else {
                    editor.putBoolean("ShakeToExit", false);
                    editor.commit();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ENABLE) {
            if(resultCode == Activity.RESULT_CANCELED) {
                pinSwitch.setChecked(false);
                LockManager.getInstance().getAppLock().disable();
                editor.putBoolean("usePIN", false);
                editor.commit();
            }
        }
    }

    private void cleanData () {
        DatabaseHelper db = new DatabaseHelper(getActivity());
        db.cleanDB();
        //Clear cache
        FileUtils.deleteQuietly(getActivity().getCacheDir());

        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.putString("PhoneNumber", "Phone Number");
        editor.putString("user_name", "Name");
        editor.putBoolean("useFingerprint", false);
        editor.putString("profileImage",null);
        editor.putBoolean("ShakeToExit", false);
        editor.putBoolean("usePIN", false);
        editor.commit();

        LockManager.getInstance().getAppLock().disableAndRemoveConfiguration();
    }
}
