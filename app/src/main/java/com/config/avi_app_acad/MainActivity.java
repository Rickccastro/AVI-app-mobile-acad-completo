package com.config.avi_app_acad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void openUserProfile(View view) {

        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void openUserMap(View view) {

        Intent intent = new Intent(this,MonitorarExercicio.class);
        startActivity(intent);
    }

    public void openSettings(View view) {

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    public void openCredits(View view) {

        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }
}
