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

public class TransactionLogsActivity extends AppCompatActivity {

    private RecyclerView transactionListView;
    private RecyclerView.Adapter adapter;

    private List<ListTransaction> listTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_logs);

        transactionListView = findViewById(R.id.transactionsListView);
        transactionListView.setHasFixedSize(true);
        transactionListView.setLayoutManager(new LinearLayoutManager(this));

        listTransactions = new ArrayList<>();

        for(int i=0; i<=9; i++){
            ListTransaction listTransaction = new ListTransaction(
                    "Harold" + i,
                    "1234 5678",
                    "-$100",
                    "654820842081",
                    "10 June 2018",
                    "15:30"
            );

            listTransactions.add(listTransaction);
        }

        adapter = new TransactAdapter(listTransactions,this);

        transactionListView.setAdapter(adapter);
    }


}
