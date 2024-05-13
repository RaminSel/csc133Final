package com.gamecodeschool.snakegame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class OptionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        // Initialize the back button and set its click listener
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

    }
}
