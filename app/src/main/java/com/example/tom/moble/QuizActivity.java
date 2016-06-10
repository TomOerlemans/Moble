package com.example.tom.moble;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {


    final int DATABASESIZE = 7;
    final private int QUIZLENGTH = 3;
    final private int LENGTH_TRAINING_DAYS = 1;
    DatabaseHandler db;
    TextView quizQuestion;
    TextView quizRound;
    TextView quizScore;
    TextView quizTitle;
    Button one;
    Button two;
    Button three;
    Button four;
    Button five;
    Button six;
    Random rgen;
    int correctAnswerDB;
    int correctAnswerButton;
    int score;
    int round;
    boolean lock;
    boolean entryTest;
    boolean dbSpamBlock;
    ArrayList takenIndices;
    ArrayList alreadyAsked;

    public SharedPreferences finalTestDatePref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout);
        db = new DatabaseHandler(this);
        quizRound = (TextView) findViewById(R.id.quizRound);
        quizScore = (TextView) findViewById(R.id.quizScore);
        quizQuestion = (TextView) findViewById(R.id.quizQuestion);
        one = (Button) findViewById(R.id.multipleChoiceAnswer1Button);
        two = (Button) findViewById(R.id.multipleChoiceAnswer2Button);
        three = (Button) findViewById(R.id.multipleChoiceAnswer3Button);
        four = (Button) findViewById(R.id.multipleChoiceAnswer4Button);
        five = (Button) findViewById(R.id.multipleChoiceAnswer5Button);
        six = (Button) findViewById(R.id.multipleChoiceAnswer6Button);
        rgen = new Random();
        takenIndices = new ArrayList();
        alreadyAsked = new ArrayList();
        score = 0;
        round = 1;
        dbSpamBlock = false;
        //check if this is the entry test or final test
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String finalTestDayString = sharedPreferences.getString("Final Test Date", null);
//        System.out.println(finalTestDayString);
//        System.out.println("==================================================");
        quizTitle = (TextView) findViewById(R.id.quizTitle);

        if (finalTestDayString == null){
            entryTest = true;
            quizTitle.setText("Entry Test");

        }else{
            entryTest = false;
            quizTitle.setText("Final Test");
        }
//        System.out.println("entryTest = "+ entryTest);
        setNewQuestion();
    }

    public void setNewQuestion(){
        lock = false;
//        System.out.println("~~~~~~~~~~~~~~~~   new question  ``````````````````");
        //
//        correctAnswerDB = rgen.nextInt(DATABASESIZE) + 1; // see above for alternative implementation
        while (true) {
            correctAnswerDB = rgen.nextInt(DATABASESIZE) + 1; // see above for alternative implementation
//            System.out.println(String.valueOf(correctAnswerDB));
//            System.out.println(db.getEntry(correctAnswerDB)._entrytest);
//            System.out.println(db.getEntry(correctAnswerDB)._finaltest);
//
//            System.out.println(db.getEntry(correctAnswerDB).toString());
            if ((entryTest == true && db.getEntry(correctAnswerDB).getEntryTest() == null) || (entryTest == false && db.getEntry(correctAnswerDB).getFinalTest() == null)) {
                break;
            }
        }

        correctAnswerButton = rgen.nextInt(5) + 1;
//        System.out.println("~~~~~~~~~~~~~~~~   before  ``````````````````");
        quizQuestion.setText(db.getEntry(correctAnswerDB).getEnglish());
//        System.out.println("~~~~~~~~~~~~~~~~   after  ``````````````````");
        one.setBackgroundColor(Color.parseColor("#6AB344"));
        two.setBackgroundColor(Color.parseColor("#6AB344"));
        three.setBackgroundColor(Color.parseColor("#6AB344"));
        four.setBackgroundColor(Color.parseColor("#6AB344"));
        five.setBackgroundColor(Color.parseColor("#6AB344"));
        six.setBackgroundColor(Color.parseColor("#6AB344"));

        takenIndices.clear();
        takenIndices.add(correctAnswerDB);
        one.setText(db.getEntry(excludeRandom()).getPortuguese());
        two.setText(db.getEntry(excludeRandom()).getPortuguese());
        three.setText(db.getEntry(excludeRandom()).getPortuguese());
        four.setText(db.getEntry(excludeRandom()).getPortuguese());
        five.setText(db.getEntry(excludeRandom()).getPortuguese());
        six.setText(db.getEntry(excludeRandom()).getPortuguese());

        switch (correctAnswerButton){
            case 1: one.setText(db.getEntry(correctAnswerDB).getPortuguese()); break;
            case 2: two.setText(db.getEntry(correctAnswerDB).getPortuguese()); break;
            case 3: three.setText(db.getEntry(correctAnswerDB).getPortuguese()); break;
            case 4: four.setText(db.getEntry(correctAnswerDB).getPortuguese()); break;
            case 5: five.setText(db.getEntry(correctAnswerDB).getPortuguese()); break;
            case 6: six.setText(db.getEntry(correctAnswerDB).getPortuguese()); break;
            default: Log.v("QUIZ ACTIVITY", "Problem at the correctAnswerButton switch!!"); break;
        }
    }

    public int excludeRandom(){

        int x = rgen.nextInt(DATABASESIZE) + 1;
        while(takenIndices.contains(x)){
            x = rgen.nextInt(DATABASESIZE) + 1;
        }
        takenIndices.add(x);
        return x;
    }

    public void quizAnswerButtonClick(View view){

            if (lock == false) {
                one.setBackgroundColor(Color.parseColor("#d1332e"));
                two.setBackgroundColor(Color.parseColor("#d1332e"));
                three.setBackgroundColor(Color.parseColor("#d1332e"));
                four.setBackgroundColor(Color.parseColor("#d1332e"));
                five.setBackgroundColor(Color.parseColor("#d1332e"));
                six.setBackgroundColor(Color.parseColor("#d1332e"));

                switch (correctAnswerButton) {
                    case 1:
                        one.setBackgroundColor(Color.parseColor("#89d771"));
                        break;
                    case 2:
                        two.setBackgroundColor(Color.parseColor("#89d771"));
                        break;
                    case 3:
                        three.setBackgroundColor(Color.parseColor("#89d771"));
                        break;
                    case 4:
                        four.setBackgroundColor(Color.parseColor("#89d771"));
                        break;
                    case 5:
                        five.setBackgroundColor(Color.parseColor("#89d771"));
                        break;
                    case 6:
                        six.setBackgroundColor(Color.parseColor("#89d771"));
                        break;
                    default:
                        Log.v("QUIZ ACTIVITY", "Problem at the correctAnswerButton switch!!");
                        break;
                }

                switch (view.getId()) {
                    case R.id.multipleChoiceAnswer1Button:
                        if (correctAnswerButton == 1) {
                            score++;
                            writeToDB("correct");
                        }
                        else{
                            writeToDB("wrong");
                        }
                        break;
                    case R.id.multipleChoiceAnswer2Button:
                        if (correctAnswerButton == 2) {
                            score++;
                            writeToDB("correct");
                        }
                        else{
                            writeToDB("wrong");
                        }
                        break;
                    case R.id.multipleChoiceAnswer3Button:
                        if (correctAnswerButton == 3) {
                            score++;
                            writeToDB("correct");
                        }
                        else{
                            writeToDB("wrong");
                        }
                        break;
                    case R.id.multipleChoiceAnswer4Button:
                        if (correctAnswerButton == 4) {
                            score++;
                            writeToDB("correct");
                        }
                        else{
                            writeToDB("wrong");
                        }
                        break;
                    case R.id.multipleChoiceAnswer5Button:
                        if (correctAnswerButton == 5) {
                            score++;
                            writeToDB("correct");
                        }
                        else{
                            writeToDB("wrong");
                        }
                        break;
                    case R.id.multipleChoiceAnswer6Button:
                        if (correctAnswerButton == 6) {
                            score++;
                            writeToDB("correct");
                        }
                        else{
                            writeToDB("wrong");
                        }
                        break;
                    default:
                        Log.v("QUIZ ACTIVITY", "Problem at the scoring switch!!");
                        break;
                }
                lock = true;
                quizScore.setText("Score: " + Integer.toString(score));
                if(round >= QUIZLENGTH){
                    Button nextButton = (Button) findViewById(R.id.nextButton);
                    nextButton.setText("SEE RESULTS");

                }
            }
    }

    public void writeToDB(String s){

//        System.out.println("*******************************************************");
//        System.out.println("***                    writeToDB                    ***");
//        System.out.println("String s: "+s);
        DatabaseEntry dbe = db.getEntry(correctAnswerDB);
//        System.out.println("dbe: "+ dbe.getEnglish().toString());
//        System.out.println("dbe: "+ dbe.getEntryTest()==null);
//        System.out.println("dbe: "+ dbe.getFinalTest()==null);
//        System.out.println("boolean entryTest ="+entryTest);
            if (entryTest == true) {

                dbe.setEntryTest(s);


                db.updateEntry(dbe);
            }
            else{
//                DatabaseEntry dbe = db.getEntry(correctAnswerDB);
                dbe.setFinalTest(s);
                db.updateEntry(dbe);
            }
//        System.out.println("dbe: "+ dbe.getEntryTest()==null);
//        System.out.println("dbe: "+ dbe.getFinalTest()==null);
//        System.out.println("***                                                 ***");
//        System.out.println("*******************************************************");


    }

    public void nextButtonClick(View view) {

        if (dbSpamBlock == false) {
            if (round >= QUIZLENGTH && entryTest == true) {
//            System.out.println("great! finally in end");
                dbSpamBlock =true; // protects against spamming see results button
                setContentView(R.layout.post_quiz);

                TextView postQuizTextView = (TextView) findViewById(R.id.postQuizText);
                String postQuizeString = getString(R.string.post_entry_quiz, score, round);
                postQuizTextView.setText(postQuizeString);

                // create date when ready
                Calendar cal = Calendar.getInstance();
                cal.getTime();
                cal.add(Calendar.DATE, LENGTH_TRAINING_DAYS);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                String finalTestDateString = dateFormat.format(cal.getTime());

                // save datein shared pref
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Final Test Date", finalTestDateString);
                editor.apply();

            } else if (round >= QUIZLENGTH && entryTest == false) {
//            System.out.println("great! finally in end");
                dbSpamBlock =true; // protects against spamming see results button

                setContentView(R.layout.post_quiz);

                TextView postQuizTextView = (TextView) findViewById(R.id.postQuizText);
                String postQuizeString = getString(R.string.post_entry_quiz, score, round);
                postQuizTextView.setText(postQuizeString);

                // save  shared pref
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Final Test Done", true);
                editor.apply();


                //upload everything to db


                Firebase.setAndroidContext(this);
                Firebase myFirebaseRef = new Firebase("https://moble.firebaseio.com/");
                String id = android.os.Build.SERIAL;
                String categoryCSV = "";
                String englishCSV = "";
                String portugueseCSV = "";
                String entrytestCSV = "";
                String finaltestCSV = "";
                String notificationCSV = "";

                Random rgen = new Random();

                Firebase user = myFirebaseRef.child(Integer.toString(rgen.nextInt(1000)));

                for (int i = 1; i < db.getEntryCount() - 1; i++) {
                    categoryCSV = db.getEntry(i).getCategory() + " , " + categoryCSV;
                    englishCSV = db.getEntry(i).getEnglish() + " , " + englishCSV;
                    portugueseCSV = db.getEntry(i).getPortuguese() + " , " + portugueseCSV;
                    entrytestCSV = db.getEntry(i).getEntryTest() + " , " + entrytestCSV;
                    finaltestCSV = db.getEntry(i).getFinalTest() + " , " + finaltestCSV;
                    notificationCSV = db.getEntry(i).getNotification() + " , " + notificationCSV;
                }

                user.child("Category").setValue(categoryCSV);
                user.child("English").setValue(englishCSV);
                user.child("Portuguese").setValue(portugueseCSV);
                user.child("Entry Test").setValue(entrytestCSV);
                user.child("Final Test").setValue(finaltestCSV);
                user.child("Notification").setValue(notificationCSV);

            } else {
                if (lock == true) {
                    round++;
                    quizRound.setText("Round: " + Integer.toString(round));
                    setNewQuestion();
                }
            }
        }


    }

    public void endQuiz(View view){
        finish();
    }
}
