package com.pocketwallet.pocket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ViewHolder> {

    private List<ListContract> listContracts;
    private Context context;

    public ContractAdapter(List<ListContract> listContracts, Context context) {
        this.listContracts = listContracts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position){
        ListContract listContract = listContracts.get(position);

        holder.textViewName.setText(listContract.getName());
        holder.textViewNumber.setText(listContract.getNumber());
        holder.textViewDescription.setText(listContract.getDescription());
        holder.textViewEndDate.setText(listContract.getEndDate());
    }

    @Override
    public int getItemCount(){
        return listContracts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewNumber, textViewDescription, textViewEndDate;

        public ViewHolder(View itemView){
            super(itemView);

            textViewName = itemView.findViewById(R.id.nameInvolved);
            textViewNumber = itemView.findViewById(R.id.numberInvolved);
            textViewDescription = itemView.findViewById(R.id.description);
            textViewEndDate = itemView.findViewById(R.id.endDate);
        }
    }
}
