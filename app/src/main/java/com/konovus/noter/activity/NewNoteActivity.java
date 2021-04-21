package com.konovus.noter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.konovus.noter.R;
import com.konovus.noter.databinding.ActivityNewNoteBinding;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;
import com.konovus.noter.viewmodel.AddNoteViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewNoteActivity extends AppCompatActivity {

    private ActivityNewNoteBinding binding;
    private AddNoteViewModel viewModel;
    private Note note;
    private boolean isSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_note);
        viewModel = new ViewModelProvider(this).get(AddNoteViewModel.class);

        note = new Note();

        binding.backArrow.setOnClickListener(v -> {
            isSaved = true;
            onBackPressed();
        });
        binding.noteItBtn.setOnClickListener(v -> saveNote());

//        noteTextWatcher();
    }

    private void noteTextWatcher() {
        binding.noteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                note.setText(binding.noteText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void saveNote() {
        Note note = new Note();
        if(getIntent().getIntExtra("note_type", -1) != -1)
            if(getIntent().getIntExtra("note_type", -1) == 0)
                note.setNote_type(NOTE_TYPE.MEMO);
            else note.setNote_type(NOTE_TYPE.JOURNAL);

        if(binding.title.getText() != null)
            note.setTitle(binding.title.getText().toString());
        if(binding.noteText.getText() != null && !binding.noteText.getText().toString().isEmpty() )
            note.setText(binding.noteText.getText().toString());
        else {
            Toast.makeText(this, "Note text cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String date = new SimpleDateFormat("MMMM dd 'at' HH:mm a", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        note.setDate(date);

        viewModel.addNote(note);
        isSaved = true;

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("NoteR", "onDestroy");
        if(!isSaved)
            saveNote();

    }
}