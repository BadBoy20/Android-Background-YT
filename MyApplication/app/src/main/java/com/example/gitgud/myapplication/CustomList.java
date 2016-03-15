package com.example.gitgud.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] title;
    private final String[] published;
    private final String[] views;
    private final String[] channels;
    private final Bitmap[] vidImages;


    public CustomList(Activity context,
                      String[] title, String[] published, String[] views, String[] channels, Bitmap[] bitmaps) {
        super(context, R.layout.list_single, title);
        this.context = context;
        this.title = title;
        this.published = published;
        this.views = views;
        this.channels = channels;
        this.vidImages = bitmaps;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        TextView subtitle = (TextView) rowView.findViewById(R.id.subtitle);
        TextView viewsTitle = (TextView) rowView.findViewById(R.id.views);
        TextView channelBox = (TextView) rowView.findViewById(R.id.channel);
        ImageView vidImage = (ImageView) rowView.findViewById(R.id.imageView);
        txtTitle.setText(title[position]);
        subtitle.setText(published[position]);
        viewsTitle.setText(views[position]);
        channelBox.setText("Channel: " + channels[position]);
        vidImage.setImageBitmap(vidImages[position]);

        return rowView;
    }
}