package com.pie.weatherapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView cityName;
    Button search;
    TextView show;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        class getWeather extends AsyncTask<String, Void, String>{
            @Override
            protected String doInBackground(String... urls){
                StringBuilder result = new StringBuilder();
                try{
                    URL url= new URL(urls[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line="";
                    while((line = reader.readLine()) != null){
                        result.append(line).append("\n");
                    }
                    return result.toString();
                }catch(Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result){
                super.onPostExecute(result);
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    String weatherInfo = jsonObject.getString("main");
                    weatherInfo = weatherInfo.replace("temp","Temperature");
                    weatherInfo = weatherInfo.replace("feels_like","Feels Like");
                    weatherInfo = weatherInfo.replace("temp_max","Temperature Max");
                    weatherInfo = weatherInfo.replace("temp_min","Temperature Min");
                    weatherInfo = weatherInfo.replace("pressure","Pressure");
                    weatherInfo = weatherInfo.replace("humidity","Humidity");
                    weatherInfo = weatherInfo.replace("{","");
                    weatherInfo = weatherInfo.replace("}","");
                    weatherInfo = weatherInfo.replace(",","\n");
                    weatherInfo = weatherInfo.replace(":"," : ");
                    show.setText(weatherInfo);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        cityName = findViewById(R.id.cityname);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);
        final String[] temp={""};
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "Button Clicked! ", Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString();
                try{
                    if(city!=null){
                        url="https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=1762dde44d035bd8133d5df1a9602843";
                       // url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid={Enter your api Key Here}";
                    }else{
                        Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                    }
                    getWeather task= new getWeather();
                    temp[0] = task.execute(url).get();
                }catch(ExecutionException e){
                    e.printStackTrace();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                if(temp[0] == null){
                    show.setText("Cannot able to find Weather");
                }

            }
        });
    }
}