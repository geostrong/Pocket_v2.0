package com.pocketwallet.pocket;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class LoyaltyActivity_Details extends AppCompatActivity {
    ImageView myLoyaltyQR;
    ImageView myLoyaltyBarcode;
    private String num;
    private String name;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty__details);

        extras = getIntent().getExtras();
        if (extras != null) {
            num = extras.getString("num");
            name = extras.getString("name");
        }
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        myLoyaltyQR = (ImageView) findViewById(R.id.myLoyaltyQR);
        myLoyaltyBarcode = (ImageView) findViewById(R.id.myLoyaltyBarcode);
        myLoyaltyQR.post(new Runnable() {
            @Override
            public void run() {
                generateMyQR();
                generateMyBarcode();
            }
        });
    }

    public void generateMyQR() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            String toQR = num;
            BitMatrix bitMatrix = multiFormatWriter.encode(toQR, BarcodeFormat.QR_CODE, myLoyaltyQR.getWidth(), myLoyaltyQR.getHeight());
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            myLoyaltyQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void generateMyBarcode() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            String toBarcode = num;
            BitMatrix bitMatrix = multiFormatWriter.encode(toBarcode, BarcodeFormat.CODE_39, myLoyaltyBarcode.getWidth(), myLoyaltyBarcode.getHeight());
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            myLoyaltyBarcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
