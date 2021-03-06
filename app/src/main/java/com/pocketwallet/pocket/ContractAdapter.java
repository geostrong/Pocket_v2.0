package com.pocketwallet.pocket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ViewHolder> {

    int mExpandedPosition = -1;
    int previousExpandedPosition = -1;

    private ArrayList <ListContract> listContracts;
    private Context context;
    private String userId;
    Bundle extras;

    public ContractAdapter(ArrayList<ListContract> listContracts, Context context) {
        this.listContracts = listContracts;
        this.context = context;
        Intent intent = ((Activity) context).getIntent();
        extras = intent.getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_contract_view,parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, final int position){
        ListContract listContract = listContracts.get(position);

        holder.textViewContractName.setText((listContract.getContractName()));
        holder.textViewEndDate.setText(listContract.getEndDate());
        if(listContract.getContractStatus().equals("0")) {
            holder.textViewContractStatus.setText("Pending");
        }else if(listContract.getContractStatus().equals("1")){
            holder.textViewContractStatus.setText("Accepted");
        }else if(listContract.getContractStatus().equals("2")){
            holder.textViewContractStatus.setText("Active");
        }else if(listContract.getContractStatus().equals("3")){
            holder.textViewContractStatus.setText("Declined");
        }else{
            holder.textViewContractStatus.setText("Terminated");
        }

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
                newIntent.putExtra("listContracts",listContracts);
                newIntent.putExtra("position",position);
                v.getContext().startActivity(newIntent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return listContracts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDashText, textViewMoreBtn, textViewFrequency, textViewContractName, textViewContractStatus, textViewDescription, textViewAmount, textViewStartDate, textViewEndDate, textViewEndDate2, textViewPayeeName;

        public ViewHolder(View itemView){
            super(itemView);

            textViewContractStatus = itemView.findViewById(R.id.status);
            textViewContractName = itemView.findViewById(R.id.contractName);
            textViewEndDate = itemView.findViewById(R.id.endDate);
            textViewAmount = itemView.findViewById(R.id.amount);
            textViewFrequency = itemView.findViewById(R.id.frequency);
            textViewMoreBtn = itemView.findViewById(R.id.moreBtn);
            //textViewContractID = itemView.findViewById(R.id.);
            textViewStartDate = itemView.findViewById(R.id.startDate);
            textViewDashText = itemView.findViewById(R.id.dashText);
            textViewPayeeName = itemView.findViewById(R.id.payeeName);
        }
    }
}
