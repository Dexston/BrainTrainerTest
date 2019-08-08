package com.example.braintrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;


public class ResultActivity extends AppCompatActivity {

    private Intent intent;
    private SharedPreferences mainRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mainRecord = PreferenceManager.getDefaultSharedPreferences(this);

        intent = new Intent (ResultActivity.this, MainActivity.class);

        TextView textViewResult = findViewById(R.id.textViewResult);

        Intent fromMainActivity = getIntent();                                                  //Принимаем интент из MainActivity

        int answersCounter = fromMainActivity.getIntExtra("answersCounter",0);
        int correctAnswersCounter = fromMainActivity.getIntExtra("correctAnswersCounter", 0);
        int record = mainRecord.getInt("record", 0);

        String example; //Строка для варианта слова "пример"

        if ((answersCounter % 10 == 1) && (answersCounter % 100 != 11)) {
            example = getString(R.string.example1);
        } else {
            if (((answersCounter % 10 == 2) && (answersCounter % 100 != 12))
             || ((answersCounter % 10 == 3) && (answersCounter % 100 != 13))
             || ((answersCounter % 10 == 4) && (answersCounter % 100 != 14))) {
                example = getString(R.string.example2);
            } else {
                example = getString(R.string.examples);
            }
        }
        String result = String.format(getString(R.string.result), answersCounter, example, correctAnswersCounter, record);
        textViewResult.setText(result);
    }

    public void onClickRestart(View view) {
        startActivity(intent);
    }

    public void onClickResetRecord(View view) {
        mainRecord.edit().putBoolean("reset", true).apply();
        startActivity(intent);
    }
}
