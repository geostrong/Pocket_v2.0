package com.pocketwallet.pocket;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        CardView scanCard = findViewById(R.id.scanCard);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null || !nfcAdapter.isEnabled()){
            //No NFC
            System.out.println("Device does not support NFC");
            nfcCard.setClickable(false);
            nfcCard.setEnabled(false);
            nfcCard.setAlpha(0.5f);
        }

        nfcCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestingAmount != null) {
                    Intent nfcIntent = new Intent(RequestActivity.this, RequestActivity_NFC_Ready.class);
                    nfcIntent.putExtra("userId", userId);
                    nfcIntent.putExtra("requestingAmount", requestingAmount);
                    System.out.println("Requesting Amount =" + requestingAmount);
                    System.out.println("User ID =" + userId);
                    startActivityForResult(nfcIntent, 1);
                }else{
                    showEnterAmountError();
                }
            }
        });


        qrCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestingAmount != null) {
                    Intent qrIntent = new Intent(RequestActivity.this, RequestActivity_QR.class);
                    qrIntent.putExtra("userId", userId);
                    qrIntent.putExtra("requestingAmount", requestingAmount);
                    System.out.println("Requesting Amount =" + requestingAmount);
                    System.out.println("User ID =" + userId);
                    startActivity(qrIntent);
                }else{
                    showEnterAmountError();
                }
            }
        });

        scanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestingAmount != null) {
                    Intent scanIntent = new Intent(RequestActivity.this, ScanQRActivity.class);
                    scanIntent.putExtra("userId", userId);
                    scanIntent.putExtra("requestingAmount", requestingAmount);
                    scanIntent.putExtra("requestActivity", "requestActivity");
                    System.out.println("Requesting Amount =" + requestingAmount);
                    System.out.println("User ID =" + userId);
                    startActivity(scanIntent);
                }else{
                    showEnterAmountError();
                }
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
                if(requestingAmount != null && requestingAmount.length() > 1) {
                    if (requestingAmount.charAt(requestingAmount.length() - 1) == '.') {
                        requestingAmount = requestingAmount.substring(0, requestingAmount.length() - 1);
                    }
                    if(requestingAmount.charAt(0) == '.'){
                        requestingAmount = null;
                    }
                }
                if(requestingAmount != null){
                    if(requestingAmount.equals("") || requestingAmount.equals(".")){
                        requestingAmount = null;
                    }
                }
                if(requestingAmount == null){
                    requestingAmountView.setText("$" + "0");
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    dialog.cancel();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(RequestActivity.this);
                    builder.setMessage("Please enter a valid amount!").setTitle("Invalid Amount")
                            .setPositiveButton("Ok", dialogClickListener).show();
                }else {
                    requestingAmountView.setText("$" + requestingAmount);
                    Toast.makeText(getApplicationContext(), "Requesting amount updated", Toast.LENGTH_SHORT).show();
                }
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

    public void showEnterAmountError(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.cancel();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(RequestActivity.this);
        builder.setMessage("Please enter an amount to request!").setTitle("Invalid Amount")
                .setPositiveButton("Ok", dialogClickListener).show();
    }
}
