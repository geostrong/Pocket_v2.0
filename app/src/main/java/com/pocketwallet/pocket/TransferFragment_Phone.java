package com.pocketwallet.pocket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class TransferFragment_Phone extends Fragment {
    private final String GET_CHECKPHONENUMBER_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/exist/";
    private EditText phoneNumberInput;
    private EditText amountInput;
    private EditText messageInput;
    private Button continueButton;

    private String phoneNumber;
    private String amount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer_fragment__phone, container, false);
        phoneNumberInput = view.findViewById(R.id.numberRequest);
        amountInput = view.findViewById(R.id.amountRequest);
        messageInput = view.findViewById(R.id.customMessage);

        phoneNumberInput.addTextChangedListener(textWatcher);
        messageInput.addTextChangedListener(textWatcher);

        continueButton = view.findViewById(R.id.continueBtn);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfUserExists(phoneNumber);
            }
        });

        return view;
    }

    private void checkIfUserExists(String number) {
        boolean isExist = false;

        //API CALL TO CHECK IF USER EXIST
        String url = GET_CHECKPHONENUMBER_URL + number;

        if (isExist) {
            Intent intent = new Intent(getActivity(), TransferActivity_Phone_Confirmation.class);
            startActivity(intent);
        } else {
            //PROMPT THAT WALLET/USER NOT FOUND
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Wallet not found")
                    .setMessage("Pocket wallet associated with this phone number is not found. Please ensure you had entered the correct phone number")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            phoneNumber = phoneNumberInput.getText().toString().trim();
            amount = amountInput.getText().toString().trim();

            continueButton.setEnabled(!phoneNumber.isEmpty() && !amount.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
