package com.config.avi_app_acad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class UserProfileActivity extends AppCompatActivity {

    private EditText etSex, etWeight, etHeight;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        etSex = findViewById(R.id.editTextSex);
        etWeight = findViewById(R.id.editTextWeight);
        etHeight = findViewById(R.id.editTextHeight);
        datePicker = findViewById(R.id.datePicker);


        SharedPreferences sharedPreferences = getSharedPreferences("user_profile", MODE_PRIVATE);
        etSex.setText(sharedPreferences.getString("sex", ""));
        etWeight.setText(sharedPreferences.getString("weight", ""));
        etHeight.setText(sharedPreferences.getString("height", ""));
        long savedDate = sharedPreferences.getLong("birth_date", 0);
        if (savedDate != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(savedDate);
            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }

    }

    public void onSaveButtonClick(View view) {

        SharedPreferences.Editor editor = getSharedPreferences("user_profile", MODE_PRIVATE).edit();
        editor.putString("sex", etSex.getText().toString());
        editor.putString("weight", etWeight.getText().toString());
        editor.putString("height", etHeight.getText().toString());

        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        editor.putLong("birth_date", calendar.getTimeInMillis());

        editor.apply();

        Toast.makeText(this, "Perfil do usuário salvo", Toast.LENGTH_SHORT).show();
    }
}
