package com.pocketwallet.pocket;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionLogsActivity extends AppCompatActivity implements TransactionLogsAdapter.TransactAdapterListener{

    private RecyclerView transactionListView;
    private TransactionLogsAdapter adapter;

    private List<Transaction> listTransactions;

    private SearchView searchView;

    private String userId;
    private String urlRetrieveTransactionHistory = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/transactionhistory";

    Bundle extras;

    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_logs);

        getSupportActionBar().setTitle("Transaction History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //Transaction List
        transactionListView = findViewById(R.id.transactionsListView);
        transactionListView.setHasFixedSize(true);
        transactionListView.setLayoutManager(new LinearLayoutManager(this));

        listTransactions = new ArrayList<>();

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        StickyHeaderTransaction sectionItemDecoration =
                new StickyHeaderTransaction(getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height),
                        true,
                        new StickyHeaderTransaction.SectionCallback() {
                            @Override
                            public boolean isSection(int position) {
                                String start = listTransactions.get(position)
                                        .getTimestampToString().substring(0, 10);

                                System.out.println();
                                System.out.println("position: " + position);
                                System.out.println("start: " + start);

                                /*
                                if(position -1 >= 0) {
                                    System.out.println("pos-1: " + listTransactions.get(position - 1)
                                            .getTimestampToString().substring(0, 10));
                                }

                                if (position-1 == -1) {
                                    System.out.println("Came");

                                    return position == 0;
                                }

                                if(listTransactions.get(position).getTitle().equals(listTransactions.get(position - 1).getTitle())){
                                    System.out.println("Came2");
                                    return false;
                                }else{
                                    System.out.println("Came3");
                                    return true;
                                }
                                */

                                return position == 0 || !start.equals(listTransactions.get(position - 1)
                                        .getTimestampToString().substring(0, 10));
                            }

                            @Override
                            public CharSequence getSectionHeader(int position) {
                                int monthNum = 0;
                                String[] when = {"Today", "Yesterday", "2 Days Ago", "This Week", "Last 30 Days", "Past Year", "More Than a Year Ago"};
                                SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:a");
                                SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd");

                                Date currentDay = null;
                                try {
                                    currentDay = dateOnly.parse(dateOnly.format(new Date()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String temp = "";
                                CharSequence title = listTransactions.get(position)
                                        .getTimestampToString();
                                title = title.subSequence(0,10);
                                temp = title.toString();
                                try {
                                    Date date = (Date) dateOnly.parse(temp);
                                    if (date.equals(currentDay)) {
                                        monthNum = 0;
                                        title = when[monthNum];
                                        listTransactions.get(position).setTitle(when[monthNum]);
                                    }
                                    else {
                                        long difference = currentDay.getTime() - date.getTime();
                                        difference = difference/1000;
                                        difference = difference/60;
                                        difference = difference/60;
                                        difference = difference/24;
                                        // System.out.println(difference);
                                        if (difference == 1) {
                                            monthNum = 1;
                                            title = when[monthNum];
                                            listTransactions.get(position).setTitle(when[monthNum]);
                                        }
                                        else if (difference == 2) {
                                            monthNum = 2;
                                            title = when[monthNum];
                                            listTransactions.get(position).setTitle(when[monthNum]);
                                        }
                                        else if (difference > 2 && difference < 6) {
                                            monthNum = 3;
                                            title = when[monthNum];
                                            listTransactions.get(position).setTitle(when[monthNum]);
                                        }
                                        else if (difference > 5 && difference <= 30) {
                                            monthNum = 4;
                                            title = when[monthNum];
                                            listTransactions.get(position).setTitle(when[monthNum]);
                                        }
                                        else if (difference > 30 && difference <= 365){
                                            monthNum = 5;
                                            title = when[monthNum];
                                            listTransactions.get(position).setTitle(when[monthNum]);
                                        }
                                        else{
                                            monthNum = 6;
                                            title = when[monthNum];
                                            listTransactions.get(position).setTitle(when[monthNum]);
                                        }
                                        System.out.println("MonthNum: " + monthNum + " | Difference: " + difference);
                                        System.out.println("The title is: " + title);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return listTransactions.get(position).getTitle();

                            }
                        });

        transactionListView.addItemDecoration(sectionItemDecoration);

        createAdapterView();
        getTransactions();
        processGraph();

    }

    public void getTransactions(){
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

                                if(tempTransaction.getString("from").equals("-")){

                                   Transaction transaction = new Transaction(tempTransaction.getString("transactionID"), tempTransaction.getString("type"),
                                                                             tempTransaction.getString("from"),tempTransaction.getString("to"), tempTransaction.getString("amount"),
                                                                                tempTransaction.getString("timestamp"), false);
                                    listTransactions.add(transaction);
                                }else{

                                    Transaction transaction = new Transaction(tempTransaction.getString("transactionID"), tempTransaction.getString("type"),
                                            tempTransaction.getString("from"),tempTransaction.getString("to"), tempTransaction.getString("amount"),
                                            tempTransaction.getString("timestamp"), true);
                                    listTransactions.add(transaction);
                                }
                            }
                        }
                    }catch(JSONException e){
                        System.out.println("Error: " + e);
                    }
                    //createAdapterView();
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

    public void createAdapterView(){
        adapter = new TransactionLogsAdapter(listTransactions,this);
        transactionListView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        transactionListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transact_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo((searchManager.getSearchableInfo(getComponentName())));
        searchView.setMaxWidth(Integer.MAX_VALUE);


        System.out.println("Submit = " + searchView.isSubmitButtonEnabled());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void processGraph() {

        //Graphs
        LineChart chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();

        //Add data x and y data here
        entries.add(new Entry(0, 4));
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 4));
        entries.add(new Entry(4, 4));
        entries.add(new Entry(5, 1));
        entries.add(new Entry(6, 2));
        entries.add(new Entry(7, 4));
        entries.add(new Entry(8, 1));
        entries.add(new Entry(9, 2));
        entries.add(new Entry(10, 4));
        entries.add(new Entry(11, 3));

        LineDataSet dataSet = new LineDataSet(entries, "Transactions");

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months[(int) value];
            }
        };

        //Line style
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.1f);
        dataSet.setColor(getColor(R.color.colorPrimary));
        dataSet.setDrawHighlightIndicators(false);
        dataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(10f);

        //Chart Style
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getXAxis().setValueFormatter(formatter);
        chart.getXAxis().setGranularity(1f);
        //chart.getXAxis().setTextColor(getResources().getColor(R.color.colorPrimary));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getXAxis().setTextSize(14f);
        chart.setExtraOffsets(10, 10, 10, 10);
        chart.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        chart.getRenderer().getPaintRender().setShadowLayer(1, 0, 2, Color.GRAY);

        //Set Data
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.invalidate();
        chart.setVisibleXRange(1,3);
        chart.animateY(2000, Easing.Linear);
        chart.centerViewToAnimated(Calendar.getInstance().get(Calendar.MONDAY),0, YAxis.AxisDependency.LEFT,2000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onTransactionSelected(Transaction transaction) {
        Toast.makeText(getApplicationContext(), "Selected: " + transaction.getName() + ", " + transaction.getAmount(), Toast.LENGTH_LONG).show();
    }
}
