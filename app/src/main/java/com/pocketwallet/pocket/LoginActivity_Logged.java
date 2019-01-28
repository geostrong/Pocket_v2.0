package com.pocketwallet.pocket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class LoginActivity_Logged extends AppCompatActivity {

    final String LOGIN_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/login";

    private SharedPreferences userPreferences;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_loggedin);

        //Get user's name from shared preferences
        userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String user_name = userPreferences.getString("user_name", "Name");
        TextView name = findViewById(R.id.signinName);
        name.setText(user_name);

        //FINGERPRINT/PIN LOGIN HERE
        //login(phoneNumber,password);
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
                            launchMainActivity(userId);
                        }else{
                            System.out.println("===================Failed to Login===================");
                        }

                    }catch(JSONException e){

                    }
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

    //LAUNCH MAIN ACTIVITY
    public void launchMainActivity(String userId){
        Intent intent = new Intent(LoginActivity_Logged.this, MainActivity.class);
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
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
