package com.konovus.noter.viewmodel;

import android.app.Application;

import com.konovus.noter.entity.Note;
import com.konovus.noter.repository.NotesRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class AddNoteViewModel extends AndroidViewModel {

    private NotesRepository repository;

    public AddNoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NotesRepository(application);
    }

    public void updateNote(Note note){
        repository.update(note);
    }
    public void addNote(Note note){
        repository.insert(note);
    }
    public Note getNoteById(int id){return repository.getNoteById(id);}
    public void deleteNote(Note note){repository.delete(note);}

}
