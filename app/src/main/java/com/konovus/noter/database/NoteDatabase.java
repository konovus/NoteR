package com.konovus.noter.database;

import android.content.Context;

import com.konovus.noter.dao.NoteDao;
import com.konovus.noter.entity.Note;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;
    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context){
        if(instance == null)
            instance = Room.databaseBuilder(
                    context, NoteDatabase.class, "notes_db"
            ).build();

        return instance;
    }

}
