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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.vision.text.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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

    private final String[] months = {"Jan", "Feb", "Apr", "Mar", "Jun", "July", "August", "Sep", "Oct", "Nov", "December" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_logs);

        processGraph();

        //Transaction List
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

        adapter = new TransactAdapter(listTransactions,this);
        transactionListView.setAdapter(adapter);
    }

    public void GetTransactions(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            System.out.println("User ID: " +jsonBody);
            urlRetrieveTransactionHistory += "/" + userId;
            System.out.println("urlRetrieveTransactionHistory: " + urlRetrieveTransactionHistory);
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlRetrieveTransactionHistory, jsonBody,
                    new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("result");
                        System.out.println("Results: " + result);
                        if(result.equalsIgnoreCase("Success")){

                        }
                    }catch(JSONException e){
                        System.out.println("Error: " + e);
                    }
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
            System.out.println("Error Json Body = " + new String(jsonObjectRequest.getBody()));
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processGraph() {
        //Graphs
        LineChart chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();

        entries.add(new Entry(0, 4));
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 4));

        LineDataSet dataSet = new LineDataSet(entries, "Transactions");
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        //Set Data
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }
}
