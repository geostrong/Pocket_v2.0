package com.pocketwallet.pocket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQRActivity extends AppCompatActivity
                implements ZXingScannerView.ResultHandler{

    private ZXingScannerView qrScanner;
    private TextView mTextView;
    private ImageView qrScannerView;

    private Bundle extras;
    private String urlPayment = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/payment/";

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr);

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        mTextView = findViewById(R.id.mTextView);

        qrScanner = new ZXingScannerView(getApplicationContext());
        setContentView(qrScanner);
        qrScanner.setResultHandler(this);
        qrScanner.startCamera();
        System.out.println("userid: " + userId);
    }

    @Override
    protected void onPause(){
        super.onPause();
        qrScanner.stopCamera();
    }

    @Override
    public void handleResult(Result result){
        System.out.println("Result is: " + result.getText());
        qrScanner.stopCamera();
        setContentView(R.layout.activity_scanqr);
        String resultText = result.getText();
        String merchantUserId = (String) resultText.subSequence(0,36);
        String amount = (String)resultText.subSequence(37,resultText.length());
        System.out.println("Merchant User ID: " + merchantUserId);
        System.out.println("Amount: " + amount);
        processPayment(merchantUserId,amount);
    }

    public void processPayment(String merchantUserId,String amount){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try{
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("payee_id", userId);
        jsonBody.put("merchant_id", merchantUserId);
        jsonBody.put("amount", amount);
        //jsonBody.put("auth_code", authCode);
        System.out.println("TEST PRINTING: " + jsonBody);
        final Activity act = this;
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, urlPayment, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final String transactionNumber = response.getString("Transaction Number");
                    final String result = response.getString("Result");

                    System.out.println("Response : " + response);
                    System.out.println("Result: " + result);
                    System.out.println("Transaction Number: " + transactionNumber);
                    act.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            if(result.equals("Success")) {
                                mTextView.setText("Transaction is successful!"
                                        + "\nTransaction Number:" + transactionNumber);
                            }else{
                                mTextView.setText("Transaction failed"
                                        + "\nTransaction Number:" + transactionNumber);
                            }
                        }

                    });

                }catch(JSONException e){

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
