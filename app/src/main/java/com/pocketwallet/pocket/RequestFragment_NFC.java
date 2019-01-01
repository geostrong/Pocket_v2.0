package com.pocketwallet.pocket;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class  RequestFragment_NFC extends Fragment {
    @Nullable
    Button confirmButton;
    TextView mTextView;
    TextView amountText;
    private String userId;
    private String payeeUserId;
    private String amount;
    private String authCode;
    private String urlPayment = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/payment/";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_fragment__nfc, container, false);

        amountText = (TextView)view.findViewById(R.id.amountTxt);
        amountText.setSelectAllOnFocus(true);
        confirmButton = (Button)view.findViewById(R.id.confirmButton);
        Bundle extras = new Bundle();
        extras = getArguments();
        if (extras != null) {
            userId = extras.getString("userId");
        }
        return view;
    }



}
