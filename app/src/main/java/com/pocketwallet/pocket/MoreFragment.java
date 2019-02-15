package com.pocketwallet.pocket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MoreFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, null);
        ((MainActivity)getActivity()).getSupportActionBar().show();
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Settings");
        ((MainActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        ((MainActivity)getActivity()).getSupportActionBar().setElevation(4);

        Button changepasswordBtn = (Button) view.findViewById(R.id.changePassword);
        changepasswordBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(newIntent);
            }
        });

        Button logoutBtn = (Button) view.findViewById(R.id.logoutButton);
        logoutBtn.setOnClickListener(new  View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                cleanData();
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void cleanData () {
        DatabaseHelper db = new DatabaseHelper(getActivity());
        db.cleanDB();

        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.putString("PhoneNumber", "Phone Number");
        editor.putString("user_name", "Name");
        editor.commit();
    }
}
