package com.pocketwallet.pocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TransferActivity_Phone_Confirmation extends AppCompatActivity {
    private TextView amount;
    private TextView name;
    private TextView phoneNumber;
    private Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer__phone_confirmation);

        //GET INFORMATION FROM BUNDLE

        phoneNumber = findViewById(R.id.involvedNumber2);
        name = findViewById(R.id.involvedName2);
        amount = findViewById(R.id.amountInvolved);

        name.setText("Test User2");
        phoneNumber.setText("88881111");
        amount.setText("$500");

        payButton = findViewById(R.id.payButtonPhone);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PROCESS PAYMENT
            }
        });

    }
}
