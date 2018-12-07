package com.example.asharh.noteseditor;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class NoteActivity extends AppCompatActivity {

    private EditText mTitle;
    private EditText mContent;
    private TextView mDate;
    private String title,content; //used in getDataFromInput()
    private long dateTime; //used in getDataFromInput()
    private String fileName = null;
    private Note savedNote = null;
    private static boolean HAS_BEEN_EDITED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = findViewById(R.id.mTitleEditText);
        mContent = findViewById(R.id.mContentEditText);
        mDate = findViewById(R.id.mNoteActivityDateTextView);
        mTitle.requestFocus();

        //Check if intent has a filename
        //if yes it means an existing note is opened for editing
        fileName = getIntent().getStringExtra("FileName");


        if (fileName != null && !fileName.isEmpty()){
            savedNote = FileHelper.getNoteByFileName(this,fileName);
            mTitle.setText(savedNote.getTitle());
            mTitle.setSelection(mTitle.getText().length());
            mContent.setText(savedNote.getContent());
            //mContent.setSelection(mContent.getText().length());
            mDate.setText(savedNote.getDateTimeFormattedAsString(this,"EEE, MMM d, yyyy "));
            mDate.append("at "+savedNote.getDateTimeFormattedAsString(this,"h:mm aaa "));
            mDate.setVisibility(View.VISIBLE); //This textView is set to invisible in xml. Not shown for new notes. Only shown for existing notes

            getSupportActionBar().setTitle("Edit Note");

            //Used in textWatcher's afterTextChanged() method
            final String oldTitle = mTitle.getText().toString();
            final String oldContent = mContent.getText().toString();


            //Adding TextWatcher on title and content editTexts to capture any change
            //Otherwise updateNote message always gets displayed even when user doesn't change anything.
            //If user makes any changes, HAS_BEEN_EDITED flag will be set to true
            TextWatcher tt = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                   // Log.i("HASHCODE","S.hashcode= "+s.hashCode()+" mTitle.hashcode= "+mTitle.getText().hashCode());

                    //Check s belongs to which editText
                    //if true, s belongs to the title editText
                    if(s.hashCode() == mTitle.getText().hashCode() ) {

                        //if user changes the title but then delete all the changes before saving,
                        //HAS_BEEN_EDITED shouldn't be set to true because the note hasn't been changed.

                        if (!(s.toString().trim().equals(oldTitle))){
                            HAS_BEEN_EDITED=true;
                        }
                        else {
                            HAS_BEEN_EDITED=false;
                        }
                    }
                    //otherwise s belongs to the content editText
                    else{

                        //if user changes the content but then undo all the changes before saving the note, then
                        //HAS_BEEN_EDITED shouldn't be set to true because the note hasn't been changed.

                        if (!(s.toString().trim().equals(oldContent))){
                            HAS_BEEN_EDITED=true;
                        }
                        else {
                            HAS_BEEN_EDITED=false;
                        }

                    }

                }
            };

            mTitle.addTextChangedListener(tt);
            mContent.addTextChangedListener(tt);


        }
        else {
            //user is creating a new note
            getSupportActionBar().setTitle("New Note");
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){


            case android.R.id.home:

                //SAVE NOTE IF TITLE OR CONTENT IS NOT EMPTY
                getDataFromInputFields();

                //call the local method to save the note
                saveNote(title,content,dateTime);

                super.onBackPressed();

                break;

        }

        return true;
    }


    /*
    * If bottom back button is pressed then
    * first save the note then go back to the last activity in backStack
    * */
   // @Override

    public void onBackPressed() {

        //SAVE NOTE IF TITLE OR CONTENT IS NOT EMPTY
        getDataFromInputFields();

        //call the local method to save the note
        saveNote(title,content,dateTime);

        super.onBackPressed();
    }


    //For avoiding code redundancy in onBackPressed() and onOptionsItemSelected()

    private void getDataFromInputFields(){

        title = mTitle.getText().toString().trim();
        content = mContent.getText().toString().trim();
        dateTime = System.currentTimeMillis();

    }


    //For avoiding code redundancy in onBackPressed() and onOptionsItemSelected()

    private boolean saveNote(String title,String content,long dateTime){


        //Check if the note is empty

        if(!title.isEmpty() || !content.isEmpty()){

            //Check if the note already exists
            //Also check if the note has been edited i.e., user made any changes in the title or in the content

            if (savedNote != null){

                Log.e("HASBEENEDITED",HAS_BEEN_EDITED+"");

                if (HAS_BEEN_EDITED){

                    //Call updateNote method in FileHelper class

                    if(FileHelper.updateNote(this,savedNote.getFileName(),
                            title,content,dateTime)){

                        //Toast.makeText(this,"Changes Saved",Toast.LENGTH_SHORT).show();
                        Toast toast = Toast.makeText(NoteActivity.this, "Changes Saved", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 175);
                        toast.show();

                        HAS_BEEN_EDITED = false; // Resetting the flag for next time.

                        return true;
                    }

                    else{
                        //Toast.makeText(this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                        Toast toast = Toast.makeText(NoteActivity.this, "Something went wrong!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 175);
                        toast.show();
                        return false;
                    }

                }

            }
            else{
                //Create a new note and save it
                Note mNote = new Note(title,content,dateTime);
                if(FileHelper.saveNote(this,mNote)){
                    //Toast.makeText(this,"Note Saved",Toast.LENGTH_SHORT).show();

                    Toast toast = Toast.makeText(NoteActivity.this, "Note Saved", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 175);
                    toast.show();
                    return true;
                }
                else
                    return false;

            }
        }

        return true;
    }

}
