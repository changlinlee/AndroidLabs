package com.example.androidlabs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {

    private static final String TAG = "WeatherForecast";

    ImageView weatherIcon;
    TextView current, min, max, uv;
    ProgressBar progressBar;
    String[] urls = {
            "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric",
            "http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        weatherIcon = (ImageView) findViewById(R.id.img_weather_icon);
        current = (TextView) findViewById(R.id.current_temp);
        min = (TextView) findViewById(R.id.min_temp);
        max = (TextView) findViewById(R.id.max_temp);
        uv = (TextView) findViewById(R.id.uv_rating);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ForecastQuery forecastQuery = new ForecastQuery();
        forecastQuery.execute(urls);
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {

        String currentTemp, minTemp, maxTemp, uvRate, iconName;
        Bitmap image = null;

        @Override
        protected String doInBackground(String... args) {
            try {
                URL urlXml = new URL(args[0]);
                HttpURLConnection connection = (HttpURLConnection) urlXml.openConnection();
                InputStream response = connection.getInputStream();

                // JSON part
                URL urlJson = new URL(args[1]);
                HttpURLConnection connectionJ = (HttpURLConnection) urlJson.openConnection();
                InputStream responseJ = connectionJ.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(responseJ, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                JSONObject uvReport = new JSONObject(result);
                uvRate = String.valueOf(uvReport.getDouble("value"));
                connectionJ.disconnect();

                // XML part
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("temperature")) {
                            currentTemp = xpp.getAttributeValue(null, "value");
                            publishProgress(25);
                            Thread.sleep(500);
                            minTemp = xpp.getAttributeValue(null, "min");
                            publishProgress(50);
                            Thread.sleep(500);
                            maxTemp = xpp.getAttributeValue(null, "max");
                            publishProgress(75);
                            Thread.sleep(500);
                        }

                        if (xpp.getName().equals("weather")) {
                            iconName = xpp.getAttributeValue(null, "icon");
                            String imgName = iconName + ".png";

                            if (fileExistence(imgName)) {
                                Log.i(TAG, "Image " + imgName + " is already exists");
                                FileInputStream fis = null;
                                try {
                                    fis = openFileInput(imgName);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                image = BitmapFactory.decodeStream(fis);
                            } else {
                                Log.i(TAG, "Image " + imgName + " is not exists, downloading...");
                                URL imgUrl = new URL("http://openweathermap.org/img/w/" + imgName);
                                HttpURLConnection urlConnection = (HttpURLConnection) imgUrl.openConnection();
                                urlConnection.connect();
                                int responseCode = urlConnection.getResponseCode();

                                if (responseCode == 200) {
                                    image = BitmapFactory.decodeStream(urlConnection.getInputStream());
                                    FileOutputStream outputStream = openFileOutput(imgName, MODE_PRIVATE);
                                    image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                    outputStream.flush();
                                    outputStream.close();
                                }
                                urlConnection.disconnect();
                            }
                            publishProgress(100);
                            Thread.sleep(500);
                        }
                    }
                    eventType = xpp.next();
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "All elements are loaded";
        }

        @Override
        protected void onProgressUpdate(Integer... args) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }

        @Override
        protected void onPostExecute(String fromDoInBackground) {
            Log.i(TAG, fromDoInBackground);
            current.setText("Current Temperature: " + currentTemp);
            min.setText("Min Temperature: " + minTemp);
            max.setText("Max Temperature: " + maxTemp);
            uv.setText("UV Rating: " + uvRate);
            weatherIcon.setImageBitmap(image);
            progressBar.setVisibility(View.INVISIBLE);
        }

        public boolean fileExistence(String fName) {
            File file = getBaseContext().getFileStreamPath(fName);
            return file.exists();
        }
    }
}