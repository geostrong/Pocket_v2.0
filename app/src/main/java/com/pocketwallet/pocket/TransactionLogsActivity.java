package com.pocketwallet.pocket;

import android.app.LauncherActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class TransactionLogsActivity extends AppCompatActivity {

    private RecyclerView transactionListView;
    private RecyclerView.Adapter adapter;

    private List<Transaction> listTransactions;
    private ArrayList transactionsArrayList;

    private String userId;
    private String urlRetrieveTransactionHistory = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/transactionhistory";

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_logs);

        transactionListView = findViewById(R.id.transactionsListView);
        transactionListView.setHasFixedSize(true);
        transactionListView.setLayoutManager(new LinearLayoutManager(this));

        listTransactions = new ArrayList<>();

        for(int i=0; i<=9; i++){
            Transaction listTransaction = new Transaction(
                    "Harold" + i,
                    "1234 5678",
                    "-$100",
                    "654820842081",
                    "10 June 2018",
                    "15:30"
            );

            listTransactions.add(listTransaction);

            extras = getIntent().getExtras();
            if (extras != null) {
                userId = extras.getString("userId");
            }
        }
        GetTransactions();

        //adapter = new TransactAdapter(listTransactions,this);
        //transactionListView.setAdapter(adapter);
    }

    public void GetTransactions(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            System.out.println("TEST PRINTING: " + jsonBody);

            //requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
