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

public class FragmentsViewModel extends AndroidViewModel {

    private NotesRepository repository;

    public FragmentsViewModel(@NonNull Application application) {
        super(application);
        repository = new NotesRepository(application);
    }
    public void addNote(Note note){
        repository.insert(note);
    }
    public LiveData<List<Note>> getAllNotes(NOTE_TYPE note_type){
        return repository.getAllNotes(note_type);
    }
    public LiveData<List<Note>> searchNotes(String searchQuery, NOTE_TYPE note_type){
        return repository.searchNotes(searchQuery, note_type);
    }
    public void deleteNote(Note note){repository.delete(note);}
    public void deleteAllNotes(){repository.deleteAllNotes();}
}
