package com.example.showoffassignment;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MediaData{

    private String id;
    private String mediaType;
    private String mediaUrl;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }



}
