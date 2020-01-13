package com.example.showoffassignment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MediaAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<MediaData> mMediaData;
    LayoutInflater inflter;


    public MediaAdapter(Context c, ArrayList<MediaData> mediaData) {
        mContext = c;
        mMediaData=mediaData;
        inflter = (LayoutInflater.from(c));
    }
    @Override
    public int getCount() {
        return mMediaData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflter.inflate(R.layout.gridview_content, null); // inflate the layout
        ImageView icon = (ImageView) view.findViewById(R.id.selectedImage);
        // get the reference of ImageView

        Picasso.get().load(mMediaData.get(position).getMediaUrl()).into(icon);

        Log.v("MediaAdapter",""+mMediaData.get(position).getMediaUrl());
        //icon.setImageResource(logos[i]); // set logo images
        return view;
    }
}
