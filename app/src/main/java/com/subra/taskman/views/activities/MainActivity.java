package com.subra.taskman.views.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.subra.taskman.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getSupportFragmentManager().beginTransaction().add(android.R.id.content, new FragmentCrm(), "myFragmentTag").addToBackStack("tag").commit(); // without FrameLayout

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

}