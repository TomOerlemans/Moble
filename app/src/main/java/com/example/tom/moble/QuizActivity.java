package com.example.tom.moble;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {


    final int DATABASESIZE = 7;
    final private int QUIZLENGTH = 3;
    DatabaseHandler db;
    TextView quizQuestion;
    TextView quizRound;
    TextView quizScore;
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
    ArrayList takenIndices = new ArrayList();


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
        setNewQuestion();
        score = 0;
        round =1;
    }

    public void setNewQuestion(){
        lock = false;
        correctAnswerDB = rgen.nextInt(DATABASESIZE) + 1;
        correctAnswerButton = rgen.nextInt(5) + 1;
        quizQuestion.setText(db.getEntry(correctAnswerDB).getEnglish());

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
                        }
                        break;
                    case R.id.multipleChoiceAnswer2Button:
                        if (correctAnswerButton == 2) {
                            score++;
                        }
                        break;
                    case R.id.multipleChoiceAnswer3Button:
                        if (correctAnswerButton == 3) {
                            score++;
                        }
                        break;
                    case R.id.multipleChoiceAnswer4Button:
                        if (correctAnswerButton == 4) {
                            score++;
                        }
                        break;
                    case R.id.multipleChoiceAnswer5Button:
                        if (correctAnswerButton == 5) {
                            score++;
                        }
                        break;
                    case R.id.multipleChoiceAnswer6Button:
                        if (correctAnswerButton == 6) {
                            score++;
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

    public void nextButtonClick(View view){
        if(round >= QUIZLENGTH){

            setContentView(R.layout.post_quiz);


            TextView postQuizScore = (TextView) findViewById(R.id.postQuizScore);
            postQuizScore.setText("Score:" + String.valueOf(score));

            //((TextView) findViewById(R.id.postQuizScore)).setText(score);
        }else{
            if (lock == true) {
                round++;
                quizRound.setText("Round: " + Integer.toString(round));
                setNewQuestion();
            }
        }
    }

    public void endQuiz(View view){
        finish();
    }

}
