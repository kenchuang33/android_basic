package com.example.bmrcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class update extends AppCompatActivity {
    private MainActivity.LoadDataTask.Record selectedRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Button updateButton = findViewById(R.id.modifyingsend);

        EditText nameEditText = findViewById(R.id.modifyingName);
        EditText ageEditText = findViewById(R.id.modifyingAge);
        EditText heightEditText = findViewById(R.id.modifyingweight);
        EditText weightEditText = findViewById(R.id.modifyingheight);
        RadioGroup radioGroupGender = findViewById(R.id.modifyingradioGroup);
        RadioButton radioButtonMale = findViewById(R.id.modifyingMale);
        RadioButton radioButtonFemale = findViewById(R.id.modifyingFemale);

        // 從意圖中獲取選定的記錄
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String gender = bundle.getString("gender");
        int height  = bundle.getInt(    "height");
        int weight = bundle.getInt("weight");
        int age = bundle.getInt("age");

        nameEditText.setText(name);
        ageEditText.setText(String.valueOf(age));
        heightEditText.setText(String.valueOf(height));
        weightEditText.setText(String.valueOf(weight));
        if (gender.equals("Male")) {
            radioButtonMale.setChecked(true);
        } else if (gender.equals("Female")) {
            radioButtonFemale.setChecked(true);
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡執行網絡請求，連接到您的 PHP 文件並更新記錄
                updateRecord();
                Intent intent = new Intent(update.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }

    private void updateRecord() {
        // 獲取所需的數據
        String name = ((EditText) findViewById(R.id.modifyingName)).getText().toString();
        String gender = ((RadioButton) findViewById(R.id.modifyingMale)).isChecked() ? "Male" : "Female";
        int age = Integer.parseInt(((EditText) findViewById(R.id.modifyingAge)).getText().toString());
        int height = Integer.parseInt(((EditText) findViewById(R.id.modifyingweight)).getText().toString());
        int weight = Integer.parseInt(((EditText) findViewById(R.id.modifyingheight)).getText().toString());
        int bmr = calculateBMR(gender, age, height, weight); // 計算 BMR
        int bmi = calculateBMI(height, weight); // 計算 BMI

        // 創建一個 AsyncTask 來執行網絡請求
        new UpdateDataTask().execute(name, gender, String.valueOf(age), String.valueOf(height),
                String.valueOf(weight), String.valueOf(bmr), String.valueOf(bmi));
    }

    private class UpdateDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String resultMessage = "";

            try {
                URL url = new URL("http://10.0.2.2/update.php"); // 更新 PHP 文件的 URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // 构建要发送的数据
                String postData = "Name=" + params[0] + "&Gender=" + params[1] + "&Age=" + params[2] +
                        "&Height=" + params[3] + "&Weight=" + params[4] + "&BMR=" + params[5] + "&BMI=" + params[6];

                // 获取输出流并写入数据
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                outputStream.close();

                // 获取服务器响应
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 请求成功，读取服务器响应
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    resultMessage = response.toString();
                } else {
                    // 请求失败，处理错误
                    resultMessage = "請求失敗，錯誤代碼：" + responseCode;
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                resultMessage = "請求失敗，發生異常：" + e.getMessage();
            }

            return resultMessage;
        }

        @Override
        protected void onPostExecute(String resultMessage) {
            // 在 UI 線程中顯示結果，例如顯示 Toast 通知
            Toast.makeText(update.this, resultMessage, Toast.LENGTH_SHORT).show();
        }
    }

    // 添加計算 BMR 和 BMI 的方法
    private int calculateBMR(String gender, int age, int height, int weight) {
        int bmr;
        if (gender.equals("Male")) {
            bmr = (int) (66 + (13.7 * weight + 5 * height - 6.8 * age));
        } else {
            bmr = (int) (655 + (9.6 * weight + 1.8 * height - 4.7 * age));
        }
        return bmr;
    }

    private int calculateBMI(int height, int weight) {
        return (int) (weight / ((height / 100.0) * (height / 100.0)));
    }
}
