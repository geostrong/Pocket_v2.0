package com.pocketwallet.pocket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "pocket_db";

    //TABLE NAME
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String TABLE_LOYALTY_CARDS = "loyalty_cards";

    //TABLE COLUMNS
    private static final String NOTIF_KEY_ID = "id";
    private static final String NOTIF_KEY_TITLE = "title";
    private static final String NOTIF_KEY_BODY = "body";

    //TABLE COLUMNS
    private static final String LOYALTY_KEY_ID = "id";
    private static final String LOYALTY_KEY_COMPANY_NAME = "company_name";
    private static final String LOYALTY_KEY_NUM = "number";
    private static final String LOYALTY_KEY_EXPIRY = "expiry";


    final String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
            + NOTIF_KEY_ID + " INTEGER PRIMARY KEY," + NOTIF_KEY_TITLE + " TEXT,"
            + NOTIF_KEY_BODY + " TEXT " + ")";

    final String CREATE_LOYALTY_TABLE = "CREATE TABLE " + TABLE_LOYALTY_CARDS + "("
            + LOYALTY_KEY_ID + " INTEGER PRIMARY KEY," + LOYALTY_KEY_COMPANY_NAME + " TEXT,"
            + LOYALTY_KEY_NUM + " TEXT, " + LOYALTY_KEY_EXPIRY + " TEXT " + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
        db.execSQL(CREATE_LOYALTY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOYALTY_CARDS);

        // Create tables again
        onCreate(db);
    }

    //NOTIFICATIONS
    public void addNotification(String title, String body) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTIF_KEY_TITLE, title);
        values.put(NOTIF_KEY_BODY, body);

        db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
    }

    public List<Notification> getAllNotifications () {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_NOTIFICATIONS;
        Cursor cursor = db.rawQuery(query, null);
        List<Notification> notificationList = new ArrayList<Notification>();
        Notification notification = null;

        if (cursor.moveToFirst()) {
            do {
                notification = new Notification();
                notification.setId(cursor.getString(0));
                notification.setTitle(cursor.getString(1));
                notification.setMessage(cursor.getString(2));
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }
        return notificationList;
    }

    public void deleteNotification (Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTIFICATIONS, "id = ?", new String[] { String.valueOf(notification.getId()) });
        db.close();
    }

    //LOYALTY
    public void addLoyalty(String name, String num, String expiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOYALTY_KEY_COMPANY_NAME, name);
        values.put(LOYALTY_KEY_NUM, num);
        values.put(LOYALTY_KEY_EXPIRY, expiry);

        db.insert(TABLE_LOYALTY_CARDS, null, values);
        db.close();
    }


    public List<LoyaltyCard> getAllLoyaltyCards () {
        int i = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("DB HELPER:" + i++);
        String query = "SELECT  * FROM " + TABLE_LOYALTY_CARDS;
        System.out.println("DB HELPER:" + i++);
        Cursor cursor = db.rawQuery(query, null);
        System.out.println("DB HELPER:" + i++);
        List<LoyaltyCard> cardList = new ArrayList<LoyaltyCard>();
        LoyaltyCard card = null;
        System.out.println("DB HELPER:" + i++);
        if (cursor.moveToFirst()) {
            do {
                card = new LoyaltyCard();
                card.setId(cursor.getString(0));
                card.setCompanyName(cursor.getString(1));
                card.setNum(cursor.getString(2));
                card.setExpiry(cursor.getString(3));
                cardList.add(card);
            } while (cursor.moveToNext());
        }
        System.out.println("DB HELPER:" + i++);
        return cardList;
    }

    public void deleteLoyaltyCard (LoyaltyCard card) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOYALTY_CARDS, "id = ?", new String[] { String.valueOf(card.getId()) });
        db.close();
    }

    public void cleanDB () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTIFICATIONS, null,null);
        db.delete(TABLE_LOYALTY_CARDS,null,null);
    }
}
