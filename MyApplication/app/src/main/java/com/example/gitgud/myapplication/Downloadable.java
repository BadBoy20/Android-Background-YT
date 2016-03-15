package com.example.gitgud.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Downloadable extends AppCompatActivity {

    public static String ytLink2;
    String theTitleBox2, theViews2, theURLs2, thePublished2, theChannels2;
    Bitmap bitImage2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadable);

        Intent myIntent = getIntent();

        TextView titleBox = (TextView) findViewById(R.id.textView4);
        TextView subtitle = (TextView) findViewById(R.id.textView5);
        TextView viewsBox = (TextView) findViewById(R.id.textView6);
        ImageView user_image = (ImageView) findViewById(R.id.imageView3);

        theTitleBox2 = myIntent.getStringExtra("Title");
        theViews2 = myIntent.getStringExtra("Views");
        theURLs2 = myIntent.getStringExtra("URLs");
        thePublished2 = myIntent.getStringExtra("Subtitle");
        theChannels2 = myIntent.getStringExtra("channels");

        titleBox.setText(theTitleBox2);
        subtitle.setText(thePublished2);
        viewsBox.setText(theViews2);
        ytLink2 = theURLs2.replace("https://www.youtube.com/watch?v=","");
        bitImage2 = (Bitmap) myIntent.getParcelableExtra("BitmapImage");
        user_image.setImageBitmap(bitImage2);


    }


    public void nextActivity(View v){
        new fetcher().execute();
    }


    class fetcher extends AsyncTask<Void,Void, Void> {
        ProgressDialog progressDialog;
        @Override
        protected  void onPreExecute()
        {
            MainActivity.songUUID = UUID.randomUUID().toString().substring(24);
            progressDialog =ProgressDialog.show(Downloadable.this, "","Server side downloading..");
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String url = "http://ming.skies.tw:2000/";
                // String url = "http://192.168.2.92:2000/";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", MainActivity.UAPassword);
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                String urlParameters = "&ytlink="+ytLink2 + "&uuid=" + MainActivity.songUUID;

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                byte[] buffer = new byte[con.getInputStream().available()];
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            progressDialog.dismiss();
            MainActivity.musicStatus = false;
            Intent intent = new Intent(Downloadable.this, DownloadActivity.class);
            intent.putExtra("Title", theTitleBox2);
            intent.putExtra("Subtitle", thePublished2);
            intent.putExtra("Views", theViews2);
            intent.putExtra("URLs",theURLs2);
            intent.putExtra("channels",theChannels2);
            intent.putExtra("BitmapImage", bitImage2);
            startActivity(intent);
            finish();
        }
    }


}
