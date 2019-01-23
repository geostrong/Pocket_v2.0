package com.pocketwallet.pocket;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ScanQRActivity_Dynamic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr_dynamic);

        Button payBtn = (Button)findViewById(R.id.payButtonDynamic);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to transaction result
                Intent newIntent = new Intent(ScanQRActivity_Dynamic.this, ResultActivity.class);
                startActivity(newIntent);
            }
        });

    }
}
