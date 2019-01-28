package com.pocketwallet.pocket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private String amount;
    private Date timestamp;

        /*
    public Transaction (String nameInvolved, String numberInvolved, String transactAmount, String transactID, String transactDate, String transactTime){
        this.nameInvolved = nameInvolved;
        this.numberInvolved = numberInvolved;
        this.transactAmount = transactAmount;
        this.transactID = transactID;
        this.transactDate = transactDate;
        this.transactTime = transactTime;
    }*/

    public Transaction(String transactionRefID, String type, String senderID, String receiverID, String amount,
                       String date){
        this.transactionID = transactionRefID;
        this.type = type;
        this.origin = senderID;
        this.destination = receiverID;
        this.amount = amount;
        Date date1 = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            date1 = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.timestamp = date1;
        System.out.println("Timestamp: " + timestamp);
        //Test
    }
    public String getTransactionID(){
        return transactionID;
    }

    public String getType(){
        return type;
    }

    public String getOrigin(){
        return origin;
    }

    public String getDestination(){
        return destination;
    }

    public String getAmount(){
        return amount;
    }

    public String getName(){
        return nameInvolved;
    }

    public String getNumber(){
        return numberInvolved;
    }
}
