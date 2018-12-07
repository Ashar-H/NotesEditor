package com.example.asharh.noteseditor;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Note implements Serializable{

    private static final long serialVersionUID = 1L;
    private  String mTitle;
    private  String mContent;
    private  long mDateTime;
    private  String fileName;

    public Note(String title, String content, long dateTime){

        this.mTitle = title;
        this.mContent = content;
        this.mDateTime = dateTime;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public void setDateTime(long dateTime){
        this.mDateTime = dateTime;
    }

    public long getDateTime() {
        return mDateTime;
    }

    public String getFileName(){ return this.fileName;}

    public void setFileName(String fileName){ this.fileName = fileName; }

    public String getDateTimeFormattedAsString(Context context, String format) {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format,
                context.getResources().getConfiguration().locale);

        simpleDateFormat.setTimeZone(TimeZone.getDefault());

        return simpleDateFormat.format(new Date(mDateTime));
    }

}
