package com.pocketwallet.pocket;

import java.io.Serializable;

public class ListContract implements Serializable {
    private String nameInvolved;
    private String numberInvolved;
    private String feePerFreq;
    private String status;
    //
    private String payingOn;


    private String contractID,contractStatus,user1ID,user2ID,user1ACK,user2ACK,
                    description,amount,frequency,penaltyAmount,createdDate,startDate,endDate,
                    receiverName,payeeName,contractName,user1_phone,user2_phone;

    private static final long serialVersionUID = 1L;

    public ListContract(String nameInvolved, String numberInvolved, String description, String endDate, String payingOn, String feePerFreq, String frequency, String status, String startDate){
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
                        String endDate,String contractName, String user1_phone, String user2_phone /* String receiverName, String receiverPhoneNum, String payeeName, String payeePhoneNum*/){
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

        this.payeeName = "TestName1";
        this.receiverName = "TestName2";

        this.contractName = contractName;
        this.user1_phone = user1_phone;
        this.user2_phone = user2_phone;
    }

    public String getContractID() {
        return contractID;
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
    public String getUser1PhoneNum() {
        return user1_phone;
    }
    public String getUser2PhoneNum() {
        return user2_phone;
    }
    public String getReceiverName() {
        return receiverName;
    }
    public String getPayeeName() {
        return payeeName;
    }
    public String getContractName(){return contractName;};

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public void setContractName(String contractName) {
        this.contractName = contractName;
    }
}
