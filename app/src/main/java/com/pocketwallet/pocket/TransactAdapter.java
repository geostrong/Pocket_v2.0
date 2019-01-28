package com.pocketwallet.pocket;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;


public class TransactAdapter extends RecyclerView.Adapter<TransactAdapter.ViewHolder> {

    int mExpandedPosition = -1;
    int previousExpandedPosition = -1;

    private List<Transaction> listTransactions;
    private Context context;

    public TransactAdapter(List<Transaction> listTransactions, Context context) {
        this.listTransactions = listTransactions;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_transaction_view,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, final int position){
        Transaction listTransaction = listTransactions.get(position);

        holder.textViewName.setText(listTransaction.getName());

        if (Integer.valueOf(listTransaction.getTransactAmount()) < 0) {
            holder.textViewTransactAmount.setTextColor(    ContextCompat.getColor(context,R.color.colorAccent));
            holder.textViewTransactAmount.setText("-$" + listTransaction.getTransactAmount());
        } else {
            holder.textViewTransactAmount.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
            holder.textViewTransactAmount.setText("+$" + listTransaction.getTransactAmount());
        }

        final boolean isExpanded = position==mExpandedPosition;

        holder.textViewMoreBtn.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);

        if (isExpanded)
            previousExpandedPosition = position;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount(){
        return listTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewNumber, textViewTransactAmount, textViewTransactID, textViewMoreBtn;

        public ViewHolder(View itemView){
            super(itemView);

            textViewName = itemView.findViewById(R.id.nameInvolved);
            textViewNumber = itemView.findViewById(R.id.transactTimestamp);
            textViewTransactAmount = itemView.findViewById(R.id.transactAmount);
            textViewMoreBtn = itemView.findViewById(R.id.moreButton);
        }

    }

}
