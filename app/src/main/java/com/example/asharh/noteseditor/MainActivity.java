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


public class MainActivity extends AppCompatActivity {


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
    private Menu menu;

    private FragmentManager fragmentManager;
    private Bundle savedInstanceState;
    private NoteFragment noteFragment;

    private String msg = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer gravida vulputate massa, nec cursus eros blandit nec. Nam ut imperdiet enim. Sed tempus molestie velit vel malesuada. Aliquam erat volutpat. Proin tellus dui, dapibus id lacus quis, feugiat volutpat elit. Etiam at mi pellentesque, sagittis ligula euismod, molestie diam. Nullam in condimentum urna. Cras non erat velit. Etiam pharetra, ante vel tempus luctus, purus turpis varius nisi, scelerisque sollicitudin felis nunc a felis. Donec ac lobortis urna, sed hendrerit felis. In sit amet lorem convallis, congue arcu non, commodo erat. Donec tincidunt ultricies tempor. Integer vulputate felis quam, in pretium diam elementum malesuada. Donec mattis sapien at massa fermentum aliquet.\n" +
            "\n" +
            "Nullam egestas, massa a euismod consequat, odio eros ultrices quam, ac egestas enim dolor ut lorem. Vivamus laoreet ac magna vel sodales. Maecenas aliquet faucibus lacus id gravida. Fusce sollicitudin posuere ipsum a venenatis. Morbi pretium elit eu erat mattis, quis interdum ante pulvinar. Donec lectus risus, feugiat sit amet consectetur rhoncus, euismod ut metus. Proin porta molestie erat, sit amet interdum arcu aliquam nec. Nam eleifend lorem ut dapibus elementum. In sodales finibus tempor. Nulla cursus efficitur erat, in ornare lorem euismod pellentesque. Suspendisse eleifend lorem ac imperdiet aliquet. Maecenas et orci at libero mattis mollis.\n" +
            "\n" +
            "Vivamus non nulla a neque fringilla consectetur quis a eros. Phasellus laoreet, nulla eu feugiat efficitur, augue dui rutrum nunc, nec scelerisque mi eros ac purus. Phasellus consequat malesuada libero non bibendum. Nulla aliquam pulvinar massa, at vulputate diam. Etiam lectus ipsum, suscipit at tortor sed, fermentum bibendum magna. Sed hendrerit eu leo ac ultrices. Donec condimentum condimentum nisl, eu bibendum lectus ornare eget. Nam tincidunt id dui a viverra. Sed hendrerit nisi et velit dictum eleifend. Morbi faucibus dui mauris, vehicula malesuada ante ultricies vitae. Sed id nulla quis lectus accumsan elementum. Pellentesque convallis egestas velit sit amet mattis. Maecenas sollicitudin erat gravida dapibus egestas. Proin tempor dolor nec urna tempor auctor. Nullam massa ante, posuere in libero laoreet, finibus egestas dolor. Vivamus rhoncus lobortis nisi vel volutpat.\n" +
            "\n" +
            "Fusce ac consequat mi. Ut varius odio nec ultricies interdum. In orci lacus, vestibulum vel felis et, mattis eleifend nunc. Sed feugiat purus id accumsan convallis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aenean laoreet, neque in rhoncus tempus, turpis orci aliquam libero, ac vulputate ligula odio in enim. In blandit consectetur varius.\n" +
            "\n" +
            "Donec consequat id magna vel euismod. Curabitur id condimentum velit. Etiam lobortis varius pretium. Sed ultricies diam eu ante sagittis, vitae aliquam risus ornare. Ut fermentum eleifend enim eget tincidunt. Quisque eget ornare sem. Nulla consectetur porttitor malesuada. Aliquam pretium vitae mi vitae luctus.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Log.d("Main_Activity","OnCreate()");

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

        //Log.d("Main_Activity","OnResume()");

        super.onResume();

        //Note Fragment hide the following,that's why make them visible again once the fragment is closed and onResume() is ran.
        mRecyclerView.setVisibility(View.VISIBLE);
        fab.show();

        showOverflowMenu(true);

        mList.clear();

        getSupportActionBar().setTitle("Notes Editor");

        //Testing
        //FileHelper.deleteAll(this);

        //Create 100 notes for testing
        /*for (int i=0;i<100;i++){
            FileHelper.saveNote(this,new Note("Note "+(i+1),msg+i,System.currentTimeMillis()));
        }*/


        //Get All Saved Notes
        for (Note note: FileHelper.getAllSavedNotes(this)){
            //Log.d("Main_Activity","OnResume()-Getting all saved notes.");
            mList.add(note);
        }

        //If there are no saved notes,
        if(mList == null || mList.size() == 0){
            //Log.d("Main_Activity","OnResume()-No saved notes.");
            Toast.makeText(this,"No Saved Notes !",Toast.LENGTH_SHORT).show();

        }
        //notify the adapter to display all the saved notes
        else{
            //sort the list
            //Log.d("Main_Activity","OnResume()-Calling sortListView()");
            sortListView();
            //Log.d("Main_Activity","onOptionsItemSelected()-Refresh recycler view");
            mAdapter.notifyDataSetChanged();
        }


        //Setting Click Listener for each item in RecyclerView

        mAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mRecyclerView.getChildAdapterPosition(view);


                //Log.d("Main_Activity","onResume()-RecyclerView Click Listener-Note at position "+position+" is clicked.");

                //If contextual app bar is running
                //then clicking on the listItem must not open the note for editing
                //instead it must check/uncheck the checkbox
                if(deleteActionFlag){

                    //Log.d("Main_Activity","onResume()-RecyclerView Click Listener-Check if deleteActionFlag is true, if yes it means contextual menu is running. Toggle checkbox state");

                    CheckBox chk = view.findViewById(R.id.mCheckbox);
                    if (mAdapter.mCheckBoxState.get(position)){
                        //Log.d("Main_Activity","onResume()-RecyclerView Click Listener-checkBox set to false at Tag  "+chk.getTag());
                        chk.setChecked(false);
                        // mAdapter.mCheckBoxState.put(position,false);
                    }
                    else {
                        //Log.d("Main_Activity","onResume()-RecyclerView Click Listener-checkBox set to true at Tag  "+chk.getTag());
                        chk.setChecked(true);
                        // mAdapter.mCheckBoxState.put(position,true);
                    }

                }
                else{

                    //Log.d("Main_Activity","onResume()-RecyclerView Click Listener-Opening fragment for editing the note");

                    //To avoid overlapping of fragment on recyclerView, hide the recyclerView and fab
                    mRecyclerView.setVisibility(View.GONE);
                    fab.hide();

                    //Start fragment for editing by passing the fileName of the selected note
                    startNoteFragment(mList.get(position).getFileName());

                }

            }
        });

    }


    //Start fragment method
    //If fileName is present then open fragment for editing otherwise open fragment with blank editTexts.
    public void startNoteFragment(String fileName){

        //Disable menuItems
        showOverflowMenu(false);

        noteFragment = new NoteFragment();
        fragmentManager = getSupportFragmentManager();

        if (fileName != null && !fileName.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putString("FileName",fileName);
            noteFragment.setArguments(bundle);
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container,noteFragment,null);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

    }


    //fab onClick method
    public void newNoteButtonPressed(View view){

        //Log.d("Main_Activity","Floating Action Button Clicked - Running newNoteButtonPressed()");

       /* Intent intent = new Intent(MainActivity.this,NoteActivity.class);
        startActivity(intent);*/

       //Open Note Fragment with null value passed as fileName
        mRecyclerView.setVisibility(View.GONE);
        fab.hide();

        startNoteFragment(null);
    }


    @Override
    public void onBackPressed() {

        //Log.d("Main_Activity","onBackPressed()");

        //Check if fragment is running, if yes then close the fragment and go back to mainActivity
        //Otherwise, exit the application
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>0;

        if (canGoBack){

            //Log.d("Main_Activity","onBackPressed()-canGoBack is true-Close the running fragment and save the note");

            //close keyboard if open


            noteFragment.saveNoteFromActivity();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            onResume();
        }

        else
        {
            //Log.d("Main_Activity","onBackPressed()-canGoBack is false-Exit the application");
            super.onBackPressed();
        }

    }

    //Options Menu on actionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;

        //Log.d("Main_Activity","onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }


    public void showOverflowMenu(boolean showMenu){
        if(menu == null)
            return;
        menu.setGroupVisible(R.id.main_menu_group, showMenu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Log.d("Main_Activity","onOptionsItemSelected()");

        switch (item.getItemId()){

            case android.R.id.home:

                //Log.d("Main_Activity","onOptionsItemSelected()-Home Button Pressed on Actionbar");

                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {

                    //Log.d("Main_Activity","onOptionsItemSelected()-Note fragment is currently opened.Save Note and go to main");
                    getSupportActionBar().setTitle("Notes Editor");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    noteFragment.saveNoteFromActivity();
                    fm.popBackStack();
                    onResume();
                }
                break;

            case R.id.action_delete:
                //Log.d("Main_Activity","onOptionsItemSelected()-Delete Button Pressed on Actionbar");
                if (mList.size()>0){
                    //Log.d("Main_Activity","onOptionsItemSelected()-List size is greater than 0, turn on contextual menu");
                    deleteActionFlag = true;
                    //Log.d("Main_Activity","onOptionsItemSelected()-Show checkboxes");
                    mAdapter.notifyDataSetChanged();
                    fab.hide();
                    MyActionBarCallBack callBack = new MyActionBarCallBack();
                    actionMode = startActionMode(callBack);
                }
                else{
                    //Log.d("Main_Activity","onOptionsItemSelected()-No notes to delete");
                    Toast.makeText(this,"Nothing to delete",Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.action_sort:

                //Log.d("Main_Activity","onOptionsItemSelected()-Sort Button Pressed on Actionbar");
                //Change the sorting order
                SORT_NEW_TO_OLD = !SORT_NEW_TO_OLD;

                //SAVE THE NEW SORTING ORDER IN SHARED PREFERENCES
                //Log.d("Main_Activity","onOptionsItemSelected()-Save sorting order in SharedPreferences");
                SharedPreferences.Editor editor = mPreference.edit();
                editor.putBoolean(getString(R.string.sort_order),SORT_NEW_TO_OLD);
                editor.commit();

                //CALL THE METHOD TO SORT LIST WITH NEW SORTING ORDER
                //Log.d("Main_Activity","onOptionsItemSelected()-Calling sorting");
                sortListView();

                //Toast
                if (SORT_NEW_TO_OLD)
                    Toast.makeText(this,"Newest First",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Oldest First",Toast.LENGTH_SHORT).show();

                //Refresh ListView
                //Log.d("Main_Activity","onOptionsItemSelected()-Refresh recycler view");
                mAdapter.notifyDataSetChanged();
                break;
        }


        return true;
    }


    //Contextual App Bar
    class MyActionBarCallBack implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            //Log.d("Main_Activity","onCreateActionMode()-creating contextual app bar");

            mode.getMenuInflater().inflate(R.menu.contextual_menu,menu);
            mode.setTitle(userSelection.size()+" items selected");
            actionMode = mode;

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            //Log.d("Main_Activity","onPrepareActionMode()");
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {


            switch (item.getItemId()){
                case R.id.action_del:
                    //Log.d("Main_Activity","onActionItemClicked()-Delete button on contextual app bar is clicked");
                    removeNote(userSelection);
                    actionMode.finish();
                    MainActivity.deleteActionFlag=false;
                    //Log.d("Main_Activity","onActionItemClicked()-Refresh recyclerView");
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this,"Successfully Deleted",Toast.LENGTH_SHORT).show();
            break;
        }



            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            //Log.d("Main_Activity","onDestroyActionMode()-Closing contextual menu");

            userSelection.clear();
            mAdapter.mCheckBoxState.clear();
            MainActivity.deleteActionFlag=false;
            //Log.d("Main_Activity","onDestroyActionMode()-Refresh recyclerView");
            mAdapter.notifyDataSetChanged();
            fab.show();
        }

        public void removeNote(List<Note> userSelection){

            //Log.d("Main_Activity","removeNote()");

            for(Note note:userSelection){

                mList.remove(note);
                FileHelper.deleteNote(MainActivity.this,note.getFileName());
            }

        }
    }


    //Sorting method
    public void sortListView() {

        //Log.d("Main_Activity","sortListView()");

     //   //Log.d("Sorting",""+SORT_NEW_TO_OLD);
        Note temp;


        //Log.d("Main_Activity","sortListView()-Reading sorting order from sharedPreferences if present otherwise new to old by default");
        SORT_NEW_TO_OLD = mPreference.getBoolean(getString(R.string.sort_order),true);


        if (SORT_NEW_TO_OLD) {

            //Log.d("Main_Activity","sorting - new to old");
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
            //Log.d("Main_Activity","sorting - old to new");

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
