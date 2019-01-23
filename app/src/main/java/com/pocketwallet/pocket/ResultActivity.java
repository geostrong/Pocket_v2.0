package com.pocketwallet.pocket;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResultActivity extends AppCompatActivity {
        String toolbarTitle;
        String resultTitleText;
        int imageID;
        ImageView image;
        TextView resultTitle;
        TextView transactionID;
        TextView fromTo;
        TextView involvedName;
        TextView amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        image = findViewById(R.id.resultImage);
        resultTitle = findViewById(R.id.result);
        transactionID = findViewById(R.id.transID);
        fromTo = findViewById(R.id.fromTo);
        involvedName = findViewById(R.id.nameInvolved);
        amount = findViewById(R.id.amountInvolved);

        Button returnBtn = findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Change toolbar title here
        if (true) {
            toolbarTitle = "Scan QR";
        }

        getSupportActionBar().setTitle(toolbarTitle);
        //Change image here
        if (true) {
            imageID = R.drawable.img_transaction;
        } else {
            imageID = R.drawable.img_transfer;
        }
        image.setImageResource(imageID);

        //Change result title here
        if (true) {
            resultTitleText = "Transaction successful!";
        }
        resultTitle.setText(resultTitleText);


        //Change from/to here
        if (true){
            fromTo.setText("To");
        } else {
            fromTo.setText("From");
        }

        //Change name here
        involvedName.setText("-");

        //Change amount here
        amount.setText("$ -");

        //Change transaction id here
        transactionID.setText("-");
    }
}
