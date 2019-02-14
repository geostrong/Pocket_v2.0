package com.pocketwallet.pocket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LoyaltyActivity extends AppCompatActivity {
    private RecyclerView cardListView;
    private LoyaltyAdapter adapter;
    private FloatingActionButton fab;
    private List<LoyaltyCard> cardList;

    private DatabaseHelper db;

    private static final int EDIT_CODE = 31;
    boolean reloadNeeded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty);

        getSupportActionBar().setTitle("Loyalty Cards");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        cardList = new ArrayList<>();

        cardListView = findViewById(R.id.loyalCardListView);
        cardListView.setHasFixedSize(true);
        cardListView.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.addLoyaltyBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (LoyaltyActivity.this, LoyaltyActivity_Create.class);
                startActivityForResult(intent, EDIT_CODE);
            }
        });

        db = new DatabaseHelper(this);

        cardList = db.getAllLoyaltyCards();
        adapter = new LoyaltyAdapter(cardList,this);
        cardListView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_CODE) { // Ah! We are back from EditActivity, did we make any changes?
            if (resultCode == Activity.RESULT_OK) {
                this.reloadNeeded = true;
            }
        }
    }

        @Override
        public void onResume() {
            super.onResume();

            if (this.reloadNeeded)
                this.reloadData();

            this.reloadNeeded = false; // do not reload anymore, unless I tell you so...
        }

        private void reloadData() {
            cardList = db.getAllLoyaltyCards();
            adapter = new LoyaltyAdapter(cardList,this);
            cardListView.setAdapter(adapter);
        }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
