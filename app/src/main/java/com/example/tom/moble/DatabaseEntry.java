package com.example.tom.moble;

/**
 * Created by Tom on 5/28/2016.
 */
public class DatabaseEntry {
    // private variables
    public int _id;
    public String _category;
    public String _english;
    public String _portuguese;
    public String _entrytest;
    public String _finaltest;


    public DatabaseEntry() {
    }

    // constructor
    public DatabaseEntry(String _category, String _english, String _portuguese) {

        this._category = _category;
        this._english = _english;
        this._portuguese = _portuguese;
        this._entrytest="";
        this._finaltest="";

    }




    public DatabaseEntry(int id, String _category, String _english, String _portuguese, String _entrytest, String _finaltest) {
        this._id = id;
        this._category = _category;
        this._english = _english;
        this._portuguese = _portuguese;
        this._entrytest=_entrytest;
        this._finaltest=_finaltest;

    }

    // constructor
    public DatabaseEntry(String _category, String _english, String _portuguese, String _entrytest, String _finaltest) {
        this._category = _category;
        this._english = _english;
        this._portuguese = _portuguese;
        this._entrytest=_entrytest;
        this._finaltest=_finaltest;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    // getting category
    public String getCategory() {
        return this._category;
    }

    // setting category
    public void setCategory(String _category) {
        this._category = _category;
    }

    // getting english
    public String getEnglish() {
        return this._english;
    }

    // setting english
    public void setEnglish(String _english) {
        this._english = _english;
    }

    // getting portuguese
    public String getPortuguese() {
        return this._portuguese;
    }

    // setting portuguese
    public void setPortuguese(String _portuguese) {
        this._portuguese = _portuguese;
    }

    // getting enrty test
    public String getEntryTest() {
        return this._entrytest;
    }

    // setting portuguese
    public void setEntryTest(String _entrytest) {
        this._entrytest = _entrytest;
    }

    // getting portuguese
    public String getFinalTest() {
        return this._finaltest;
    }

    // setting portuguese
    public void setFinalTest(String _finaltest) {
        this._finaltest = _finaltest;
    }

}