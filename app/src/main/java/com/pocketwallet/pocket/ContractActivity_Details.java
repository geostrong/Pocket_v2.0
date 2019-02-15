package com.pocketwallet.pocket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContractActivity_Details extends AppCompatActivity {

    private RecyclerView contractListView;
    private RecyclerView.Adapter adapter;

    private ArrayList<ListContract> listContracts;

    private Bundle extras;
    private String userId;
    private String name,phoneNumber;
    private String contractId;
    private int position;

    Button acceptButton;
    Button declineButton;

    private SharedPreferences userPreferences;

    String TERMINATECONTRACT_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/transactional/contract/terminate";
    String ACKNOWLEDGE_URL = "http://pocket.ap-southeast-1.elasticbeanstalk.com/users/transactional/contract/ack";

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
        }
        //Get user's name from shared preferences
        userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        name = userPreferences.getString("user_name", "Name");
        phoneNumber = userPreferences.getString("PhoneNumber", "PhoneNumber");

        acceptButton = findViewById(R.id.acceptButton);
        declineButton = findViewById(R.id.declineButton);
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

        if(userId.equals(listContracts.get(position).getUser2ID())){
            listContracts.get(position).setPayeeName(name);
            listContracts.get(position).setPayeePhoneNum(phoneNumber);
            listContracts.get(position).setReceiverName(listContracts.get(position).getUser1ID());
            if(!listContracts.get(position).getContractStatus().equals(("0"))){
                acceptButton.setVisibility(View.GONE);
                declineButton.setVisibility(View.GONE);
            }
        }else{
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
            listContracts.get(position).setReceiverName(name);
            listContracts.get(position).setReceiverPhoneNum(phoneNumber);
            listContracts.get(position).setPayeeName(listContracts.get(position).getUser2ID());
        }
        contractId = listContracts.get(position).getContractID();

        //createAdapterView();
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            jsonBody.put("contract_id",contractId);
            jsonBody.put("decision",decision);
            System.out.println("Details: " + jsonBody);
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, ACKNOWLEDGE_URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //try {
                        System.out.println("Came");
                        //String result = response.getString("result");
                        //System.out.println("Results: " + result);
                        //if (result.equals("success")) {
                        //    System.out.println("Acknowledge Contract Success!");
                        //} else {
                        //    System.out.println("Acknowledge Contract Failed");
                       // }
                    //} //catch (JSONException e) {

                    //}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.getLocalizedMessage();
                    onBackPressed();
                }
            });
            requestQueue.add(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void terminateContract(){

    }

    TextView textViewReceiverName, textViewReceiverPhoneNum, textViewPayeeName, textViewPayeePhoneNum,
            textViewContractName, textViewContractStatus, textViewAmount, textViewStartDate, textViewEndDate,
            textViewPenaltyAmount, textViewDescription;
    public void GetViewItems(){
        textViewReceiverName = findViewById(R.id.receiverName);
        textViewReceiverPhoneNum = findViewById(R.id.receiverPhoneNum);
        textViewPayeeName = findViewById(R.id.payeeName);
        textViewPayeePhoneNum = findViewById(R.id.payeePhoneNum);
        textViewContractName = findViewById(R.id.contractName);
        textViewContractStatus = findViewById(R.id.status);
        textViewAmount = findViewById(R.id.amount);
        textViewStartDate = findViewById(R.id.startDate);
        textViewEndDate = findViewById(R.id.endDate);
        textViewPenaltyAmount = findViewById(R.id.penaltyAmount);
        textViewDescription = findViewById(R.id.description);

        ListContract listContract = listContracts.get(position);
        textViewReceiverName.setText(listContract.getReceiverName());
        textViewReceiverPhoneNum.setText(listContract.getReceiverPhoneNum());
        textViewPayeeName.setText(listContract.getPayeeName());
        textViewPayeePhoneNum.setText(listContract.getPayeePhoneNum());
        //holder.textViewContractName.setText(listContract.getContractName());
        if(listContract.getContractStatus().equals("0")) {
            textViewContractStatus.setText("Pending");
        }else if(listContract.getContractStatus().equals("1")){
            textViewContractStatus.setText("Accepted");
        }else if(listContract.getContractStatus().equals("2")){
            textViewContractStatus.setText("Active");
        }else if(listContract.getContractStatus().equals("3")){
            textViewContractStatus.setText("Declined");
        }else{
            textViewContractStatus.setText("Terminated");
        }
        textViewAmount.setText(listContract.getAmount());
        textViewStartDate.setText(listContract.getStartDate());
        textViewEndDate.setText(listContract.getEndDate());
        textViewPenaltyAmount.setText(listContract.getPenaltyAmount());
        textViewDescription.setText(listContract.getDescription());
    }
}
