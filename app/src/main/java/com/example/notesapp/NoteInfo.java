package com.example.notesapp;

import android.os.Parcel;
import android.os.Parcelable;

public final class NoteInfo implements Parcelable{
    private CourseInfo mCourse;
    private String mTitle;
    private String mText;
    private int mId;

    public NoteInfo(int id, CourseInfo course, String title, String text) {
        setId(id);
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    public NoteInfo(CourseInfo course, String title, String text) {
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    protected NoteInfo(Parcel parcel) {
        /**
         * reading a parcelabel is different from reading primitive types
         * we need to pass a class loader information for that type.
         * class loader provide information on how to create instances of that type.
         */
        mCourse = parcel.readParcelable(CourseInfo.class.getClassLoader());
        mTitle = parcel.readString();
        mText = parcel.readString();
    }


    public CourseInfo getCourse() {
        return mCourse;
    }

    public void setCourse(CourseInfo course) {
        mCourse = course;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getCourseId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo that = (NoteInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    public int describeContents() {
        // this method is used to configure any special behaviour(s) that our parcelling might need
        // it's not used in most cases thus returns 0
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        /**
         * this method is responsible for writing the member formations for the type instance (NoteInfo)
         * into the parcel. Thereby we go through each and every member of NoteInfo and write each into the parcel.
         * writing string instance variables are straight forward we simple use parcel.writeString(field)
         * however for instance variables that are of Reference Types, we have to ensure that they also
         * implement the Parcelabel interface and we write them into the parcel using parcel.writeParcel
         * writeParcelable accepts two arguments i.e. the variable of the reference type and the flag indicating any special behaviour
         *  we pass 0 if there is not special parcelling needs
         */
        parcel.writeParcelable(mCourse, 0);
        // write mTitle and mText into the parcel is straightforward since they are just Strings
        parcel.writeString(mTitle);
        parcel.writeString(mText);
    }

    // we need to add CREATOR to make our class recreatable from a parcel
    // the information returned from the CREATOR is going to be used to create instances of NoteInfo from the parcel
    // as a value we need to create an implementation of our class that provides the Parsable.Creator behavior
    // generally, this is done using anonymous classes
    public static final Parcelable.Creator<NoteInfo> CREATOR = new Parcelable.Creator<NoteInfo>() {
        @Override
        public NoteInfo createFromParcel(Parcel parcel) {
            /**
             * This method allow us to create a new instance of our type i.e. NoteInfo and set values inside it using the parcel
             * Parcel values must be accessed in the same order that they were written.
             * This is because values stored inside it have no identifiers.
             * Common technique used when implementing createFromParcel is using a private constructor to set the values rather than
             * setting them directly
             */
            return new NoteInfo(parcel);
        }

        @Override
        public NoteInfo[] newArray(int size) {
            // responsible of creating an array of our type of appropriate size
            // receives an integer parameter which indicates the desired size of the array
            return new NoteInfo[size];
        }
    };

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
