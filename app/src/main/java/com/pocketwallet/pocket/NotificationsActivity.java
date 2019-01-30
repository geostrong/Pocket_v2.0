package com.pocketwallet.pocket;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView notificationsListView;
    private NotificationsAdapter adapter;

    private List<Notification> notificationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        notificationsList = new ArrayList<>();

        //Transaction List
        notificationsListView = findViewById(R.id.notificationListView);
        notificationsListView.setHasFixedSize(true);
        notificationsListView.setLayoutManager(new LinearLayoutManager(this));

        for(int i=0; i<=9; i++){
            Notification notification = new Notification();
            notification.setTitle("Title " + i);
            notification.setMessage(String.valueOf(i));
            notificationsList.add(notification);
        }

        adapter = new NotificationsAdapter(notificationsList,this);
        notificationsListView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        notificationsListView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
