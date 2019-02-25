package com.pocketwallet.pocket;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContractActivity_Details extends AppCompatActivity {

    private RecyclerView contractListView;
    private RecyclerView.Adapter adapter;

    private ArrayList<ListContract> listContracts;

    private Bundle extras;
    private String userId;
    private String name,phoneNumber;
    private String contractId;
    private int position;
    private String balance;
    private String penaltyAmount;

    private static final int TopUpCode = 1;

    Button acceptButton;
    Button declineButton;
    Button terminateButton;

    private SharedPreferences userPreferences;

    String TERMINATECONTRACT_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/contract/terminate";
    String ACKNOWLEDGE_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/contract/ack";
    private String GETBALANCE_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/";

    //Session Token
    private String sessionToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract__details);
        getSupportActionBar().setTitle("Contract Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        Intent intent = getIntent();
        extras = intent.getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            this.listContracts = (ArrayList<ListContract>) intent.getSerializableExtra(
                    "listContracts");
            position = extras.getInt("position");
            //SET URL
            if(!GETBALANCE_URL.contains("/balance")) {
                GETBALANCE_URL = GETBALANCE_URL + userId + "/balance";
            }

        }
        //Get user's name from shared preferences
        userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        name = userPreferences.getString("user_name", "Name");
        phoneNumber = userPreferences.getString("PhoneNumber", "PhoneNumber");

        sessionToken = userPreferences.getString("sessionToken", "");

        acceptButton = findViewById(R.id.acceptButton);
        declineButton = findViewById(R.id.declineButton);
        terminateButton = findViewById(R.id.terminateButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acknowledgeContract("1");
            }
        });
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acknowledgeContract("0");
            }
        });
        terminateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Float.parseFloat(balance) < Float.parseFloat(penaltyAmount)) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent intent = new Intent(getApplicationContext(), TopUpActivity.class);
                                        intent.putExtra("userId", userId);
                                        startActivityForResult(intent, TopUpCode);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:

                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(ContractActivity_Details.this);
                        builder.setMessage("Your wallet does not have enough balance to pay. Press 'Top Up' to go to the top up page.").setTitle("Insufficient Balance")
                                .setPositiveButton("Top Up", dialogClickListener)
                                .setNegativeButton("Cancel", dialogClickListener).show();
                    } else {
                        terminateContract();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });


        if(userId.equals(listContracts.get(position).getUser2ID())){
            listContracts.get(position).setPayeeName(name);
            listContracts.get(position).setReceiverName(listContracts.get(position).getUser1ID());
            System.out.println("TestName: " + name);
            System.out.println("TestName2: " + listContracts.get(position).getUser1ID());
            if(!listContracts.get(position).getContractStatus().equals(("0"))){
                acceptButton.setVisibility(View.GONE);
                declineButton.setVisibility(View.GONE);
            }
            if(listContracts.get(position).getContractStatus().equals(("1"))){
                terminateButton.setVisibility(View.VISIBLE);
            }

            if(listContracts.get(position).getContractStatus().equals(("2"))){
                terminateButton.setVisibility(View.VISIBLE);
            }
        }else{
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
            System.out.println("TestName: " + name);
            listContracts.get(position).setReceiverName(name);
            listContracts.get(position).setPayeeName(listContracts.get(position).getUser2ID());
        }
        contractId = listContracts.get(position).getContractID();

        //createAdapterView();
        updateBalance();
        GetViewItems();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void createAdapterView(){
        adapter = new ContractDetailsAdapter(listContracts,this);
        //contractListView.setAdapter(adapter);
    }

    public void acknowledgeContract(String decision){
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            jsonBody.put("contract_id",contractId);
            jsonBody.put("decision",decision);
            System.out.println("Details: " + jsonBody);
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, ACKNOWLEDGE_URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("result");
                        System.out.println("Results: " + result);
                        if (result.equals("success")) {
                            System.out.println("Acknowledge Contract Success!");
                            requestQueue.stop();
                            finish();
                        } else {
                            System.out.println("Acknowledge Contract Failed");
                            requestQueue.stop();
                            finish();
                        }
                    }catch (JSONException e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.getLocalizedMessage();
                    requestQueue.stop();
                    finish();
                }
            }){
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

    public void terminateContract(){
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            jsonBody.put("contract_id",contractId);
            System.out.println("Details: " + jsonBody);
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, TERMINATECONTRACT_URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                    String result = response.getString("result");
                    System.out.println("Results: " + result);
                    if (result.equals("success")) {
                        System.out.println("Terminate Contract Success!");
                        requestQueue.stop();
                        finish();
                    } else {
                        System.out.println("Terminate Contract Failed");
                        requestQueue.stop();
                        finish();
                     }
                    }catch (JSONException e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.getLocalizedMessage();
                    finish();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + sessionToken);//put your token here
                    System.out.println("Header: " + headers.values());
                    return headers;
                }
            };;
            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateBalance(){
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        final RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        JsonObjectRequest requestJsonObject = new JsonObjectRequest(Request.Method.GET, GETBALANCE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    balance = response.getString("balance");
                    System.out.println(response.getString("balance"));
                    requestQueue.stop();
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("", "Error: " + error.toString());
                requestQueue.stop();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + sessionToken);//put your token here
                System.out.println("Header: " + headers.values());
                return headers;
            }
        };
        requestQueue.add(requestJsonObject);
    }

    TextView textViewReceiverName, textViewReceiverPhoneNum, textViewPayeeName, textViewPayeePhoneNum,
            textViewContractName, textViewContractStatus, textViewAmount, textViewStartDate, textViewEndDate,
            textViewPenaltyAmount, textViewDescription;
    public void GetViewItems(){
        textViewContractName = findViewById(R.id.contractNameCreate);
        textViewReceiverName = findViewById(R.id.receiverName);
        textViewReceiverPhoneNum = findViewById(R.id.receiverPhoneNum);
        textViewPayeeName = findViewById(R.id.payeeName);
        textViewPayeePhoneNum = findViewById(R.id.payeePhoneNum);
        textViewContractStatus = findViewById(R.id.status);
        textViewAmount = findViewById(R.id.amount);
        textViewStartDate = findViewById(R.id.startDate);
        textViewEndDate = findViewById(R.id.endDate);
        textViewPenaltyAmount = findViewById(R.id.penaltyAmount);
        textViewDescription = findViewById(R.id.description);

        ListContract listContract = listContracts.get(position);
        textViewReceiverName.setText(listContract.getReceiverName());
        System.out.println("The Receiver Name: " + listContract.getReceiverName());
        System.out.println("TextViewName: " + textViewReceiverName.getText().toString());
        textViewReceiverPhoneNum.setText(listContract.getUser1PhoneNum());
        textViewPayeeName.setText(listContract.getPayeeName());
        textViewPayeePhoneNum.setText(listContract.getUser2PhoneNum());
        textViewContractName.setText(listContract.getContractName());
        if(listContract.getContractStatus().equals("0")) {
            textViewContractStatus.setText("Pending");
            textViewContractStatus.setTextColor(Color.parseColor("#e7b416"));
        }else if(listContract.getContractStatus().equals("1")){
            textViewContractStatus.setText("Accepted");
        }else if(listContract.getContractStatus().equals("2")){
            textViewContractStatus.setText("Active");
        }else if(listContract.getContractStatus().equals("3")){
            textViewContractStatus.setText("Declined");
            textViewContractStatus.setTextColor(Color.parseColor("#FF0000"));
        }else{
            textViewContractStatus.setText("Terminated");
            textViewContractStatus.setTextColor(Color.parseColor("#FF0000"));
        }
        textViewAmount.setText("$" + listContract.getAmount());
        textViewStartDate.setText(listContract.getStartDate());
        textViewEndDate.setText(listContract.getEndDate());
        textViewPenaltyAmount.setText("$" + listContract.getPenaltyAmount());
        penaltyAmount = listContract.getPenaltyAmount();
        textViewDescription.setText(listContract.getDescription());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TopUpCode) {
            if (resultCode == Activity.RESULT_OK) {
                System.out.println("Top Up Successful");
                updateBalance();
            }
        }
    }
}
