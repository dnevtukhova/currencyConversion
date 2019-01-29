package com.dnevtukhova.currencyconverter;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    TextView textViewCurrencyIn;
    Button btnConvert;
    TextView textResultName;
    TextView textResult1;
    String currencyGet1;
    String currencyGet2;
    String currencyValue;
    double amountCurrencyValue;
    double course;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewCurrencyIn = findViewById(R.id.editTextCurrencyIn);
        btnConvert = findViewById(R.id.btnConvert);
        textResultName = findViewById(R.id.textNameResult);
        textResultName.setVisibility(View.INVISIBLE);
        textResult1 = findViewById(R.id.textResult);
        textResult1.setVisibility(View.INVISIBLE);


        final List<String> val = new ArrayList<String>();
        val.add("EUR");
        val.add("USD");
        val.add("GBP");
        val.add("RUB");
        val.add("ALL");
        val.add("XCD");
        val.add("BBD");
        val.add("BTN");
        val.add("BND");
        val.add("XAF");
        val.add("CUP");

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, val);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Обработка нажатия для спиннера Конвертировать Из
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner1.setAdapter(adapter);

        // устанавливаем обработчик нажатия
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                //обработка нажатия, получаем название выбранной валюты
                currencyGet1 = val.get(position);
                System.out.println(currencyGet1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        //Обработка нажатия для спиннера Конвертировать B
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter);

        // устанавливаем обработчик нажатия
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                //обработка нажатия
                currencyGet2 = val.get(position);
                System.out.println(currencyGet2);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        //Обработка нажатия для кнопки Конвертировать
        //создаем обработчик нажатия
        View.OnClickListener oclBtnConvert = new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                //получим сумму валюты
                currencyValue = textViewCurrencyIn.getText().toString();
                try {
                    amountCurrencyValue = Double.parseDouble(currencyValue);
                } catch (NumberFormatException e) {
                    Toast.makeText(getBaseContext(), "Заполните сумму валюты!", Toast.LENGTH_SHORT).show();
                }
                //формируем URL для получения курса валюты и отправлям запрос
                String url = "https://free.currencyconverterapi.com/api/v6/convert?q=" + currencyGet1 + "_" + currencyGet2 + "&compact=ultra";
                Convert conver = new Convert();
                conver.execute(url);

            }
        };
        //присваеваем обработчик кнопке
        btnConvert.setOnClickListener(oclBtnConvert);
    }

    class Convert extends AsyncTask<String, Void, String> {
    //используем HTTp клиент для обращения по переданному URL
        OkHttpClient client = new OkHttpClient();

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        }
        //получаем ответ от НТТр клиента
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected String doInBackground(String... urls) {
          String response = null;
            try {
                response = this.run(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            result = result.replace("{", "").replace("}", "");
            String[] str = result.split("\\:");
            super.onPostExecute(str[1]);
            //получаем курс валюты
            course=Double.parseDouble(str[1]);
            //считаем сумму валюты, в которую конвертировали
            double amountFinalCalculation = amountCurrencyValue*course;
            textResultName.setVisibility(View.VISIBLE);
            textResult1.setVisibility(View.VISIBLE);
            //выводим результат в текстовое поле
            textResult1.setText(String.format("%.1f",amountCurrencyValue)+ " "+ currencyGet1+ " -> " + String.format("%.1f",amountFinalCalculation) + " "+ currencyGet2);
        }
    }
}
