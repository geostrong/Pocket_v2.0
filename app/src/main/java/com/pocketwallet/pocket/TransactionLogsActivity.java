package com.pocketwallet.pocket;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
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

    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
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

    float janPRTotal = 0;
    float febPRTotal = 0;
    float marPRTotal = 0;
    float aprPRTotal = 0;
    float mayPRTotal = 0;
    float junPRTotal = 0;
    float julPRTotal = 0;
    float augPRTotal = 0;
    float sepPRTotal = 0;
    float octPRTotal = 0;
    float novPRTotal = 0;
    float decPRTotal = 0;

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

    float janRTotal = 0;
    float febRTotal = 0;
    float marRTotal = 0;
    float aprRTotal = 0;
    float mayRTotal = 0;
    float junRTotal = 0;
    float julRTotal = 0;
    float augRTotal = 0;
    float sepRTotal = 0;
    float octRTotal = 0;
    float novRTotal = 0;
    float decRTotal = 0;

    float tempAmount = 0;
    int latestMonth = 100;
    int i = 100;
    int tabSelected = 0;

    //Session Token
    private String sessionToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_logs);
        setupTabLayout();

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

    private void setupTabLayout() {
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());
            }
        });
    }

    private void onTabTapped(int position) {
        switch (position) {
            case 0:     tabSelected = 0;
                        adapter.getFilter().filter("");
                        processGraph();
                break;
            case 1:
                        tabSelected = 1;
                        adapter.getFilter().filter("-$");
                        processGraph();
                break;
            case 2:     tabSelected = 2;
                        adapter.getFilter().filter("+$");
                        processGraph();
            default:
        }
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
                if (latestMonth == 100)
                    if (monthFirst == 0){
                        if (monthSec == 1){
                            latestMonth = 0;
                        }
                        else  if (monthSec == 2){
                            latestMonth = 1;
                        }
                        else if (monthSec == 3){
                            latestMonth = 2;
                        }
                        else  if (monthSec == 4){
                            latestMonth = 3;
                        }
                        else if (monthSec == 5){
                            latestMonth = 4;
                        }
                        else  if (monthSec == 6){
                            latestMonth = 5;
                        }
                        else if (monthSec == 7){
                            latestMonth = 6;
                        }
                        else if (monthSec == 8){
                            latestMonth = 7;
                        }
                        else if (monthSec == 9){
                            latestMonth = 8;
                        }
                    }
                    else if (monthFirst == 1){
                        if (monthSec == 0){
                            latestMonth = 9;
                        }
                        else if (monthSec == 1){
                            latestMonth = 10;
                        }
                        else if (monthSec == 2){
                            latestMonth = 11;
                        }
                }

//==================================================GetPayments==================================================================================

            if (!t.getisIncoming())
                if (monthFirst == 0){
                    if (monthSec == 1){
                        janTotal = janTotal + tempAmount;
                        janPRTotal = janPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                    else  if (monthSec == 2){
                        febTotal = febTotal + tempAmount;
                        febPRTotal = febPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 3){
                        marTotal = marTotal + tempAmount;
                        marPRTotal = marPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                    else  if (monthSec == 4){
                        aprTotal = aprTotal + tempAmount;
                        aprPRTotal = aprPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 5){
                        mayTotal = mayTotal + tempAmount;
                        mayPRTotal = mayPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                    else  if (monthSec == 6){
                        junTotal = junTotal + tempAmount;
                        junPRTotal = junPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 7){
                        junTotal = julTotal + tempAmount;
                        julPRTotal = julPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 8){
                        augTotal = augTotal + tempAmount;
                        augPRTotal = augPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 9){
                        sepTotal = sepTotal + tempAmount;
                        sepPRTotal = sepPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                }
                else if (monthFirst == 1){
                    if (monthSec == 0){
                        octTotal = octTotal + tempAmount;
                        octPRTotal = octPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 1){
                        novTotal =  novTotal + tempAmount;
                        novPRTotal = novPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 2){
                        decTotal = decTotal + tempAmount;
                        decPRTotal = decPRTotal - tempAmount;
                        tempAmount = 0;
                    }
                }

//==================================================GetReceives==================================================================================

            if (t.getisIncoming())
                if (monthFirst == 0){
                    if (monthSec == 1){
                        janRTotal = janRTotal + tempAmount;
                        janPRTotal = janPRTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else  if (monthSec == 2){
                        febRTotal = febRTotal + tempAmount;
                        febPRTotal = febPRTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 3){
                        marRTotal = marRTotal + tempAmount;
                        marPRTotal = marPRTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else  if (monthSec == 4){
                        aprRTotal = aprRTotal + tempAmount;
                        aprPRTotal = aprPRTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 5){
                        mayRTotal = mayRTotal + tempAmount;
                        mayPRTotal = mayPRTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else  if (monthSec == 6){
                        junRTotal = junRTotal + tempAmount;
                        junPRTotal = junPRTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 7){
                        julRTotal = julRTotal + tempAmount;
                        julPRTotal = julPRTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 8){
                        augRTotal = augRTotal + tempAmount;
                        augPRTotal = augPRTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 9){
                        sepRTotal = sepRTotal + tempAmount;
                        sepPRTotal = sepPRTotal + tempAmount;
                        tempAmount = 0;
                    }
                }
                else if (monthFirst == 1){
                    if (monthSec == 0){
                        octRTotal = octRTotal + tempAmount;
                        octPRTotal = octPRTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 1){
                        novRTotal =  novRTotal + tempAmount;
                        novPRTotal = novPRTotal + tempAmount;
                        tempAmount = 0;
                    }
                    else if (monthSec == 2){
                        decRTotal = decRTotal + tempAmount;
                        decPRTotal = decPRTotal + tempAmount;
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

        /*
        incomingButton on click liksterner
        on click
        {
        adapter.getFilter().filter("+$");
        }

        outgoingButton on click liksterner
        on click
        {
        adapter.getFilter().filter("-$");
        }

        allButton on click liksterner
        on click
        {
        adapter.getFilter()

         */

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

        Float[] latestMonthTotal = {janPRTotal,febPRTotal,marPRTotal,aprPRTotal,mayPRTotal,junPRTotal,julPRTotal,augPRTotal,sepPRTotal,octPRTotal,novPRTotal,decPRTotal};
        Float[] latestMonthPayment = {janTotal,febTotal,marTotal,aprTotal,mayTotal,junTotal,julTotal,augTotal,sepTotal,octTotal,novTotal,decTotal};
        Float[] latestMonthReceives = {janRTotal,febRTotal,marRTotal,aprRTotal,mayRTotal,junRTotal,julRTotal,augRTotal,sepRTotal,octRTotal,novRTotal,decRTotal};

        //Graphs
        LineChart chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<>();
        List<Entry> receiveEntries = new ArrayList<>();
        List<Entry> totalEntries = new ArrayList<>();

        System.out.println(janTotal + "HHHHHHHHHHHHHHHH");
        System.out.println(febTotal + "HHHHHHHHHHHHHHHH");
        //Add data x and y data here
        switch (latestMonth) {
            case 11:    if (i == 100) {
                            i = 11;
                        }
            case 10:    if (i == 100) {
                            i = 10;
                        }
            case 9:     if (i == 100) {
                            i = 9;
                        }
            case 8:     if (i == 100) {
                            i = 8;
                        }
            case 7:     if (i == 100) {
                            i = 7;
                        }
            case 6:     if (i == 100) {
                            i = 6;
                        }
            case 5:     if (i == 100) {
                            i = 5;
                        }
            case 4:     if (i == 100) {
                            i = 4;
                        }
            case 3:     if (i == 100) {
                            i = 3;
                        }
            case 2:     if (i == 100) {
                            i = 2;
                        }
            case 1:     if (i == 100) {
                            i = 1;
                        }
            case 0:     if (i == 100) {
                            i = 0;
                        }
                        break;
            case 100:   System.out.println("There's no data!");
                        break;
        }

        System.out.println(i + " is the current i");

        int k = i;
        int h = 0;

        if (latestMonth != 100){
            for (int j = 0; j < 12; j++) {
                if (h != 0) {
                    k--;
                    if (k == -1) {
                        k = 11;
                    }
                }
                h++;
                System.out.println("Looped " + h + " times");
                System.out.println("month " + j + " got " + latestMonthReceives[k]);
                entries.add(new Entry(j, latestMonthPayment[k]));
                receiveEntries.add(new Entry(j, latestMonthReceives[k]));
                totalEntries.add(new Entry(j, latestMonthTotal[k]));
            }
        }

        else if (latestMonth == 100){
            for (int j = 0; j < 12; j++){
                entries.add(new Entry(j, latestMonthPayment[j]));
                receiveEntries.add(new Entry(j, latestMonthReceives[j]));
                totalEntries.add(new Entry(j, latestMonthTotal[j]));
            }
        }


        LineDataSet dataSet = new LineDataSet(entries, "Payments");
        LineDataSet datasetReceive = new LineDataSet(receiveEntries, "Received");
        LineDataSet datasetTotal = new LineDataSet(totalEntries, "Total");

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (i == 0){
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
                }else{
                    return months[(int) value];
                }


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

        //Line style payment
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.1f);
        dataSet.setColor(getColor(R.color.colorPrimary));
        dataSet.setDrawHighlightIndicators(false);
        dataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(10f);

        //Line style receives
        datasetReceive.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        datasetReceive.setCubicIntensity(0.1f);
        datasetReceive.setColor(getColor(R.color.colorPrimary));
        datasetReceive.setDrawHighlightIndicators(false);
        datasetReceive.setCircleColor(getResources().getColor(R.color.colorPrimary));
        datasetReceive.setLineWidth(2f);
        datasetReceive.setCircleRadius(10f);

        //Line style total
        datasetTotal.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        datasetTotal.setCubicIntensity(0.1f);
        datasetTotal.setColor(getColor(R.color.colorPrimary));
        datasetTotal.setDrawHighlightIndicators(false);
        datasetTotal.setCircleColor(getResources().getColor(R.color.colorPrimary));
        datasetTotal.setLineWidth(2f);
        datasetTotal.setCircleRadius(10f);

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
        chart.removeAllViews();
        LineData chartData = new LineData();

        if (tabSelected == 2) {
            chartData.addDataSet(datasetReceive);
        }
        else if (tabSelected == 1) {
            chartData.addDataSet(dataSet);
        }
        else if (tabSelected == 0) {
            chartData.addDataSet(datasetTotal);
        }


        chart.setData(chartData);
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
