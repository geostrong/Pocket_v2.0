package com.pocketwallet.pocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RequestActivity_NFC_Ready extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request__nfc__ready);

        /* to go to request
        Intent newIntent = new Intent(RequestActivity_NFC_Ready.this, ResultActivity.class);
        startActivity(newIntent);
        */
    }
}
