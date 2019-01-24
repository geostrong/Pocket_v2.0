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
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ContractActivity extends AppCompatActivity {

    private RecyclerView contractListView;
    private RecyclerView.Adapter adapter;

    private List<ListContract> listContracts;
    private FloatingActionButton createBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract);
        getSupportActionBar().setTitle("Contract");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        createBtn = findViewById(R.id.addBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ContractActivity.this, ContractActivity_Create.class);
                startActivity(intent);
            }
        });
        contractListView = findViewById(R.id.contractListView);
        contractListView.setHasFixedSize(true);
        contractListView.setLayoutManager(new LinearLayoutManager(this));

        listContracts = new ArrayList<>();

        for(int i=0; i<=9; i++){
            ListContract listContract = new ListContract(
                    "Harold" + i,
                    "1234 5678",
                    "HDB Rent",
                    "10-12-2022"
            );

            listContracts.add(listContract);
        }

        adapter = new ContractAdapter(listContracts,this);

        contractListView.setAdapter(adapter);
    }
}
