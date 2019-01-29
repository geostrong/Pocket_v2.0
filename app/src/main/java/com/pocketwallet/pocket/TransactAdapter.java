package com.pocketwallet.pocket;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TransactAdapter extends RecyclerView.Adapter<TransactAdapter.ViewHolder> implements Filterable {

    int mExpandedPosition = -1;
    int previousExpandedPosition = -1;

    private List<Transaction> listTransactions;
    private List<Transaction> transactionListFiltered;
    private Context context;
    private TransactAdapterListener listener;

    public TransactAdapter(List<Transaction> listTransactions, Context context) {
        this.listTransactions = listTransactions;
        this.context = context;
        this.transactionListFiltered = listTransactions;
    }

    @Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_transaction_view,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, final int position){
        Transaction t = transactionListFiltered.get(position);

        holder.textViewName.setText(t.getName());

        if (!t.getisIncoming()) {
            holder.textViewTransactAmount.setTextColor(    ContextCompat.getColor(context,R.color.colorAccent));
            holder.textViewTransactAmount.setText("-$" + t.getAmount());
            holder.textViewTimestamp.setText("Sent on " + t.getTimestampToString());
        } else {
            holder.textViewTransactAmount.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
            holder.textViewTransactAmount.setText("+$" + t.getAmount());
            holder.textViewTimestamp.setText(t.getTimestampToString());
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
        return transactionListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                System.out.println("F) Filtered string: + " + charSequence.toString());
                if (charString.isEmpty()) {
                    transactionListFiltered = listTransactions;
                } else {
                    List<Transaction> filteredList = new ArrayList<>();
                    for (Transaction t: listTransactions) {

                        //name match condition
                        if (t.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(t);
                        }
                    }

                    System.out.println("F) Filtered list size: + " + filteredList.size());
                    transactionListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = transactionListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                transactionListFiltered = (ArrayList<Transaction>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public interface TransactAdapterListener {
        void onTransactionSelected(Transaction transaction);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewNumber, textViewTransactAmount, textViewTransactID, textViewMoreBtn, textViewTimestamp;

        public ViewHolder(View itemView){
            super(itemView);

            textViewName = itemView.findViewById(R.id.nameInvolved);
            textViewNumber = itemView.findViewById(R.id.transactTimestamp);
            textViewTransactAmount = itemView.findViewById(R.id.transactAmount);
            textViewMoreBtn = itemView.findViewById(R.id.moreButton);
            textViewTimestamp = itemView.findViewById(R.id.transactTimestamp);
        }

    }

}
