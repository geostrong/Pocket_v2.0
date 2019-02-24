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

import java.util.HashMap;
import java.util.Map;

public class ScanQRActivity_Dynamic extends AppCompatActivity {

    private static final int REQUEST_CODE_UNLOCK = 12;

    private String userId;
    private String targetName;
    private String amount;
    private String targetUserId;
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

    //Session Token
    private String sessionToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr_dynamic);

        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        Button payBtn = (Button)findViewById(R.id.payButtonDynamic);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to transaction result
                if(Double.parseDouble(balance) < Double.parseDouble(amount)){
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    Intent intent = new Intent (getApplicationContext(), TopUpActivity.class);
                                    intent.putExtra("userId",userId);
                                    startActivityForResult(intent, TopUpCode);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRActivity_Dynamic.this);
                    builder.setMessage("Your wallet does not have enough balance to pay. Press 'Top Up' to go to the top up page.")
                            .setPositiveButton("Top Up", dialogClickListener)
                            .setNegativeButton("Cancel", dialogClickListener).show();
                }else {
                    if (LockManager.getInstance().isAppLockEnabled()) {
                        Intent intent = new Intent(ScanQRActivity_Dynamic.this, CustomPinActivity.class);
                        intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                        startActivityForResult(intent,REQUEST_CODE_UNLOCK);

                    } else {
                        //PROCESS PAYMENT
                        processPayment(targetUserId, amount);
                    }
                }
            }
        });

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            amount = extras.getString("amount");
            targetUserId = extras.getString("targetUserId");
            targetName = extras.getString("targetName");
            //SET URL
            if(!GETBALANCE_URL.contains("/balance")) {
                GETBALANCE_URL = GETBALANCE_URL + userId + "/balance";
            }
        }

        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sessionToken = userPreferences.getString("sessionToken", "");

        balanceTxt = (TextView) findViewById(R.id.balanceText);
        totalAmountText = (TextView) findViewById(R.id.totalAmount);
        totalAmountText.setText("$"+ amount);
        targetNameText = (TextView) findViewById(R.id.involvedName2);
        targetNameText.setText(targetName);
        updateBalance();
    }

    public void processPayment(String merchantUserId, String amount){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try{
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("payee_id", userId);
            jsonBody.put("merchant_id", merchantUserId);
            jsonBody.put("amount", amount);
            //jsonBody.put("auth_code", authCode);
            System.out.println("TEST PRINTING: " + jsonBody);
            final String amount1 = amount;
            final Activity act = this;
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, urlPayment, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        final String transactionNumber = response.getString("transaction_id");
                        final String result = response.getString("result");

                        System.out.println("Response : " + response);
                        System.out.println("Result: " + result);
                        System.out.println("Transaction ID: " + transactionNumber);
                        act.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                if(result.equalsIgnoreCase("Success")) {
                                    System.out.println("Successful Payment!");
                                    //mTextView.setText("Transaction is successful!"
                                    //        + "\nTransaction Number:" + transactionNumber);
                                    //Move to transaction result
                                    Intent newIntent = new Intent(ScanQRActivity_Dynamic.this, ResultActivity.class);
                                    newIntent.putExtra("title","Transaction");
                                    newIntent.putExtra("results",result);
                                    newIntent.putExtra("transactionNumber",transactionNumber);
                                    newIntent.putExtra("amount", amount1);
                                    System.out.println("Amount : " + amount1);
                                    startActivity(newIntent);
                                    finish();
                                }else{
                                    //mTextView.setText("Transaction failed"
                                    //        + "\nTransaction Number:" + transactionNumber);
                                    //Move to transaction result
                                    System.out.println("NOT Successful Payment!");
                                    Intent newIntent = new Intent(ScanQRActivity_Dynamic.this, ResultActivity.class);
                                    newIntent.putExtra("title","Transaction");
                                    newIntent.putExtra("results",result);
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
                    onBackPressed();
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
            requestQueue.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateBalance(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest requestJsonObject = new JsonObjectRequest(Request.Method.GET, GETBALANCE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    balance = response.getString("balance");
                    System.out.println(response.getString("balance"));
                    balanceTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            balanceTxt.setText("$"+balance);
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
                System.out.println("Top Up Successful");
                updateBalance();
            }
        } else if (requestCode == REQUEST_CODE_UNLOCK) {
            if (resultCode == Activity.RESULT_OK) {
                processPayment(targetUserId, amount);;
            }
        }
    }
}
