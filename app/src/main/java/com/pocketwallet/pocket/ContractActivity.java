package com.pocketwallet.pocket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.Map;

public class ContractActivity extends AppCompatActivity {

    String urlRetrieveContracts = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/";

    private RecyclerView contractListView;
    private RecyclerView.Adapter adapter;

    private ArrayList<ListContract> listContracts;
    private FloatingActionButton createBtn;

    private Bundle extras;
    private String userId;

    //Session Token
    private String sessionToken;

    @Override
    protected void onResume(){
        getContracts();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract);
        getSupportActionBar().setTitle("Contract");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }
        //Session Token
        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sessionToken = userPreferences.getString("sessionToken", "");

        createBtn = findViewById(R.id.addBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ContractActivity.this, ContractActivity_Create.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });
        contractListView = findViewById(R.id.contractListView);
        contractListView.setHasFixedSize(true);
        contractListView.setLayoutManager(new LinearLayoutManager(this));

        listContracts = new ArrayList<>();

        createAdapterView();
        //getContracts();
    }

    public void getContracts(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        listContracts.clear();
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            if(!urlRetrieveContracts.contains("contract")) {
                urlRetrieveContracts = urlRetrieveContracts + "contract" + "/" + userId;
            }

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlRetrieveContracts, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String result = response.getString("result");
                               if(result.equalsIgnoreCase("Success")){
                                    JSONArray contractsArray = response.getJSONArray("contracts");
                                    for(int i = 0; i < contractsArray.length(); i++){
                                        JSONObject tempContract = contractsArray.getJSONObject(i);

                                            ListContract contract = new ListContract(tempContract.getString("contractID"), tempContract.getString("contractStatus"),
                                                    tempContract.getString("user1_id"),tempContract.getString("user2_id"), tempContract.getString("user1_ack"),
                                                    tempContract.getString("user2_ack"),tempContract.getString("description"), tempContract.getString("amount"),
                                                    tempContract.getString("frequency"), tempContract.getString("penaltyAmount"),tempContract.getString("createdDate"),
                                                    tempContract.getString("startDate"),tempContract.getString("endDate"),tempContract.getString("contractName"),
                                                    tempContract.getString("user1_phone"),tempContract.getString("user2_phone"));
                                            listContracts.add(contract);
                                    }
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
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + sessionToken);
                    System.out.println("Header: " + headers.values());
                    return headers;
                }
            };;
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
        adapter = new ContractAdapter(listContracts,this);
        contractListView.setAdapter(adapter);
    }
}
