package com.example.bmrcalculator;

import static android.util.Log.d;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView nameTextView = findViewById(R.id.ResultName);
        TextView bmrTextView = findViewById(R.id.ResultBMR);
        TextView bmiTextView = findViewById(R.id.ResultBMI);

        // 从Intent中获取传递的数据
        Intent intent = getIntent();
        //if (intent != null) {
            String name = intent.getStringExtra("name");
            double bmr = intent.getDoubleExtra("bmr", 0.0);
            double bmi = intent.getDoubleExtra("bmi", 0.0);

            int age = intent.getIntExtra("age", 0);
            String gender = intent.getStringExtra("gender");
            int height = intent.getIntExtra("height", 0);
            double weight = intent.getDoubleExtra("weight", 0.0);


            // 显示数据
            nameTextView.setText("Name: " + name);
            bmrTextView.setText("BMR: " + bmr);
            bmiTextView.setText("BMI: " + bmi);

        //}

        Button cancelButton = findViewById(R.id.ResultCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击Cancel按钮时，返回到前一页（CalculateActivity）
                onBackPressed();
            }
        });




        Button resultSaveButton = findViewById(R.id.ResultSave);

        resultSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put("Name", name);
                dataMap.put("Gender", gender);
                dataMap.put("Age", String.valueOf(age));
                dataMap.put("Height", String.valueOf(height));
                dataMap.put("Weight", String.valueOf(weight));
                dataMap.put("bmr", String.valueOf(bmr));
                dataMap.put("bmi", String.valueOf(bmi));


                insertRecord(dataMap);
                // 点击"ResultSave"按钮时，返回到Main页面
                Intent intent = new Intent(result.this, MainActivity.class);
                startActivity(intent);


            }

            private synchronized void insertRecord(HashMap<String, String > map){
                Thread thread = new Thread(new Runnable() {
                    HashMap<String, String> _map;

                    @Override
                    public void run() {
                        String path = "http://10.0.2.2/insert.php";

                        executeHttpPost(path, _map);
                        Log.d("internet thread", "End");

                    }

                    public Runnable init(HashMap<String, String> map) {
                        _map = map;
                        return this;
                    }
                }.init(map));
                thread.start();
                while (thread.isAlive()) {
                    //Main Thread do nothing wait for internet thread
                }
                Log.d("insertRecord()", "End");

            }


            private void executeHttpPost (String path, HashMap < String, String > map){

                try {
                    // request method is POST
                    URL urlObj = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept-Charset", "UTF-8");
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.connect();

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(mapToString(map));
                    wr.flush();
                    wr.close();

                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("Save Record", "result: " + result.toString());
                    conn.disconnect();

                } catch (IOException e) {
                    Log.v("Save. Record", "Record saved failed");
                    e.printStackTrace();
                }

            }

            private String mapToString(HashMap<String, String> map) {
                StringBuilder builder = new StringBuilder();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (builder.length() > 0) {
                        builder.append("&");
                    }
                    builder.append(entry.getKey());
                    builder.append("=");
                    builder.append(entry.getValue());
                }
                return builder.toString();
            }


        });


    }



}