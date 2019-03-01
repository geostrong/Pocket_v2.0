package com.pocketwallet.pocket;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.android.volley.toolbox.Volley;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.LockManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScanQRActivity_Dynamic extends AppCompatActivity {

    private static final int REQUEST_CODE_UNLOCK = 12;

    private String userId;
    private String targetName;
    private String amount;
    private String targetUserId;
    private String targetPhoneNumber;
    private String authCode;
    private String paymentType;
    private String balance;
    private static final int TopUpCode = 1;

    private Bundle extras;
    private String urlPayment = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/payment/";
    private String urlPaymentQuickPay = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/payment/quickpay";
    private String GETBALANCE_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/";

    TextView totalAmountText;
    TextView balanceTxt;
    TextView targetNameText;
    TextView payText;
    TextView targetPhoneNumberText;

    //Session Token
    private String sessionToken;
    private String perTransactionLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr_dynamic);

        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        Button payBtn = (Button)findViewById(R.id.payButtonDynamic);

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            amount = extras.getString("amount");
            targetUserId = extras.getString("targetUserId");
            targetName = extras.getString("targetName");
            targetPhoneNumber = extras.getString("targetPhoneNumber");
            paymentType = extras.getString("paymentType");
            authCode = extras.getString("targetAuthCode");
            //SET URL
            if(!GETBALANCE_URL.contains("/balance")) {
                GETBALANCE_URL = GETBALANCE_URL + userId + "/balance";
            }
        }

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to transaction result
                if (paymentType.equalsIgnoreCase("Dynamic")) {;
                    if (Double.parseDouble(balance) < Double.parseDouble(amount)) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent intent = new Intent(getApplicationContext(), TopUpActivity.class);
                                        intent.putExtra("userId", userId);
                                        startActivityForResult(intent, TopUpCode);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:

                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRActivity_Dynamic.this);
                        builder.setMessage("Your wallet does not have enough balance to pay. Press 'Top Up' to go to the top up page.").setTitle("Insufficient Balance")
                                .setPositiveButton("Top Up", dialogClickListener)
                                .setNegativeButton("Cancel", dialogClickListener).show();
                    } else {
                        if (LockManager.getInstance().isAppLockEnabled()) {
                            Intent intent = new Intent(ScanQRActivity_Dynamic.this, CustomPinActivity.class);
                            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                            startActivityForResult(intent, REQUEST_CODE_UNLOCK);

                        } else {
                            //PROCESS PAYMENT
                            if (Double.parseDouble(amount) < Double.parseDouble(perTransactionLimit)) {
                                processPayment(targetUserId, amount);
                            } else {
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                dialog.cancel();
                                                break;
                                        }
                                    }
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRActivity_Dynamic.this);
                                builder.setMessage("The amount payable is greater than your 'per transaction limit'").setTitle("Exceeded Limit")
                                        .setPositiveButton("Ok", dialogClickListener).show();
                            }
                        }
                    }
                } else {
                    processPaymentQuickPay(targetUserId,authCode,amount);
                }
            }
        });

        payText = (TextView)findViewById(R.id.payText);
        if (paymentType.equalsIgnoreCase("QuickQR")) {
            payText.setText("You're Requesting");
            payBtn.setText("REQUEST");
        }


        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sessionToken = userPreferences.getString("sessionToken", "");
        perTransactionLimit = userPreferences.getString("per_transaction_limit", "999");

        balanceTxt = (TextView) findViewById(R.id.balanceText);
        totalAmountText = (TextView) findViewById(R.id.totalAmount);
        totalAmountText.setText("$"+ amount);
        targetNameText = (TextView) findViewById(R.id.involvedName2);
        targetNameText.setText(targetName);
        targetPhoneNumberText = (TextView) findViewById(R.id.involvedNumber2);
        targetPhoneNumberText.setText(targetPhoneNumber);
        updateBalance();
    }

    public void processPayment(String merchantUserId, String amount){
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        try{
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("payee_id", userId);
            jsonBody.put("merchant_id", merchantUserId);
            jsonBody.put("amount", amount);
            //jsonBody.put("auth_code", authCode);
            final String amount1 = amount;
            final Activity act = this;
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, urlPayment, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        final String transactionNumber = response.getString("transaction_id");
                        final String result = response.getString("result");

                        act.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                if(result.equalsIgnoreCase("Success")) {
                                    //Move to transaction result
                                    Intent newIntent = new Intent(ScanQRActivity_Dynamic.this, ResultActivity.class);
                                    newIntent.putExtra("title","Transaction");
                                    newIntent.putExtra("results",result);
                                    newIntent.putExtra("transactionNumber",transactionNumber);
                                    newIntent.putExtra("amount", amount1);
                                    newIntent.putExtra("mode", "0");
                                    newIntent.putExtra("to",targetName);
                                    requestQueue.stop();
                                    startActivity(newIntent);
                                    finish();
                                }else{
                                    //Move to transaction result
                                    Intent newIntent = new Intent(ScanQRActivity_Dynamic.this, ResultActivity.class);
                                    newIntent.putExtra("title","Transaction");
                                    newIntent.putExtra("mode", "0");
                                    newIntent.putExtra("results",result);
                                    requestQueue.stop();
                                    startActivity(newIntent);
                                    finish();
                                }
                            }
                        });

                    }catch(JSONException e){
                        System.out.println("Error: " + e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestQueue.stop();
                    onBackPressed();
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

    public void updateBalance(){
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        JsonObjectRequest requestJsonObject = new JsonObjectRequest(Request.Method.GET, GETBALANCE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    balance = response.getString("balance");
                    balanceTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            balanceTxt.setText("$"+balance);
                            requestQueue.stop();
                        }
                    });
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("", "Error: " + error.toString());
                requestQueue.stop();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + sessionToken);//put your token here
                System.out.println("Header: " + headers.values());
                return headers;
            }
        };
        requestQueue.add(requestJsonObject);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TopUpCode) {
            if (resultCode == Activity.RESULT_OK) {
                updateBalance();
            }
        } else if (requestCode == REQUEST_CODE_UNLOCK) {
            if (resultCode == Activity.RESULT_OK) {
                processPayment(targetUserId, amount);;
            }
        }
    }

    public void processPaymentQuickPay(String payeeUserId, String authCode, String amount){
        //SENDER IS MERCHANT
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("payee_id", payeeUserId);
            jsonBody.put("merchant_id", userId);
            jsonBody.put("amount", amount);
            jsonBody.put("auth_code", authCode);
            final String amount1 = amount;
            final Activity act = this;
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, urlPaymentQuickPay, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        final String transactionNumber = response.getString("transaction_id");
                        final String result = response.getString("result");
                        act.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                if(result.equalsIgnoreCase("Success")) {
                                    //Move to transaction result
                                    Intent newIntent = new Intent(ScanQRActivity_Dynamic.this, ResultActivity.class);
                                    newIntent.putExtra("title","Transaction");
                                    newIntent.putExtra("results",result);
                                    newIntent.putExtra("transactionNumber",transactionNumber);
                                    newIntent.putExtra("amount", amount1);
                                    newIntent.putExtra("mode", "1");
                                    newIntent.putExtra("to",targetName);
                                    startActivity(newIntent);
                                    finish();
                                }
                            }
                        });
                    }catch(JSONException e){
                        System.out.println("Error: " + e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if(error.networkResponse.statusCode == 400){
                        Intent newIntent = new Intent(ScanQRActivity_Dynamic.this, ResultActivity.class);
                        newIntent.putExtra("title","Transaction");
                        newIntent.putExtra("results","failed");
                        startActivity(newIntent);
                        finish();
                    }
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
}
