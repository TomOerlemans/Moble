package com.example.tom.moble;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Tom on 5/28/2016.
 */

public class DatabaseHandler  extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "wordDatabase";

    // Contacts table name
    private static final String TABLE_WORDS = "words";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ENGLISH = "english_translation";
    private static final String KEY_PORTUGUESE= "portuguese_translation";
    private static final String KEY_DAYTIME = "daytime_key";
    private final ArrayList<DatabaseEntry> word_list = new ArrayList<DatabaseEntry>();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_WORDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ENGLISH + " TEXT,"
                + KEY_PORTUGUESE + " TEXT," + KEY_DAYTIME + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void Add_Contact(DatabaseEntry databaseEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ENGLISH, databaseEntry.getName()); // Contact Name
        values.put(KEY_PORTUGUESE, databaseEntry.getPhoneNumber()); // Contact Phone
        values.put(KEY_DAYTIME, databaseEntry.getEmail()); // Contact Email
        // Inserting Row
        db.insert(TABLE_WORDS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    DatabaseEntry Get_Contact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WORDS, new String[] { KEY_ID,
                        KEY_ENGLISH, KEY_PORTUGUESE, KEY_DAYTIME }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        DatabaseEntry databaseEntry = new DatabaseEntry(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return contact
        cursor.close();
        db.close();

        return databaseEntry;
    }

    // Getting All Contacts
    public ArrayList<DatabaseEntry> Get_Contacts() {
        try {
            word_list.clear();

            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_WORDS;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    DatabaseEntry databaseEntry = new DatabaseEntry();
                    databaseEntry.setID(Integer.parseInt(cursor.getString(0)));
                    databaseEntry.setName(cursor.getString(1));
                    databaseEntry.setPhoneNumber(cursor.getString(2));
                    databaseEntry.setEmail(cursor.getString(3));
                    // Adding contact to list
                    word_list.add(databaseEntry);
                } while (cursor.moveToNext());
            }

            // return contact list
            cursor.close();
            db.close();
            return word_list;
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("all_contact", "" + e);
        }

        return word_list;
    }

    // Updating single contact
    public int Update_Contact(DatabaseEntry databaseEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ENGLISH, databaseEntry.getName());
        values.put(KEY_PORTUGUESE, databaseEntry.getPhoneNumber());
        values.put(KEY_DAYTIME, databaseEntry.getEmail());

        // updating row
        return db.update(TABLE_WORDS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(databaseEntry.getID()) });
    }

    // Deleting single contact
    public void Delete_Contact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORDS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    // Getting contacts Count
    public int Get_Total_Contacts() {
        String countQuery = "SELECT  * FROM " + TABLE_WORDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}

