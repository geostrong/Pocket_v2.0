package com.pocketwallet.pocket;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QuickQrActivity extends AppCompatActivity {

    private String userId;
    private Bundle extras;
    ImageView myQR;

    private TextView authCodeText;
    private String GETAUTHCODE_URL;
    private String authCode;

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
        GETAUTHCODE_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/"+ userId + "/auth-code";

        myQR = (ImageView) findViewById(R.id.myQR);
        authCodeText = (TextView) findViewById(R.id.authCodeText);
        getAuthCode();
        myQR.post(new Runnable() {
            @Override
            public void run() {
                generateMyQR();
            }
        });
    }

    public void generateMyQR() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            //String toQR = userId + "|" + amount;
            String toQR = "Static|" + userId;
            System.out.println("TOQR: " + toQR);
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestString = new StringRequest(Request.Method.GET, GETAUTHCODE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                authCode = response.toString();
                authCode = (String) authCode.subSequence(14,authCode.length()-2);
                System.out.println("AuthCode is: " + authCode);
                UpdateSharedPreference("authCode",authCode);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("", "Error: " + error.toString());
            }
        });
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

