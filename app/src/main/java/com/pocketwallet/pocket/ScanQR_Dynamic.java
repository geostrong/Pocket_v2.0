package com.pocketwallet.pocket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

public class ScanQR_Dynamic extends AppCompatActivity {
    Button payBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr_dynamic);

        payBtn = (Button)findViewById(R.id.payButtonDynamic);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to transaction result
                Intent newIntent = new Intent(ScanQR_Dynamic.this, TransactionResult.class);
                startActivity(newIntent);
            }
        });
    }
}
