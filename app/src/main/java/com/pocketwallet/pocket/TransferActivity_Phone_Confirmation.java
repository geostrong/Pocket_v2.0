package com.pocketwallet.pocket;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class TransferActivity_Phone_Confirmation extends AppCompatActivity {

    private static final int REQUEST_CODE_UNLOCK = 12;

    private TextView amountText;
    private TextView nameText;
    private TextView phoneNumberText;
    private Button payButton;

    private Bundle extras;
    private String userId;

    private String amount;
    private String targetPhoneNumber;
    private String name;

    private String urlPayment = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/payment/phone";
    private String GETBALANCE_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/";

    TextView balanceTxt;
    String balance;
    private static final int TopUpCode = 1;
    //Session Token
    private String sessionToken;

    private String perTransactionLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer__phone_confirmation);

        //GET INFORMATION FROM BUNDLE
        extras = getIntent().getExtras();

        if (extras != null) {
            userId = extras.getString("userId");
            amount = extras.getString("sendAmount");
            targetPhoneNumber = extras.getString("targetPhoneNumber");
            name = extras.getString("targetName");
            //SET URL
            if(!GETBALANCE_URL.contains("/balance")) {
                GETBALANCE_URL = GETBALANCE_URL + userId + "/balance";
            }
        }

        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sessionToken = userPreferences.getString("sessionToken", "");
        perTransactionLimit = userPreferences.getString("per_transaction_limit", "999");

        phoneNumberText = findViewById(R.id.involvedNumber2);
        nameText = findViewById(R.id.involvedName2);
        amountText = findViewById(R.id.amountInvolved);

        nameText.setText(name);
        phoneNumberText.setText(targetPhoneNumber);
        amountText.setText("$"+amount);

        balanceTxt = findViewById(R.id.balanceText);
        updateBalance();
        payButton = findViewById(R.id.payButtonPhone);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(TransferActivity_Phone_Confirmation.this);
                    builder.setMessage("Your wallet does not have enough balance to pay. Press 'Top Up' to go to the top up page.").setTitle("Insufficient balance")
                            .setPositiveButton("Top Up", dialogClickListener)
                            .setNegativeButton("Cancel", dialogClickListener).show();
                }else{
                    if (LockManager.getInstance().isAppLockEnabled()) {
                        Intent intent = new Intent(TransferActivity_Phone_Confirmation.this, CustomPinActivity.class);
                        intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                        startActivityForResult(intent,REQUEST_CODE_UNLOCK);
                    } else {
                        //PROCESS PAYMENT
                        if(Double.parseDouble(amount) < Double.parseDouble(perTransactionLimit)) {
                            processPayment();
                        }else{
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            dialog.cancel();
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(TransferActivity_Phone_Confirmation.this);
                            builder.setMessage("The amount payable is greater than your 'per transaction limit'").setTitle("Exceeded limit")
                                    .setPositiveButton("Ok", dialogClickListener).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TopUpCode) {
            if (resultCode == Activity.RESULT_OK) {
                updateBalance();
            }
        } else if (requestCode == REQUEST_CODE_UNLOCK) {
            if (resultCode == Activity.RESULT_OK) {
                processPayment();
            }
        }
    }

    private void processPayment(){
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("payee_id", userId);
            jsonBody.put("merchant_phone", targetPhoneNumber);
            jsonBody.put("amount", amount);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, urlPayment, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("result");
                        String transactionNumber = response.getString("transaction_id");
                        if(result.equals("success")){
                            //Move to transaction result
                            Intent newIntent = new Intent(TransferActivity_Phone_Confirmation.this, ResultActivity.class);
                            newIntent.putExtra("title","Transaction");
                            newIntent.putExtra("results",result);
                            newIntent.putExtra("transactionNumber",transactionNumber);
                            newIntent.putExtra("amount", amount);
                            newIntent.putExtra("to", name+ " (" + targetPhoneNumber + ")");
                            newIntent.putExtra("mode", "0");
                            setResult(Activity.RESULT_OK);
                            requestQueue.stop();
                            startActivity(newIntent);
                            finish();
                        }else {
                            //Failed
                            Intent newIntent = new Intent(TransferActivity_Phone_Confirmation.this, ResultActivity.class);
                            newIntent.putExtra("title","Transaction");
                            newIntent.putExtra("results","phonePaymentFailed");
                            requestQueue.stop();
                            startActivity(newIntent);
                            finish();
                        }
                    }catch(JSONException e){

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    System.out.println("Error Message: " + error.getMessage());
                    System.out.println("Error Network Response Data: " + new String(error.networkResponse.data));
                    System.out.println("Error Network Response Status Code" + error.networkResponse.statusCode);
                    if(error.networkResponse.statusCode == 400){
                        String result = "failed";
                        String message = error.networkResponse.data.toString();
                    }
                    requestQueue.stop();
                    //getActivity().onBackPressed();
                }
            }) {
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest requestJsonObject = new JsonObjectRequest(Request.Method.GET, GETBALANCE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    balance = response.getString("balance");
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
                headers.put("Authorization", "Bearer " + sessionToken);
                System.out.println("Header: " + headers.values());
                return headers;
            }
        };
        requestQueue.add(requestJsonObject);
    }
}
