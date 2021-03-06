package com.pocketwallet.pocket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

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

    //Session Token
    private String sessionToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer_fragment__phone, container, false);
        phoneNumberInput = view.findViewById(R.id.numberRequest);
        amountInput = view.findViewById(R.id.amountRequest);
        messageInput = view.findViewById(R.id.customMessage);

        phoneNumberInput.addTextChangedListener(textWatcher);
        amountInput.addTextChangedListener(textWatcher);

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
        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        sessionToken = userPreferences.getString("sessionToken", "");
        return view;
    }

    private void checkIfUserExists() {
        //API CALL TO CHECK IF USER EXIST
        String url = GET_CHECKPHONENUMBER_URL + phoneNumber;// Instantiate the cache

        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        JSONObject jsonBody = new JSONObject();
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.GET, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String result = response.getString("exist");
                    if(result.equals("true")){
                        String targetName = response.getString("name");
                        Intent intent = new Intent(getActivity(), TransferActivity_Phone_Confirmation.class);
                        intent.putExtra("userId",userId);
                        intent.putExtra("targetPhoneNumber",phoneNumber);
                        intent.putExtra("sendAmount",amount);
                        intent.putExtra("targetName", targetName);
                        requestQueue.stop();
                        startActivityForResult(intent, 1);
                    }else {
                        //PROMPT THAT WALLET/USER NOT FOUND
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Wallet not found")
                                .setMessage("Pocket wallet associated with this phone number is not found. Please ensure you had entered the correct phone number")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestQueue.stop();
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //getActivity().onBackPressed();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + sessionToken);
                System.out.println("Header: " + headers.values());
                return headers;
            }
        };
        requestQueue.add(jsonObject);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().finish();
            }
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
