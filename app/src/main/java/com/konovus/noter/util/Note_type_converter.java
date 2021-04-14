package com.konovus.noter.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;

public class Note_type_converter {

    @TypeConverter
    public String fromNote_type(NOTE_TYPE note_type){
        return note_type.toString();
    }
    @TypeConverter
    public NOTE_TYPE toNote_type(String note_type) {
        return NOTE_TYPE.valueOf(note_type);
    }
}
