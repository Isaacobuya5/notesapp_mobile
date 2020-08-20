package com.example.notesapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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
    public static final int DATABASE_VERSION = 1;
    public NoteKeeperDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(NoteKeeperDatabaseContract.NoteInfoEntry.SQL_CREATE_TABLE);

        DatabaseDataWorker worker = new DatabaseDataWorker(db);
        worker.insertCourses();
        worker.insertSampleNotes();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
