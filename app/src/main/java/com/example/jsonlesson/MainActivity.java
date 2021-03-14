package com.example.jsonlesson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection = null;
            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data!=-1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            }
            catch (Exception e)
            {
                e.printStackTrace();

                return null;

            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonobject = new JSONObject(s);
                String message = "";
                String weatherInfo = jsonobject.getString("weather");

                Log.i("Weather Content",weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for(int i = 0; i<arr.length();i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                     if(!main.equals("") && !description.equals(""))
                     {
                         message += main+": "+description+"\r\n";
                     }
                    
                }

                if(!message.equals(""))
                {
                    resultTextView.setText(message);
                    }
                else
                {
                    resultTextView.setText("");
                    Toast.makeText(getApplicationContext(),"Could not find Weather:(",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                resultTextView.setText("");
                Toast.makeText(getApplicationContext(),"Could not find Weather:(",Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void getWeather(View view){

        try {
            //In case city name has any spaces or other characters. It converts the name into url format
            String encodedCityName = URLEncoder.encode(editText.getText().toString(),"UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=81ae09feeedc6e66df508ee4d21dc08d");

            //UI-UX Tip
            //For better experience we hide the keyboard once the get weather button is clicked to show the result properly
            InputMethodManager mpr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mpr.hideSoftInputFromWindow(editText.getWindowToken(),0);

        } catch (Exception e) {
            e.printStackTrace();
            //In case wrong city name is entered.Give error in form of a toast
            resultTextView.setText("");
            Toast.makeText(getApplicationContext(),"Could not find Weather:(",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextTextPersonName);      //from front end
        resultTextView = findViewById(R.id.resultTextView);    //from front end

    }
}
