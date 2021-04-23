package com.konovus.noter.dao;

import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Note note);
    @Delete
    void delete(Note note);
    @Query("SELECT * FROM notes WHERE id = :id")
    Note getNoteById(int id);
    @Query("SELECT * FROM notes WHERE note_type = :note_type ORDER BY date DESC")
    LiveData<List<Note>> getAllNotes(NOTE_TYPE note_type);
    @Query("DELETE FROM notes")
    void deleteAllNotes();
    @Query("SELECT * FROM notes WHERE note_type = :note_type AND (title LIKE '%' || :searchQuery || '%'" +
            " OR text LIKE '%' || :searchQuery || '%' OR tag LIKE '%' || :searchQuery || '%') ORDER BY date ")
    LiveData<List<Note>> searchNotes(String searchQuery, NOTE_TYPE note_type);
}
