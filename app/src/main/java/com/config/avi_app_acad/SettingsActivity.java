package com.config.avi_app_acad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private Spinner exerciseTypeSpinner;
    private RadioGroup speedUnitRadioGroup;
    private RadioGroup mapOrientationRadioGroup;
    private RadioGroup mapTypeRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        exerciseTypeSpinner = findViewById(R.id.spinnerExerciseType);
        speedUnitRadioGroup = findViewById(R.id.radioGroupSpeedUnit);
        mapOrientationRadioGroup = findViewById(R.id.radioGroupMapOrientation);
        mapTypeRadioGroup = findViewById(R.id.radioGroupMapType);


        SharedPreferences sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        int savedExerciseType = sharedPreferences.getInt("exercise_type", 0);
        int savedSpeedUnit = sharedPreferences.getInt("speed_unit", 0);
        int savedMapOrientation = sharedPreferences.getInt("map_orientation", 0);
        int savedMapType = sharedPreferences.getInt("map_type", 0);

        exerciseTypeSpinner.setSelection(savedExerciseType);
        speedUnitRadioGroup.check(savedSpeedUnit);
        mapOrientationRadioGroup.check(savedMapOrientation);
        mapTypeRadioGroup.check(savedMapType);

    }

    public void onSaveButtonClick(View view) {

        SharedPreferences.Editor editor = getSharedPreferences("app_settings", MODE_PRIVATE).edit();
        editor.putInt("exercise_type", exerciseTypeSpinner.getSelectedItemPosition());
        editor.putInt("speed_unit", speedUnitRadioGroup.getCheckedRadioButtonId());
        editor.putInt("map_orientation", mapOrientationRadioGroup.getCheckedRadioButtonId());
        editor.putInt("map_type", mapTypeRadioGroup.getCheckedRadioButtonId());

        editor.apply();

        Toast.makeText(this, "Configurações salvas com sucesso!", Toast.LENGTH_SHORT).show();
    }
}
