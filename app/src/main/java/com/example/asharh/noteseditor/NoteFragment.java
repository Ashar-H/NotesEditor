package com.example.asharh.noteseditor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NoteFragment extends Fragment {


    private EditText mTitle;
    private EditText mContent;
    private TextView mDate;
    private String title,content; //used in getDataFromInput()
    private long dateTime; //used in getDataFromInput()
    private String fileName = null;
    private Note savedNote = null;
    private static boolean HAS_BEEN_EDITED = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       Log.e("Note_Fragment","onCreateView()");

        View view =  inflater.inflate(R.layout.note_fragment, container, false);

       Log.e("Note_Fragment","onCreateView()-Enable Home Up Button on Actionbar");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = view.findViewById(R.id.mTitleEditText);
        mContent = view.findViewById(R.id.mContentEditText);
        mDate = view.findViewById(R.id.mNoteActivityDateTextView);
        mTitle.requestFocus();

        //Check if arguments has a filename
        //if yes it means an existing note is opened for editing

        Bundle bundle = getArguments();
        if (bundle != null){

           Log.e("Note_Fragment","OnCreateView()-Checking bundle for fileName");
            fileName = bundle.getString("FileName");
        }



        if (fileName != null && !fileName.isEmpty()){

           Log.e("Note_Fragment","OnCreateView()-fileName found in bundle-Load the note in editText fields for editing");

            savedNote = FileHelper.getNoteByFileName(getContext(),fileName);
            mTitle.setText(savedNote.getTitle());
            mTitle.setSelection(mTitle.getText().length());
            mContent.setText(savedNote.getContent());
            //mContent.setSelection(mContent.getText().length());
            mDate.setText(savedNote.getDateTimeFormattedAsString(getContext(),"EEE, MMM d, yyyy "));
            mDate.append("at "+savedNote.getDateTimeFormattedAsString(getContext(),"h:mm aaa "));
            mDate.setVisibility(View.VISIBLE); //This textView is set to invisible in xml. Not shown for new notes. Only shown for existing notes

            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Edit Note");

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
                   Log.e("Note_Fragment","OnCreateView()-TextWatcher-afterTextChanged()");

                    //Log.e("HASHCODE","S.hashcode= "+s.hashCode()+" mTitle.hashcode= "+mTitle.getText().hashCode());

                    //Check s belongs to which editText
                    //if true, s belongs to the title editText
                    if(s.hashCode() == mTitle.getText().hashCode() ) {

                        //if user changes the title but then delete all the changes before saving,
                        //HAS_BEEN_EDITED shouldn't be set to true because the note hasn't been changed.

                        if (!(s.toString().trim().equals(oldTitle))){
                           Log.e("Note_Fragment","OnCreateView()-afterTextChanged()-Title has been edited-Setting HAS_BEEN_EDITED to true");
                            HAS_BEEN_EDITED=true;
                        }
                        else {
                           Log.e("Note_Fragment","OnCreateView()-afterTextChanged()-Title has not been edited-Setting HAS_BEEN_EDITED to false");
                            HAS_BEEN_EDITED=false;
                        }
                    }
                    //otherwise s belongs to the content editText
                    else{

                        //if user changes the content but then undo all the changes before saving the note, then
                        //HAS_BEEN_EDITED shouldn't be set to true because the note hasn't been changed.

                        if (!(s.toString().trim().equals(oldContent))){
                           Log.e("Note_Fragment","OnCreateView()-afterTextChanged()-Content has been edited-Setting HAS_BEEN_EDITED to true");
                            HAS_BEEN_EDITED=true;
                        }
                        else {
                           Log.e("Note_Fragment","OnCreateView()-afterTextChanged()-Content has not been edited-Setting HAS_BEEN_EDITED to false");
                            HAS_BEEN_EDITED=false;
                        }

                    }

                }
            };

            mTitle.addTextChangedListener(tt);
            mContent.addTextChangedListener(tt);


        }
        else {

           Log.e("Note_Fragment","OnCreateView()-fileName not found in bundle - New Note - Empty EditTexts are loaded");
            //user is creating a new note
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("New Note");
        }





        return view;
    }

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       Log.e("Note_Fragment","onOptionsItemSelected()");
        switch (item.getItemId()){

            case android.R.id.home:

               Log.e("Note_Fragment","onOptionsItemSelected()");

                break;

        }

        return true;
    }*/


    //For avoiding code redundancy in onBackPressed() and onOptionsItemSelected()

    private void getDataFromInputFields(){

       Log.e("Note_Fragment","getDataFromInputFields()");

        title = mTitle.getText().toString().trim();
        content = mContent.getText().toString().trim();
        dateTime = System.currentTimeMillis();

    }


    public void saveNoteFromActivity(){

       Log.e("Note_Fragment","saveNoteFromActivity()- Called by Main_Activity");
        getDataFromInputFields();
        saveNote(title,content,dateTime);

    }

    //For avoiding code redundancy in onBackPressed() and onOptionsItemSelected()

    private boolean saveNote(String title,String content,long dateTime){

       Log.e("Note_Fragment","saveNote()");

        //Check if the note is empty

       Log.e("Note_Fragment","saveNote()-Checking if the note is empty");
        if(!title.isEmpty() || !content.isEmpty()){

           Log.e("Note_Fragment","saveNote()-Note is not empty");

            //Check if the note already exists
            //Also check if the note has been edited i.e., user made any changes in the title or in the content

           Log.e("Note_Fragment","saveNote()-Checking if the note already exists or if it is a new Note");

            if (savedNote != null){

               Log.e("Note_Fragment","saveNote()-Note already exists");


                if (HAS_BEEN_EDITED){

                   Log.e("Note_Fragment","saveNote()-HAS_BEEN_EDITED is true-The note has been edited.");

                    //Call updateNote method in FileHelper class

                    if(FileHelper.updateNote(getContext(),savedNote.getFileName(),
                            title,content,dateTime)){

                       Log.e("Note_Fragment","saveNote()-saving the changes by calling updateNote() in FileHelper");

                        //Toast.makeText(this,"Changes Saved",Toast.LENGTH_SHORT).show();
                        Toast toast = Toast.makeText(getContext(), "Changes Saved", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 175);
                        toast.show();

                       Log.e("Note_Fragment","saveNote()-setting HAS_BEEN_EDITED to false for the next time.");
                        HAS_BEEN_EDITED = false; // Resetting the flag for next time.

                        return true;
                    }

                    else{
                       Log.e("Note_Fragment","saveNote()-updateNote() failed");
                        //Toast.makeText(this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                        Toast toast = Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 175);
                        toast.show();
                        return false;
                    }

                }

            }
            else{
               Log.e("Note_Fragment","saveNote()-Note doesn't already exist.It is a new note");
                //Create a new note and save it
                Note mNote = new Note(title,content,dateTime);
                if(FileHelper.saveNote(getContext(),mNote)){

                   Log.e("Note_Fragment","saveNote()-saving the note by calling saveNote() in FileHelper");
                    //Toast.makeText(this,"Note Saved",Toast.LENGTH_SHORT).show();

                    Toast toast = Toast.makeText(getContext(), "Note Saved", Toast.LENGTH_SHORT);
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
