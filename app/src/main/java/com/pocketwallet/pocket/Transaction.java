package com.pocketwallet.pocket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
    private boolean isIncoming;

    private String title = " ";

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
                       String dateStr, boolean incoming){
        this.transactionID = transactionRefID;
        this.type = type;
        this.origin = senderID;
        this.destination = receiverID;
        this.amount = amount;
        this.isIncoming = incoming;

        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        Date date = null;

        try {
            date = sourceFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.timestamp = date;
        System.out.println("Timestamp: " + timestamp);

    }

    public String getTransactionID(){
        return transactionID;
    }

    public String getType(){
        return type;
    }

    public String getAmount(){
        return amount;
    }

    public String getNumber(){
        return numberInvolved;
    }

    public boolean getisIncoming() {return isIncoming; }

    public String getName() {
        if (!getisIncoming()) {
            return destination;
        }

        return origin;
    }
    public String getTimestampToString () {
        SimpleDateFormat destinationFormat = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mma");
        destinationFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String dateFormat = destinationFormat.format(timestamp);
        return dateFormat;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String tempTitle){
        this.title = tempTitle;
    }
}
