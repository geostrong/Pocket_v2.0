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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, final int position){
        ListContract listContract = listContracts.get(position);

        holder.textViewName.setText(listContract.getName());
        holder.textViewNumber.setText(listContract.getNumber());
        holder.textViewDescription.setText(listContract.getDescription());
        holder.textViewEndDate.setText(listContract.getEndDate());

        final boolean isExpanded = position==mExpandedPosition;
        holder.textViewDescription.setVisibility(isExpanded?View.GONE:View.VISIBLE);
        holder.textViewEndDate.setVisibility(isExpanded?View.GONE:View.VISIBLE);
        holder.textViewDescription2.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewEndDate2.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewFeePerFreq.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewFrequency.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewPerSign.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.textViewTerminateContractBtn.setVisibility(isExpanded?View.VISIBLE:View.GONE);
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
        return listContracts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewNumber, textViewDescription, textViewEndDate, textViewDescription2, textViewEndDate2, textViewFeePerFreq, textViewFrequency, textViewPerSign, textViewTerminateContractBtn, textViewMoreBtn;

        public ViewHolder(View itemView){
            super(itemView);

            textViewName = itemView.findViewById(R.id.nameInvolved);
            textViewNumber = itemView.findViewById(R.id.numberInvolved);
            textViewDescription = itemView.findViewById(R.id.description);
            textViewEndDate = itemView.findViewById(R.id.endDate);
            textViewDescription2 = itemView.findViewById(R.id.description2);
            textViewEndDate2 = itemView.findViewById(R.id.endDate2);
            textViewFeePerFreq = itemView.findViewById(R.id.feePerFreq);
            textViewFrequency = itemView.findViewById(R.id.frequency);
            textViewPerSign = itemView.findViewById(R.id.perSign);
            textViewTerminateContractBtn = itemView.findViewById(R.id.terminateContractBtn);
            textViewMoreBtn = itemView.findViewById(R.id.moreBtn);
        }
    }
}
