package com.pocketwallet.pocket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RequestActivity extends AppCompatActivity {

    private String userId;
    private TextView requestingAmountView;
    private Button updateButton;
    private EditText requestingInput;
    private String requestingAmount;
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
                Intent nfcIntent = new Intent(RequestActivity.this, RequestActivity_NFC_Ready.class);
                nfcIntent.putExtra("userId",userId);
                nfcIntent.putExtra("requestingAmount",requestingAmount);
                System.out.println("Requesting Amount =" + requestingAmount);
                System.out.println("User ID =" + userId);
                startActivityForResult(nfcIntent, 1);
            }
        });


        qrCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrIntent = new Intent(RequestActivity.this, RequestActivity_QR.class);
                qrIntent.putExtra("userId",userId);
                qrIntent.putExtra("requestingAmount",requestingAmount);
                System.out.println("Requesting Amount =" + requestingAmount);
                System.out.println("User ID =" + userId);
                startActivity(qrIntent);
            }
        });

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        requestingAmountView = findViewById(R.id.requestingAmount);
        requestingInput = findViewById(R.id.requestingInput);
        updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestingAmount = requestingInput.getText().toString();
                requestingAmountView.setText("$" + requestingAmount);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
