package com.pocketwallet.pocket;

public class ListContract {
    private String nameInvolved;
    private String numberInvolved;
    private String description;
    private String endDate;
    private String description2;
    private String endDate2;
    private String feePerFreq;
    private String frequency;



    public ListContract(String nameInvolved, String numberInvolved, String description, String endDate, String description2, String endDate2, String feePerFreq, String frequency){
        this.nameInvolved = nameInvolved;
        this.numberInvolved = numberInvolved;
        this.description = description;
        this.endDate = endDate;
        this.description2 = description2;
        this.endDate2 = endDate2;
        this.feePerFreq = feePerFreq;
        this.frequency = frequency;
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

    public String getDescription2(){
        return description2;
    }

    public String getEndDate2(){
        return endDate2;
    }

    public String getFeePerFreq(){
        return feePerFreq;
    }

    public String getFrequency(){
        return frequency;
    }
}
