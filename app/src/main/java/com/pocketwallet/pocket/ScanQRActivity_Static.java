package com.pocketwallet.pocket;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class ScanQRActivity_Static extends AppCompatActivity {
    EditText amountInput;
    Button payBtn;
    TextView balanceTxt;
    TextView targetNameText;

    private String amount;
    private String userId;
    private String targetUserId;
    private String targetName;
    private String paymentType;
    private String authCode;
    private Bundle extras;

    private String urlPayment = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/payment/";
    private String urlPaymentQuickPay = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/payment/quickpay/";
    private String GETBALANCE_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/";
    //Session Token
    private String sessionToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr_static);

        amountInput = (EditText)findViewById(R.id.amountTransferStatic);
        amountInput.addTextChangedListener(loginTextWatcher);
        payBtn = (Button)findViewById(R.id.payButtonStatic);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to transaction result
                //Intent newIntent = new Intent(ScanQRActivity_Static.this, ResultActivity.class);
                //startActivity(newIntent);
                if (paymentType.equalsIgnoreCase("Static")) {
                    processPayment(targetUserId,amount);
                }else{
                    processPaymentQuickPay(targetUserId,authCode,amount);
                }
            }
        });

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            amount = extras.getString("amount");
            targetUserId = extras.getString("targetUserId");
            targetName = extras.getString("targetName");
            authCode = extras.getString("targetAuthCode");
            paymentType = extras.getString("paymentType");
            //SET URL
            if(!GETBALANCE_URL.contains("/balance")) {
                GETBALANCE_URL = GETBALANCE_URL + userId + "/balance";
            }
        }

        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sessionToken = userPreferences.getString("sessionToken", "");

        if (paymentType.equalsIgnoreCase("QuickQR")){
            TextView payText = (TextView) findViewById(R.id.payText);
            payText.setText("");
        }
        amount = "";

        balanceTxt = (TextView) findViewById(R.id.balanceText);
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
                                    Intent newIntent = new Intent(ScanQRActivity_Static.this, ResultActivity.class);
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
                                    Intent newIntent = new Intent(ScanQRActivity_Static.this, ResultActivity.class);
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
    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            amount = amountInput.getText().toString().trim();
            payBtn.setEnabled(!amount.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void processPaymentQuickPay(String payeeUserId, String authCode, String amount){
        //SENDER IS MERCHANT
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("payee_id", payeeUserId);
            jsonBody.put("merchant_id", userId);
            jsonBody.put("amount", amount);
            jsonBody.put("auth_code", authCode);
            System.out.println("TEST PRINTING: " + jsonBody);
            final String amount1 = amount;
            final Activity act = this;
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, urlPaymentQuickPay, jsonBody, new Response.Listener<JSONObject>() {
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
                                    //Move to transaction result
                                    Intent newIntent = new Intent(ScanQRActivity_Static.this, ResultActivity.class);
                                    newIntent.putExtra("title","Transaction");
                                    newIntent.putExtra("results",result);
                                    newIntent.putExtra("transactionNumber",transactionNumber);
                                    newIntent.putExtra("amount", amount1);
                                    System.out.println("Amount : " + amount1);
                                    startActivity(newIntent);
                                    finish();
                                }else{
                                    System.out.println("NOT Successful Payment!");
                                    Intent newIntent = new Intent(ScanQRActivity_Static.this, ResultActivity.class);
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
                    error.printStackTrace();
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
            System.out.println("Error: " + e);
            e.getMessage();
        }
    }
    public void updateBalance(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest requestJsonObject = new JsonObjectRequest(Request.Method.GET, GETBALANCE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    final String balance = response.getString("balance");
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
}
