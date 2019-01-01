package com.pocketwallet.pocket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText phonenumberInput;
    private EditText passwordInput;
    private Button login;
    private TextView signup;
    private String phoneNumber;
    private String password;
    private boolean doubleBackToExitPressedOnce = false;

    //LOGIN API URL
    final String LOGIN_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/login";
    String POSTFCM_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/fcmtoken";
    //---TEST---
    private Button loginTest1;
    private Button loginTest2;
    //----------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //---TEST BUTTONS---
        loginTest1 = (Button)findViewById(R.id.loginTest1);
        loginTest2 = (Button)findViewById(R.id.loginTest2);
        //----------

        //SETUP BUTTONS AND EDITTEXT
        phonenumberInput = (EditText)findViewById(R.id.loginPhone);
        passwordInput = (EditText)findViewById(R.id.loginPassword);
        login = (Button)findViewById(R.id.loginButton);
        signup = (TextView)findViewById(R.id.signupButton);

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

        loginTest1.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view){
                LoginTestUser1();
            }
        });

        loginTest2.setOnClickListener (new View.OnClickListener() {
            public void onClick(View view){
                LoginTestUser2();
            }
        });


    }

    //POST LOGIN REQUEST
    private void login(String phoneNumber, String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            JSONObject jsonBody = new JSONObject();
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
                        System.out.println("Results: " + result);
                        System.out.println("User: " + userId);
                        if(!userId.equals("failed")){
                            postFCMToken(userId);
                            launchMainActivity(userId);
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
                    onBackPressed();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    //headers.put("Authorization", "Basic " + "c2FnYXJAa2FydHBheS5jb206cnMwM2UxQUp5RnQzNkQ5NDBxbjNmUDgzNVE3STAyNzI=");//put your token here
                    return headers;
                }
            };

            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

            login.setEnabled(!phoneNumber.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //LAUNCH MAIN ACTIVITY
    public void launchMainActivity(String userId){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    //---TEST---
    private void LoginTestUser1(){
        String userId = "1cdd62b4-9f59-47c7-a7a5-919b6574bae3";
        launchMainActivity(userId);
    }

    private void LoginTestUser2(){
        String userId = "b814cb32-eebf-4490-bdde-9695a7ef23a8";
        launchMainActivity(userId);
    }
    //----------
}
