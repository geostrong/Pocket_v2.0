package com.pocketwallet.pocket;

public class Notification {
    private String id;
    private String title;
    private String message;

    public Notification() {
    }

    public Notification(String id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
