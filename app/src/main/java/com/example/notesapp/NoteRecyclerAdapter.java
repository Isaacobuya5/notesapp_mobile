package com.example.notesapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private View mItemView;

    // first step to binding data into views, we need the list of notes
    // next, with this we can now indicate the size of data we have inside the getItemsCount
    // then we can bind data to views using onBindViewHolder()
    private final List<NoteInfo> mNotes;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> notes) {
        mContext = context;
        // in order to create a view from a layout resource we need to use a class LayoutInflator
        // create a layout inflator using the current context
        mLayoutInflater = LayoutInflater.from(context);
        mNotes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /**
         * responsible for creating ViewHolder instances
         * also creates the views themselves
         *  we then use the layoutInflator to then inflate the layout resources into actual View hierachies
         *  first argument -> the actual layout resource we want to inflate
         *  false - we don't want this newly created view to automatically attach itself to the parent
         *  itemView therefore points to the root of the view that is created when the layout resource is inflated i.e. top-level view that contains our card view
         *  we then return an instance of our newly created ViewHolder
         *  this is what recyclerview will use to create a pool of Views
         */
        mItemView = mLayoutInflater.inflate(R.layout.item_note_list, parent, false);
        return new ViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /**
         * takes the ViewHolder that is created together with its corresponding position
         * we can now use position to get that particular note
         */
        // getting note at that corresponding position
        NoteInfo note = mNotes.get(position);
        // bind data to the contained views(TextViews) in the ViewHolder
        holder.mTextCourse.setText(note.getCourse().getTitle());
        holder.mTextTitle.setText(note.getTitle());
        holder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        // return total number of items we want to display
        return mNotes.size();
    }

    /**
     * The views in the list are represented by "view holder objects"
     * These objects are instances of a class you define by extending RecycleView.ViewHolder
     * Each view holder is responsible for displaying a single item within a view
     * The RecyclerView creates only as many view holders as are needed to display the on-screen portion of the dynamic content plus few extra ones.
     * As the user crolls through the list, the RecyclerView takes off the screen views and rebinds them to the data which is scrolling onto the screen.
     * The view holder objects are managed by an adapter. Adapter creates view holders as needed.
     * Adpater also binds the view holders to their data.
     * It does this by assigning the viewholder to a position  and calling the adapter's "onBindViewHolder()".
     * That method uses the view holders  position to determine what contents should be based on its list position.
     */

    public class ViewHolder extends RecyclerView.ViewHolder {
        // we need a reference to the contained views
        // we need them as public fields so that we can access them directly from outside class
        public final TextView mTextCourse;
        public final TextView mTextTitle;
        // we need the position of this particular view
        public int mCurrentPosition;


        public ViewHolder(@NonNull View itemView) {
            // receives a view
            super(itemView);
            mTextCourse = (TextView) mItemView.findViewById(R.id.text_course);
            mTextTitle = (TextView) mItemView.findViewById((R.id.text_title));

            // associate each view with a click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // show note activity for that current position
                    Intent intent = new Intent(mContext, NoteActivity.class);
                    // extra info
                    intent.putExtra(NoteActivity.NOTE_POSITION, mCurrentPosition);
                    mContext.startActivity(intent);
                }
            });
        }

    }
}
