package com.example.notesapp;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;

public class NotesAppTest {

    @Rule
    public ActivityTestRule<NotesApp> mActivityTestRule = new ActivityTestRule<>(NotesApp.class);

    @Test
    public void NextThroughNotes() {
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));
        onView(withId(R.id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        int index = 0;
        // getting note at position 0
        NoteInfo note = notes.get(index);
        // assertions
        onView(withId(R.id.text_course)).check(matches(withSpinnerText(note.getCourse().getTitle())));
        onView(withId(R.id.text_title)).check(matches(withText(note.getTitle())));
        onView(withId(R.id.textView)).check(matches(withText(note.getText())));
    }

}