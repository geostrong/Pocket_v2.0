package com.pocketwallet.pocket;

import android.app.LauncherActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContractActivity extends AppCompatActivity {

    private RecyclerView contractListView;
    private RecyclerView.Adapter adapter;

    private List<ListContract> listContracts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract);

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
