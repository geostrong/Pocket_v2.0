package com.pocketwallet.pocket;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionLogsActivity extends AppCompatActivity implements TransactionLogsAdapter.TransactAdapterListener{

    private RecyclerView transactionListView;
    private TransactionLogsAdapter adapter;

    private List<Transaction> listTransactions;

    private SearchView searchView;

    private String userId;
    private String urlRetrieveTransactionHistory = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/transactionhistory";

    Bundle extras;

    private final String[] months0 = {"Jan", "Dec", "Nov", "Oct", "Sep", "Aug", "Jul", "Jun", "May", "Apr", "Mar", "Feb" };
    private final String[] months1 = {"Feb", "Jan", "Dec", "Nov", "Oct", "Sep", "Aug", "Jul", "Jun", "May", "Apr", "Mar" };
    private final String[] months2 = {"Mar", "Feb", "Jan", "Dec", "Nov", "Oct", "Sep", "Aug", "Jul", "Jun", "May", "Apr" };
    private final String[] months3 = {"Apr", "Mar", "Feb", "Jan", "Dec", "Nov", "Oct", "Sep", "Aug", "Jul", "Jun", "May" };
    private final String[] months4 = {"May", "Apr", "Mar", "Feb", "Jan", "Dec", "Nov", "Oct", "Sep", "Aug", "Jul", "Jun" };
    private final String[] months5 = {"Jun", "May", "Apr", "Mar", "Feb", "Jan", "Dec", "Nov", "Oct", "Sep", "Aug", "Jul" };
    private final String[] months6 = {"Jul", "Jun", "May", "Apr", "Mar", "Feb", "Jan", "Dec", "Nov", "Oct", "Sep", "Aug" };
    private final String[] months7 = {"Aug", "Jul", "Jun", "May", "Apr", "Mar", "Feb", "Jan", "Dec", "Nov", "Oct", "Sep" };
    private final String[] months8 = {"Sep", "Aug", "Jul", "Jun", "May", "Apr", "Mar", "Feb", "Jan", "Dec", "Nov", "Oct" };
    private final String[] months9 = {"Oct", "Sep", "Aug", "Jul", "Jun", "May", "Apr", "Mar", "Feb", "Jan", "Dec", "Nov" };
    private final String[] months10 = {"Nov", "Oct", "Sep", "Aug", "Jul", "Jun", "May", "Apr", "Mar", "Feb", "Jan", "Dec" };
    private final String[] months11 = {"Dec", "Nov", "Oct", "Sep", "Aug", "Jul", "Jun", "May", "Apr", "Mar", "Feb", "Jan" };

    float janTotal = 0;
    float febTotal = 0;
    float marTotal = 0;
    float aprTotal = 0;
    float mayTotal = 0;
    float junTotal = 0;
    float julTotal = 0;
    float augTotal = 0;
    float sepTotal = 0;
    float octTotal = 0;
    float novTotal = 0;
    float decTotal = 0;
    float tempAmount = 0;
    int latestMonth = 100;
    int i = 100;

    //Session Token
    private String sessionToken;

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

        SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sessionToken = userPreferences.getString("sessionToken", "");

        StickyHeaderTransaction sectionItemDecoration =
                new StickyHeaderTransaction(getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height),
                        true,
                        new StickyHeaderTransaction.SectionCallback() {
                            @Override
                            public boolean isSection(int position) {
                                String start = listTransactions.get(position).getTitle();
                                String prev = "0";

                                if (position != 0 ) {
                                    prev = listTransactions.get(position-1).getTitle();
                                }

                                return position == 0 || start != prev;
                            }

                            @Override
                            public CharSequence getSectionHeader(int position) {
                                return listTransactions.get(position).getTitle();

                            }
                        });

        transactionListView.addItemDecoration(sectionItemDecoration);

        createAdapterView();
        getTransactions();

    }

    public void processTransactionHeaders() {
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

        for (Transaction t : listTransactions) {
            String temp = t.getTimestampToString().substring(0,10);
            String tempMonthAmount = t.getAmount();
            tempAmount = Float.parseFloat(tempMonthAmount);
            int monthFirst = temp.charAt(5) - '0';
            int monthSec = temp.charAt(6) - '0';
            if (!t.getisIncoming())
                if (monthFirst == 0){
                    if (monthSec == 1){
                        if (latestMonth == 100){
                            latestMonth = 0;
                        }
                        janTotal = janTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else  if (monthSec == 2){
                        if (latestMonth == 100){
                            latestMonth = 1;
                        }
                        febTotal = febTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 3){
                        if (latestMonth == 100){
                            latestMonth = 2;
                        }
                        marTotal = marTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else  if (monthSec == 4){
                        if (latestMonth == 100){
                            latestMonth = 3;
                        }
                        aprTotal = aprTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 5){
                        if (latestMonth == 100){
                            latestMonth = 4;
                        }
                        mayTotal = mayTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else  if (monthSec == 6){
                        if (latestMonth == 100){
                            latestMonth = 5;
                        }
                        junTotal = junTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 7){
                        if (latestMonth == 100){
                            latestMonth = 6;
                        }
                        junTotal = julTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 8){
                        if (latestMonth == 100){
                            latestMonth = 7;
                        }
                        augTotal = augTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 9){
                        if (latestMonth == 100){
                            latestMonth = 8;
                        }
                        sepTotal = sepTotal + tempAmount;
                        tempAmount = 0;
                    }
                }
                else if (monthFirst == 1){
                    if (monthSec == 0){
                        if (latestMonth == 100){
                            latestMonth = 9;
                        }
                        octTotal = octTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 1){
                        if (latestMonth == 100){
                            latestMonth = 10;
                        }
                        novTotal =  novTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 2){
                        if (latestMonth == 100){
                            latestMonth = 11;
                        }
                        decTotal = decTotal + tempAmount;
                        tempAmount = 0;
                    }
                }

            try {
                Date date = (Date) dateOnly.parse(temp);

                if (date.equals(currentDay)) {
                    monthNum = 0;
                } else {
                    long difference = currentDay.getTime() - date.getTime();
                    difference = difference / 1000;
                    difference = difference / 60;
                    difference = difference / 60;
                    difference = difference / 24;

                    if (difference == 1) {
                        monthNum = 1;
                    } else if (difference == 2) {
                        monthNum = 2;
                    } else if (difference > 2 && difference <= 7) {
                        monthNum = 3;
                    } else if (difference > 7 && difference <= 30) {
                        monthNum = 4;
                    } else if (difference > 30 && difference <= 365) {
                        monthNum = 5;
                    } else {
                        monthNum = 6;
                    }
                }

                t.setTitle(when[monthNum]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
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

                    processTransactionHeaders();
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
                    headers.put("Authorization", "Bearer " + sessionToken);//put your token here
                    System.out.println("Header: " + headers.values());
                    return headers;
                }
            };;
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

        Float[] latestMonthY = {janTotal,febTotal,marTotal,aprTotal,mayTotal,junTotal,julTotal,augTotal,sepTotal,octTotal,novTotal,decTotal};

        //Graphs
        LineChart chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();

        System.out.println(janTotal + "HHHHHHHHHHHHHHHH");
        System.out.println(febTotal + "HHHHHHHHHHHHHHHH");
        //Add data x and y data here
        switch (latestMonth) {
            case 11:    entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 11;
                        }
            case 10:    entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 10;
                        }
            case 9:     entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 9;
                        }
            case 8:     entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 8;
                        }
            case 7:     entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 7;
                        }
            case 6:     entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 6;
                        }
            case 5:     entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 5;
                        }
            case 4:     entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 4;
                        }
            case 3:     entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 3;
                        }
            case 2:     entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 2;
                        }
            case 1:     entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 1;
                        }
                        System.out.println("Case 1");
            case 0:     entries.add(new Entry(latestMonth, latestMonthY[latestMonth]));
                        if (i == 100) {
                            i = 0;
                        }
                        System.out.println("Case 0");
                        break;
            case 100:   System.out.println("There's no data!");
                        break;
        }

        System.out.println(i + " is the current i");
        for (int j=11;i<=j-i;j--)
        {
            System.out.println("hi" + j);
            entries.add(new Entry(j, latestMonthY[j]));
        }
       /* entries.add(new Entry(0, janTotal));
        entries.add(new Entry(1, febTotal));
        entries.add(new Entry(2, marTotal));
        entries.add(new Entry(3, aprTotal));
        entries.add(new Entry(4, mayTotal));
        entries.add(new Entry(5, junTotal));
        entries.add(new Entry(6, julTotal));
        entries.add(new Entry(7, augTotal));
        entries.add(new Entry(8, sepTotal));
        entries.add(new Entry(9, octTotal));
        entries.add(new Entry(10, novTotal));
        entries.add(new Entry(11, decTotal));*/

        LineDataSet dataSet = new LineDataSet(entries, "Transactions");

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (i == 0){
                    System.out.println("here");
                    return months0[(int) value];
                }else if (i == 1){
                    return months1[(int) value];
                }else if (i == 2){
                    return months2[(int) value];
                }else if (i == 3){
                    return months3[(int) value];
                }else if (i == 4){
                    return months4[(int) value];
                }else if (i == 5){
                    return months5[(int) value];
                }else if (i == 6){
                    return months6[(int) value];
                }else if (i == 7){
                    return months7[(int) value];
                }else if (i == 8){
                    return months8[(int) value];
                }else if (i == 9){
                    return months9[(int) value];
                }else if (i == 10){
                    return months10[(int) value];
                }else if (i == 11) {
                    return months11[(int) value];
                }
                return (months2)[(int) value];


               /* switch(i){
                    case 0: return months0[(int) value];
                        break;
                    case 1: return months1[(int) value];
                        break;
                    case 2: return months2[(int) value];
                        break;
                    case 3: return months3[(int) value];
                        break;
                    case 4: return months4[(int) value];
                        break;
                    case 5: return months5[(int) value];
                        break;
                    case 6: return months6[(int) value];
                        break;
                    case 7: return months7[(int) value];
                        break;
                    case 8: return months8[(int) value];
                        break;
                    case 9: return months9[(int) value];
                        break;
                    case 10: return months10[(int) value];
                        break;
                    case 11: return months11[(int) value];
                        break;
                    default: return months11[(int) value];
                }*/

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
        //chart.centerViewToAnimated(Calendar.getInstance().get(Calendar.MONDAY),0, YAxis.AxisDependency.LEFT,2000);
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
