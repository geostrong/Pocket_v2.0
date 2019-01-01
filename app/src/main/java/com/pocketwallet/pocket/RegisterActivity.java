package com.pocketwallet.pocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //SETUP BUTTONS
        Button registerBtn = (Button) findViewById(R.id.registerBtn);
        EditText registerName = (EditText) findViewById(R.id.registerName);
        EditText registerPassword = (EditText) findViewById(R.id.registerPassword);
        EditText registerPhone = (EditText) findViewById(R.id.registerPhone);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SIGN UP HERE

                //Once done exit back to login
                finish();
            }
        });
    }
}
