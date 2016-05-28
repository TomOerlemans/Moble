package com.example.tom.moble;

/**
 * Created by Tom on 5/28/2016.
 */
public class DatabaseEntry {
    // private variables
    public int _id;
    public String _english;
    public String _portuguese;
    public String _daytime;

    public DatabaseEntry() {
    }

    // constructor
    public DatabaseEntry(int id, String name, String _portuguese, String _daytime) {
        this._id = id;
        this._english = name;
        this._portuguese = _portuguese;
        this._daytime = _daytime;

    }

    // constructor
    public DatabaseEntry(String name, String _portuguese, String _daytime) {
        this._english = name;
        this._portuguese = _portuguese;
        this._daytime = _daytime;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    // getting name
    public String getName() {
        return this._english;
    }

    // setting name
    public void setName(String name) {
        this._english = name;
    }

    // getting portuguese
    public String getPortuguese() {
        return this._portuguese;
    }

    // setting portuguese
    public void setPortuguese(String portuguese) {
        this._portuguese = portuguese;
    }

    // getting email
    public String getDaytime() {
        return this._daytime;
    }

    // setting email
    public void setDaytime(String daytime) {
        this._daytime = daytime;
    }

}