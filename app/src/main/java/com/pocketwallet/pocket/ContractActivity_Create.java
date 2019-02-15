package com.pocketwallet.pocket;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ContractActivity_Create extends AppCompatActivity {

    private static final String TAG = "ContractActivity_Create";

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private EditText startDate;
    private EditText endDate;
    private DatePickerDialog.OnDateSetListener startDateListener;
    private DatePickerDialog.OnDateSetListener endDateListener;

    private String[] months = {"Jan", "Feb", "Mar", "Apr", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private Button requestContractButton;
    private TextView contractNameView;
    private TextView frequencyView;
    private TextView phoneInvolvedView;
    private TextView penaltyAmountView;
    private EditText descriptionView;
    private EditText amountContract;

    private Bundle extras;
    private String contractName;
    private String userId;
    private String phoneInvolved;
    private String description;
    private String amount;
    private String frequency;
    private String penaltyAmount;

    private String urlCreateContract = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/contract";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract__create);
        getSupportActionBar().setTitle("Create New Contract");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        phoneInvolvedView = findViewById(R.id.phoneInvolved);
        contractNameView = findViewById(R.id.contractName);
        penaltyAmountView = findViewById(R.id.penaltyAmount);
        frequencyView = findViewById(R.id.frequencyText);
        amountContract = findViewById(R.id.contractAmount);
        descriptionView = findViewById(R.id.descriptionContract);

        //RequestContractButton
        requestContractButton = findViewById(R.id.RequestContract);
        requestContractButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Spinner mySpinner = (Spinner) findViewById(R.id.frequency);
                contractName = contractNameView.getText().toString();
                phoneInvolved = phoneInvolvedView.getText().toString();
                frequency = frequencyView.getText().toString();
                amount = amountContract.getText().toString();
                penaltyAmount = penaltyAmountView.getText().toString();
                description = descriptionView.getText().toString();
                createContract();
            }
        });

        // Frequency
        Spinner contractFrequency = findViewById(R.id.frequency);

        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(ContractActivity_Create.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.frequencies));

        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contractFrequency.setAdapter(frequencyAdapter);

        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ContractActivity_Create.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        startDateListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "-" + ++month + "-" + year;
                startDate.setText(date);
            }};

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ContractActivity_Create.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        endDateListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "-" + ++month + "-" + year;
                endDate.setText(date);
            }};
    }

    public void createContract(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        try {
            description = "NoDescription";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user1_id", userId);
            jsonBody.put("user2_phone", phoneInvolved);
            jsonBody.put("description", description);
            jsonBody.put("amount", amount);
            jsonBody.put("frequency", frequency);
            jsonBody.put("penaltyAmount", penaltyAmount);
            jsonBody.put("startDate", startDate.getText().toString());
            jsonBody.put("endDate", endDate.getText().toString());

            System.out.println("JSON BODY: " +jsonBody);


            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlCreateContract, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String result = response.getString("result");
                                System.out.println("Results: " + result);
                                if(result.equalsIgnoreCase("Success")){
                                    System.out.println(response.getString("contract_id"));
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
}
