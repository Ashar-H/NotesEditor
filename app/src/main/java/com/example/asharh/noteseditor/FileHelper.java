package com.example.asharh.noteseditor;


import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

//THIS CLASS HANDLES SAVING, UPDATING AND RETRIEVING NOTES IN APP PRIVATE DIRECTORY
public class FileHelper {

    public static final String FILE_EXTENSTION = ".bin";


    //Creates a unique filename using inner class MyRandomStringGenerator
    private static String createFileName(Context context){

        Log.e("FileHelper Class","createFileName()");

        String fileName;

        //Get the list of all filenames stored in the app directory

        File fileDirectory = context.getFilesDir();
        ArrayList<String> fileNames = new ArrayList<>();

        Log.e("FileHelper Class","createFileName()-Getting all the filenames currently stored in the app directory");

        //Get all files ending with .bin in arrayList
        for (String name : fileDirectory.list()){
            if (name.startsWith("Note") && name.endsWith(FILE_EXTENSTION)){
                fileNames.add(name);
            }
        }

        Log.e("FileHelper Class","createFileName()-Generate a random file name");
        //Create a random filename
        //Check if it already exists in filename arrayList created above
        do
        {
            fileName = "Note_"+MyRandomStringGenerator.generateRandomString()+FILE_EXTENSTION;
        }
        while (fileNames.contains(fileName));


        Log.e("FileHelper Class","createFileName()-Unique filename generated: "+fileName);
        return fileName;

    }


    //Delete All Notes
    public static void deleteAll(Context context){

        Log.e("FileHelper Class","deleteAll()");

        ArrayList<Note> allNotes = getAllSavedNotes(context);
        for (Note note:allNotes)
            deleteNote(context,note.getFileName());
    }


    //This method saves the note as .bin file
    public static boolean saveNote(Context context, Note note){

        Log.e("FileHelper Class","saveNote()");

        FileOutputStream fos;
        ObjectOutputStream oos;

        try{

            Log.e("FileHelper Class","saveNote()-setFileName in note object by creating a unique filename");
            note.setFileName(createFileName(context));

            Log.e("FileHelper Class","saveNote()-opened openFileOutput stream");
            fos = context.openFileOutput(note.getFileName(),Context.MODE_PRIVATE);

            Log.e("FileHelper Class","saveNote()-creating ObjectOutputStream");
            oos = new ObjectOutputStream(fos);

            Log.e("FileHelper Class","saveNote()-writing note object to .bin file");
            oos.writeObject(note);
            oos.close();
            fos.close();

            Log.e("FileHelper Class","saveNote()-output stream closed");

            return true;
        }
        catch (IOException e){

            Log.e("FileHelper Class","saveNote()-Exception occurred");

            e.printStackTrace(); // Tell user something went wrong

            return false;
        }


    }


    //Update Note
    public static boolean updateNote(Context context, String fileName, String newTitle, String newContent, long newDateTime){

        Log.e("FileHelper Class","updateNote()");

        Note note = getNoteByFileName(context,fileName);

        if(note != null)
        {

            Log.e("FileHelper Class","updateNote()-File found.");
            note.setContent(newContent);
            note.setTitle(newTitle);
            note.setDateTime(newDateTime);

            Log.e("FileHelper Class","updateNote()-saving the file with a new name");
            saveNote(context,note);
            Log.e("FileHelper Class","updateNote()-deleting the file with the old name");
            deleteNote(context,fileName);

            return true;
        }

        Log.e("FileHelper Class","updateNote()-File not found.");
        return false; //Something went wrong

    }

    public static boolean deleteNote(Context context, String fileName){

        Log.e("FileHelper Class","deleteNote()");

        File file = new File (context.getFilesDir(),fileName);

        Log.d("FILENAME",fileName);
        if(file.exists()){
            Log.e("FileHelper Class","deleteNote()-File found.");
            file.delete();
            return true;
        }
        Log.e("FileHelper Class","deleteNote()-File not found.");

        return false;
    }

    //This method retrieve all notes from the app's directory
    public static ArrayList<Note> getAllSavedNotes(Context context){

        Log.e("FileHelper Class","getAllSavedNotes()");

        ArrayList<Note> savedNotes = new ArrayList<>();
        ArrayList<String> fileNames = new ArrayList<>();

        File fileDirectory = context.getFilesDir();

        //Get all files ending with .bin in arrayList
        for (String fileName : fileDirectory.list()){
            if (fileName.endsWith(FILE_EXTENSTION)){
                fileNames.add(fileName);

            }
        }

        FileInputStream fis;
        ObjectInputStream ois;

        //Read Note objects from all .bin files
        for (int i=0; i<fileNames.size(); i++){

            Log.e("FileHelper Class","getAllSavedNotes()-Reading all note objects from .bin files");

            try {
                fis = context.openFileInput(fileNames.get(i));
                ois = new ObjectInputStream(fis);
                savedNotes.add((Note) ois.readObject());

                fis.close();
                ois.close();
            }
            catch (IOException | ClassNotFoundException e){

                Log.e("FileHelper Class","getAllSavedNotes()-Exception occurred-Check the exception stack trace");
                e.printStackTrace();
            }

        }

        return savedNotes;


    }

    // This method only returns one note
    public static Note getNoteByFileName(Context context, String fileName){

        Log.e("FileHelper Class","getNoteByFileName() Filename: "+fileName);

        Note note;
        File fileDirectory = context.getFilesDir();
        File file = new File(fileDirectory,fileName);

        if(file.exists()){
            Log.e("FileHelper Class","getNoteByFileName()-File Found");
            FileInputStream fis;
            ObjectInputStream ois;


            try {
                fis = context.openFileInput(fileName);
                ois = new ObjectInputStream(fis);
                note = (Note) ois.readObject();

                fis.close();
                ois.close();
            }
            catch (IOException | ClassNotFoundException e){
                Log.e("FileHelper Class","getNoteByFileName()-Exception occurred");
                e.printStackTrace();
                return null;
            }

            return note;
        }

        Log.e("FileHelper Class","getNoteByFileName()-File Not Found");
        return null;
    }


    //Random String Generator
    //For creating random filename in createFileName() method
    private static class MyRandomStringGenerator {

        private static final String CHAR_LIST =
             "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        private static final int RANDOM_STRING_LENGTH = 15;

        private static String generateRandomString(){

            Log.e("MyRandomStringGenerator","generateRandomString()");

            String rand = "";
            for(int i=0; i<RANDOM_STRING_LENGTH; i++){
                int number = getRandomNumber();
                char ch = CHAR_LIST.charAt(number);
                rand=rand+ch;
            }


            return rand;
        }

        private static int getRandomNumber() {

            Log.e("MyRandomStringGenerator","getRandomNumber()");

            int randomInt;
            Random randomGenerator = new Random();
            randomInt = randomGenerator.nextInt(CHAR_LIST.length());
            if (randomInt - 1 == -1) {
                return randomInt;
            } else {
                return randomInt - 1;
            }
        }

    }


}
