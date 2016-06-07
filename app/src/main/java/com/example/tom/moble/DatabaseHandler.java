package com.example.tom.moble;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Tom on 5/28/2016.
 */

public class DatabaseHandler  extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 5; //?

    // Database Name
    private static final String DATABASE_NAME = "wordDatabase";

    // Contacts table name
    private static final String TABLE_WORDS = "words";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_ENGLISH= "english";
    private static final String KEY_PORTUGUESE = "portuguese";
    private static final String KEY_ENTRYTEST = "entrytest";
    private static final String KEY_FINALTEST = "finaltest";
    private final ArrayList<DatabaseEntry> word_list = new ArrayList<DatabaseEntry>();
    private final ArrayList<DatabaseEntry> id_list = new ArrayList<DatabaseEntry>();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
//        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_WORDS + "("
//                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_CATEGORY + " TEXT,"
//                + KEY_ENGLISH + " TEXT," + KEY_PORTUGUESE + " TEXT," + KEY_ENTRYTEST
//                + " TEXT," + KEY_FINALTEST + " TEXT" +" );";
//        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_WORDS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_CATEGORY + " TEXT," +
                KEY_ENGLISH + " TEXT," +
                KEY_PORTUGUESE + " TEXT," +
                KEY_ENTRYTEST + " TEXT," +
                KEY_FINALTEST + " TEXT" + ");";
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

    // Adding new entry
    public void addEntry(DatabaseEntry databaseEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
//        System.out.println("@ addNtry: "+ values.toString());
        values.put(KEY_CATEGORY, databaseEntry.getCategory());
        values.put(KEY_ENGLISH, databaseEntry.getEnglish());
        values.put(KEY_PORTUGUESE, databaseEntry.getPortuguese());
        values.put(KEY_ENTRYTEST, databaseEntry.getEntryTest());
        values.put(KEY_FINALTEST, databaseEntry.getFinalTest());
        // Inserting Row
        db.insert(TABLE_WORDS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single entry
    public DatabaseEntry getEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WORDS, new String[] { KEY_ID,
                        KEY_CATEGORY, KEY_ENGLISH, KEY_PORTUGUESE, KEY_ENTRYTEST, KEY_FINALTEST }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
//        System.out.println("~~~~~~~~~~~~~~~~   WHITHIN DBH.JAVA  ``````````````````");
//        System.out.println(String.valueOf(id));
//        System.out.println(cursor.getColumnCount());
//        System.out.println(cursor.getColumnName(cursor.getColumnCount()));
//        System.out.println(cursor.getColumnName(cursor.getColumnCount()-1));
//        System.out.println(String.valueOf(getAllEntries().size()));
//        System.out.println(cursor.getString(3));
//        System.out.println(cursor.getString(4));
//        System.out.println(cursor.getString(5));
//        System.out.println(cursor.getClass());
        DatabaseEntry databaseEntry = new DatabaseEntry(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        // return entry
//        System.out.println("~~~~~~~~~~~~~~~~   WHITHIN DBH.JAVA2  ``````````````````");
        cursor.close();
        db.close();
        return databaseEntry;
    }





    // Getting All Entry
    public ArrayList<DatabaseEntry> getAllEntries() {
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
                    databaseEntry.setCategory(cursor.getString(1));
                    databaseEntry.setEnglish(cursor.getString(2));
                    databaseEntry.setPortuguese(cursor.getString(3));
                    databaseEntry.setEntryTest(cursor.getString(4));
                    databaseEntry.setFinalTest(cursor.getString(5));
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



    public ArrayList<DatabaseEntry> getAllIdByCategory(String category) {
        try {
            id_list.clear();

            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_WORDS+" WHERE KEY_CATEGORY ="+ category;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    DatabaseEntry databaseEntry = new DatabaseEntry();
                    databaseEntry.setID(Integer.parseInt(cursor.getString(0)));
                    databaseEntry.setCategory(cursor.getString(1));
                    databaseEntry.setEnglish(cursor.getString(2));
                    databaseEntry.setPortuguese(cursor.getString(3));
                    databaseEntry.setEntryTest(cursor.getString(4));
                    databaseEntry.setFinalTest(cursor.getString(5));
//                     Adding contact to list
                    id_list.add(databaseEntry);
                } while (cursor.moveToNext());
            }

            // return contact list
            cursor.close();
            db.close();
            return id_list;
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("all_contact", "" + e);
        }

        return id_list;
    }




    // Updating single entry
    public int updateEntry(DatabaseEntry databaseEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY, databaseEntry.getCategory());
        values.put(KEY_ENGLISH, databaseEntry.getEnglish());
        values.put(KEY_PORTUGUESE, databaseEntry.getPortuguese());
        values.put(KEY_ENTRYTEST, databaseEntry.getPortuguese());
        values.put(KEY_FINALTEST, databaseEntry.getPortuguese());

        // updating row
        return db.update(TABLE_WORDS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(databaseEntry.getID()) });
    }

    // Deleting single entry
    public void deleteEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORDS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    // Getting entries Count
    public int getEntryCount() {
        String countQuery = "SELECT  * FROM " + TABLE_WORDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;


    }

}

