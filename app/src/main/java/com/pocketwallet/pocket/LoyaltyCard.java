package com.pocketwallet.pocket;

public class LoyaltyCard {
    private String id;
    private String company_name;
    private String num;
    private String expiry;

    public LoyaltyCard() {
    }

    public LoyaltyCard(String id, String company_name, String num, String expiry) {
        this.id = id;
        this.company_name = company_name;
        this.num = num;
        this.expiry = expiry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return company_name;
    }

    public void setCompanyName(String comapny_name) {
        this.company_name = comapny_name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
