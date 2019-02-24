package com.pocketwallet.pocket;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
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
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

public class QuickQrActivity extends AppCompatActivity {

    private String userId;
    private Bundle extras;
    ImageView myQR;

    private TextView authCodeText;
    private String GETAUTHCODE_URL;
    private String authCode;

    //Session Token
    private String sessionToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quickqr);

        getSupportActionBar().setTitle("Quick QR");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sessionToken = userPreferences.getString("sessionToken", "");

        GETAUTHCODE_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/"+ userId + "/auth-code";

        myQR = (ImageView) findViewById(R.id.myQR);
        authCodeText = (TextView) findViewById(R.id.authCodeText);
        getAuthCode();
    }

    public void generateMyQR() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            //String toQR = userId + "|" + amount;
            SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String toQR = "QuickQR|" + userId + "|" + authCode + "|" + userPreferences.getString("user_name", "Name");
            System.out.println("TOQR: " + toQR);

            try {
                toQR = AESUtils.encrypt(toQR);
               System.out.println("encrypted:" + toQR);
            } catch (Exception e) {
                e.printStackTrace();
            }

            BitMatrix bitMatrix = multiFormatWriter.encode(toQR, BarcodeFormat.QR_CODE, myQR.getWidth(), myQR.getHeight());
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            myQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    //Get Authentication Code
    private void getAuthCode(){
        StringRequest requestString;
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        requestString = new StringRequest(Request.Method.GET, GETAUTHCODE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                authCode = response.toString();
                authCode = (String) authCode.subSequence(14,authCode.length()-2);
                System.out.println("AuthCode is: " + authCode);
                UpdateSharedPreference("authCode",authCode);
                authCodeText.setText(authCode);
                myQR.post(new Runnable() {
                    @Override
                    public void run() {
                        requestQueue.stop();
                        generateMyQR();
                    }
                });
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
        };;
        requestQueue.add(requestString);
    }

    public void UpdateSharedPreference(String key, String value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

