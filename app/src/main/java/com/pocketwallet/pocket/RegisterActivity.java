package com.pocketwallet.pocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {
    private String name;
    private String phoneNumber;
    private String password;
    Button registerBtn;
    EditText registerNameInput;
    EditText registerPasswordInput;
    EditText registerPhoneInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //SETUP BUTTONS
        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerNameInput = (EditText) findViewById(R.id.registerName);
        registerPasswordInput = (EditText) findViewById(R.id.registerPassword);
        registerPhoneInput = (EditText) findViewById(R.id.registerPhone);


        registerNameInput.addTextChangedListener(registerTextWatcher);
        registerPasswordInput.addTextChangedListener(registerTextWatcher);
        registerPhoneInput.addTextChangedListener(registerTextWatcher);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SIGN UP HERE

                //Once done exit back to login
                finish();
            }
        });
    }

    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            phoneNumber = registerPhoneInput.getText().toString().trim();
            password = registerPasswordInput.getText().toString().trim();
            name = registerNameInput.getText().toString().trim();

            registerBtn.setEnabled(!phoneNumber.isEmpty() && !password.isEmpty() && !name.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
