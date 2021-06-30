package com.konovus.noter.entity;

import com.konovus.noter.util.NOTE_TYPE;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String text;
    private String tag;

    private Date date;
    private Date removal_date;
    private String image_path;
    private String color;
    private String reminder;
    private NOTE_TYPE note_type;
    private HashMap<String, String> checkList;


    public Note() {}

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", checkList=" + checkList +
                ", tag=" + tag +
                '}';
    }

        @Override
    public boolean equals(Object o) {
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;
        Note note = (Note) o;
        // field comparison
        return Objects.equals(title, note.title)
                && Objects.equals(text, note.text)
                && Objects.equals(tag, note.tag)
                && Objects.equals(date, note.date)
                && Objects.equals(removal_date, note.removal_date)
                && Objects.equals(image_path, note.image_path)
                && Objects.equals(color, note.color)
                && Objects.equals(reminder, note.reminder)
                && Objects.equals(checkList, note.checkList)
                && Objects.equals(note_type, note.note_type);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getRemoval_date() {
        return removal_date;
    }

    public void setRemoval_date(Date removal_date) {
        this.removal_date = removal_date;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public HashMap<String, String> getCheckList() {
        return checkList;
    }

    public void setCheckList(HashMap<String, String> checkList) {
        this.checkList = checkList;
    }

}
