package com.pocketwallet.pocket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ExitActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean exit = extras.getBoolean("EXIT");
            if(exit == true){
                finishAffinity();
            }
        }
    }
}
