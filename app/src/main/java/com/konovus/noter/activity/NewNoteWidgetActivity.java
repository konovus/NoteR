package com.konovus.noter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.konovus.noter.R;
import com.konovus.noter.databinding.ActivityNewNoteWidgetBinding;
import com.konovus.noter.entity.Note;
import com.konovus.noter.repository.NotesRepository;
import com.konovus.noter.util.ChecklistBuilder;
import com.konovus.noter.util.NOTE_TYPE;
import com.konovus.noter.util.StaticM;
import com.konovus.noter.util.WorkerNoteIt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.konovus.noter.activity.MainActivity.TAG;
import static com.konovus.noter.activity.MainActivity.max_id;
import static com.konovus.noter.activity.MainActivity.widgetNotesMain;

public class NewNoteWidgetActivity extends AppCompatActivity {

    private ActivityNewNoteWidgetBinding binding;
    private NotesRepository repository;
    private final Note note = new Note();
    private String date_reminder;
    private final Calendar myCalendar = Calendar.getInstance();
    private ChecklistBuilder checklistBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note_widget);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setFinishOnTouchOutside(false);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_note_widget);
        repository = new NotesRepository(getApplication());

        setupInitially();
        setupTextWatcher();
    }


    private void setupInitially() {
        binding.alarmWidget.setEnabled(false);
        binding.noteText.setOnFocusChangeListener((v, hasFocus) -> showKeyboard());
        binding.noteText.requestFocus();

        binding.noteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()) {
                    binding.alarmWidget.setEnabled(true);
                    binding.alarmWidget.setBackgroundResource(R.drawable.alarm);
                } else {
                    binding.alarmWidget.setEnabled(false);
                    binding.alarmWidget.setBackgroundResource(R.drawable.alarm_disabled);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.save.setOnClickListener(v -> {
            saveNote("", false);
            closeKeyboard();
            finish();
        });
        binding.cancelWidget.setOnClickListener(v -> {
            finish();
            closeKeyboard();
        });
        binding.alarmWidget.setOnClickListener(v -> setupAlarm());
        binding.checkbox.setOnClickListener(v -> {
            if (checklistBuilder == null || checklistBuilder.getCheckList().isEmpty()) {
//            To change min height, both methods setMinHeight and setMinimumHeight are needed !
                binding.noteText.setMinimumHeight(0);
                binding.noteText.setMinHeight(0);
                binding.alarmWidget.setVisibility(View.GONE);
                binding.checkbox.setBackgroundResource(R.drawable.close);
                checklistBuilder = new ChecklistBuilder(this, binding.checklistWrapper, "#2A2A2A");
                checklistBuilder.build(null);
            } else {
                binding.alarmWidget.setVisibility(View.VISIBLE);
                binding.checkbox.setBackgroundResource(R.drawable.check);
                checklistBuilder.clearChecklist();
            }
        });

    }
    private void setupTextWatcher() {
        binding.noteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (note.getText() == null)
                    saveNote(s.toString(), true);
                else {
                    note.setText(s.toString());
                    repository.update(note);
                }
            }
        });
    }

    private void saveNote(String s, boolean emergency) {
        if (note.getId() == 0 && emergency)
            note.setId(max_id + 1);

        if(emergency)
            note.setNote_type(NOTE_TYPE.TRASH);
        else
            note.setNote_type(NOTE_TYPE.MEMO);

        if (emergency && !s.trim().isEmpty())
            note.setText(s);
        else if (binding.noteText.getText() != null && !binding.noteText.getText().toString().trim().isEmpty())
            note.setText(binding.noteText.getText().toString());
        else {
            Toast.makeText(this, "Note text cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(checklistBuilder != null) {
            note.setCheckList(checklistBuilder.getCheckList());
            checklistBuilder.clearChecklist();
        }
        String selectedColor = "#1C2226";
        note.setColor(selectedColor);

        if (note.getDate() == null)
            note.setDate(Calendar.getInstance().getTime());

        if (date_reminder != null)
            setupWorker();
        if(emergency)
            repository.insert(note);
        else {
            repository.update(note);
            List<Note> noteList = StaticM.loadNotesFromPhone("notes", this);
            noteList.add(0, note);
            StaticM.saveNotesToPhone(noteList, "notes", this);
            int appWidgetId = PreferenceManager.getDefaultSharedPreferences(this)
                    .getInt("appWidgetId", 0);
            AppWidgetManager.getInstance(this)
                    .notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView);
        }
    }

    private void setupAlarm() {
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            openTimePicker(myCalendar);
        };

        new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openTimePicker(Calendar myCalendar) {
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            myCalendar.set(Calendar.HOUR, selectedHour);
            myCalendar.set(Calendar.MINUTE, selectedMinute);
            myCalendar.set(Calendar.SECOND, 0);
            date_reminder = new SimpleDateFormat("MMMM dd 'at' HH:mm a", Locale.ENGLISH).format(myCalendar.getTime());
            note.setReminder(date_reminder);
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    private void setupWorker() {
        long duration = myCalendar.getTimeInMillis() - System.currentTimeMillis();
        String title = "";
        String text = "";
        if (binding.noteText.getText() != null && !binding.noteText.getText().toString().trim().isEmpty())
            text = binding.noteText.getText().toString();
//        data is used to send the note title and note text to the Worker class
        Data data = new Data.Builder()
                .putString("title", title)
                .putString("text", text)
                .build();

        WorkRequest request =
                new OneTimeWorkRequest.Builder(WorkerNoteIt.class)
                        .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                        .setInputData(data)
                        .build();
        WorkManager
                .getInstance(getApplicationContext())
                .enqueue(request);

        Toast.makeText(this, "Reminder set for " + date_reminder, Toast.LENGTH_LONG).show();
    }
    private void showKeyboard(){
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(view, 0);
//        }
        InputMethodManager inputMgr = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        //if keyboard it's not showing, you can set .SHOW_FORCED
        inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.RESULT_HIDDEN);
    }
    private void closeKeyboard(){
      View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}