package com.pocketwallet.pocket;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LoyaltyAdapter extends RecyclerView.Adapter<LoyaltyAdapter.ViewHolder3>{
        private List<LoyaltyCard> cardList;
        private Context context;

    public LoyaltyAdapter(List<LoyaltyCard> cardList, Context context) {
        this.cardList = cardList;
        this.context = context;
    }

    @NonNull
    @Override
    public LoyaltyAdapter.ViewHolder3 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_loyalty_card_view,viewGroup, false);
        return new LoyaltyAdapter.ViewHolder3(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LoyaltyAdapter.ViewHolder3 viewHolder, int i) {
        LoyaltyCard card = cardList.get(i);

        viewHolder.textViewName.setText(card.getCompanyName());
        viewHolder.textViewNum.setText(card.getNum());
        viewHolder.textViewExpiry.setText("Until: " + card.getExpiry());

        final String num = card.getNum();
        final String name = card.getCompanyName();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(v.getContext(), LoyaltyActivity_Details.class);
                newIntent.putExtra("num",num);
                newIntent.putExtra("name", name);
                v.getContext().startActivity(newIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class ViewHolder3 extends RecyclerView.ViewHolder {

        TextView textViewName, textViewNum, textViewExpiry;

        public ViewHolder3(View itemView){
            super(itemView);

            textViewName = itemView.findViewById(R.id.companyName);
            textViewNum = itemView.findViewById(R.id.loyaltyCardNum);
            textViewExpiry = itemView.findViewById(R.id.loyaltyCardExpiry);
        }

    }
}
