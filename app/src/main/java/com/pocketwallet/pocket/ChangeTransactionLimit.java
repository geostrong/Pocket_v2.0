package com.pocketwallet.pocket;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeTransactionLimit extends AppCompatActivity {
    EditText transactionLimitInput;
    EditText passwordInput;
    String transactionLimit;
    String password;
    String userId;

    TextView currentTransactionLimit;
    Button changeTransactionLimitButton;
    String currentTLimit;
    String sessionToken;

    String CHANGEPERTRANSACTIONLIMIT_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users";
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_transaction_limit);
        getSupportActionBar().setTitle("Change Transaction Limit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sessionToken = userPreferences.getString("sessionToken", "");
        currentTLimit = userPreferences.getString("per_transaction_limit", "999");
        transactionLimitInput = findViewById(R.id.transactionLimit);
        passwordInput = findViewById(R.id.password);
        currentTransactionLimit = findViewById(R.id.currentTransactionLimit);
        currentTransactionLimit.setText("Current Daily Limit : $" + currentTLimit);

        transactionLimitInput.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);

        changeTransactionLimitButton = findViewById(R.id.changeTransactionLimit);
        changeTransactionLimitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestChangePerTransactionLimit();
            }
        });

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            if(!CHANGEPERTRANSACTIONLIMIT_URL.contains("limit")) {
                CHANGEPERTRANSACTIONLIMIT_URL = CHANGEPERTRANSACTIONLIMIT_URL + "/" + userId + "/limit";
            }
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            transactionLimit = transactionLimitInput.getText().toString().trim();
            password = passwordInput.getText().toString().trim();
            password = SHA256.hashSHA256(password);
            changeTransactionLimitButton.setEnabled(!transactionLimit.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //POST LOGIN REQUEST
    private void requestChangePerTransactionLimit() {
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id",userId);
            jsonBody.put("limit", transactionLimit);
            jsonBody.put("option",0);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, CHANGEPERTRANSACTIONLIMIT_URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("result");
                        if(result.equalsIgnoreCase("success")){
                            UpdateSharedPreference("per_transaction_limit",response.getString("per_transaction_limit"));
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            dialog.cancel();
                                            finish();
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangeTransactionLimit.this);
                            builder.setMessage("Successfully changed per transaction limit!\nPer transaction limit is now $"+ response.getString("per_transaction_limit"))
                                    .setPositiveButton("Ok", dialogClickListener).show();
                        }
                    }catch(JSONException e){

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    dialog.cancel();
                                    finish();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangeTransactionLimit.this);
                    builder.setMessage("Failed to change per transaction limit")
                            .setPositiveButton("Ok", dialogClickListener).show();
                    requestQueue.stop();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + sessionToken);
                    System.out.println("Header: " + headers.values());
                    return headers;
                }
            };

            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void UpdateSharedPreference(String key, String value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
