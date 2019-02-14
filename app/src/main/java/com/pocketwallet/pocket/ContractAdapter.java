package com.pocketwallet.pocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;


public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ViewHolder> {

    int mExpandedPosition = -1;
    int previousExpandedPosition = -1;

    private List<ListContract> listContracts;
    private Context context;
    private String userId;

    public ContractAdapter(List<ListContract> listContracts, Context context) {
        this.listContracts = listContracts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_contract_view,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, final int position){
        ListContract listContract = listContracts.get(position);


        holder.textViewDescription.setText((listContract.getDescription()));
        holder.textViewUser2ID.setText(listContract.getUser2ID());
        holder.textViewEndDate.setText(listContract.getEndDate());
        holder.textViewContractStatus.setText(listContract.getContractStatus());
        //holder.textViewAmount.setText(listContract.getAmount());
        //holder.textViewFrequency.setText(listContract.getFrequency());
        holder.textViewStartDate.setText(listContract.getStartDate());


        final boolean isExpanded = position==mExpandedPosition;
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

        holder.textViewMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(v.getContext(), ContractActivity_Details.class);
                newIntent.putExtra("userId",userId);
                v.getContext().startActivity(newIntent);
                System.out.println("========================== User ID: " + userId + " =========================");
            }
        });
    }

    @Override
    public int getItemCount(){
        return listContracts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //TextView textViewName, textViewNumber, textViewDescription, textViewEndDate, textViewPayingOn, textViewEndDate2, textViewFeePerFreq, textViewFrequency, textViewPerSign, textViewMoreBtn, textViewStatus, textViewStartDate, textViewDashText, textViewPayingOnText;

        TextView textViewDashText, textViewMoreBtn, textViewFrequency, textViewContractID, textViewContractStatus, textViewDescription, textViewAmount, textViewStartDate, textViewEndDate, textViewEndDate2, textViewUser2ID;

        public ViewHolder(View itemView){
            super(itemView);

            textViewContractStatus = itemView.findViewById(R.id.status);
            textViewDescription = itemView.findViewById(R.id.contractName);
            textViewEndDate = itemView.findViewById(R.id.endDate);
            textViewAmount = itemView.findViewById(R.id.amount);
            textViewFrequency = itemView.findViewById(R.id.frequency);
            textViewMoreBtn = itemView.findViewById(R.id.moreBtn);
            //textViewContractID = itemView.findViewById(R.id.);
            textViewStartDate = itemView.findViewById(R.id.startDate);
            textViewDashText = itemView.findViewById(R.id.dashText);
            textViewUser2ID = itemView.findViewById(R.id.user2ID);
        }
    }
}
