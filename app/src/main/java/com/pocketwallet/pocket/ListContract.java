package com.pocketwallet.pocket;

public class ListContract {
    private String nameInvolved;
    private String numberInvolved;
    //private String description;
    //private String endDate;
    private String feePerFreq;
    //private String frequency;
    private String status;
    //private String startDate;
    private String payingOn;

    private String contractID,contractStatus,user1ID,user2ID,user1ACK,user2ACK,
                    description,amount,frequency,penaltyAmount,createdDate,startDate,endDate;

    public ListContract(String nameInvolved, String numberInvolved, String description, String endDate, String payingOn, String endDate2, String feePerFreq, String frequency, String status, String startDate){
        this.nameInvolved = nameInvolved;
        this.numberInvolved = numberInvolved;
        this.description = description;
        this.endDate = endDate;
        this.feePerFreq = feePerFreq;
        this.frequency = frequency;
        this.status = status;
        this.startDate = startDate;
        this.payingOn = payingOn;
    }

    public ListContract(String contractID, String contractStatus, String user1ID, String user2ID,String user1Ack, String user2Ack,
                        String description, String amount,String frequency, String penaltyAmount, String createdDate, String startDate,
                        String endDate){
        this.contractID = contractID;
        this.contractStatus = contractStatus;
        this.user1ID = user1ID;
        this.user2ID = user2ID;
        this.user1ACK = user1Ack;
        this.user2ACK = user2Ack;
        this.description = description;
        this.amount = amount;
        this.frequency = frequency;
        this.penaltyAmount = penaltyAmount;
        this.createdDate = createdDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getContractStatus() { return contractStatus; }
    public String getUser1ID() { return user1ID; }
    public String getUser2ID() { return user2ID; }
    public String getUser1ACK() { return user1ACK; }
    public String getUser2ACK() { return user2ACK; }
    public String getAmount() { return amount; }
    public String getPenaltyAmount() { return penaltyAmount; }
    public String getCreatedDate() { return createdDate; }

    public String getName(){
        return nameInvolved;
    }
    public String getNumber(){
        return numberInvolved;
    }
    public String getDescription(){
        return description;
    }
    public String getEndDate(){
        return endDate;
    }
    public String getEndDate2(){
        return endDate;
    }
    public String getFeePerFreq(){
        return feePerFreq;
    }
    public String getFrequency(){
        return frequency;
    }
    public String getStatus(){
        return status;
    }
    public String getStartDate(){
        return startDate;
    }
    public String getPayingOn(){
        return payingOn;
    }
}
