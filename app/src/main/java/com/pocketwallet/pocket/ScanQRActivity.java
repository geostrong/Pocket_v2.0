package com.pocketwallet.pocket;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQRActivity extends AppCompatActivity{

    private ZXingScannerView qrScanner;
    private TextView mTextView;
    private SurfaceView qrScannerView;
    SurfaceView surfaceView;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;

    private Bundle extras;
    private String urlPayment = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/payment/";

    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr);
        getSupportActionBar().setTitle("Scan QR");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        mTextView = findViewById(R.id.mTextView);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    return;
                }
                try{
                    cameraSource = new CameraSource.Builder(ScanQRActivity.this,barcodeDetector).setRequestedPreviewSize(surfaceView.getWidth(),surfaceView.getHeight()).setAutoFocusEnabled(true).build();
                    cameraSource.start(holder);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new com.google.android.gms.vision.Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(com.google.android.gms.vision.Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if(qrCodes.size()!=0) {
                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    System.out.println("Result is: " + qrCodes.valueAt(0).displayValue);

                    /*
                    String resultText = qrCodes.valueAt(0).displayValue;
                    String merchantUserId = (String) resultText.subSequence(0, 36);
                    String amount = (String) resultText.subSequence(37, resultText.length());
                    System.out.println("Merchant User ID: " + merchantUserId);
                    System.out.println("Amount: " + amount);
                    */

                    boolean isDynamicQR = true;
                    //HANDLE STATIC /DYNAMIC HERE
                    if (isDynamicQR) {
                        //Dynamic
                        Intent dynamicIntent = new Intent(ScanQRActivity.this, ScanQRActivity_Dynamic.class);
                        startActivity(dynamicIntent);
                    } else {
                        //Static
                        Intent staticIntent = new Intent(ScanQRActivity.this, ScanQRActivity_Static.class);
                        startActivity(staticIntent);
                    }

                    //Change process payment
                    //processPayment(merchantUserId,amount);

                    finish();
                }
            }
        });
        System.out.println("userid: " + userId);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
