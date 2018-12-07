package com.example.asharh.noteseditor;

import android.content.Intent;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;

import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.CheckBox;

import android.widget.ListView;

import android.widget.Toast;
import android.view.ActionMode;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Note> mList;
    public static List<Note> userSelection = new ArrayList<>();
    private FloatingActionButton fab;
    public static boolean deleteActionFlag = false;
    public static ActionMode actionMode;
    private static boolean SORT_NEW_TO_OLD;
    private SharedPreferences mPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Get the last sorting order that user selected.
        //New to Old is the default order.
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        SORT_NEW_TO_OLD = mPreference.getBoolean(getString(R.string.sort_order),true);

        fab = findViewById(R.id.fab);
        mList = new ArrayList<>();

        //RecyclerView
        mRecyclerView = findViewById(R.id.mRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MyRecyclerViewAdapter(this,mList);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();

        mList.clear();

        //Testing
        /*FileHelper.deleteAll(this);

        //Create 50 notes for testing
        for (int i=0;i<50;i++){
            FileHelper.saveNote(this,new Note("Testing"+i,""+i,System.currentTimeMillis()));
        }*/


        //Get All Saved Notes
        for (Note note: FileHelper.getAllSavedNotes(this)){
            mList.add(note);
        }

        //If there are no saved notes,
        if(mList == null || mList.size() == 0){
            Toast.makeText(this,"No Saved Notes !",Toast.LENGTH_SHORT).show();

        }
        //notify the adapter to display all the saved notes
        else{
            //sort the list
            sortListView();
            mAdapter.notifyDataSetChanged();
        }

    }


    //fab onClick method
    public void newNoteButtonPressed(View view){
        Intent intent = new Intent(MainActivity.this,NoteActivity.class);
        startActivity(intent);
    }

    //Options Menu delete and sort buttons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_delete:
                if (mList.size()>0){
                    deleteActionFlag = true;
                    mAdapter.notifyDataSetChanged();
                    fab.hide();
                    MyActionBarCallBack callBack = new MyActionBarCallBack();
                    actionMode = startActionMode(callBack);
                }
                else{
                    Toast.makeText(this,"Nothing to delete",Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.action_sort:
                //Change the sorting order
                SORT_NEW_TO_OLD = !SORT_NEW_TO_OLD;

                //SAVE THE NEW SORTING ORDER IN SHARED PREFERENCES
                SharedPreferences.Editor editor = mPreference.edit();
                editor.putBoolean(getString(R.string.sort_order),SORT_NEW_TO_OLD);
                editor.commit();

                //CALL THE METHOD TO SORT LIST WITH NEW SORTING ORDER
                sortListView();

                //Toast
                if (SORT_NEW_TO_OLD)
                    Toast.makeText(this,"Newest First",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Oldest First",Toast.LENGTH_SHORT).show();

                //Refresh ListView
                mAdapter.notifyDataSetChanged();
                break;
        }


        return true;
    }



    //listView onItemClickListener
    //Open the pressed note in noteActivity for editing
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //If contextual app bar is running
        //then clicking on the listitem must not open the note for editing
        //instead it must check/uncheck the checkbox
        if(deleteActionFlag){
            CheckBox chk = view.findViewById(R.id.mCheckbox);
            if (mAdapter.mCheckBoxState.get(position)){
                chk.setChecked(false);
               // mAdapter.mCheckBoxState.put(position,false);
            }
            else {
                chk.setChecked(true);
               // mAdapter.mCheckBoxState.put(position,true);
            }

        }
        else{
            Intent intent = new Intent(MainActivity.this,NoteActivity.class);
            intent.putExtra("FileName",mList.get(position).getFileName());
            startActivity(intent);
        }

    }



    //Contextual App Bar
    class MyActionBarCallBack implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_menu,menu);
            mode.setTitle(userSelection.size()+" items selected");
            actionMode = mode;

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()){
                case R.id.action_del:
                    removeNote(userSelection);
                    actionMode.finish();
                    MainActivity.deleteActionFlag=false;
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this,"Successfully Deleted",Toast.LENGTH_SHORT).show();
            break;
        }



            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {


            userSelection.clear();
            mAdapter.mCheckBoxState.clear();
            MainActivity.deleteActionFlag=false;
            mAdapter.notifyDataSetChanged();
            fab.show();
        }

        private void removeNote(List<Note> userSelection){

            for(Note note:userSelection){

                mList.remove(note);
                FileHelper.deleteNote(MainActivity.this,note.getFileName());
            }
            //notifyDataSetChanged();
        }
    }


    //Sorting method
    public void sortListView() {


     //   Log.d("Sorting",""+SORT_NEW_TO_OLD);
        Note temp;


        SORT_NEW_TO_OLD = mPreference.getBoolean(getString(R.string.sort_order),true);


        if (SORT_NEW_TO_OLD) {
            //Newest First
            for (int i = 0; i < mList.size() - 1; i++) {
                for (int j = i + 1; j < mList.size(); j++) {
                    if (mList.get(i).getDateTime() < mList.get(j).getDateTime()) {
                        temp = mList.get(j);
                        mList.set(j, mList.get(i));
                        mList.set(i, temp);
                    }
                }
            }

        } else {
            //Oldest first
            for (int i = 0; i < mList.size() - 1; i++) {
                for (int j = i + 1; j < mList.size(); j++) {
                    if (mList.get(i).getDateTime() > mList.get(j).getDateTime()) {
                        temp = mList.get(i);
                        mList.set(i, mList.get(j));
                        mList.set(j, temp);
                    }
                }
            }

        }



    }
}
