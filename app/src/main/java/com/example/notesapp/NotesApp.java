package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.notesapp.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notesapp.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class NotesApp extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_NOTES = 0;
    private Toolbar mMToolBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private RecyclerView mRecyclerItems;
    private LinearLayoutManager mNotesLayoutManager;
    private GridLayoutManager mCoursesLayoutManager;
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;

    // we need to hold a reference to the SQLiteOpenHelper class so that we can manage it throughout Activity life cycle
    private NoteKeeperDbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // setting our own Toolbar
        mMToolBar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mMToolBar);

        // creating an instance of SQLiteOpenHelper class
        mDbOpenHelper = new NoteKeeperDbOpenHelper(this);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer);
        // getting the navigation view
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        // add an ActionBarDrawerToggle - displays the hamburger icon in the toolbar
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mMToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // add the toggle as a listener to our drawer layout so that it can animate the toggle upon opening and closing the drawer
        mDrawer.addDrawerListener(mDrawerToggle);
        // sync the toggle state so that it can know when to animate a hamburger and back icon
        mDrawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_notes:
                        displayNotes();
                        break;
                    case R.id.nav_courses:
                        displayCourses();
                        break;
                }

                return true;
            }
        });

        initializeDisplayContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_cancel) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeDisplayContent() {

        DataManager.loadFromDatabase(mDbOpenHelper);
        mRecyclerItems = (RecyclerView) findViewById(R.id.list_items);
        mNotesLayoutManager = new LinearLayoutManager(this);
        mCoursesLayoutManager = new GridLayoutManager(this, 2);

//        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, null);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mCourseRecyclerAdapter = new CourseRecyclerAdapter(this, courses);
        displayNotes();

    }

    private void displayNotes() {
        mRecyclerItems.setLayoutManager(mNotesLayoutManager);
        mRecyclerItems.setAdapter(mNoteRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_notes);
    }

    private void selectNavigationMenuItem(int id) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    private void displayCourses() {
        mRecyclerItems.setLayoutManager(mCoursesLayoutManager);
        mRecyclerItems.setAdapter(mCourseRecyclerAdapter);
        selectNavigationMenuItem(R.id.nav_courses);
    }

    @Override
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        // we need to close SQLiteOpenHelper class here to avoid memory leaks
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // get latest data from the database
//        loadNotes();
        LoaderManager.getInstance(this).restartLoader(LOADER_NOTES, null, this);

        super.onResume();
    }

    private void loadNotes() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        // query database for notes
        final String[] noteColumns = {
                NoteInfoEntry._ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT,
                NoteInfoEntry.COLUMN_COURSE_ID
        };
        // ordering notes by multiple columns
        String notesOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + "," + NoteInfoEntry.COLUMN_NOTE_TITLE;
        final Cursor notesCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns, null, null, null, null, notesOrderBy);
        // associate the cursor with the RecyclerAdapter
        mNoteRecyclerAdapter.changeCursor(notesCursor);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_NOTES) {
            loader = new CursorLoader(this) {
                @Override
                public Cursor loadInBackground() {
                    SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                    // query database for notes
                    final String[] noteColumns = {
                            NoteInfoEntry.getName(NoteInfoEntry._ID),
                            NoteInfoEntry.COLUMN_NOTE_TITLE,
                            CourseInfoEntry.COLUMN_COURSE_TITLE
                    };

                    String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " +
                            CourseInfoEntry.TABLE_NAME + " ON " +
                            NoteInfoEntry.getName(NoteInfoEntry.COLUMN_COURSE_ID) +
                            " = " + CourseInfoEntry.getName(CourseInfoEntry.COLUMN_COURSE_ID);

                    // ordering notes by multiple columns
                    String notesOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE + "," + NoteInfoEntry.COLUMN_NOTE_TITLE;
                    return db.query(tablesWithJoin, noteColumns, null, null, null, null, notesOrderBy);
                };
            };
        };
        return loader;
    };

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_NOTES)
            mNoteRecyclerAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_NOTES) {
            mNoteRecyclerAdapter.changeCursor(null);
        }
    }
}