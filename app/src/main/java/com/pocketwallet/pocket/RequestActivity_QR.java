package com.pocketwallet.pocket;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    ImageView generatedQR;
    EditText amountInput;
    Button generateQrBtn;
    Button generateBtn;

    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_qr);

        generatedQR = (ImageView) findViewById(R.id.generatedQR);
        amountInput = (EditText) findViewById(R.id.amountRequestQR);
        generateQrBtn = (Button) findViewById(R.id.generateButton);
        amountInput.addTextChangedListener(textWatcher);

        generateQrBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    System.out.println(amountInput.getText().toString());
                    String amount = amountInput.getText().toString();
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

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            amount = amountInput.getText().toString().trim();
            generateQrBtn.setEnabled(!amount.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
