package com.pocketwallet.pocket;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
        String toolbarTitle;
        String resultTitleText;
        int imageID;
        ImageView image;
        TextView resultTitle;
        TextView transactionID;
        TextView fromTo;
        TextView involvedName;
        TextView amountInvolved;
        TextView amountTitle;
        TextView transIdTitle;

        String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        Bundle info = getIntent().getExtras();

        image = findViewById(R.id.resultImage);
        resultTitle = findViewById(R.id.result);
        transactionID = findViewById(R.id.transID);
        fromTo = findViewById(R.id.fromTo);
        amountTitle = findViewById(R.id.amountTitle);
        transIdTitle = findViewById(R.id.transIdTitle);
        involvedName = findViewById(R.id.nameInvolved);
        amountInvolved = findViewById(R.id.amountInvolved);
        Button returnBtn = findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Change toolbar title here
        toolbarTitle = info.getString("title");

        String results = "-";
        String transactionNumber = "-";
        if (toolbarTitle.equalsIgnoreCase("transaction")){
            results = info.getString("results");
            if(results.equalsIgnoreCase("Success")) {
                transactionNumber = info.getString("transactionNumber");
                amount = info.getString("amount");
            }
        }

        getSupportActionBar().setTitle(toolbarTitle);
        //Change image here
        if (toolbarTitle.equalsIgnoreCase("transaction")) {
            imageID = R.drawable.img_transaction;
        } else if (toolbarTitle.equalsIgnoreCase("top up")){
            imageID = R.drawable.img_transfer;
        } else if (toolbarTitle.equalsIgnoreCase("change password")) {
            imageID = R.drawable.img_settings_success;
        }
        image.setImageResource(imageID);

        //Change result title here
        if (toolbarTitle.equalsIgnoreCase("transaction")) {
            if(results.equalsIgnoreCase("Success")){
                resultTitleText = "Transaction successful!";
            }else{
                resultTitleText = "Transaction has failed";
            }
        } else if (toolbarTitle.equalsIgnoreCase("top up")) {
            resultTitleText = "Top up successful!";
        } else if (toolbarTitle.equalsIgnoreCase("change password")) {
            resultTitleText = "Password changed succesful!";
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
        amountInvolved.setText("$" + amount);

        //Change transaction id here
        transactionID.setText(transactionNumber);

        if (toolbarTitle.equalsIgnoreCase("top up") || toolbarTitle.equalsIgnoreCase("change password")) {
            involvedName.setVisibility(View.GONE);
            amountInvolved.setVisibility(View.GONE);
            transactionID.setVisibility(View.GONE);
            fromTo.setVisibility(View.GONE);
            transIdTitle.setVisibility(View.GONE);
            amountTitle.setVisibility(View.GONE);
        }
    }
}
