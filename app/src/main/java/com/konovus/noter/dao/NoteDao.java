package com.konovus.noter.dao;

import com.konovus.noter.entity.Note;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Note note);
    @Delete
    void delete(Note note);
    @Query("SELECT * FROM notes WHERE id = :id")
    void getNoteById(int id);
    @Query("SELECT * FROM notes ORDER BY timestamp ")
    LiveData<List<Note>> getAllNotes();
    @Query("SELECT * FROM notes WHERE title OR text OR tag LIKE '%' || :searchQuery || '%' ORDER BY timestamp ")
    LiveData<List<Note>> searchNotes(String searchQuery);
}
