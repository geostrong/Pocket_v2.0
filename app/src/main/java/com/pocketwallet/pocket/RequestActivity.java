package com.pocketwallet.pocket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class RequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        CardView nfcCard = (CardView) findViewById(R.id.nfcCard);

        nfcCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nfcIntent = new Intent(RequestActivity.this, RequestActivity_NFC.class);
                startActivity(nfcIntent);
            }
        });
    }
}
