package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    //    private ArrayAdapter<NoteInfo> mAdapterNotes;
    private NoteRecyclerAdapter mNoteRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));

            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mAdapterNotes.notifyDataSetChanged();
        mNoteRecyclerAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
        // for ListView
//        final ListView listNotes = findViewById(R.id.list_notes);
//
//        List<NoteInfo> notes = DataManager.getInstance().getNotes();
//        mAdapterNotes = new ArrayAdapter<>(this,
//                android.R.layout.simple_list_item_1, notes);
//
//        listNotes.setAdapter(mAdapterNotes);
//
//        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
////                NoteInfo note = (NoteInfo) listNotes.getItemAtPosition(position);
//                intent.putExtra(NoteActivity.NOTE_POSITION, position);
//                startActivity(intent);
//            }
//        });

        /** now we want to use RecyclerView which provide more customization options
         *it uses a different kind of adapter and uses Layout Manager to control how Views ara arranged within the RecyclerView
         * Adapter holds the data and is responsible for creating new view instances and populating each view with its data
         * steps
         * a.) Get a reference to the RecyclerView that was loaded with our layout resource.
         * b.) create an instance of LayoutManager. RecyclerView needs LayoutManager to control arrangement of items within it.
         *  in this case, we use a LinearLayoutManager which display items in a linear fashion just like ListView but that is more flexible and customizable
         *  other implementations of LayoutManagers include  GridLayoutManager and StaggeredGridLayoutManager
         * c.) associate the LinearLayoutManager with the RecyclerView
         * d.) Design an Item View - need a separate layout resource for this ( want to create a Card appearance of the Item View Layout).
         * - there is no implicit relationship between this View and and that for list_notes. We can however give our recyclerview a hint of what each item will
         * look like.
         **/
        final RecyclerView recyclerNotes = findViewById(R.id.list_notes);
        // layout manager
        final LinearLayoutManager noteLayoutManager = new LinearLayoutManager(this);
        // associate LinearLayoutManager with the RecyclerView
        recyclerNotes.setLayoutManager(noteLayoutManager);
        // get list of notes we want to display inside our Recyclerview
        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        // create an instance of NoteRecycleAdapter class
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);
        // associate this adapter with our recycler view
        recyclerNotes.setAdapter(mNoteRecyclerAdapter);

    }

}
