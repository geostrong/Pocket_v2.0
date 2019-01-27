package com.pocketwallet.pocket;

import android.app.Dialog;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {
    private String name;
    private String phoneNumber;
    private String password;
    Button registerBtn;
    EditText registerNameInput;
    EditText registerPasswordInput;
    EditText registerPhoneInput;
    private TextView login;

    Dialog popupBox;
    private String result;
    private String REGISTER_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //SETUP BUTTONS
        login = (TextView)findViewById(R.id.alreadyHaveButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerNameInput = (EditText) findViewById(R.id.registerName);
        registerPasswordInput = (EditText) findViewById(R.id.registerPassword);
        registerPhoneInput = (EditText) findViewById(R.id.registerPhone);

        registerNameInput.addTextChangedListener(registerTextWatcher);
        registerPasswordInput.addTextChangedListener(registerTextWatcher);
        registerPhoneInput.addTextChangedListener(registerTextWatcher);

        popupBox = new Dialog(this);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SIGN UP HERE
                System.out.println("RegisterPhone: " + phoneNumber);
                System.out.println("RegisterName: " + name);
                System.out.println("RegisterPassword: " + password);
                registerAccount();
                //Once done exit back to login
                //finish();
            }
        });
    }

    public void registerAccount(){
        System.out.println("Sending Registration request To Server...");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("name", name);
            jsonBody.put("password", password);
            jsonBody.put("phoneNumber", phoneNumber);
            System.out.println("RegisterDetails: " + jsonBody);
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, REGISTER_URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        System.out.println("Response: " + response.toString());
                        String tempResult = response.getString("result");
                        if(tempResult.equals("success")) {
                            result = "Success";
                            showPopup();
                            //mTextView.setText("Transaction is successful!"
                            //        + "\nTransaction Number:" + transactionNumber);
                        }else{
                            result = "Fail";
                            showPopup();
                        }
                    }catch(JSONException e){

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int status = error.networkResponse.statusCode;
                    switch (status){
                        case 417:
                            result = "fail";
                            showPopup();
                            break;
                    }
                    //onBackPressed();
                }
            });
            requestQueue.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(popupBox != null && popupBox.isShowing()){
            popupBox.dismiss();
        }
    }

    private void showPopup(){
        Button closeButton;
        TextView mTextView;
        popupBox.setContentView(R.layout.registerpopupbox);
        closeButton =(Button) popupBox.findViewById(R.id.closeButton);
        mTextView = (TextView) popupBox.findViewById(R.id.mTextView);
        if(result.equals("Success")) {
            mTextView.setText("\t\t\t\t\tAccount successfully registered!\n\n"
                    + "\t\t\t\t\tRegistered Name: " + name + "\n"
                    + "\t\t\t\t\tPhone Number: " + phoneNumber);
        }else{
            mTextView.setText("\t\t\t\t\tFailed to register account\n\n"
                            + "\t\t\t\t\tThe number has already\n\t\t\t\t\t been registered.");
        }
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SIGN UP HERE
                //Once done exit back to login
                popupBox.dismiss();
                finish();
            }
        });

        popupBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupBox.show();
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
