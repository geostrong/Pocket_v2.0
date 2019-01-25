package com.pocketwallet.pocket;

public class ListTransaction {
    private String nameInvolved;
    private String numberInvolved;
    private String transactAmount;
    private String transactID;
    private String transactDate;
    private String transactTime;

    public ListTransaction(){

    }

    public ListTransaction (String nameInvolved, String numberInvolved, String transactAmount, String transactID, String transactDate, String transactTime){
        this.nameInvolved = nameInvolved;
        this.numberInvolved = numberInvolved;
        this.transactAmount = transactAmount;
        this.transactID = transactID;
        this.transactDate = transactDate;
        this.transactTime = transactTime;
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
