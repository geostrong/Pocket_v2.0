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

public class ChangeTransactionLimit extends AppCompatActivity {
    EditText transactionLimitInput;
    EditText passwordInput;
    String transactionLimit;
    String password;
    TextView currentTransactionLimit;
    Button changeTransactionLimitButton;
    String currentTLimit = "Hi change me ==========";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_transaction_limit);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        transactionLimitInput = findViewById(R.id.transactionLimit);
        passwordInput = findViewById(R.id.password);
        currentTransactionLimit = findViewById(R.id.currentTransactionLimit);
        currentTransactionLimit.setText("Current Daily Limit : $" + currentTLimit);

        transactionLimitInput.addTextChangedListener(textWatcher);
        transactionLimitInput.addTextChangedListener(textWatcher);

        changeTransactionLimitButton = findViewById(R.id.changeTransactionLimit);
        changeTransactionLimitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ChangeTransactionLimit.this, ResultActivity.class);
                Bundle b = new Bundle();
                b.putString("title", "Change Transaction Limit");
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
            transactionLimit = transactionLimitInput.getText().toString().trim();
            password = passwordInput.getText().toString().trim();

            changeTransactionLimitButton.setEnabled(!transactionLimit.isEmpty() && !password.isEmpty());
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
