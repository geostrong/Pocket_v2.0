package com.pocketwallet.pocket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoyaltyActivity_Create extends AppCompatActivity {
    Button addLoyaltyCardButton;
    EditText companyName;
    EditText num;
    EditText expiry;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty__create);

        getSupportActionBar().setTitle("Add New Loyalty Card");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        companyName = findViewById(R.id.companyName);
        num = findViewById(R.id.num);
        expiry = findViewById(R.id.expiry);

        db = new DatabaseHelper(this);

        //RequestContractButton
        addLoyaltyCardButton = findViewById(R.id.confirmLoyaltyCardBtn);
        addLoyaltyCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addLoyalty(companyName.getText().toString(),num.getText().toString(),expiry.getText().toString());
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
