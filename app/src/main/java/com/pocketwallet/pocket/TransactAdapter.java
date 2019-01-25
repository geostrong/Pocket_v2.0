package com.pocketwallet.pocket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class TransactAdapter extends RecyclerView.Adapter<TransactAdapter.ViewHolder> {

    int mExpandedPosition = -1;
    int previousExpandedPosition = -1;

    private List<ListTransaction> listTransactions;
    private Context context;

    public TransactAdapter(List<ListTransaction> listTransactions, Context context) {
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
        ListTransaction listTransaction = listTransactions.get(position);

        holder.textViewName.setText(listTransaction.getName());
        holder.textViewTransactAmount.setText(listTransaction.getTransactAmount());

        final boolean isExpanded = position==mExpandedPosition;
        holder.textViewNumber.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewTransactID.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewTransactDate.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewTransactTime.setVisibility(isExpanded?View.VISIBLE:View.GONE);
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

        TextView textViewName, textViewNumber, textViewTransactAmount, textViewTransactID, textViewTransactDate, textViewTransactTime;

        public ViewHolder(View itemView){
            super(itemView);

            textViewName = itemView.findViewById(R.id.nameInvolved);
            textViewNumber = itemView.findViewById(R.id.numberInvolved);
            textViewTransactAmount = itemView.findViewById(R.id.description);
            textViewTransactID = itemView.findViewById(R.id.endDate);
            textViewTransactDate = itemView.findViewById(R.id.description2);
            textViewTransactTime = itemView.findViewById(R.id.endDate2);
        }

    }

}
