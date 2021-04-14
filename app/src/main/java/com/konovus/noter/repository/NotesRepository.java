package com.konovus.noter.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.konovus.noter.dao.NoteDao;
import com.konovus.noter.database.NoteDatabase;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NotesRepository {

    private NoteDao noteDao;

    public NotesRepository(Application application){
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        noteDao = noteDatabase.noteDao();
    }

    public void insert(Note note){
        new InsertNoteAsyncTask(noteDao).execute(note);
    }
    public void update(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }
    public void delete(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }
    public Note getNoteById(int id){
        return noteDao.getNoteById(id);
    }
    public LiveData<List<Note>> getAllNotes(String note_type){ return noteDao.getAllNotes(note_type);}
    public LiveData<List<Note>> searchNotes(String searchQuery, String note_type){
        return noteDao.searchNotes(searchQuery, note_type);}

    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        private InsertNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }
    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }
    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }
}
