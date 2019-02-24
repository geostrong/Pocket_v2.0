package com.pocketwallet.pocket;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class RequestActivity_QR extends AppCompatActivity {
    ImageView generatedQR;
    EditText amountInput;
    TextView requestedAmount;

    String amount;
    Bundle extras;
    String userId;

    SharedPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            amount = extras.getString("requestingAmount");
            System.out.println("Amount =" + amount);
        }
        userPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_request_qr);
        getSupportActionBar().setTitle("Request QR");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        generatedQR = (ImageView) findViewById(R.id.generatedQR);
        requestedAmount = findViewById(R.id.requestedAmountQR);
        requestedAmount.setText("$" + amount);
        /*
        generateQrBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    System.out.println(amountInput.getText().toString());
                    String amount = amountInput.getText().toString();
                    String toQR = "Dynamic|" + userId + "|" + amount + "|" + userPreferences.getString("user_name", "Name");;
                    System.out.println("TOQR: " + toQR);
                    try {
                        toQR = AESUtils.encrypt(toQR);
                        System.out.println("encrypted:" + toQR);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("MY QR SIZ 2 -> Width = " + generatedQR.getWidth() + " |  Height = " +  generatedQR.getHeight());
                    BitMatrix bitMatrix = multiFormatWriter.encode(toQR, BarcodeFormat.QR_CODE,generatedQR.getWidth(),generatedQR.getHeight());
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    generatedQR.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
        */
        generatedQR.post(new Runnable() {
            @Override
            public void run() {
                generateQR();
            }
        });
    }

    private void generateQR() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            String toQR = "Dynamic|" + userId + "|" + amount + "|" + userPreferences.getString("user_name", "Name");;
            System.out.println("TOQR: " + toQR);
            try {
                toQR = AESUtils.encrypt(toQR);
                System.out.println("encrypted:" + toQR);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("MY QR SIZ 2 -> Width = " + generatedQR.getWidth() + " |  Height = " +  generatedQR.getHeight());
            BitMatrix bitMatrix = multiFormatWriter.encode(toQR, BarcodeFormat.QR_CODE,generatedQR.getWidth(),generatedQR.getHeight());
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            generatedQR.setImageBitmap(bitmap);
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
