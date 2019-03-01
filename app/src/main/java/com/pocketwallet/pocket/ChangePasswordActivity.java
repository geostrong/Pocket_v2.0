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

public class ChangePasswordActivity extends AppCompatActivity {
    EditText currentPasswordInput;
    EditText newPasswordInput;
    EditText confirmNewPasswordInput;
    String currentPassword;
    String newPassword;
    String confirmNewPassword;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        currentPasswordInput = findViewById(R.id.currentPassword);
        newPasswordInput = findViewById(R.id.newPassword);
        confirmNewPasswordInput = findViewById(R.id.confirmNewPassword);

        currentPasswordInput.addTextChangedListener(textWatcher);
        newPasswordInput.addTextChangedListener(textWatcher);
        confirmNewPasswordInput.addTextChangedListener(textWatcher);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ChangePasswordActivity.this, ResultActivity.class);
                Bundle b = new Bundle();
                b.putString("title", "Change Password");
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
            currentPassword = currentPasswordInput.getText().toString().trim();
            newPassword = newPasswordInput.getText().toString().trim();
            confirmNewPassword = confirmNewPasswordInput.getText().toString().trim();
            saveButton.setEnabled(!currentPassword.isEmpty() && !newPassword.isEmpty() && !confirmNewPassword.isEmpty());
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
