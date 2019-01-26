package com.pocketwallet.pocket;

public class ListContract {
    private String nameInvolved;
    private String numberInvolved;
    private String description;
    private String endDate;
    private String feePerFreq;
    private String frequency;
    private String status;
    private String startDate;
    private String payingOn;


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
