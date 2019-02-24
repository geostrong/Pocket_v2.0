package com.pocketwallet.pocket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.LockManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText phonenumberInput;
    private EditText passwordInput;
    private Button login;
    private TextView signup;
    private String phoneNumber;
    private String password;
    private boolean doubleBackToExitPressedOnce = false;
    private SharedPreferences logInPreferences;
    private TextInputLayout phoneNumberInputLayout;
    //LOGIN API URL
    final String LOGIN_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/login";
    final String POSTFCM_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/fcmtoken";
    String GETDETAILS_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/";
    //----------
    private String name;
    private SharedPreferences userPreferences;
    private boolean useFingerprint;
    private SharedPreferences.Editor editor;

    //SessionToken
    String sessionToken;
    String sessionTokenExpiry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //----------
        userPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        editor =  userPreferences.edit();
        useFingerprint = userPreferences.getBoolean("useFingerprint", false);

        //SETUP BUTTONS AND EDITTEXT
        phonenumberInput = (EditText)findViewById(R.id.loginPhone);
        passwordInput = (EditText)findViewById(R.id.loginPassword);
        login = (Button)findViewById(R.id.loginButton2);
        signup = (TextView)findViewById(R.id.signupButton);
        phoneNumberInputLayout = findViewById(R.id.textInputLayout7);

        final Switch bioSwitch = (Switch) findViewById(R.id.switch1);
        bioSwitch.setChecked(useFingerprint);
        bioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(LoginActivity.this);
                    if (!fingerprintManagerCompat.isHardwareDetected()) {
                        System.out.println("Device does not have fingerprint scanner");

                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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


        phonenumberInput.addTextChangedListener(loginTextWatcher);
        passwordInput.addTextChangedListener(loginTextWatcher);

        login.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view){

                login(phoneNumber, password);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        new RSSPullService(getApplicationContext());
    }

    //POST LOGIN REQUEST
    private void login(String phoneNumber, String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("mode",0);
            jsonBody.put("phoneNumber", phoneNumber);
            jsonBody.put("password", password);

            System.out.println("Login Details: " + jsonBody);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        System.out.println("Response: " + response);
                        String result = response.getString("result");
                        String userId = response.getString("user_id");
                        name = response.getString("name");
                        JSONObject testToken = response.getJSONObject("session_token");
                        sessionToken = testToken.getString("token");
                        sessionTokenExpiry = testToken.getString("expiry");
                        System.out.println("Results: " + result);
                        System.out.println("User: " + userId);
                        System.out.println("Session Token is :" + sessionToken);

                        if(!userId.equals("failed")){
                            updateToken();
                            postFCMToken(userId);
                            //GETDETAILS_URL += userId;

                            name = response.getString("name");
                            launchMainActivity(userId,name);
                        }else{
                            System.out.println("===================Failed to Login===================");
                        }

                    }catch(JSONException e){

                    }
                    //String userId;
                    //launchMainActivity(userId);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    phoneNumberInputLayout.setError("Phone number or password is wrong!");
                }
            });

            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    String KEY_NAME;
    
    private void updateToken(){
        KEY_NAME = phoneNumber + "|" + password;
        KEY_NAME = SHA256.hashSHA256(KEY_NAME);
        System.out.println("KEY_NAME: " + KEY_NAME);

        logInPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = logInPreferences.edit();
        editor.putString("KEY_NAME", KEY_NAME);
        editor.commit();
    }


    //POST FCMTOKEN
    private void postFCMToken(String userId) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String fcmToken = prefs.getString("FCM_TOKEN", "DEFAULT");
        System.out.println(fcmToken);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            jsonBody.put("fcm_token", fcmToken);

            System.out.println("Login Details: " + jsonBody);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, POSTFCM_URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("result");
                        System.out.println("Results: " + result);
                        if (result.equals("success")) {
                            System.out.println("Post FCM Token Success!");
                        } else {
                            System.out.println("Post FCM Token Failed :(");
                        }
                    } catch (JSONException e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onBackPressed();
                }
            });
            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            phoneNumber = phonenumberInput.getText().toString().trim();
            password = passwordInput.getText().toString().trim();
            password = SHA256.hashSHA256(password);
            login.setEnabled(!phoneNumber.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //LAUNCH MAIN ACTIVITY
    public void launchMainActivity(String userId,String name){
        //Update logged in
        logInPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println("isLoggedIn = " + logInPreferences.getBoolean("isLoggedIn", false));
        SharedPreferences.Editor editor = logInPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("PhoneNumber", phoneNumber);
        editor.putString("user_name",name);
        UpdateSharedPreference("sessionToken",sessionToken);
        UpdateSharedPreference("sessionTokenExpiry",sessionTokenExpiry);
        editor.commit();

        System.out.println("Phone Number login = " + logInPreferences.getString("PhoneNumber", "DEFAULT"));
        System.out.println("isLoggedIn = " + logInPreferences.getBoolean("isLoggedIn", false));

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
        finish();
    }

    //---TEST---
    private void LoginTestUser1(){
        String userId = "1cdd62b4-9f59-47c7-a7a5-919b6574bae3";
        launchMainActivity(userId,"TestUser1");
    }

    private void LoginTestUser2(){
        String userId = "b814cb32-eebf-4490-bdde-9695a7ef23a8";
        launchMainActivity(userId,"TestUser2");
    }
    //----------
    public void UpdateSharedPreference(String key, String value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
