package com.konovus.noter.database;

import android.content.Context;

import com.konovus.noter.dao.NoteDao;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.CheckListConverters;
import com.konovus.noter.util.DateConverter;
import com.konovus.noter.util.Note_type_converter;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = Note.class, version = 5, exportSchema = false)
@TypeConverters({Note_type_converter.class, DateConverter.class, CheckListConverters.class})
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;
    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context){
//        Migration is used when a new field for an entity is added, in this case , field 'reminder'
//        was added for entity Note
        Migration MIGRATION_1_2 = new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("ALTER TABLE notes ADD COLUMN reminder TEXT ");

            }
        };
        Migration MIGRATION_3_4 = new Migration(3, 4) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("ALTER TABLE notes ADD COLUMN removal_date INTEGER ");

            }
        };
        Migration MIGRATION_4_5 = new Migration(4, 5) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("ALTER TABLE notes ADD COLUMN checkList TEXT ");

            }
        };
        if(instance == null)
            instance = Room.databaseBuilder(
                    context, NoteDatabase.class, "notes_db"
            ).addMigrations(MIGRATION_1_2, MIGRATION_3_4, MIGRATION_4_5)
            .fallbackToDestructiveMigration()
            .build();

        return instance;
    }

}
