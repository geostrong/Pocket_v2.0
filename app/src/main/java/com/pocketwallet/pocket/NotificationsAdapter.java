package com.pocketwallet.pocket;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder2> {

    private List<Notification> notificationList;
    private Context context;

    public NotificationsAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_notification_view,viewGroup, false);
        return new ViewHolder2(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder2 viewHolder2, int i) {
        Notification notification = notificationList.get(i);


        viewHolder2.textViewTitle.setText(notification.getTitle());
        viewHolder2.textViewMessage.setText(notification.getMessage());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewMessage;

        public ViewHolder2(View itemView){
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.notificationTitle);
            textViewMessage = itemView.findViewById(R.id.notificationMessage);
        }

    }
}
