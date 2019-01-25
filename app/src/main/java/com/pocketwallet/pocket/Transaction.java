package com.pocketwallet.pocket;

import java.util.Date;

public class Transaction {
    private String nameInvolved;
    private String numberInvolved;
    private String transactAmount;
    private String transactID;
    private String transactDate;
    private String transactTime;

    private String transactionID;
    private String type;
    private String origin;
    private String destination;
    private double amount;
    private Date timestamp;

    public Transaction (String nameInvolved, String numberInvolved, String transactAmount, String transactID, String transactDate, String transactTime){
        this.nameInvolved = nameInvolved;
        this.numberInvolved = numberInvolved;
        this.transactAmount = transactAmount;
        this.transactID = transactID;
        this.transactDate = transactDate;
        this.transactTime = transactTime;
    }

    public Transaction(String transactionRefID, String type, String senderID, String receiverID, double amount,
                       Date date){
        this.transactionID = transactionRefID;
        this.type = type;
        this.origin = senderID;
        this.destination = receiverID;
        this.amount = amount;
        this.timestamp = date;
    }

    public String getName(){
        return nameInvolved;
    }

    public String getNumber(){
        return numberInvolved;
    }

    public String getTransactAmount(){
        return transactAmount;
    }

    public String getTransactID(){
        return transactID;
    }

    public String getTransactDate(){
        return transactDate;
    }

    public String getTransactTime(){
        return transactTime;
    }
}
