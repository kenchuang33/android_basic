package com.example.bmrcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<LoadDataTask.Record> data = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<String> dataList; // 声明 dataList 为类级别的变量
    List<LoadDataTask.Record> recorddata = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        dataList = new ArrayList<>();

        new LoadDataTask(listView).execute();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 长按事件的处理代码
                showDeleteConfirmationDialog(position);
                return true; // 返回 true 防止触发普通点击事件
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // 获取选中项的数据
                    LoadDataTask.Record selectedRecord = recorddata.get(position);

                    // 创建 Intent 以启动 update 活动
                    Intent intent = new Intent(MainActivity.this, update.class);

                    // 创建 Bundle 来存储要传递的数据
                    Bundle bundle = new Bundle();
                    bundle.putString("name", selectedRecord.getName());
                    bundle.putString("gender", selectedRecord.getGender());
                    bundle.putInt("age", selectedRecord.getAge());
                    bundle.putInt("height", selectedRecord.getHeight());
                    bundle.putInt("weight", selectedRecord.getWeight());
                    bundle.putInt("bmr", selectedRecord.getBmr());
                    bundle.putInt("bmi", selectedRecord.getBmi());

                    // 将 Bundle 放入 Intent 中
                    intent.putExtras(bundle);

                    // 启动 update 活动
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "出現錯誤：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 异步任务用于执行网络请求
    class LoadDataTask extends AsyncTask<Void, Void, List<LoadDataTask.Record>> {
        private ListView listView;

        public LoadDataTask(ListView listView) {
            this.listView = listView;
        }

        @Override
        protected List<Record> doInBackground(Void... params) {


            try {
                URL url = new URL("http://10.0.2.2/query.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                // 解析 JSON 数据
                JSONArray jsonArray = new JSONArray(stringBuilder.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("Name");
                    String gender = jsonObject.getString("Gender");
                    int age = jsonObject.getInt("Age");
                    int height = jsonObject.getInt("Height");
                    int weight = jsonObject.getInt("Weight");
                    int bmi = jsonObject.getInt("bmi");
                    int bmr = jsonObject.getInt("bmr");
                    Record record = new Record(name, gender, bmr, age, height, weight, bmi);
                    recorddata.add(record);

                }

                reader.close();
                inputStream.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("LoadDataTask", "Error fetching data: " + e.getMessage());
            }
            Log.d("LoadDataTask", "Data size: " + data.size()); // 添加這行以查看數據大小

            return recorddata;
        }

        public  class Record implements Serializable {
            private String name;
            private String gender;
            private int bmr;
            private int age;
            private int height;
            private int weight;
            private int bmi;
            public Record(String name, String gender, int bmr, int age, int height, int weight, int bmi) {
                this.name = name;
                this.bmr = bmr;
                this.gender = gender;
                this.age = age;
                this.height = height;
                this.weight = weight;
                this.bmi = bmi;
            }

            public String getName() {
                return name;
            }
            public String getGender() {
                return gender;
            }
            public int getAge() {
                return age;
            }
            public int getWeight() {
                return weight;
            }
            public int getHeight() {
                return height;
            }

            public int getBmr(){
                if (gender.equals("Male")) {
                 bmr = (int) (66 + (13.7 * weight + 5 * height - 6.8 * age));
            }
                else {
                 bmr = (int) (655 + (9.6 * weight + 1.8 * height - 4.7 * age));
            }
                return bmr;
            }
            public int getBmi(){return (int) ( weight / ((height / 100.0) * (height / 100.0)));}

            @Override
            public String toString() {
                return name + "                       |                        " + bmr;
            }
        }

        protected void onPostExecute(List<LoadDataTask.Record> data) {
            if (data != null) {
                dataList = new ArrayList<>(); // 初始化 dataList

                List<String> displayData = new ArrayList<>();
                for (LoadDataTask.Record record : data) { // 使用完全限定的类名
                    displayData.add(record.toString());
                    dataList.add(record.getName()); // 添加到 dataList 中
                }
                Log.d("LoadDataTask1", "onPostExecute called");
                if (listView != null) {
                    // 做一些操作
                    Log.e("LoadDataTask2", "ListView is");
                } else {
                    Log.e("LoadDataTask2", "ListView is null");
                }
                Log.d("LoadDataTask3", "Data: " + data.toString());
                adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, displayData);
                listView.setAdapter(adapter);
            } else {
                // 在 data 為空時執行適當的處理
                // 例如，顯示一個錯誤消息或採取其他措施
                Log.d("sss","error");
            }
        }

    }


        private void showDeleteConfirmationDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("確認刪除")
                    .setMessage("確定要刪嗎？")
                    .setPositiveButton("確認刪除", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // 获取要删除的项目的 Name
                            String nameToDelete = dataList.get(position);

                            // 执行删除操作（调用 PHP 删除脚本）
                            new DeleteDataTask().execute(nameToDelete);

                            // 从适配器和数据列表中删除项目
                            adapter.remove(adapter.getItem(position));
                            dataList.remove(position);

                            // 通知适配器数据已更改
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // 用户取消了删除操作，不执行任何操作
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }


    private class DeleteDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String nameToDelete = params[0];
            String resultMessage = "";

            try {
                URL url = new URL("http://10.0.2.2/delete.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // 构建要发送的数据（以 "name" 为键，要删除的名字为值）
                String postData = "Name=" + nameToDelete;

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
            // 在删除操作完成后，处理结果
            // 这里可以根据需要执行适当的操作，例如显示 Toast 通知
            Toast.makeText(MainActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public void gotocreate(View v) {
        Intent it = new Intent(this, create.class);
        startActivity(it);
    }
}



