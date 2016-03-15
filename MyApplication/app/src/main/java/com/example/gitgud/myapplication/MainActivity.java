package com.example.gitgud.myapplication;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public static boolean musicStatus = false;
    public static ArrayList<videoObject> mainList = new ArrayList<videoObject>();

    public static String filesDir;

    /* THIS PASSWORD IS WHAT CHECKS AGAINST THE SERVER TO ENSURE ITS YOUR PHONE */
    static String UAPassword = "afbqi";
    /*                                                                          */
    static MediaPlayer mp = new MediaPlayer();
    public static videoObject playingObject;
    public static Bitmap playingBitmap;
    Button nowPlayingButton;
    public static String songUUID;

    ListView list;

    String[] title,published ,views, URLs , channels;
    Bitmap[] bitmaps;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nowPlayingButton = (Button)findViewById(R.id.button);
        if(musicStatus == false){
            nowPlayingButton.setVisibility(View.INVISIBLE);
        }
        else{
            nowPlayingButton.setVisibility(View.VISIBLE);
        }

        registerReceiver(showPlayButton, new IntentFilter("abcd"));
        registerReceiver(hidePlayButton, new IntentFilter("efgh"));

filesDir = getApplicationContext().getFilesDir().getAbsolutePath();

    }

    private final BroadcastReceiver showPlayButton = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nowPlayingButton.setVisibility(View.VISIBLE);
        }
    };

    private final BroadcastReceiver hidePlayButton = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nowPlayingButton.setVisibility(View.INVISIBLE);
        }
    };

    public void nowPlaying(View v){
        Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
        startActivity(intent);
    }

    public static InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return in;
    }

    public static Bitmap DownloadImage(String URL) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return bitmap;
    }

    public void searchButton(View v) throws IOException {
        mainList.clear();
        EditText searchBox = (EditText)findViewById(R.id.searchBox);
        String searchTerms = searchBox.getText().toString();
        ProgressDialog progress;
        progress = ProgressDialog.show(this, "Searching",
                "Please wait...", true);
        searchFunction(searchTerms, progress);
    }

    public void searchFunction(String query,ProgressDialog progress) throws IOException {
        new parseYT(query, progress).execute();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(showPlayButton);
        unregisterReceiver(hidePlayButton);
        super.onDestroy();
    }

    public class parseYT extends AsyncTask<Void, Void, Void> {
        String searchQuery;
        ProgressDialog pro;
        public parseYT(String query, ProgressDialog progress){
            searchQuery = query;
            pro = progress;
        }

        @Override
        protected void onPreExecute() {
            mainList = new ArrayList<videoObject>();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String queries = null;
            try {
                queries = URLEncoder.encode(searchQuery, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Document doc = null;
            try {
                doc = Jsoup.connect("https://www.youtube.com/results?search_query=" + queries)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:44.0) Gecko/20100101 Firefox/44.0")
                        .referrer("http://www.google.com").get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements links = doc.select(".yt-lockup-content");

            for (Element element : links) {
                Element element2 = element.select("h3").get(0).select("a").get(0);
                if(!element2.attr("title").startsWith("Mix - ")){
                    String title = null;
                    String URLs = null;
                    String Channel = null;
                    String published = null;
                    String views = null;

                    try{
                        title = element2.attr("title");
                        URLs = element2.attr("href").replace("/watch?v=", "");
                        Channel = element.select("div").get(1).select("a").get(0).text();
                        published = element.select("div").get(2).select("ul").get(0).select("li").get(0).text();
                        views = element.select("div").get(2).select("ul").get(0).select("li").get(1).text();
                        if(!URLs.contains("/user/")){
                            videoObject vO = new videoObject(title, views, published, Channel, URLs);
                            mainList.add(vO);
                        }
                    }
                    catch(IndexOutOfBoundsException IOOBE){

                    }
                    catch (NullPointerException NPE){

                    }
                }
            }
            for(int i = 0; i< mainList.size(); i++){
                try{
                    String link = "http://i.ytimg.com/vi/" + mainList.get(i).getURLs() + "/mqdefault.jpg";
                    // System.out.println("LINK IS!!!!: " +link);
                    mainList.get(i).setBitmap(DownloadImage(link));
                }
                catch(RuntimeException RE){

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pro.dismiss();


            title = new String[MainActivity.mainList.size()];
            published = new String[MainActivity.mainList.size()];
            views = new String[MainActivity.mainList.size()];
            URLs = new String[MainActivity.mainList.size()];
            channels = new String[MainActivity.mainList.size()];
            bitmaps = new Bitmap[MainActivity.mainList.size()];
            for(int i = 0; i< MainActivity.mainList.size(); i++){
                title[i] = MainActivity.mainList.get(i).getTitle();
                published[i] = MainActivity.mainList.get(i).getPublished();
                views[i] = MainActivity.mainList.get(i).getViews();
                URLs[i] = MainActivity.mainList.get(i).getURLs();
                channels[i] = MainActivity.mainList.get(i).getChannel();
                bitmaps[i] = MainActivity.mainList.get(i).getBitmap();
            }
            CustomList adapter = new
                    CustomList(MainActivity.this, title, published, views, channels, bitmaps);
            list=(ListView)findViewById(R.id.listView2);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent(MainActivity.this, Downloadable.class);
                    intent.putExtra("Title",title[+position]);
                    intent.putExtra("Subtitle", published[+position]);
                    intent.putExtra("Views",views[+position]);
                    intent.putExtra("URLs",URLs[+position]);
                    intent.putExtra("channels",channels[+position]);
                    intent.putExtra("BitmapImage", bitmaps[+position]);
                    startActivity(intent);

                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
