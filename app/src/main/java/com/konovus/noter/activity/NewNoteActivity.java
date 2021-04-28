package com.konovus.noter.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;

import com.konovus.noter.R;
import com.konovus.noter.databinding.ActivityNewNoteBinding;
import com.konovus.noter.databinding.PalleteLayoutBinding;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;
import com.konovus.noter.util.StorageUtils;
import com.konovus.noter.util.WorkerNoteIt;
import com.konovus.noter.viewmodel.AddNoteViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NewNoteActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final String CHANNEL_ID = "1";
    private ActivityNewNoteBinding binding;
    private AddNoteViewModel viewModel;
    private Note note;
    private boolean isSaved;
    private String selectedColor;
    private Bitmap bitmap;
    private String date_reminder;
    final Calendar myCalendar = Calendar.getInstance();


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

        getWindow().setStatusBarColor(Color.WHITE);
        binding.addColor.setOnClickListener(v -> setupPalette());
        binding.addImg.setOnClickListener(v -> setupAddImage());
        binding.addAlarm.setOnClickListener(v -> setupAlarm());
    }

    private void setupAlarm() {

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                openTimePicker(myCalendar);
            }
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
        if(binding.title.getText() != null && !binding.title.getText().toString().trim().isEmpty())
            title = binding.title.getText().toString();
        if(binding.noteText.getText() != null && !binding.noteText.getText().toString().trim().isEmpty())
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

    private void setupAddImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        } else selectImage();
    }

    private void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start( this);
    }

    private void setupPalette() {

        PopupWindow mypopupWindow;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup_view = inflater.inflate(R.layout.pallete_layout, null);

        mypopupWindow = new PopupWindow(popup_view, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);
        mypopupWindow.showAsDropDown(binding.addColor);
        PalleteLayoutBinding palleteBinding = DataBindingUtil.bind(mypopupWindow.getContentView());
        List<ImageView> colors = new ArrayList<>();
        Collections.addAll(colors, palleteBinding.imageColor1, palleteBinding.imageColor2,
                palleteBinding.imageColor3, palleteBinding.imageColor4, palleteBinding.imageColor5, palleteBinding.imageColor6);

        for(ImageView color : colors)
            color.setOnClickListener(v -> {
                selectedColor = color.getTag().toString();
                binding.newNoteWrapper.setBackgroundColor(Color.parseColor(selectedColor));
                getWindow().setStatusBarColor(Color.parseColor(selectedColor));
                for(ImageView c : colors)
                    if(c.getTag().toString().equals(selectedColor))
                        c.setImageResource(R.drawable.ic_check);
                    else c.setImageResource(0);
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
        if(binding.noteText.getText() != null && !binding.noteText.getText().toString().trim().isEmpty() )
            note.setText(binding.noteText.getText().toString());
        else {
            Toast.makeText(this, "Note text cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectedColor != null)
            note.setColor(selectedColor);
//      saving the cropped image
        new SaveInternallyAsync().execute();

        String date = new SimpleDateFormat("MMMM dd 'at' HH:mm a", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        note.setDate(date);

        if(date_reminder != null)
            setupWorker();

        viewModel.addNote(note);
        isSaved = true;
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("note_type", getIntent().getIntExtra("note_type", -1));
        startActivity(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0)
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImage();
            else Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = result.getUri();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.noteImg.setImageBitmap(bitmap);
                        binding.noteImg.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private class SaveInternallyAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String name = String.valueOf(System.currentTimeMillis());
            String path = StorageUtils.saveToInternalStorage(bitmap, getApplicationContext(), name);
            note.setImage_path(path);
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("NoteR", "onDestroy");
        if(!isSaved)
            saveNote();

    }
}