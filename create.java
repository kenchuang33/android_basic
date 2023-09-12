package com.example.bmrcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class create extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);


        final EditText editTextName = findViewById(R.id.Name);
        final EditText editTextAge = findViewById(R.id.Age);
        final EditText editTextHeight = findViewById(R.id.height);
        final EditText editTextWeight = findViewById(R.id.weight);
        final RadioGroup radioGroupGender = findViewById(R.id.radioGroup);

        Button calculateButton = findViewById(R.id.send);
        Button cancelButton = findViewById(R.id.cancel);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                int age = Integer.parseInt(editTextAge.getText().toString());
                int height = Integer.parseInt(editTextHeight.getText().toString());
                double weight = Double.parseDouble(editTextWeight.getText().toString());

                RadioButton selectedRadioButton = findViewById(radioGroupGender.getCheckedRadioButtonId());
                String gender = selectedRadioButton.getText().toString();

                double bmr;
                if (gender.equals("Male")) {
                    bmr = 66 + (13.7 * weight + 5 * height - 6.8 * age);
                } else {
                    bmr = 655 + (9.6 * weight + 1.8 * height - 4.7 * age);
                }

                double bmi = weight / ((height / 100.0) * (height / 100.0));

                // 创建一个Intent，将结果数据传递到下一页
                Intent intent = new Intent(create.this, result.class);
                intent.putExtra("name", name);
                intent.putExtra("bmr", bmr);
                intent.putExtra("bmi", bmi);
                intent.putExtra("age", age);
                intent.putExtra("height", height);
                intent.putExtra("weight", weight);
                intent.putExtra("gender", gender);

                // 启动下一页
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextName.setText("");
                editTextAge.setText("");
                editTextHeight.setText("");
                editTextWeight.setText("");
                radioGroupGender.clearCheck();
            }
        });



    }
}

