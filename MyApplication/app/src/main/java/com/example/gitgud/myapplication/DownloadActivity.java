package com.example.gitgud.myapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownloadActivity extends AppCompatActivity {

    public static String ytLink;

    Button pause;
    static SeekBar seek;
    static boolean newSong = false;
    static boolean serviceRunning = false;
    String theTitleBox, theViews, theURLs, thePublished, theChannels;

    Bitmap bitImage;
    //public Intent thisIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        registerReceiver(abcd, new IntentFilter("xyz"));

        Intent myIntent = getIntent();
        pause = (Button) findViewById(R.id.button4);
        seek = (SeekBar) findViewById(R.id.seekBar);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(MainActivity.mp != null && fromUser){
                    MainActivity.mp.seekTo(progress);
                }
            }
        });


        TextView titleBox = (TextView) findViewById(R.id.textView);
        TextView subtitle = (TextView) findViewById(R.id.textView2);
        TextView viewsBox = (TextView) findViewById(R.id.textView3);
        ImageView user_image = (ImageView) findViewById(R.id.imageView2);

        if(MainActivity.musicStatus == true){
            pause.setEnabled(true);
            seek.setMax(MainActivity.mp.getDuration());
            seek.setProgress(MainActivity.mp.getCurrentPosition());
            titleBox.setText(MainActivity.playingObject.getTitle());
            subtitle.setText(MainActivity.playingObject.getPublished());
            viewsBox.setText(MainActivity.playingObject.getViews());
            ytLink = MainActivity.playingObject.getURLs().replace("https://www.youtube.com/watch?v=","");
            user_image.setImageBitmap(MainActivity.playingBitmap);
            if(MainActivity.mp.isPlaying()){
                pause.setText("Pause");
            }else {
                pause.setText("Resume");
            }
        }
        else{

             theTitleBox = myIntent.getStringExtra("Title");
             theViews = myIntent.getStringExtra("Views");
             theURLs = myIntent.getStringExtra("URLs");
             thePublished = myIntent.getStringExtra("Subtitle");
            theChannels = myIntent.getStringExtra("channels");


            titleBox.setText(theTitleBox);
            subtitle.setText(thePublished);
            viewsBox.setText(theViews);
            try{
                ytLink = theURLs.replace("https://www.youtube.com/watch?v=","");
                bitImage = (Bitmap) myIntent.getParcelableExtra("BitmapImage");
                user_image.setImageBitmap(bitImage);
                playSong();
            }
            catch (NullPointerException NPE){
System.out.println("reached here error");
            }
        }
    }

    private final BroadcastReceiver abcd = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    public static Runnable r=new Runnable() {
        public void run() {
            seek.setProgress(MainActivity.mp.getCurrentPosition());
            if (MainActivity.mp.getCurrentPosition()<MainActivity.mp.getDuration()) {
                seek.postDelayed(r, 33);
               // System.out.println("The current pos is 2 " + MainActivity.mp.getCurrentPosition());
            }
        }
    };

    public void playSong(){
        MainActivity.playingObject = new videoObject(theTitleBox, theViews, thePublished,theChannels,theURLs);
        MainActivity.playingBitmap = bitImage;
        newSong = true;
        if(serviceRunning == true){
            processStopService(MyService.TAG);
        }
        processStartService(MyService.TAG);
        pause.setEnabled(true);
        MainActivity.musicStatus = true;
        pause.setText("Pause");
    }

    public void playSong(View v){
        MainActivity.playingObject = new videoObject(theTitleBox, theViews, thePublished,theChannels,theURLs);
        MainActivity.playingBitmap = bitImage;
        // startService(new Intent(DownloadActivity.this, MyService.class));
        newSong = true;
        if(serviceRunning == true){
            processStopService(MyService.TAG);
        }

        processStartService(MyService.TAG);
        pause.setEnabled(true);
        MainActivity.musicStatus = true;
        pause.setText("Pause");

    }

    public void stopSong(View v){
        // stopService(new Intent(DownloadActivity.this, MyService.class));
        MainActivity.playingObject = null;
        MainActivity.playingBitmap = null;

    processStopService(MyService.TAG);

        pause.setEnabled(false);
        MainActivity.musicStatus = false;
        pause.setText("Pause");
        newSong = false;
        finish();
    }

    public void pauseSong(View v) throws IllegalStateException, InvocationTargetException, NullPointerException{
        if(MainActivity.mp.isPlaying()){
            MainActivity.mp.pause();
            pause.setText("Resume");
        }else {
            MainActivity.mp.start();
            pause.setText("Pause");
        }
    }

    private void processStartService ( final String tag){
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        intent.addCategory(tag);
        startService(intent);
    }

    public void processStopService(final String tag) {
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        intent.addCategory(tag);
        stopService(intent);
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(abcd);
        super.onDestroy();
    }


    public static class MyService extends Service{
        public static final String TAG = "MyServiceTag";


        File mediaFile = new File(MainActivity.filesDir + "/ytMusicBuff/mediafile");;
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            serviceRunning = true;
            new downloadPlay().execute();
            return 0;
        }



        class downloadPlay extends AsyncTask<Void,Void, Void>{

            String mediaUrl = "http://ming.skies.tw:2000/song/" + MainActivity.songUUID;
           // String mediaUrl = "http://192.168.2.92:2000/song/" + MainActivity.songUUID;
            InputStream is;
            @Override
            protected void onPreExecute() throws  RuntimeException{
                if(mediaFile.exists()){
                    mediaFile.delete();
                }
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Void... arg0) {
                try {
                    //URLConnection cn = new URL(mediaUrl).openConnection();
                    URL obj = new URL(mediaUrl);
                    HttpURLConnection cn = (HttpURLConnection) obj.openConnection();
                    cn.setRequestProperty("User-Agent", MainActivity.UAPassword);
                    is = cn.getInputStream();

                    File dir = new File(MainActivity.filesDir + "/ytMusicBuff/");
                    if (!dir.exists()) {
                        if (dir.mkdir()) {
                            System.out.println("Directory is created!");
                        } else {
                            System.out.println("Failed to create directory!");
                        }
                    }

                    FileOutputStream fos = new FileOutputStream(mediaFile);
                    byte buf[] = new byte[16 * 1024];
                    Log.i("FileOutputStream", "Download");

                    do {
                        int numread = is.read(buf);
                        if (numread <= 0)
                            break;
                        fos.write(buf, 0, numread);
                    } while (true);
                    fos.flush();
                    fos.close();
                    Log.i("FileOutputStream", "Saved");


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result)
            {
                sendBroadcast(new Intent("abcd"));
                MainActivity.mp = new MediaPlayer();

                MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mplayer) {
                        newSong = false;
                        //MainActivity.mp.release();
                        onDestroy();
                    }
                };
                MainActivity.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mplayer) {
                        Log.i("MediaPlayer", "Time for file is " + MainActivity.mp.getDuration());
                        seek.setMax(MainActivity.mp.getDuration());
                        MainActivity.mp.start();
                        Log.i("MediaPlayer", "Point Reached");
                        //pause.setEnabled(true);
                        r.run();
                    }
                });
                MainActivity.mp.setOnCompletionListener(listener);

                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(mediaFile);
                    MainActivity.mp.setDataSource(fis.getFD());
                    MainActivity.mp.prepare();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Log.i("MediaPlayer", "Start Player");

            }
        }

        @Override
        public void onDestroy() {
            serviceRunning = false;
            seek.removeCallbacks(r);
            seek.setProgress(0);
            try{
                MainActivity.mp.stop();
                MainActivity.mp.release();
                MainActivity.mp = null;
            }
          catch (NullPointerException NPE){
System.out.println("ERROR REACHED!");

          }
            if(mediaFile.exists()){
                mediaFile.delete();
            }
            if(newSong == false){
                sendBroadcast(new Intent("xyz"));
            }
            sendBroadcast(new Intent("efgh"));
            super.onDestroy();
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {

            return null;
        }
    }

}
