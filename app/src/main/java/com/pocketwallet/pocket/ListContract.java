package com.pocketwallet.pocket;

public class ListContract {
    private String nameInvolved;
    private String numberInvolved;
    private String description;
    private String endDate;

    public ListContract(String nameInvolved, String numberInvolved, String description, String endDate){
        this.nameInvolved = nameInvolved;
        this.numberInvolved = numberInvolved;
        this.description = description;
        this.endDate = endDate;
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
}
