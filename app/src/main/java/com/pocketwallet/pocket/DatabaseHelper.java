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

    //TABLE COLUMNS
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_BODY + " TEXT " + ")";
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);

        // Create tables again
        onCreate(db);
    }

    public void addNotification(String title, String body) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_BODY, body);

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
}
