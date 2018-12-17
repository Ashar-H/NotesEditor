package com.example.asharh.noteseditor;

import android.content.Context;
import android.util.Log;

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

       //Log.d("Note Class","constructor");

        this.mTitle = title;
        this.mContent = content;
        this.mDateTime = dateTime;
    }


    public String getTitle() {
       //Log.d("Note Class","getTitle()");
        return mTitle;
    }

    public void setTitle(String mTitle) {

       //Log.d("Note Class","setTitle()");
        this.mTitle = mTitle;
    }

    public String getContent() {
       //Log.d("Note Class","getContent()");
        return mContent;
    }

    public void setContent(String mContent) {

       //Log.d("Note Class","setContent()");
        this.mContent = mContent;
    }

    public void setDateTime(long dateTime){

       //Log.d("Note Class","setDateTime()");
        this.mDateTime = dateTime;
    }

    public long getDateTime() {
       //Log.d("Note Class","getDateTime()");
        return mDateTime;
    }

    public String getFileName(){
       //Log.d("Note Class","getFileName()");
        return this.fileName;}

    public void setFileName(String fileName){
       //Log.d("Note Class","setFileName()");
        this.fileName = fileName; }

    public String getDateTimeFormattedAsString(Context context, String format) {

       //Log.d("Note Class","getDateTimeFormattedAsString()-Format "+format);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format,
                context.getResources().getConfiguration().locale);

        simpleDateFormat.setTimeZone(TimeZone.getDefault());

        return simpleDateFormat.format(new Date(mDateTime));
    }

}
