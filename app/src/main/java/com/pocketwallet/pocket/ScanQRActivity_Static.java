package com.pocketwallet.pocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class ScanQRActivity_Static extends AppCompatActivity {
    EditText amountInput;
    Button payBtn;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrstatic);
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            amount = amountInput.getText().toString().trim();
            payBtn.setEnabled(!amount.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
