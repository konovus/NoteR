package com.konovus.noter.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.konovus.noter.entity.Note;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;

public class Note_type_converter {



//    @TypeConverter
//    public static String fromNote(NOTE_TYPE note_type) {
//        Gson gson = new Gson();
//        String json = gson.toJson(note_type);
//        return json;
//    }
//
//    @TypeConverter
//    public static NOTE_TYPE toNoteType(String value) {
//        Type type = new TypeToken<NOTE_TYPE>() {}.getType();
//        return new Gson().fromJson(value, type);
//    }
    @TypeConverter
    public static NOTE_TYPE fromString(String note_type) {
        return note_type == null ? null : NOTE_TYPE.valueOf(note_type);
    }

    @TypeConverter
    public static String toNote_type(NOTE_TYPE note_type) {
        return note_type == null ? null : note_type.name();
    }

//    @TypeConverter
//    public String fromNote_type(NOTE_TYPE note_type){
//        if(note_type == null)
//            return null;
//        return note_type.name();
//    }
//    @TypeConverter
//    public NOTE_TYPE toNote_type(String note_type) {
//        if(note_type == null)
//            return null;
//        switch (note_type.toUpperCase()){
//            case "MEMO" :
//                return NOTE_TYPE.MEMO;
//            case "JOURNAL" :
//                return NOTE_TYPE.JOURNAL;
//            default: return null;
//        }
//    }
}
