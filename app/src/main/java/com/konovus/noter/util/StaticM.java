package com.konovus.noter.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.konovus.noter.entity.Note;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StaticM {
    public static void saveNotesToPhone(List<Note> notes, String notesName, Context context){
        Gson gson = new Gson();
        String json = gson.toJson(notes);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(notesName, json).apply();
    }

    public static List<Note> loadNotesFromPhone(String notesName, Context context){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(sharedPreferences.contains(notesName)){
            String json = sharedPreferences.getString(notesName, null);
            Type type = new TypeToken<ArrayList<Note>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return null;
    }
}
