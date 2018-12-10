package com.example.asharh.noteseditor;

import android.content.Intent;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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


    private FragmentManager fragmentManager;
    private Bundle savedInstanceState;
    private NoteFragment noteFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("Main_Activity","OnCreate()");

        this.savedInstanceState = savedInstanceState;

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

        Log.e("Main_Activity","OnResume()");

        super.onResume();

        mRecyclerView.setVisibility(View.VISIBLE);
        fab.show();

        mList.clear();



        //Testing
        /*FileHelper.deleteAll(this);

        //Create 50 notes for testing
        for (int i=0;i<50;i++){
            FileHelper.saveNote(this,new Note("Testing"+i,""+i,System.currentTimeMillis()));
        }*/


        //Get All Saved Notes
        for (Note note: FileHelper.getAllSavedNotes(this)){
            Log.e("Main_Activity","OnResume()-Getting all saved notes.");
            mList.add(note);
        }

        //If there are no saved notes,
        if(mList == null || mList.size() == 0){
            Log.e("Main_Activity","OnResume()-No saved notes.");
            Toast.makeText(this,"No Saved Notes !",Toast.LENGTH_SHORT).show();

        }
        //notify the adapter to display all the saved notes
        else{
            //sort the list
            Log.e("Main_Activity","OnResume()-Calling sortListView()");
            sortListView();
            Log.e("Main_Activity","onOptionsItemSelected()-Refresh recycler view");
            mAdapter.notifyDataSetChanged();
        }

    }


    //fab onClick method
    public void newNoteButtonPressed(View view){

        Log.e("Main_Activity","Floating Action Button Clicked - Running newNoteButtonPressed()");

       /* Intent intent = new Intent(MainActivity.this,NoteActivity.class);
        startActivity(intent);*/

       //Open Note Fragment
        fragmentManager = getSupportFragmentManager();

        //Check if view container is available
        if(findViewById(R.id.fragment_container) != null){

            Log.e("Main_Activity","Fragment Container is available");
            //Check if this is the first time app is running
            if(savedInstanceState != null){

                Log.e("Main_Activity","Checking if the fragment is running for the first time.");

                /*
                 * If the app has already ran it means the first fragment
                 * is already loaded in the container and we don't want to
                 * overlap the first fragment by creating it again.
                 * */
                return;
            }
            else{

                Log.e("Main_Activity","Loading new note fragment");
                mRecyclerView.setVisibility(View.GONE);
                fab.hide();
                noteFragment = new NoteFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment_container, noteFragment, null);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

        }
        else{
            Log.e("Main_Activity","Loading new note fragment failed - Fragment Container not available");
        }

    }



    @Override
    public void onBackPressed() {

        Log.e("Main_Activity","onBackPressed()");

        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>0;

        if (canGoBack){

            Log.e("Main_Activity","onBackPressed()-canGoBack is true-Close the running fragment and save the note");

            ((NoteFragment) noteFragment).saveNoteFromActivity();

            fragmentManager = getSupportFragmentManager();

            fragmentManager.popBackStack();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            onResume();
        }

        else
        {
            Log.e("Main_Activity","onBackPressed()-canGoBack is false-Exit the application");
            super.onBackPressed();
        }



    }

    //Options Menu delete and sort buttons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.e("Main_Activity","onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.e("Main_Activity","onOptionsItemSelected()");

        switch (item.getItemId()){

            case android.R.id.home:

                Log.e("Main_Activity","onOptionsItemSelected()-Home Button Pressed on Actionbar");

                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {

                    Log.e("Main_Activity","onOptionsItemSelected()-Note fragment is currently opened.Save Note and go to main");
                    getSupportActionBar().setTitle("Notes Editor");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    noteFragment.saveNoteFromActivity();
                    fm.popBackStack();
                    onResume();
                }
                break;

            case R.id.action_delete:
                Log.e("Main_Activity","onOptionsItemSelected()-Delete Button Pressed on Actionbar");
                if (mList.size()>0){
                    Log.e("Main_Activity","onOptionsItemSelected()-List size is greater than 0, turn on contextual menu");
                    deleteActionFlag = true;
                    Log.e("Main_Activity","onOptionsItemSelected()-Refresh recycler view");
                    mAdapter.notifyDataSetChanged();
                    fab.hide();
                    MyActionBarCallBack callBack = new MyActionBarCallBack();
                    actionMode = startActionMode(callBack);
                }
                else{
                    Log.e("Main_Activity","onOptionsItemSelected()-No notes to delete");
                    Toast.makeText(this,"Nothing to delete",Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.action_sort:

                Log.e("Main_Activity","onOptionsItemSelected()-Sort Button Pressed on Actionbar");
                //Change the sorting order
                SORT_NEW_TO_OLD = !SORT_NEW_TO_OLD;

                //SAVE THE NEW SORTING ORDER IN SHARED PREFERENCES
                Log.e("Main_Activity","onOptionsItemSelected()-Save sorting order in SharedPreferences");
                SharedPreferences.Editor editor = mPreference.edit();
                editor.putBoolean(getString(R.string.sort_order),SORT_NEW_TO_OLD);
                editor.commit();

                //CALL THE METHOD TO SORT LIST WITH NEW SORTING ORDER
                Log.e("Main_Activity","onOptionsItemSelected()-Calling sorting");
                sortListView();

                //Toast
                if (SORT_NEW_TO_OLD)
                    Toast.makeText(this,"Newest First",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Oldest First",Toast.LENGTH_SHORT).show();

                //Refresh ListView
                Log.e("Main_Activity","onOptionsItemSelected()-Refresh recycler view");
                mAdapter.notifyDataSetChanged();
                break;
        }


        return true;
    }



    //RecyclerView onItemClickListener
    //Open the pressed note in noteActivity for editing
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.e("Main_Activity","onItemClick()-A note is clicked in recyclerView");

        //If contextual app bar is running
        //then clicking on the listitem must not open the note for editing
        //instead it must check/uncheck the checkbox
        if(deleteActionFlag){

            Log.e("Main_Activity","onItemClick()-Check if deleteActionFlag is true, if yes it means contextual menu is running. Toggle checkbox state");

            CheckBox chk = view.findViewById(R.id.mCheckbox);
            if (mAdapter.mCheckBoxState.get(position)){
                Log.e("Main_Activity","onItemClick()- checkBox set to false at Tag  "+chk.getTag());
                chk.setChecked(false);
               // mAdapter.mCheckBoxState.put(position,false);
            }
            else {
                Log.e("Main_Activity","onItemClick()- checkBox set to true at Tag  "+chk.getTag());
                chk.setChecked(true);
               // mAdapter.mCheckBoxState.put(position,true);
            }

        }
        else{
            /*Intent intent = new Intent(MainActivity.this,NoteActivity.class);
            intent.putExtra("FileName",mList.get(position).getFileName());
            startActivity(intent);*/


            Log.e("Main_Activity","onItemClick()-A note is clicked for editing in recyclerView");
            mRecyclerView.setVisibility(View.GONE);
            fab.hide();
            Log.e("Main_Activity","onItemClick()-open note fragment for editing");
            noteFragment = new NoteFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, noteFragment, null);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }

    }


    //Contextual App Bar
    class MyActionBarCallBack implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            Log.e("Main_Activity","onCreateActionMode()-creating contextual app bar");

            mode.getMenuInflater().inflate(R.menu.contextual_menu,menu);
            mode.setTitle(userSelection.size()+" items selected");
            actionMode = mode;

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            Log.e("Main_Activity","onPrepareActionMode()");
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {


            switch (item.getItemId()){
                case R.id.action_del:
                    Log.e("Main_Activity","onActionItemClicked()-Delete button on contextual app bar is clicked");
                    removeNote(userSelection);
                    actionMode.finish();
                    MainActivity.deleteActionFlag=false;
                    Log.e("Main_Activity","onActionItemClicked()-Refresh recyclerView");
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this,"Successfully Deleted",Toast.LENGTH_SHORT).show();
            break;
        }



            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            Log.e("Main_Activity","onDestroyActionMode()-Closing contextual menu");

            userSelection.clear();
            mAdapter.mCheckBoxState.clear();
            MainActivity.deleteActionFlag=false;
            Log.e("Main_Activity","onDestroyActionMode()-Refresh recyclerView");
            mAdapter.notifyDataSetChanged();
            fab.show();
        }

        private void removeNote(List<Note> userSelection){

            Log.e("Main_Activity","removeNote()");

            for(Note note:userSelection){

                mList.remove(note);
                FileHelper.deleteNote(MainActivity.this,note.getFileName());
            }
            //notifyDataSetChanged();
        }
    }


    //Sorting method
    public void sortListView() {

        Log.e("Main_Activity","sortListView()");

     //   Log.d("Sorting",""+SORT_NEW_TO_OLD);
        Note temp;


        Log.e("Main_Activity","sortListView()-Reading sorting order from sharedPreferences if present otherwise new to old by default");
        SORT_NEW_TO_OLD = mPreference.getBoolean(getString(R.string.sort_order),true);


        if (SORT_NEW_TO_OLD) {

            Log.e("Main_Activity","sorting - new to old");
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
            Log.e("Main_Activity","sorting - old to new");

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
