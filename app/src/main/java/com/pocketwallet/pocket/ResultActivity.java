package com.pocketwallet.pocket;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        TextView amountText;
        TextView amountTitle;
        TextView transIdTitle;
        TextView contractNameCreate;

        String result;
        String amount = "-";
        String transactionNumber = "-";
        String sentTo = "";
        String mode = "0";
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
        involvedName = findViewById(R.id.contractNameCreate);
        amountText = findViewById(R.id.amountInvolved);
        contractNameCreate = findViewById(R.id.contractNameCreate);
        Button returnBtn = findViewById(R.id.returnButton);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Change toolbar title here
        toolbarTitle = info.getString("title");

        if (toolbarTitle.equalsIgnoreCase("transaction")) {
            result = info.getString("results");
            if(result.equalsIgnoreCase("Success")){
                transactionNumber = info.getString("transactionNumber");
                amount = info.getString("amount");
                sentTo = info.getString("to");
                mode = info.getString("mode");
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
            resultTitleText = "Transaction successful!";
            if(result.equalsIgnoreCase("failed")){
                resultTitleText = "Transaction failed";
                fromTo.setVisibility(View.GONE);
                //sentTo = "Payee's account do not have sufficient balance to make payment";
                amountTitle.setVisibility(View.GONE);
                transIdTitle.setVisibility(View.GONE);
                amountText.setVisibility(View.GONE);
                contractNameCreate.setVisibility(View.GONE);
            }else if(result.equalsIgnoreCase("phonePaymentFailed")){
                resultTitleText = "Transaction failed";
                fromTo.setVisibility(View.GONE);
                sentTo = "Your account do not have sufficient balance to make payment";
                amountTitle.setVisibility(View.GONE);
                transIdTitle.setVisibility(View.GONE);
                amountText.setVisibility(View.GONE);
                contractNameCreate.setVisibility(View.GONE);
            }
        } else if (toolbarTitle.equalsIgnoreCase("top up")) {
            resultTitleText = "Top up successful!";
        } else if (toolbarTitle.equalsIgnoreCase("change password")) {
            resultTitleText = "Password changed successful!";
        }
        resultTitle.setText(resultTitleText);

        //Change from/to here
        if (mode.equals("0")){
            fromTo.setText("To");
        } else {
            fromTo.setText("From");
        }

        //Change name here
        involvedName.setText(sentTo);

        //Change amount here
        amountText.setText("$" +amount);

        //Change transaction id here
        transactionID.setText(transactionNumber);

        if (toolbarTitle.equalsIgnoreCase("top up") || toolbarTitle.equalsIgnoreCase("change password")) {
            involvedName.setVisibility(View.INVISIBLE);
            amountText.setVisibility(View.INVISIBLE);
            transactionID.setVisibility(View.INVISIBLE);
            fromTo.setVisibility(View.INVISIBLE);
            transIdTitle.setVisibility(View.INVISIBLE);
            amountTitle.setVisibility(View.INVISIBLE);
        }
    }
}
