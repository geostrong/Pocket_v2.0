package com.pocketwallet.pocket;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MeFragment extends Fragment {
    private String userId;
    private SharedPreferences userPreferences;
    private TextView phoneNumber;
    private TextView profileName;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        ((MainActivity)getActivity()).getSupportActionBar().show();
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Me");
        ((MainActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        ((MainActivity)getActivity()).getSupportActionBar().setElevation(4);

        Bundle extras = new Bundle();
        extras = getArguments();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        profileImage = (ImageView) view.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        Button contractBtn = view.findViewById(R.id.contractBtn);
        contractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), ContractActivity.class);
                newIntent.putExtra("userId",userId);
                startActivity(newIntent);
            }
        });

        Button logsBtn = (Button) view.findViewById(R.id.logsBtn);
        logsBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent testIntent = new Intent(getActivity(), TransactionLogsActivity.class);
                testIntent.putExtra("userId",userId);
                startActivity(testIntent);
            }
        });

        Button topUpBtn = (Button) view.findViewById(R.id.topUpBtn);
        topUpBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent dynamicIntent = new Intent(getActivity(), TopUpActivity.class);
                dynamicIntent.putExtra("userId",userId);
                startActivity(dynamicIntent);
            }
        });

        Button loyaltyBtn = (Button) view.findViewById(R.id.loyaltyCardsBtn);
        loyaltyBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent dynamicIntent = new Intent(getActivity(), LoyaltyActivity.class);
                dynamicIntent.putExtra("userId",userId);
                startActivity(dynamicIntent);
            }
        });


        phoneNumber = view.findViewById(R.id.phoneNumber);
        profileName = view.findViewById(R.id.profileName);

        userPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        phoneNumber.setText(userPreferences.getString("PhoneNumber", "Phone Number"));
        profileName.setText(userPreferences.getString("user_name", "Name"));

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10 && resultCode == Activity.RESULT_OK) {

        }
    }
}