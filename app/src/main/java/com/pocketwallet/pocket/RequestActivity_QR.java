package com.pocketwallet.pocket;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class RequestActivity_QR extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request__qr);

        //<--Setup image view-->
        final ImageView generatedQR = (ImageView) findViewById(R.id.generatedQR);
        //<--End-->

        final EditText amountEntered = (EditText) findViewById(R.id.amountRequestQR);

        //<--Setup buttons-->
        final Button generateQrBtn = (Button) findViewById(R.id.generateButton);
        generateQrBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    System.out.println(amountEntered.getText().toString());
                    String amount = amountEntered.getText().toString();
                    //String toQR = userId + "|" + amount;
                    String toQR = amount;
                    System.out.println("TOQR: " + toQR);
                    BitMatrix bitMatrix = multiFormatWriter.encode(toQR, BarcodeFormat.QR_CODE,generatedQR.getWidth(),generatedQR.getHeight());
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    generatedQR.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
