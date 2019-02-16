package com.pocketwallet.pocket;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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


public class  RequestFragment_NFC extends Fragment {
    @Nullable
    Button confirmButton;
    TextView mTextView;
    TextView amountText;
    ImageView nfcImage;
    private String userId;
    private String payeeUserId;
    private String amount;
    private String authCode;
    private String urlPayment = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/payment/";

    //Session Token
    private String sessionToken;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_fragment__nfc, container, false);

        mTextView = (TextView)view.findViewById(R.id.mTextView);
        amountText = (TextView)view.findViewById(R.id.amountTxt);
        amountText.setSelectAllOnFocus(true);
        confirmButton = (Button)view.findViewById(R.id.confirmButton);
        nfcImage = (ImageView)view.findViewById(R.id.nfcImage);

        Bundle extras = new Bundle();
        extras = getArguments();
        if (extras != null) {
            userId = extras.getString("userId");
        }
        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        sessionToken = userPreferences.getString("sessionToken", "");

        return view;
    }

    public void processNFC(String merchantId, String payeeUserId,String authCode){
        System.out.println("Processing NFC..." + payeeUserId + "," + authCode);
        this.userId = merchantId;
        this.payeeUserId = payeeUserId;
        this.authCode = authCode;
        amount = amountText.getText().toString();
        Payment();
    }

    private void Payment() {
        mTextView.setText("Sending payment to server...");
        System.out.println("Sending Payment To Server...");
        System.out.println("Payee_id: " + payeeUserId);
        System.out.println("Merchant_id: " + userId);
        System.out.println("Auth_code: " + authCode);
        System.out.println("Amount: " + amount);
        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("payee_id", payeeUserId);
            jsonBody.put("merchant_id", userId);
            jsonBody.put("amount", amount);
            //jsonBody.put("auth_code", authCode);
            System.out.println("TEST PRINTING: " + jsonBody);
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, urlPayment, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String transactionNumber = response.getString("Transaction Number");
                        String result = response.getString("Result");
                        if(result.equals("Success")) {
                            nfcImage.setVisibility(View.GONE);
                            mTextView.setText("Transaction is successful!"
                                    + "\nTransaction Number:" + transactionNumber);
                        }
                    }catch(JSONException e){

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getActivity().onBackPressed();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + sessionToken);//put your token here
                    System.out.println("Header: " + headers.values());
                    return headers;
                }
            };
            requestQueue.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void UpdateSharedPreference(String key, String value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
