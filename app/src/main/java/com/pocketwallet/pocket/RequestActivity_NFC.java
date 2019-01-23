package com.pocketwallet.pocket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RequestActivity_NFC extends AppCompatActivity {
    Button confirmButton;
    EditText amountInput;
    String amount;
    ImageView nfcImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_nfc);

        amountInput = (EditText) findViewById(R.id.amountTxt);
        confirmButton = (Button) findViewById(R.id.confirmButton);
        nfcImage = (ImageView) findViewById(R.id.nfcImage);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(RequestActivity_NFC.this, RequestActivity_NFC_Ready.class);
                startActivity(newIntent);
            }
        });

    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            amount = amountInput.getText().toString().trim();
            confirmButton.setEnabled(!amount.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
