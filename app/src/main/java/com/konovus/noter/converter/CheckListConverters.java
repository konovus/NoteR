package com.konovus.noter.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.room.TypeConverter;

public class CheckListConverters {

    @TypeConverter
    public String fromCheckList(LinkedHashMap<String, String> checkList){
        if(checkList == null)
            return null;
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, String>>() {}.getType();
        String json = gson.toJson(checkList, type);
        return json;
    }
    @TypeConverter
    public LinkedHashMap<String, String> toCheckList(String checkList) {
        if (checkList == null)
            return null;

        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, String>>() {}.getType();
        return gson.fromJson(checkList, type);
    }

}
