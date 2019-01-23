package com.pocketwallet.pocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ContractActivity_Create extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract__create);

        Spinner contractFrequency = (Spinner) findViewById(R.id.frequency);

        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<String>(ContractActivity_Create.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.frequencies));

        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contractFrequency.setAdapter(frequencyAdapter);
    }
}
