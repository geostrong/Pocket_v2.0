package com.pocketwallet.pocket_v2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);

        //<--Setup buttons-->
        Button quickQrBtn = (Button) view.findViewById(R.id.quickQRBtn);
        quickQrBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), QuickQrActivity.class);
                startActivity(newIntent);
            }
        });

        Button scanQrBtn = (Button) view.findViewById(R.id.scanQRBtn);
        scanQrBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), ScanQRActivity.class);
                startActivity(newIntent);
            }
        });

        Button requestBtn = (Button) view.findViewById(R.id.reqBtn);
        requestBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), RequestActivity.class);
                startActivity(newIntent);
            }
        });
        //<--End of setup buttons-->

        return view;

    }

}
