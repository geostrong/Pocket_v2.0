package com.pocketwallet.pocket;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;


public class ContractDetailsAdapter extends RecyclerView.Adapter<ContractDetailsAdapter.ViewHolder> {

    int mExpandedPosition = -1;
    int previousExpandedPosition = -1;

    private List<ListContract> listContracts;
    private Context context;

    public ContractDetailsAdapter(List<ListContract> listContracts, Context context) {
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

        holder.textViewReceiverName.setText(listContract.getReceiverName());
        holder.textViewReceiverPhoneNum.setText(listContract.getReceiverPhoneNum());
        holder.textViewPayeeName.setText(listContract.getPayeeName());
        holder.textViewPayeePhoneNum.setText(listContract.getPayeePhoneNum());
        //holder.textViewContractName.setText(listContract.getContractName());
        holder.textViewContractStatus.setText(listContract.getContractStatus());
        holder.textViewAmount.setText(listContract.getAmount());
        holder.textViewStartDate.setText(listContract.getStartDate());
        holder.textViewEndDate.setText(listContract.getEndDate());
        holder.textViewPenaltyAmount.setText(listContract.getPenaltyAmount());
        holder.textViewDescription.setText(listContract.getDescription());
    }

    @Override
    public int getItemCount(){
        return listContracts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewReceiverName, textViewReceiverPhoneNum, textViewPayeeName, textViewPayeePhoneNum, textViewContractName, textViewContractStatus, textViewAmount, textViewStartDate, textViewEndDate, textViewPenaltyAmount, textViewDescription;


        public ViewHolder(View itemView){
            super(itemView);

            textViewReceiverName = itemView.findViewById(R.id.receiverName);
            textViewReceiverPhoneNum = itemView.findViewById(R.id.receiverPhoneNum);
            textViewPayeeName = itemView.findViewById(R.id.payeeName);
            textViewPayeePhoneNum = itemView.findViewById(R.id.payeePhoneNum);
            textViewContractName = itemView.findViewById(R.id.contractName);
            textViewContractStatus = itemView.findViewById(R.id.status);
            textViewAmount = itemView.findViewById(R.id.amount);
            textViewStartDate = itemView.findViewById(R.id.startDate);
            textViewEndDate = itemView.findViewById(R.id.endDate);
            textViewPenaltyAmount = itemView.findViewById(R.id.penaltyAmount);
            textViewDescription = itemView.findViewById(R.id.description);
        }
    }
}
