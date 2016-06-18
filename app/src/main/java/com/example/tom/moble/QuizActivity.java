package com.example.tom.moble;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
    Button nextButton;
    Button questionnaireSendResults;
    Random rgen;
    int correctAnswerDB;
    int correctAnswerButton;
    int score;
    int round;
    boolean lock;
    boolean entryTest;
    boolean dbSpamBlock;
    boolean dbSpamBlock2;
    ArrayList takenIndices;
    ArrayList alreadyAsked;
    String ratebarOneValue;
    String ratebarTwoValue;
    String ratebarThreeValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout);

        //Initialize views, database and variables
        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setBackgroundColor(Color.parseColor("#6AB344"));
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
        quizTitle = (TextView) findViewById(R.id.quizTitle);
        rgen = new Random();
        takenIndices = new ArrayList();
        alreadyAsked = new ArrayList();
        score = 0;
        round = 1;
        dbSpamBlock = false;
        dbSpamBlock2 = false;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String finalTestDayString = sharedPreferences.getString("Final Test Date", null);

        //Check whether we're starting the entry test or the final test
        if (finalTestDayString == null){
            entryTest = true;
            quizTitle.setText("Entry Test");

        }else{
            entryTest = false;
            quizTitle.setText("Final Test");
        }

        //Start setting questions
        setNewQuestion();
    }

    public void setNewQuestion(){

        //Enable the answer buttons
        lock = false;

        //Find a correct translation which hasn't been used yet
        while (true) {
            correctAnswerDB = rgen.nextInt(DATABASESIZE) + 1;
            if ((entryTest == true && db.getEntry(correctAnswerDB).getEntryTest() == null) || (entryTest == false && db.getEntry(correctAnswerDB).getFinalTest() == null)) {
                break;
            }
        }

        //Assign a button which will hold the correct answer and set the quiz question
        correctAnswerButton = rgen.nextInt(5) + 1;
        quizQuestion.setText(db.getEntry(correctAnswerDB).getEnglish());

        //Colour all buttons green
        one.setBackgroundColor(Color.parseColor("#6AB344"));
        two.setBackgroundColor(Color.parseColor("#6AB344"));
        three.setBackgroundColor(Color.parseColor("#6AB344"));
        four.setBackgroundColor(Color.parseColor("#6AB344"));
        five.setBackgroundColor(Color.parseColor("#6AB344"));
        six.setBackgroundColor(Color.parseColor("#6AB344"));

        //Set the other buttons with random Portuguese words while making sure they are not the same as the correct translation
        takenIndices.clear();
        takenIndices.add(correctAnswerDB);
        one.setText(db.getEntry(excludeRandom()).getPortuguese());
        two.setText(db.getEntry(excludeRandom()).getPortuguese());
        three.setText(db.getEntry(excludeRandom()).getPortuguese());
        four.setText(db.getEntry(excludeRandom()).getPortuguese());
        five.setText(db.getEntry(excludeRandom()).getPortuguese());
        six.setText(db.getEntry(excludeRandom()).getPortuguese());

        //Set the correct answer behind the herefor selected answer button
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

    //Method which helps to set the false answers, making sure they are dissimilar to the correct answer
    public int excludeRandom(){
        int x = rgen.nextInt(DATABASESIZE) + 1;
        while(takenIndices.contains(x)){
            x = rgen.nextInt(DATABASESIZE) + 1;
        }
        takenIndices.add(x);
        return x;
    }

    //Process the selected answer
    public void quizAnswerButtonClick(View view){

            //Lock makes sure that you cannot select an answer multiple times
            if (lock == false) {
                //Make all buttons red
                one.setBackgroundColor(Color.parseColor("#d1332e"));
                two.setBackgroundColor(Color.parseColor("#d1332e"));
                three.setBackgroundColor(Color.parseColor("#d1332e"));
                four.setBackgroundColor(Color.parseColor("#d1332e"));
                five.setBackgroundColor(Color.parseColor("#d1332e"));
                six.setBackgroundColor(Color.parseColor("#d1332e"));

                //Make the correct answer green
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

                //Count your score and write to the database whether your answer was wrong or correct
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

                //Disable the answer buttons
                lock = true;

                //Display your new score
                quizScore.setText("Score: " + Integer.toString(score));

                //If the round limit is reached change the button's text to go to the result page
                if(round >= QUIZLENGTH){
                    Button nextButton = (Button) findViewById(R.id.nextButton);
                    nextButton.setText("SEE RESULTS");

                }
            }
    }

    //Helps write to the database whether an answer was correct or wrong
    public void writeToDB(String falseOrCorrect){
        DatabaseEntry dbe = db.getEntry(correctAnswerDB);
            if (entryTest == true) {
                dbe.setEntryTest(falseOrCorrect);
                db.updateEntry(dbe);
            }
            else{
                dbe.setFinalTest(falseOrCorrect);
                db.updateEntry(dbe);
            }
    }

    //Code which is executed after clicking the next button
    public void nextButtonClick(View view) {

        //Makes sure the database isn't spammed by uploads
        if (dbSpamBlock == false) {
            //Checks whether we're at the end of the entry test
            if (round >= QUIZLENGTH && entryTest == true) {
                //Protects against spamming the database
                dbSpamBlock = true;
                //Set the post quiz layout
                setContentView(R.layout.post_quiz);
                TextView postQuizTextView = (TextView) findViewById(R.id.postQuizText);
                String postQuizeString = getString(R.string.post_entry_quiz, score, round);
                postQuizTextView.setText(postQuizeString);
                // Create a timestamp of when the entry test was completed and put this is in sharedpreference "Final Test Date"
                Calendar cal = Calendar.getInstance();
                cal.getTime();
                cal.add(Calendar.DATE, LENGTH_TRAINING_DAYS);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                String finalTestDateString = dateFormat.format(cal.getTime());
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Final Test Date", finalTestDateString);
                editor.apply();
            //Checks whether we're at the end of the final test
            } else if (round >= QUIZLENGTH && entryTest == false) {
                //Protects against spamming the database
                dbSpamBlock = true;
                //Set the post quiz layout
                setContentView(R.layout.post_quiz);
                TextView postQuizTextView = (TextView) findViewById(R.id.postQuizText);
                String postQuizeString = getString(R.string.post_entry_quiz, score, round);
                postQuizTextView.setText(postQuizeString);
            } else {
                //If we're not at the end of the quiz rounds, go to the next round
                if (lock == true) {
                    //Add to the round counter and display this to the screen
                    round++;
                    quizRound.setText("Round: " + Integer.toString(round));
                    //Set new questions
                    setNewQuestion();
                }
            }
        }
    }

    //When the quiz is done
    public void endQuiz(View view){
        if (round >= QUIZLENGTH && entryTest == true) {
            finish();
        }else{
            //Start the questionnaire
            setContentView(R.layout.questionnaire);
            questionnaireSendResults = (Button) findViewById(R.id.questionnaireSendResults);
            questionnaireSendResults.setBackgroundColor(Color.parseColor("#6AB344"));
        }

    }

    public void questionnairreRate(View view){
        RatingBar bar = (RatingBar) view;
        switch(view.getId()){
            case R.id.ratingBar1: ratebarOneValue = Float.toString(bar.getRating()); break;
            case R.id.ratingBar2: ratebarTwoValue = Float.toString(bar.getRating()); break;
            case R.id.ratingBar3: ratebarThreeValue = Float.toString(bar.getRating()); break;
        }

        Log.v("rating", ratebarOneValue);

    }

    public void sendResults(View view){
            if (dbSpamBlock2 == false) {
                dbSpamBlock2 = true;
                Toast toast = Toast.makeText(this, "Uploading data..", Toast.LENGTH_LONG);
                toast.show();

                //Edit the sharedpreference to state that the quizes have been completed
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Final Test Done", true);
                editor.apply();
                //Upload results to the database
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
                Firebase user = myFirebaseRef.child(Integer.toString(rgen.nextInt(10000)));
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
                user.child("RatebarOne").setValue(ratebarOneValue);
                user.child("RatebarTwo").setValue(ratebarTwoValue);
                user.child("RatebarThree").setValue(ratebarThreeValue);
                finish();
            }
    }


}
