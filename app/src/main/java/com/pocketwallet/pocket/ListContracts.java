package com.pocketwallet.pocket;

public class ListContracts {
    private String name;
    private String number;
    private String description;
    private String endDate;

    public ListContracts(String name, String number, String description, String endDate){
        this.name = name;
        this.number = number;
        this.description = description;
        this.endDate = endDate;
    }

    public String getName(){
        return name;
    }

    public String getNumber(){
        return number;
    }

    public String getDescription(){
        return description;
    }

    public String getEndDate(){
        return endDate;
    }
}
