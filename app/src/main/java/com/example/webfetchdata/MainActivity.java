package com.example.webfetchdata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.webfetchdata.JsonType.Inbody;
import com.example.webfetchdata.JsonType.Users;
import com.example.webfetchdata.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String dname,d1,d2;
    int Row;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //預測空值
        setnull();
        //預設日期
        setDate();
        //點選日期
        pickDate();



    }
    //預測空值
    @SuppressLint("SetTextI18n")
    private  void setnull(){
        binding.cardEx.createdtime.setVisibility(View.INVISIBLE);
        binding.cardEx.pfra.setText("右臂分析:null");
        binding.cardEx.pfla.setText("左臂分析:null");
        binding.cardEx.pft.setText("身軀分析:null");
        binding.cardEx.pfrl.setText("右腿分析:null");
        binding.cardEx.pfll.setText("左腿分析:null");
        binding.cardEx.pilra.setText("右臂分析:null");
        binding.cardEx.pilla.setText("左臂分析:null");
        binding.cardEx.pilt.setText("身軀分析:null");
        binding.cardEx.pilrl.setText("右腿分析:null");
        binding.cardEx.pilll.setText("左腿分析:null");
        binding.cardEx.score.setText("分數:null");
        binding.cardEx.fat.setText("脂肪合計:null");
        binding.cardEx.muscle.setText("肌肉合計:null");
        binding.cardDf.CardViewdf.setVisibility(View.INVISIBLE);
    }

    //預設日期
    @SuppressLint("DefaultLocale")
    private void setDate(){
        //轉Start 與 時間 上週
        Calendar s = Calendar.getInstance();
        s.setFirstDayOfWeek(Calendar.MONDAY);
        s.add(Calendar.DATE, -7);
        Date sTime = s.getTime();
        //int i = s.get(Calendar.DAY_OF_WEEK) - s.get(Calendar.SUNDAY);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        String ds = sdf.format(sTime).toString();

        //當日
        Calendar c = Calendar.getInstance();
        //年
        int year = c.get(Calendar.YEAR);
        //月
        int month= c.get(Calendar.MONTH) + 1;
        //日
        int day = c.get(Calendar.DAY_OF_MONTH);

        //設定當前時間
        binding.startDate.setText(ds);
        binding.endDate.setText(String.format("%d-%d-%d", year, month, day));

         d1 = binding.startDate.getText().toString();
         d2 = binding.endDate.getText().toString();

        CurrencyData(d1,d2);

    }

    //時間選者器
    private void pickDate() {

        Calendar startder = Calendar.getInstance();
        Calendar enddar = Calendar.getInstance();
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);

        try {
            binding.startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            setnull();
                            startder.set(Calendar.YEAR, year);
                            startder.set(Calendar.MONTH, month);
                            startder.set(Calendar.DAY_OF_MONTH, day);
                            binding.startDate.setText(sdf.format(startder.getTime()));
                            d1 = binding.startDate.getText().toString();
                            d2 = binding.endDate.getText().toString();
                            String name = dname;
                            CurrencyData(d1,d2);
                        }
                    }, startder.get(Calendar.YEAR), startder.get(Calendar.MONTH), startder.get(Calendar.DAY_OF_MONTH));
                    //dpd.getDatePicker().setMaxDate(new Date().getTime());
                    try {
                        dpd.getDatePicker().setMaxDate(sdf.parse(binding.endDate.getText().toString()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dpd.show();
                }
            });

            binding.endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            setnull();
                            enddar.set(Calendar.YEAR, year);
                            enddar.set(Calendar.MONTH, month);
                            enddar.set(Calendar.DAY_OF_MONTH, day);
                            binding.endDate.setText(sdf.format(enddar.getTime()));
                            d1 = binding.startDate.getText().toString();
                            d2 = binding.endDate.getText().toString();
                            CurrencyData(d1,d2);
                        }
                    }, enddar.get(Calendar.YEAR), enddar.get(Calendar.MONTH), enddar.get(Calendar.DAY_OF_MONTH));
                    try {
                        dpd.getDatePicker().setMinDate(sdf.parse(binding.startDate.getText().toString()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //57599時間戳 -> 23:59:59
                    dpd.getDatePicker().setMaxDate(new Date().getTime() + 57600);
                    dpd.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //姓名下拉式
    private void spinnerName(String Nameurl) {

        try {
            URL url = new URL(Nameurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

                String output;
                StringBuilder sb = new StringBuilder();
                while ((output = bufferedReader.readLine()) != null) {
                    sb.append(output);
                }
                Users users = new Gson().fromJson(sb.toString(), Users.class);
                insertUI(users);
                btnSpinner(users);
                bufferedReader.close();
                is.close();
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void insertUI(Users users) {
        Spinner spinner = findViewById(R.id.spinner);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //設定陣列[]
                ArrayList NameList = new ArrayList<Integer>();
                int size = users.size();
                // 訪問 JSON 陣列中所有 JSON 物件。
                for (int i = 0; i < size; i++) {
                    NameList.add(users.get(i).getName());
                }
                ArrayAdapter nameadapter = new  ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,NameList);
                nameadapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                spinner.setAdapter(nameadapter);
            }
        });
    }
    //下拉式 memberId抓
    private  void  btnSpinner(Users users){
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setnull();
                dname = users.get(i).getMemberid();
                //Log.d("dname",users.get(i).getMemberid());
                d1 = binding.startDate.getText().toString();
                d2 = binding.endDate.getText().toString();
                CurrencyData(d1,d2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Inbody數據
    private void CurrencyData(String s1,String s2) {

        String Nameurl = "http://192.168.1.203:5000/api/v1/ApplicationUser/User/d6d8cbd2-9f5f-4b54-bd17-ebb82bbd701a";

        String inbodyurl = "http://192.168.1.203:5000/api/v1/Inbody/Yu/"+dname+"/"+s1+"/"+s2;



        new Thread(() -> {
            if (dname == null){
                //姓名
                spinnerName(Nameurl);
            }
            try {
                //card 片
                URL url = new URL(inbodyurl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //Log.d("UpdateUI", String.valueOf(connection.getResponseCode()));

                //api 200 通過
                if (connection.getResponseCode() == 200) {
                    InputStream is = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

                    String output;
                    StringBuilder sb = new StringBuilder();
                    while ((output = bufferedReader.readLine()) != null) {
                        sb.append(output);
                    }

                    Inbody inbody = new Gson().fromJson(sb.toString(), Inbody.class);
                    if (inbody.size() == 0)
                        return ;
                    updateUI(inbody);
                    bufferedReader.close();
                    is.close();

                } else {
                    setnull();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Thread.interrupted();
    }

    //抓設定型態 非同步
    private void updateUI(Inbody inbody) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //設定陣列[]
                ArrayList timeList = new ArrayList<Integer>();
                int size = inbody.size();
                // 訪問 JSON 陣列中所有 JSON 物件。
                for (int i = 0; i < size; i++) {
                    timeList.add(inbody.get(i).getCreatedtime());
                }
                ArrayAdapter nameadapter = new  ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,timeList);
                binding.cardEx.createdtime.setAdapter(nameadapter);

                dtime(inbody);

                //binding.fra.setText(String.format("右臂分析: %.4f", cooperative.getContentType()));
                //binding.fla.setText(String.format("左臂分析: %s", cooperative.getSuccess()));
                //binding.ft.setText(String.format("軀幹分析: %s", cooperative.getData().get(0).get年底別()));
                //binding.frl.setText(String.format("右腿分析: %s", cooperative.get每股金額()));
                //binding.fll.setText(String.format("左腿分析: %s", cooperative.get總社()));

            }
        });
    }

    private void dtime(Inbody inbody) {
        binding.cardEx.createdtime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (inbody.size() > 0){
                    binding.cardEx.createdtime.setVisibility(View.VISIBLE);
                }else{
                    binding.cardEx.createdtime.setVisibility(View.INVISIBLE);
                }
                Row = i + 1;
                if(Row >= 2){
                    binding.cardDf.CardViewdf.setVisibility(View.VISIBLE);
                    //差異數值 inbody
                    GetEXEC();
                }else{
                    binding.cardDf.CardViewdf.setVisibility(View.INVISIBLE);
                }
                inbody.get(i).getCreatedtime();

                binding.cardEx.score.setText("分數:" + inbody.get(i).getScore());
                binding.cardEx.pfra.setText(String.format("右臂分析: %.2f", inbody.get(i).getPfra()));
                binding.cardEx.pfla.setText(String.format("左臂分析: %.2f", inbody.get(i).getPfla()));
                binding.cardEx.pft.setText(String.format("軀幹分析: %.2f", inbody.get(i).getPft()));
                binding.cardEx.pfrl.setText(String.format("右腿分析: %.2f", inbody.get(i).getPfrl()));
                binding.cardEx.pfll.setText(String.format("左腿分析: %.2f", inbody.get(i).getPfll()));
                binding.cardEx.fat.setText(String.format("脂肪合計: %.2f",inbody.get(i).getFat()));

                binding.cardEx.pilra.setText(String.format("右臂分析: %.2f", inbody.get(i).getPilra()));
                binding.cardEx.pilla.setText(String.format("左臂分析: %.2f", inbody.get(i).getPilla()));
                binding.cardEx.pilt.setText(String.format("軀幹分析: %.2f", inbody.get(i).getPilt()));
                binding.cardEx.pilrl.setText(String.format("右腿分析: %.2f", inbody.get(i).getPilrl()));
                binding.cardEx.pilll.setText(String.format("左腿分析: %.2f", inbody.get(i).getPilll()));
                binding.cardEx.muscle.setText(String.format("肌肉合計: %.2f",inbody.get(i).getMuscle()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //預存inbody差異
    private void GetEXEC(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                String inbodyurl = "http://192.168.1.203:5000/api/v1/Inbody/Def/"+dname+"/"+d1+"/"+d2+"/"+Row;
                try {
                    URL url = new URL(inbodyurl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream is = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

                        String line;
                        StringBuilder sb = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        Inbody inbody = new Gson().fromJson(sb.toString(), Inbody.class);
                        if (inbody.size() == 0)
                            return;
                        execUI(inbody);
                        bufferedReader.close();
                        is.close();
                    } else {
                        binding.cardDf.CardViewdf.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.interrupted();

    }
    //預存inbody差異 顯示
    @SuppressLint({"ResourceAsColor", "DefaultLocale", "SetTextI18n"})
    private void execUI(Inbody inbody) {

        runOnUiThread(()->{
            binding.cardDf.dscore.setText("分數:" + String.valueOf(inbody.get(0).getScore()));
            binding.cardDf.dpfra.setText(String.format("右臂分析: %.2f", inbody.get(0).getPfra()));
            binding.cardDf.dpfla.setText(String.format("左臂分析: %.2f", inbody.get(0).getPfla()));
            binding.cardDf.dpft.setText(String.format("軀幹分析: %.2f", inbody.get(0).getPft()));
            binding.cardDf.dpfrl.setText(String.format("右腿分析: %.2f", inbody.get(0).getPfrl()));
            binding.cardDf.dpfll.setText(String.format("左腿分析: %.2f", inbody.get(0).getPfll()));
            binding.cardDf.dfat.setText(String.format("脂肪合計: %.2f",inbody.get(0).getFat()));

            binding.cardDf.dpilra.setText(String.format("右臂分析: %.2f", inbody.get(0).getPilra()));
            binding.cardDf.dpilla.setText(String.format("左臂分析: %.2f", inbody.get(0).getPilla()));
            binding.cardDf.dpilt.setText(String.format("軀幹分析: %.2f", inbody.get(0).getPilt()));
            binding.cardDf.dpilrl.setText(String.format("右腿分析: %.2f", inbody.get(0).getPilrl()));
            binding.cardDf.dpilll.setText(String.format("左腿分析: %.2f", inbody.get(0).getPilll()));
            binding.cardDf.dmuscle.setText(String.format("肌肉合計: %.2f",inbody.get(0).getMuscle()));

            if(inbody.get(0).getScore() >= 1){binding.cardDf.dscore.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getScore() == 0){
                binding.cardDf.dscore.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dscore.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getPfra() >= 1){binding.cardDf.dpfra.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getPfra() == 0){
                binding.cardDf.dpfra.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dpfra.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getPfla() >= 1){binding.cardDf.dpfla.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getPfla() == 0){
                binding.cardDf.dpfla.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dpfla.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getPft() >= 1){binding.cardDf.dpft.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getPft() == 0){
                binding.cardDf.dpft.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dpft.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getPfrl() >= 1){binding.cardDf.dpfrl.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getPfrl() == 0){
                binding.cardDf.dpfrl.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dpfrl.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getPfll() >= 1){binding.cardDf.dpfll.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getPfll() == 0){
                binding.cardDf.dpfll.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dpfll.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getPilra() >= 1){binding.cardDf.dpilra.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getPilra() == 0){
                binding.cardDf.dpilra.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dpilra.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getPilla() >= 1){binding.cardDf.dpilla.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getPilla() == 0){
                binding.cardDf.dpilla.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dpilla.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getPilt() >= 1){binding.cardDf.dpilt.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getPilt() == 0){
                binding.cardDf.dpilt.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dpilt.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getPilrl() >= 1){binding.cardDf.dpilrl.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getPilrl() == 0){
                binding.cardDf.dpilrl.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dpilrl.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getPilll() >= 1){binding.cardDf.dpilll.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getPilll() == 0){
                binding.cardDf.dpilll.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dpilll.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getFat() >= 1){binding.cardDf.dfat.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getFat() == 0){
                binding.cardDf.dfat.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dfat.setTextColor(ContextCompat.getColor(this,R.color.green));}
            if(inbody.get(0).getMuscle() >= 1){binding.cardDf.dmuscle.setTextColor(ContextCompat.getColor(this,R.color.red));}else if(inbody.get(0).getMuscle() == 0){
                binding.cardDf.dmuscle.setTextColor(ContextCompat.getColor(this,R.color.black));}else{binding.cardDf.dmuscle.setTextColor(ContextCompat.getColor(this,R.color.green));}
        });

    }
}
