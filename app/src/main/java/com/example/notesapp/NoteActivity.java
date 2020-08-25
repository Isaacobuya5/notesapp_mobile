package com.example.notesapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.example.notesapp.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notesapp.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String NOTE_ID = "com.jwhh.notekeeper.NOTE_POSITION";
    public static final int ID_NOT_SET = -1;
    private static final int LOADER_NOTES = 0;
    private NoteInfo mNote = new NoteInfo(DataManager.getInstance().getCourses().get(0),"", "");
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;

    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;

    private int mNotePosition;
    private boolean mIsCancelling;
    private NoteActivityViewModel mViewModel;
    private NoteKeeperDbOpenHelper mMDbOpenHelper;
    private Cursor mNoteCursor;
    private int mCourseInfoPos;
    private int mTitlePos;
    private int mNoteTextPos;
    private int mNoteId;
    private SimpleCursorAdapter mAdapterCourses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // instance of the DbHelper class here
        mMDbOpenHelper = new NoteKeeperDbOpenHelper(this);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
        mViewModel = viewModelProvider.get(NoteActivityViewModel.class);

        if(mViewModel.mIsNewlyCreated && savedInstanceState != null)
            mViewModel.restoreState(savedInstanceState);

        mViewModel.mIsNewlyCreated = false;

        mSpinnerCourses = findViewById(R.id.spinner_courses);

        // we nolonger need to get the courses from DataManager as we will fetch them directly from the Database
//        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        // We will use CursorAdapter instead
//        ArrayAdapter<CourseInfo> adapterCourses =
//                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);

        mAdapterCourses = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null, new String[] {
                CourseInfoEntry.COLUMN_COURSE_TITLE}, new int[] { android.R.id.text1}, 0);
        mAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(mAdapterCourses);

        // we now need to fetch courses from the database
        loadCourseData();

        readDisplayStateValues();
        saveOriginalNoteValues();

        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if(!mIsNewNote)
            // fetch the note from database here
//            loadNoteData();
            // requesting LoaderManager to load the data from the database
            LoaderManager.getInstance(this).initLoader(LOADER_NOTES, null, this);
//            displayNote();
    }

    private void loadCourseData() {
        SQLiteDatabase db = mMDbOpenHelper.getReadableDatabase();
        String[] courseColumns = {
                CourseInfoEntry.COLUMN_COURSE_TITLE,
                CourseInfoEntry.COLUMN_COURSE_ID,
                CourseInfoEntry._ID
        };

        Cursor cursor = db.query(CourseInfoEntry.TABLE_NAME, courseColumns, null, null, null, null,CourseInfoEntry.COLUMN_COURSE_TITLE);
        // associating the cursor with the adapter
        mAdapterCourses.changeCursor(cursor);
    }

//    private void loadNoteData() {
//        // get connection to the Database - so we need an instance of dbHelper class
//        SQLiteDatabase db = mMDbOpenHelper.getReadableDatabase();
//        // specify the query criteria
////        String courseId = "android_intents";
////        String titleStart = "dynamic";
//
////        String selection = NoteInfoEntry.COLUMN_COURSE_ID + "= ?" +
////                " AND " + NoteInfoEntry.COLUMN_NOTE_TITLE + " LIKE ?";
//
////        String[] selectionArgs = { courseId, titleStart+ "%"};
//
//        // we are fetching the appropriate note by id instead
//        String selection = NoteInfoEntry._ID + " = ?";
//
//        String[] selectionArgs = {Integer.toString(mNoteId)};
//
//        // we now need to query the NoteInfo table
//        String[] noteColumns = {
//                NoteInfoEntry.COLUMN_COURSE_ID,
//                NoteInfoEntry.COLUMN_NOTE_TITLE,
//                NoteInfoEntry.COLUMN_NOTE_TEXT
//        };
//
//        // querying the database
//        mNoteCursor = db.query(NoteInfoEntry.TABLE_NAME,noteColumns,selection,selectionArgs,null,null,null);
//
//        // we now want to iterate the cursor for results
//        mCourseInfoPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
//        mTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
//        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
//
//        // place cursor to the first result
//        mNoteCursor.moveToNext();
//        displayNote();
//    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //This method is initially called before menu is displayed
        // get a reference to the menu item that we are interested in
        MenuItem menuItem = menu.findItem(R.id.action_next);
        // get index of the last note
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        // enable it for as long as we are not in the last note
        menuItem.setEnabled(mNotePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void saveOriginalNoteValues() {
        if(mIsNewNote)
            return;
//        mViewModel.mOriginalNoteCourseId = mNote.getCourse().getCourseId();
//        mViewModel.mOriginalNoteTitle = mNote.getTitle();
//        mViewModel.mOriginalNoteText = mNote.getText();
        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteTitle = mNote.getTitle();
        mOriginalNoteText = mNote.getText();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling) {
            if(mIsNewNote) {
                DataManager.getInstance().removeNote(mNotePosition);
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null)
            mViewModel.saveState(outState);
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mViewModel.mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mViewModel.mOriginalNoteTitle);
        mNote.setText(mViewModel.mOriginalNoteText);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
    }

    private void displayNote() {
        // actual values as retrieved from the database
        String courseId = mNoteCursor.getString(mCourseInfoPos);
        String mNoteTitle = mNoteCursor.getString(mTitlePos);
        String mNoteText = mNoteCursor.getString(mNoteTextPos);


        // WE are now fetching our list of courses from the db
//        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        // we are still going to fetch the spinner courses from the Data manager
//        CourseInfo course = DataManager.getInstance().getCourse(courseId);
//        int courseIndex = courses.indexOf(mNote.getCourse());
//        int courseIndex = courses.indexOf(course);
        
        // get the index corresponding to the course in the cursor.
        int courseIndex = getIndexOfCourse(courseId);

        mSpinnerCourses.setSelection(courseIndex);
//        mTextNoteTitle.setText(mNote.getTitle());
//        mTextNoteText.setText(mNote.getText());
        mTextNoteTitle.setText(mNoteTitle);
        mTextNoteText.setText(mNoteText);
    }

    private int getIndexOfCourse(String courseId) {
        // get the cursor associated with the adapter
        Cursor cursor = mAdapterCourses.getCursor();
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        int courseRowIndex = 0;
        boolean more = cursor.moveToFirst();

        while(more) {
            String cursorCourseId = cursor.getString(courseIdPos);
            if (courseId.equals(cursorCourseId))
                break;
            courseRowIndex++;
        }
        return courseRowIndex;
    }


//    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
//        List<CourseInfo> courses = DataManager.getInstance().getCourses();
//        int courseIndex = courses.indexOf(mNote.getCourse());
//        spinnerCourses.setSelection(courseIndex);
//        textNoteTitle.setText(mNote.getTitle());
//        textNoteText.setText(mNote.getText());
//    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
//        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);
        mIsNewNote = mNoteId == ID_NOT_SET;
        if(mIsNewNote){
            createNewNote();
        }
        // we are no longer getting the note from DataManager but rather fetching from the database
//            mNote = DataManager.getInstance().getNotes().get(position);
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_email) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();
        } else if (id == R.id.action_next) {
            // we want to display the next note
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    private void moveNext() {

        // to move to the next note -first increment the notes current position
        ++mNotePosition;
        // get note at that particular position
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);
        // save original note values
        saveOriginalNoteValues();
        // display that particular note
        displayNote();
        // call this method to ensure onPrepareMenuOptions is called again
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Checkout what I learned in the Pluralsight course \"" +
                course.getTitle() + "\"\n" + mTextNoteText.getText();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        mMDbOpenHelper.close();
        super.onDestroy();
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // CursorLoader is the best loader specialized for cursor based data
        // also works best for a special kind of component known as Content Providers.
        CursorLoader loader = null;
        if (id == LOADER_NOTES)
            loader = createLoaderNotes();
        return loader;
    }

    private CursorLoader createLoaderNotes() {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                // get connection to the Database - so we need an instance of dbHelper class
                SQLiteDatabase db = mMDbOpenHelper.getReadableDatabase();
                // we are fetching the appropriate note by id instead
                String selection = NoteInfoEntry._ID + " = ?";

                String[] selectionArgs = {Integer.toString(mNoteId)};

                // we now need to query the NoteInfo table
                String[] noteColumns = {
                        NoteInfoEntry.COLUMN_COURSE_ID,
                        NoteInfoEntry.COLUMN_NOTE_TITLE,
                        NoteInfoEntry.COLUMN_NOTE_TEXT
                };

                // querying the database
                return db.query(NoteInfoEntry.TABLE_NAME,noteColumns,selection,selectionArgs,null,null,null);
            }
        };
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // this method is called once we have results
        // handling the results
        // one activity can have multiple loaders thus the need to check
        if (loader.getId() == LOADER_NOTES)
            loadFinishedNotes(data);
    }

    private void loadFinishedNotes(Cursor data) {
        // assign this cursor to the member field cursor
        mNoteCursor = data;
        // we now want to iterate the cursor for results
        mCourseInfoPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);

        // place cursor to the first result
        mNoteCursor.moveToNext();
        displayNote();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_NOTES) {
            if (mNoteCursor != null)
                mNoteCursor.close();
        }
    }
}
