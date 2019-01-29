package com.pocketwallet.pocket;

import android.content.Context;
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
        holder.textViewName.setText(listContract.getName());
        holder.textViewNumber.setText(listContract.getNumber());
        holder.textViewStatus.setText(listContract.getStatus());
        holder.textViewEndDate.setText(listContract.getEndDate());
        holder.textViewStatus.setText(listContract.getStatus());
        holder.textViewEndDate2.setText(listContract.getEndDate2());
        holder.textViewFeePerFreq.setText(listContract.getFeePerFreq());
        holder.textViewFrequency.setText(listContract.getFrequency());
        holder.textViewStartDate.setText(listContract.getStartDate());
        holder.textViewPayingOn.setText(listContract.getPayingOn());


        final boolean isExpanded = position==mExpandedPosition;
        holder.textViewEndDate.setVisibility(isExpanded?View.GONE:View.VISIBLE);
        holder.textViewEndDate2.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewFeePerFreq.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewFrequency.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewPerSign.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewMoreBtn.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewStartDate.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewDashText.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewNumber.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewName.setVisibility(isExpanded?View.VISIBLE:View.GONE);
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
        return listContracts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewNumber, textViewDescription, textViewEndDate, textViewPayingOn, textViewEndDate2, textViewFeePerFreq, textViewFrequency, textViewPerSign, textViewMoreBtn, textViewStatus, textViewStartDate, textViewDashText, textViewPayingOnText;

        public ViewHolder(View itemView){
            super(itemView);

            textViewName = itemView.findViewById(R.id.receiverName);
            textViewNumber = itemView.findViewById(R.id.transactTimestamp);
            textViewDescription = itemView.findViewById(R.id.contractName);
            textViewEndDate = itemView.findViewById(R.id.endDate);
            textViewEndDate2 = itemView.findViewById(R.id.endDate2);
            textViewFeePerFreq = itemView.findViewById(R.id.amount);
            textViewFrequency = itemView.findViewById(R.id.frequency);
            textViewPerSign = itemView.findViewById(R.id.perSign);
            textViewMoreBtn = itemView.findViewById(R.id.moreBtn);
            textViewStatus = itemView.findViewById(R.id.status);
            textViewStartDate = itemView.findViewById(R.id.startDate);
            textViewDashText = itemView.findViewById(R.id.dashText);
            textViewPayingOn = itemView.findViewById(R.id.payingOn);
            textViewPayingOnText = itemView.findViewById(R.id.payingOnText);
        }
    }
}
