package com.pocketwallet.pocket;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContractActivity_Details extends AppCompatActivity {

    String urlRetrieveContracts = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/contract";
    private RecyclerView contractListView;
    private RecyclerView.Adapter adapter;

    private List<ListContract> listContracts;

    private Bundle extras;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract__details);
        getSupportActionBar().setTitle("Contract Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        createAdapterView();
        getContracts();
    }


    public void getContracts(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            System.out.println("User ID: " +jsonBody);
            urlRetrieveContracts += "/" + userId;
            System.out.println("urlRetrieverContracts: " + urlRetrieveContracts);
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlRetrieveContracts, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                System.out.println("came in details contract");
                                String result = response.getString("result");
                                System.out.println("Results: " + result);
                                if(result.equalsIgnoreCase("Success")){
                                    JSONArray contractsArray = response.getJSONArray("contracts");

                                    System.out.println(contractsArray.length());

                                    //Requires last page to send i
                                    JSONObject detailContract = contractsArray.getJSONObject(0);

                                    ListContract contract = new ListContract(detailContract.getString("contractID"), detailContract.getString("contractStatus"),
                                            detailContract.getString("user1_id"),detailContract.getString("user2_id"), detailContract.getString("user1_ack"),
                                            detailContract.getString("user2_ack"),detailContract.getString("description"), detailContract.getString("amount"),
                                            detailContract.getString("frequency"), detailContract.getString("penaltyAmount"),detailContract.getString("createdDate"),
                                            detailContract.getString("startDate"), detailContract.getString("endDate")/*, detailContract.getString("receiverName"),
                                            detailContract.getString("receiverPhoneNum"), detailContract.getString("payeeName"), detailContract.getString("payeePhoneNum")*/);
                                    System.out.println("ContractID: " + contract.getContractID() + " | contractStatus: " + contract.getContractStatus()
                                            + " | user1_id: " + contract.getUser1ID() + " | user2_id: " + contract.getUser2ID() + " | user1_ack: " + contract.getUser1ACK()
                                            + " | user2_ack: " + contract.getUser2ACK() + " | description: " + contract.getDescription() + " | amount: " + contract.getAmount()
                                            + " | frequency: " + contract.getFrequency() + " | penalty_amount: " + contract.getPenaltyAmount() + " | createdDate: " + contract.getCreatedDate()
                                            + " | startDate: " + contract.getStartDate() + " | endDate: " + contract.getEndDate() + " | receiverName: " + contract.getReceiverName()
                                            + " | receiverPhoneNum: " + contract.getReceiverPhoneNum() + " | payeeName: " + contract.getPayeeName() + " | payeePhoneNum: " + contract.getPayeePhoneNum());
                                    listContracts.add(contract);

                                        //System.out.println("ContractID: " + tempContract.getString("contractID"));
                                }
                            }catch(JSONException e){
                                System.out.println("Error: " + e);
                            }
                            createAdapterView();
                            adapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    System.out.println("Error Message: " + error.getMessage());
                    System.out.println("Error Network Response Data: " + new String(error.networkResponse.data));
                    System.out.println("Error Network Response Status Code" + error.networkResponse.statusCode);
                    //onBackPressed();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}
