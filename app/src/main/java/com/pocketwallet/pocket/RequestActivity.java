package com.pocketwallet.pocket;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class RequestActivity extends AppCompatActivity {

    private String userId;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        getSupportActionBar().setTitle("Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        CardView nfcCard = findViewById(R.id.nfcCard);
        CardView qrCard = findViewById(R.id.qrCard);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null || !nfcAdapter.isEnabled()){
            //No NFC
            System.out.println("Device does not support NFC");
            nfcCard.setClickable(false);
            nfcCard.setEnabled(false);
            nfcCard.setCardBackgroundColor(Color.LTGRAY);
        }

        nfcCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nfcIntent = new Intent(RequestActivity.this, RequestActivity_NFC.class);
                nfcIntent.putExtra("userId",userId);
                startActivity(nfcIntent);
            }
        });


        qrCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrIntent = new Intent(RequestActivity.this, RequestActivity_QR.class);
                qrIntent.putExtra("userId",userId);
                startActivity(qrIntent);
            }
        });

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
