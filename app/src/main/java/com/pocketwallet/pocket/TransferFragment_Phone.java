package com.pocketwallet.pocket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TransferFragment_Phone extends Fragment {
    private final String GET_CHECKPHONENUMBER_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/exist/";
    private EditText phoneNumberInput;
    private EditText amountInput;
    private EditText messageInput;
    private Button continueButton;

    private String phoneNumber;
    private String amount;

    Bundle extras;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer_fragment__phone, container, false);
        phoneNumberInput = view.findViewById(R.id.numberRequest);
        amountInput = view.findViewById(R.id.amountRequest);
        messageInput = view.findViewById(R.id.customMessage);

        phoneNumberInput.addTextChangedListener(textWatcher);
        messageInput.addTextChangedListener(textWatcher);

        continueButton = view.findViewById(R.id.continueBtn);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfUserExists();
            }
        });

        extras = getArguments();
        //GET INFORMATION FROM BUNDLE
        if (extras != null) {
            userId = extras.getString("userId");
        }
        System.out.println("UserID: " + userId);
        return view;
    }

    private void checkIfUserExists() {
        //API CALL TO CHECK IF USER EXIST
        String url = GET_CHECKPHONENUMBER_URL + phoneNumber;
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("phoneNumber", phoneNumber);
            System.out.println("phoneNumber: " + jsonBody);

            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.GET, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("exist");
                        String targetName = response.getString("name");
                        System.out.println("Response: " + response);
                        if(result.equals("true")){
                            Intent intent = new Intent(getActivity(), TransferActivity_Phone_Confirmation.class);
                            intent.putExtra("userId",userId);
                            intent.putExtra("targetPhoneNumber",phoneNumber);
                            intent.putExtra("sendAmount",amount);
                            intent.putExtra("targetName", targetName);
                            startActivity(intent);
                        }else {
                            //PROMPT THAT WALLET/USER NOT FOUND
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Wallet not found")
                                    .setMessage("Pocket wallet associated with this phone number is not found. Please ensure you had entered the correct phone number")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }catch(JSONException e){

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //getActivity().onBackPressed();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    //headers.put("Authorization", "Basic " + "c2FnYXJAa2FydHBheS5jb206cnMwM2UxQUp5RnQzNkQ5NDBxbjNmUDgzNVE3STAyNzI=");//put your token here
                    return headers;
                }
            };

            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            phoneNumber = phoneNumberInput.getText().toString().trim();
            amount = amountInput.getText().toString().trim();
            continueButton.setEnabled(!phoneNumber.isEmpty() && !amount.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}
