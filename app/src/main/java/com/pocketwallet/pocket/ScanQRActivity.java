package com.pocketwallet.pocket;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

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

    private boolean scanned = false;

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

        startScan();

    }

    @Override
    public void onResume(){
        super.onResume();
        scanned = false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void startScan () {

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.mTextView);
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource = new CameraSource.Builder(ScanQRActivity.this, barcodeDetector).setRequestedPreviewSize(surfaceView.getWidth(), surfaceView.getHeight()).setAutoFocusEnabled(true).build();
                    cameraSource.start(holder);
                } catch (IOException e) {
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
                if (qrCodes.size() != 0 && scanned == false) {
                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    scanned = true;
                    System.out.println("Result is: " + qrCodes.valueAt(0).displayValue);
                    String encryptedQR = qrCodes.valueAt(0).displayValue;
                    try {
                        encryptedQR = AESUtils.decrypt(encryptedQR);
                        System.out.println("decrypted:" + encryptedQR);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String resultText = encryptedQR;
                    String results[] = resultText.split("\\|");
                    String qrType = results[0];
                    //HANDLE STATIC /DYNAMIC HERE
                    if (qrType.equals("Dynamic")) {
                        //Dynamic
                        String targetUserId = results[1];
                        String amount = results[2];
                        String targetName = results[3];
                        System.out.println("Dynamic QR, the amount is: " + amount);
                        System.out.println("The target userid is: + targetUserId");
                        Intent dynamicIntent = new Intent(ScanQRActivity.this, ScanQRActivity_Dynamic.class);
                        dynamicIntent.putExtra("paymentType", "Dynamic");
                        dynamicIntent.putExtra("title", "transaction");
                        dynamicIntent.putExtra("userId", userId);
                        dynamicIntent.putExtra("amount", amount);
                        dynamicIntent.putExtra("targetUserId", targetUserId);
                        dynamicIntent.putExtra("targetName",targetName);
                        startActivity(dynamicIntent);
                        finish();
                    } else if (qrType.equals("Static")) {
                        //Static
                        String targetUserId = results[1];
                        String targetName = results[2];
                        System.out.println("Static QR, target/merchant userID is: " + targetUserId);
                        Intent staticIntent = new Intent(ScanQRActivity.this, ScanQRActivity_Static.class);
                        staticIntent.putExtra("paymentType", "Static");
                        staticIntent.putExtra("title", "transaction");
                        staticIntent.putExtra("userId", userId);
                        staticIntent.putExtra("targetUserId", targetUserId);
                        staticIntent.putExtra("targetName",targetName);
                        startActivity(staticIntent);
                        finish();
                    } else {
                        //QuickQR
                        //Static
                        for (int i = 0; i < results.length; i++) {
                            System.out.println("Test + " + i + ": " + results[i]);
                        }
                        String targetUserId = results[1];
                        String targetAuthCode = results[2];
                        String targetName = results[3];
                        System.out.println("Quick QR, payee userID is: " + targetUserId);
                        System.out.println("AuthCode is: " + targetAuthCode);
                        Intent staticIntent = new Intent(ScanQRActivity.this, ScanQRActivity_Static.class);
                        staticIntent.putExtra("paymentType", "QuickQR");
                        staticIntent.putExtra("title", "transaction");
                        staticIntent.putExtra("userId", userId);
                        staticIntent.putExtra("targetUserId", targetUserId);
                        staticIntent.putExtra("targetAuthCode", targetAuthCode);
                        staticIntent.putExtra("targetName",targetName);
                        startActivity(staticIntent);
                        finish();
                    }

                    //Change process payment
                    //processPayment(merchantUserId,amount);

                    finish();
                }
            }
        });
        System.out.println("userid: " + userId);
    }
}
