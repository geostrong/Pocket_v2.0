package com.pocketwallet.pocket;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

public class TopUpActivity extends AppCompatActivity {

    private static final String TAG = "TopUpActivity";

    private Bundle extras;
    private String cardType;
    private String cardNum;
    private String cvv;
    private String topUpAmount;
    private String userId;

    private EditText expiryDate;
    private DatePickerDialog.OnDateSetListener expiryDateListener;

    private TextView cardTypeView;
    private TextView cardNumView;
    private TextView cvvView;
    private TextView topUpAmountView;

    private String urlTopUp = "http://pocket.ap-southeast-1.elasticbeanstalk.com/transactional/topup?"; //address needs changing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);
        getSupportActionBar().setTitle("Top Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));


        extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        cardNumView = findViewById(R.id.cardNum);
        cvvView = findViewById(R.id.cvv);
        topUpAmountView = findViewById(R.id.topUpAmount);
        expiryDate = findViewById(R.id.expiryDate);

        Button confirmBtn = findViewById(R.id.topUpConfirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(TopUpActivity.this, ResultActivity.class);
                //Bundle b = new Bundle();
                //b.putString("title", "Top Up");
                //intent.putExtras(b);
                //startActivity(intent);
                //finish();
                //Spinner cardTypePass = findViewById(R.id.cardType);
                cardNum = cardNumView.getText().toString();
                cvv = cvvView.getText().toString();
                topUpAmount = topUpAmountView.getText().toString();
                //cardType = cardTypeView.getText().toString();                                         //cant get this to parse visa/mastercard to requestTopUp(); probably dont need to
                requestTopUp();
            }
        });

        Spinner cardTypeView = findViewById(R.id.cardType);

        ArrayAdapter<String> cardTypeAdapter = new ArrayAdapter<>(TopUpActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.cardTypes));
        cardTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardTypeView.setAdapter(cardTypeAdapter);

        final ImageView image = findViewById(R.id.cardImage);

        cardTypeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        image.setImageResource(R.drawable.ic_visa);
                        break;
                    case 1:
                        image.setImageResource(R.drawable.ic_mastercard);
                        break;
                    default:
                        image.setImageResource(R.drawable.ic_visa);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        expiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        TopUpActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        expiryDateListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        expiryDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "-" + ++month + "-" + dayOfMonth;
                expiryDate.setText(date);
            }};


    }

    public void requestTopUp(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("cardType", cardType);
            jsonBody.put("cardNum", cardNum);
            jsonBody.put("cvv", cvv);
            jsonBody.put("expiryDate", expiryDate.getText().toString());
            jsonBody.put("topUpAmount", topUpAmount);

            System.out.println("JSON BODY: " +jsonBody);


            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlTopUp, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String result = response.getString("result");
                                System.out.println("Results: " + result);
                                if(result.equalsIgnoreCase("Success")){
                                    System.out.println(response.getString("cardType"));
                                    finish();
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
                    finish();
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
