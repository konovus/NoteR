package com.konovus.noter.entity;

import com.konovus.noter.util.NOTE_TYPE;
import com.konovus.noter.util.Note_type_converter;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String text;
    private String tag;
    private String date;
    private String image_path;
    private String color;
    @TypeConverters(Note_type_converter.class)
    private NOTE_TYPE note_type;


    public Note() {}

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NOTE_TYPE getNote_type() {
        return note_type;
    }

    public void setNote_type(NOTE_TYPE note_type) {
        this.note_type = note_type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
