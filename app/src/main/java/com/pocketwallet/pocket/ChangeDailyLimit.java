package com.pocketwallet.pocket;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeDailyLimit extends AppCompatActivity {
    EditText dailyLimitInput;
    EditText passwordInput;
    String dailyLimit;
    String password;
    TextView currentDailyLimit;
    Button changeDailyLimitButton;
    String currentDLimit = "Hi change me ==========";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_daily_limit);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        dailyLimitInput = findViewById(R.id.dailyLimit);
        passwordInput = findViewById(R.id.password);
        currentDailyLimit = findViewById(R.id.currentDailyLimit);
        currentDailyLimit.setText("Current Daily Limit : $" + currentDLimit);

        dailyLimitInput.addTextChangedListener(textWatcher);
        dailyLimitInput.addTextChangedListener(textWatcher);

        changeDailyLimitButton = findViewById(R.id.changeDailyLimit);
        changeDailyLimitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ChangeDailyLimit.this, ResultActivity.class);
                Bundle b = new Bundle();
                b.putString("title", "Change Daily Limit");
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            dailyLimit = dailyLimitInput.getText().toString().trim();
            password = passwordInput.getText().toString().trim();

            changeDailyLimitButton.setEnabled(!dailyLimit.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
