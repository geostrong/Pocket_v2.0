package com.pocketwallet.pocket;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScanQRActivity_Dynamic extends AppCompatActivity {

    private String userId;
    private String amount;
    private String targetUserId;

    private Bundle extras;
    private String urlPayment = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/payment/";

    TextView totalAmountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr_dynamic);

        Button payBtn = (Button)findViewById(R.id.payButtonDynamic);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to transaction result
                //Intent newIntent = new Intent(ScanQRActivity_Dynamic.this, ResultActivity.class);
                //startActivity(newIntent);
                //finish();
                processPayment(targetUserId,amount);
            }
        });

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            amount = extras.getString("amount");
            targetUserId = extras.getString("targetUserId");
        }

        totalAmountText = (TextView) findViewById(R.id.totalAmountText);
        totalAmountText.setText("$:"+ amount);
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
                    //headers.put("Authorization", "Basic " + "c2FnYXJAa2FydHBheS5jb206cnMwM2UxQUp5RnQzNkQ5NDBxbjNmUDgzNVE3STAyNzI=");//put your token here
                    return headers;
                }
            };
            requestQueue.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
