package com.example.notesapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.notesapp.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notesapp.NoteKeeperDatabaseContract.NoteInfoEntry;

public class NoteKeeperDbOpenHelper extends SQLiteOpenHelper {

//    // constant containing the file name of our db
//    public static final String DATABASE_NAME = "Notekeeper.db";
//    // integer constant - the first version of our database
//    public static final int DATABASE_VERSION = 1;
//
//    public NoteKeeperDbOpenHelper(@Nullable Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//    // this method is called if database doesn't exist
//        // in this method, we execute SQL statements to create tables
//        // creating the CourseInfo table
//        db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_TABLE);
//        // creating the NoteInfo table
//        db.execSQL(NoteKeeperDatabaseContract.NoteEntry.SQL_CREATE_TABLE);
//        // we wil now add sample data into the database
//        DatabaseDataWorker worker = new DatabaseDataWorker(db);
//        // sample course
//        worker.insertCourses();
//        // sample notes
//        worker.insertSampleNotes();
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//    // called when a newer version of the database is expected
//        // here we execute SQL statements to upgrade tables.
//        // we need to preserve existing data if necessary
//
//    }

    public static final String DATABASE_NAME = "NoteKeeper.db";
//    public static final int DATABASE_VERSION = 1;
    // change to version 2 now that we have added indexes to our table.
    public static final int DATABASE_VERSION = 2;
    public NoteKeeperDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CourseInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(NoteInfoEntry.SQL_CREATE_TABLE);

        // creating the actual indexes.
        db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);

        DatabaseDataWorker worker = new DatabaseDataWorker(db);
        worker.insertCourses();
        worker.insertSampleNotes();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
            db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);
        }
    }
}
