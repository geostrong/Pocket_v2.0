package com.pocketwallet.pocket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

        //processGraph();

        //Transaction List
        transactionListView = findViewById(R.id.transactionsListView);
        transactionListView.setHasFixedSize(true);
        transactionListView.setLayoutManager(new LinearLayoutManager(this));

        listTransactions = new ArrayList<>();
        transactionsArrayList = new ArrayList<>();

        for(int i=0; i<1; i++){
            Transaction listTransaction = new Transaction(
                    //"Harold" + i,
                    //"1234 5678",
                    //"-$100",
                    //"654820842081",
                    //"10 June 2018",
                   // "15:30"
                    "TestID",
                    "type",
                    "senderID",
                    "receiverID",
                    "amount",
                    "date"
            );
            listTransactions.add(listTransaction);
        }
        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }
        GetTransactions();
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
                            JSONArray transactionArray = response.getJSONArray("transactions");
                            for(int i = 0; i < transactionArray.length(); i++){
                                JSONObject tempTransaction = transactionArray.getJSONObject(i);
                                //System.out.println("i: " + i + "|" + tempTransaction);
                                if(tempTransaction.getString("from").equals("-")){
                                   Transaction transaction = new Transaction(tempTransaction.getString("transactionID"), tempTransaction.getString("type"),
                                                                             tempTransaction.getString("from"),tempTransaction.getString("to"),"-" + tempTransaction.getString("amount"),
                                                                                tempTransaction.getString("timestamp"));
                                    listTransactions.add(transaction);
                                }else{
                                    Transaction transaction = new Transaction(tempTransaction.getString("transactionID"), tempTransaction.getString("type"),
                                            tempTransaction.getString("from"),tempTransaction.getString("to"), tempTransaction.getString("amount"),
                                            tempTransaction.getString("timestamp"));
                                    listTransactions.add(transaction);
                                }
                            }
                        }
                    }catch(JSONException e){
                        System.out.println("Error: " + e);
                    }
                    CreateAdapterView();
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

    public void CreateAdapterView(){
        adapter = new TransactAdapter(listTransactions,this);
        transactionListView.setAdapter(adapter);
    }

    public void processGraph() {
        /*
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
    */
    }
}
