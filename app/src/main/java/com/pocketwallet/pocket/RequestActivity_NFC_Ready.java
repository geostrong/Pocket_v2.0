package com.pocketwallet.pocket;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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

public class RequestActivity_NFC_Ready extends AppCompatActivity {

    TextView requestedAmountView;

    Bundle extras;
    String userId;
    String payeeUserId;
    String amount;
    String authCode;

    private String urlPayment = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/payment/quickpay";

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    //Session Token
    private String sessionToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request__nfc__ready);

        getSupportActionBar().setTitle("Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));


        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            amount = extras.getString("requestingAmount");
            UpdateSharedPreference("userId",userId);
            UpdateSharedPreference("amount",amount);
        }
        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sessionToken = userPreferences.getString("sessionToken", "");

        requestedAmountView = findViewById(R.id.requestedAmountNFC);
        requestedAmountView.setText("$" + amount);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            //No NFC
            System.out.println("Device does not support NFC");
            return;
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public void processNFC(String merchantId, String payeeUserId,String authCode){
        System.out.println("Processing NFC..." + payeeUserId + "," + authCode);
        this.userId = merchantId;
        this.payeeUserId = payeeUserId;
        this.authCode = authCode;
        Payment();
    }

    private void Payment() {
        System.out.println("Sending Payment To Server...");
        System.out.println("Payee_id: " + payeeUserId);
        System.out.println("Merchant_id: " + userId);
        System.out.println("Auth_code: " + authCode);
        System.out.println("Amount: " + amount);
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("payee_id", payeeUserId);
            jsonBody.put("merchant_id", userId);
            jsonBody.put("amount", amount);
            jsonBody.put("auth_code", authCode);
            System.out.println("TEST PRINTING: " + jsonBody);
            final String amount1 = amount;
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, urlPayment, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String transactionNumber = response.getString("transaction_id");
                        String result = response.getString("result");
                        System.out.println("Results: " + result);
                        if(result.equalsIgnoreCase("Success")){
                            //Move to transaction result
                            Intent newIntent = new Intent(RequestActivity_NFC_Ready.this, ResultActivity.class);
                            newIntent.putExtra("title","Transaction");
                            newIntent.putExtra("results",result);
                            newIntent.putExtra("transactionNumber",transactionNumber);
                            newIntent.putExtra("amount", amount1);
                            newIntent.putExtra("mode", "1");
                            System.out.println("Amount : " + amount1);
                            setResult(Activity.RESULT_OK);
                            requestQueue.stop();
                            startActivity(newIntent);
                            finish();
                        }
                    }catch(JSONException e){
                        System.out.println("Error: " + e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse.statusCode == 400){
                        Intent newIntent = new Intent(RequestActivity_NFC_Ready.this, ResultActivity.class);
                        newIntent.putExtra("title","Transaction");
                        newIntent.putExtra("results","failed");
                        setResult(Activity.RESULT_OK);
                        requestQueue.stop();
                        startActivity(newIntent);
                        finish();
                    }
                    //onBackPressed();
                }
            }) {
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

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        getTagInfo(intent);
    }

    private void getTagInfo(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        processIntent(intent);
    }

    public void processIntent(Intent intent) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];

        String encryptedText = new String(msg.getRecords()[0].getPayload());
        try {
            encryptedText = AESUtils.decrypt(encryptedText);
            System.out.println("decrypted:" + encryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String results[] = encryptedText.split("\\|");
        String payeeUserId = results[0];
        String authCode = results[1];

        // record 0 contains the MIME type, record 1 is the AAR, if present
        System.out.println("NFC MESSAGE RECEIVED: " + new String(msg.getRecords()[0].getPayload()));
        System.out.println("PAYEEUSERID: " + payeeUserId);
        processNFC(userId, payeeUserId,authCode);
    }

    public String getUserId(){
        return userId;
    }

    public void UpdateSharedPreference(String key, String value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
