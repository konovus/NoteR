package com.konovus.noter.viewmodel;

import android.app.Application;

import com.konovus.noter.database.NoteDatabase;
import com.konovus.noter.entity.Note;
import com.konovus.noter.repository.NotesRepository;
import com.konovus.noter.util.NOTE_TYPE;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MemosViewModel extends AndroidViewModel {

    private NotesRepository repository;

    public MemosViewModel(@NonNull Application application) {
        super(application);
        repository = new NotesRepository(application);
    }

    public LiveData<List<Note>> getAllNotes(String note_type){
        return repository.getAllNotes(note_type);
    }
    public LiveData<List<Note>> searchNotes(String searchQuery, String note_type){
        return repository.searchNotes(searchQuery, note_type);
    }
    public void deleteNote(Note note){repository.delete(note);}

}
