package com.konovus.noter.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;

public class Note_type_converter {

    @TypeConverter
    public static NOTE_TYPE fromString(String note_type) {
        return note_type == null ? null : NOTE_TYPE.valueOf(note_type);
    }

    @TypeConverter
    public static String toNote_type(NOTE_TYPE note_type) {
        return note_type == null ? null : note_type.name();
    }
}
