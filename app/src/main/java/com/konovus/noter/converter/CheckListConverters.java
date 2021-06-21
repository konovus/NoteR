package com.konovus.noter.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.room.TypeConverter;

public class CheckListConverters {

    @TypeConverter
    public String fromCheckList(HashMap<String, String> checkList){
        if(checkList == null)
            return null;
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        String json = gson.toJson(checkList, type);
        return json;
    }
    @TypeConverter
    public HashMap<String, String> toCheckList(String checkList) {
        if (checkList == null)
            return null;

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        return gson.fromJson(checkList, type);
    }

}
