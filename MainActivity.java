package com.example.braintrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView textViewTimer, textViewRiddle, textViewCounter;
    private ArrayList<TextView> buttons = new ArrayList<>();
    private int answersCounter, correctAnswersCounter, numberOfRightAnswer, rightAnswer, symbol;
    private int min = 5;	//Диапазон чисел от min
    private int max = 30;	//до max
    private int time = 15;	//Время игры
    private boolean gameOver = false;   //Триггер, запрещающий нажатие на кнопки, если время игры вышло

    private SharedPreferences mainRecord;
    private Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainRecord = PreferenceManager.getDefaultSharedPreferences(this);

        if (mainRecord.getBoolean("reset", false)) {
            mainRecord.edit().putInt("record", 0).apply();
            mainRecord.edit().putBoolean("reset", false).apply();
        }


        TextView textView1Answer = findViewById(R.id.textView1Answer);
        TextView textView2Answer = findViewById(R.id.textView2Answer);
        TextView textView3Answer = findViewById(R.id.textView3Answer);
        TextView textView4Answer = findViewById(R.id.textView4Answer);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewRiddle = findViewById(R.id.textViewRiddle);
        textViewCounter = findViewById(R.id.textViewCounter);
        buttons.add(textView1Answer);
        buttons.add(textView2Answer);
        buttons.add(textView3Answer);
        buttons.add(textView4Answer);

        setCounter();

        startTimer();

        makeARiddle();

    }

    private void setCounter() {
        String counter = String.format(getString(R.string.counter), answersCounter, correctAnswersCounter, mainRecord.getInt("record", 0));
        textViewCounter.setText(counter);
    }

    private void startTimer () {
        CountDownTimer timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long l) {
                textViewTimer.setText(setTimer(l));
            }

            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, R.string.toast_time_over, Toast.LENGTH_SHORT).show();
                textViewTimer.setText(setTimer(0));
                gameOver = true;
                Intent toResultActivity = new Intent(MainActivity.this,ResultActivity.class);
                toResultActivity.putExtra("answersCounter", answersCounter);
                toResultActivity.putExtra("correctAnswersCounter", correctAnswersCounter);
                startActivity(toResultActivity);
            }
        };
        timer.start();
    }

    private String setTimer (long millis) {
        int seconds = (int) millis / 1000;
        int minutes = seconds / 60;
        if (millis < 10000) {
            textViewTimer.setTextColor(Color.RED);
        }
        if (millis == 0) {
            return String.format(getString(R.string.timer), minutes, seconds);
        } else {
            return String.format(getString(R.string.timer), minutes, seconds + 1);
        }
    }

    public void onClickAnswer(View view) {
        if (!gameOver) {
            TextView pressedButton = (TextView) view;
            if (buttons.indexOf(pressedButton) == numberOfRightAnswer) {
                Toast.makeText(getApplicationContext(), R.string.toast_right, Toast.LENGTH_SHORT).show();
                correctAnswersCounter++;
                if (correctAnswersCounter > mainRecord.getInt("record", 0)) {
                    mainRecord.edit().putInt("record", correctAnswersCounter).apply();
                }
            } else {
                Toast.makeText(this, R.string.toast_wrong, Toast.LENGTH_SHORT).show();
            }
            answersCounter++;
            setCounter();
            makeARiddle();
        }
    }

    private void makeARiddle() {

        symbol = 1;
        String symbolChar;

        if (rand.nextBoolean()) {
            symbolChar = "+";
        } else {
            symbol = -1;
            symbolChar = "-";
        }
        int x = getRandomNumber();
        int y = getRandomNumber();
        rightAnswer = getRandomAnswer(x, y);
        String riddle = String.format(getString(R.string.riddle), x, symbolChar, y);
        textViewRiddle.setText(riddle);
        setTextOnButtons();
    }

    private void setTextOnButtons() {

        int i = 0;
        while (i < buttons.size()) {
            int x = getRandomNumber();
            int y = getRandomNumber();
            int randAnswer = getRandomAnswer(x, y);
            if (randAnswer != rightAnswer) {
                String answer = String.format(getString(R.string.answer), randAnswer);
                buttons.get(i).setText(answer);
                i++;
            }
        }

        int randomButton = rand.nextInt(buttons.size());
        numberOfRightAnswer = randomButton;
        String answer = String.format(getString(R.string.answer), rightAnswer);
        buttons.get(randomButton).setText(answer);
    }

    private int getRandomNumber() {
        return min + rand.nextInt(max - min);
    }

    private int getRandomAnswer(int x, int y) {
        return x + y * symbol;
    }

}
