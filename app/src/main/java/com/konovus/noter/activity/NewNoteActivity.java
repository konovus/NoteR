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
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.konovus.noter.R;
import com.konovus.noter.databinding.ActivityNewNoteBinding;
import com.konovus.noter.databinding.ChecklistRowBinding;
import com.konovus.noter.databinding.ChecklistRowViewingBinding;
import com.konovus.noter.databinding.PalleteLayoutBinding;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.ChecklistBuilder;
import com.konovus.noter.util.EncryptorFiles;
import com.konovus.noter.util.NOTE_TYPE;
import com.konovus.noter.util.StorageUtils;
import com.konovus.noter.util.WorkerNoteIt;
import com.konovus.noter.viewmodel.AddNoteViewModel;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class NewNoteActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private ActivityNewNoteBinding binding;
    private AddNoteViewModel viewModel;
    private Note note;
    private String selectedColor = "#1C2226";
    private Bitmap bitmap;
    private String date_reminder;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_note);
        viewModel = new ViewModelProvider(this).get(AddNoteViewModel.class);

        if (getIntent().getSerializableExtra("note") != null)
            fillPageWithData();
        else {
            note = new Note();
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorSmokeBlack));
        }

        binding.backArrow.setOnClickListener(v -> {
            ChecklistBuilder.clearChecklist();
            setResult(RESULT_CANCELED);
            onBackPressed();
        });
        binding.noteItBtn.setOnClickListener(v -> {
            if (note.getText() != null && !note.getText().trim().isEmpty()) {
                if (note.getNote_type() == NOTE_TYPE.TRASH) {
                    note.setNote_type(NOTE_TYPE.MEMO);
                    viewModel.updateNote(note);
                    setResult(RESULT_OK);
                    finish();
                } else saveNote("", false);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("note", note);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        if (note.getColor() != null && !note.getColor().trim().isEmpty())
            selectedColor = note.getColor();

        binding.addColor.setOnClickListener(v -> setupPalette());
        binding.addImg.setOnClickListener(v -> setupAddImage());
        binding.addAlarm.setOnClickListener(v -> setupAlarm());
        binding.addLock.setOnClickListener(v -> encryptNote());
        binding.addChecklist.setOnClickListener(v -> {
            if (ChecklistBuilder.getCheckList().isEmpty()) {
//            To change min height, both methods setMinHeight and setMinimumHeight are needed !
                binding.noteText.setMinimumHeight(0);
                binding.noteText.setMinHeight(0);
                ChecklistBuilder checklistBuilder = new ChecklistBuilder(this, this, selectedColor);
                checklistBuilder.build(null);
            }
        });

        binding.deleteImg.setOnClickListener(v -> {
            binding.noteImg.setImageBitmap(null);
            binding.noteImg.setVisibility(View.GONE);
            binding.deleteImg.setVisibility(View.GONE);
            new File(note.getImage_path()).delete();
            note.setImage_path(null);
            bitmap = null;
        });

        binding.delete.setOnClickListener(v -> deleteNote());

        setupTextWatcher();
    }

    private void encryptNote() {
        Intent intent = new Intent(this, MainActivity.class);
        if (note.getNote_type() == NOTE_TYPE.VAULT) {
            note.setNote_type(NOTE_TYPE.MEMO);
//            if(note.getImage_path() != null && !note.getImage_path().trim().isEmpty()){
//                String name = note.getImage_path().substring(note.getImage_path().lastIndexOf("/") + 1);
//                File file = new File(getExternalFilesDir("/").getAbsolutePath()+"/images" + name);
//                file.delete();
//            }
        } else {
            saveNote("", false);
            note.setNote_type(NOTE_TYPE.VAULT);
//            if (note.getImage_path() != null && !note.getImage_path().trim().isEmpty()) {
//                File file = new File(note.getImage_path());
//                note.setImage_path(EncryptorFiles.encryptFile(this, note.getImage_path()));
//                if (file.exists())
//                    file.delete();
//            }
        }
        viewModel.updateNote(note);
        startActivity(intent);
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
                    viewModel.updateNote(note);
                }
            }
        });
    }

    private void deleteNote() {
        ChecklistBuilder.clearChecklist();
        if (note.getNote_type() == NOTE_TYPE.TRASH) {
            if (note.getImage_path() != null && !note.getImage_path().trim().isEmpty())
                new File(note.getImage_path()).delete();
            viewModel.deleteNote(note);
            setResult(RESULT_OK);
            finish();
        } else {
            note.setRemoval_date(Calendar.getInstance().getTime());
            note.setNote_type(NOTE_TYPE.TRASH);
            viewModel.updateNote(note);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("note_type", getIntent().getIntExtra("note_type", -1));
            startActivity(intent);
        }
    }

    private void fillPageWithData() {
        note = (Note) getIntent().getSerializableExtra("note");
        if (note.getNote_type() == NOTE_TYPE.TRASH) {
            binding.delete.setColorFilter(ContextCompat.getColor(this, R.color.colorBrandy),
                    PorterDuff.Mode.MULTIPLY);
            binding.noteItBtn.setImageResource(R.drawable.ic_baseline_replay);
        } else if (note.getNote_type() == NOTE_TYPE.VAULT)
            binding.addLock.setImageResource(R.drawable.ic_baseline_lock_open_24);

        if (note.getTitle() != null)
            binding.title.setText(note.getTitle());
        binding.noteText.setText(note.getText());
        if (note.getTag() != null)
            binding.tag.setText(note.getTag());
        if (note.getColor() != null) {
            binding.newNoteWrapper.setBackgroundColor(Color.parseColor(note.getColor()));
            getWindow().setStatusBarColor(Color.parseColor(note.getColor()));
        } else getWindow().setStatusBarColor(Color.WHITE);
        if (note.getImage_path() != null && !note.getImage_path().trim().isEmpty()) {
            binding.noteImg.setImageURI(Uri.parse(note.getImage_path()));
            binding.noteImg.setVisibility(View.VISIBLE);
            binding.deleteImg.setVisibility(View.VISIBLE);
        }
        if (note.getCheckList() != null && note.getCheckList().size() != 0) {
            ChecklistBuilder checklistBuilder = new ChecklistBuilder(this, this, note.getColor());
            checklistBuilder.build(note.getCheckList());
            binding.noteText.setMinimumHeight(0);
            binding.noteText.setMinHeight(0);
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
        if (binding.title.getText() != null && !binding.title.getText().toString().trim().isEmpty())
            title = binding.title.getText().toString();
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

    private void setupAddImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        } else selectImage();
    }

    private void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    private void setupPalette() {

        PopupWindow mypopupWindow;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup_view = inflater.inflate(R.layout.pallete_layout, null);

        mypopupWindow = new PopupWindow(popup_view, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);
        mypopupWindow.showAsDropDown(binding.addColor);
        PalleteLayoutBinding palleteBinding = DataBindingUtil.bind(mypopupWindow.getContentView());
        List<ImageView> colors = new ArrayList<>();
        Collections.addAll(colors, palleteBinding.imageColor0, palleteBinding.imageColor1, palleteBinding.imageColor2,
                palleteBinding.imageColor3, palleteBinding.imageColor4, palleteBinding.imageColor5, palleteBinding.imageColor6);


        for (ImageView color : colors) {
            if (!selectedColor.trim().isEmpty())
                for (ImageView c : colors)
                    if (c.getTag().toString().equals(selectedColor))
                        c.setImageResource(R.drawable.ic_check);
                    else c.setImageResource(0);
            color.setOnClickListener(v -> {
                selectedColor = color.getTag().toString();
                binding.newNoteWrapper.setBackgroundColor(Color.parseColor(selectedColor));
                getWindow().setStatusBarColor(Color.parseColor(selectedColor));
                for (ImageView c : colors)
                    if (c.getTag().toString().equals(selectedColor))
                        c.setImageResource(R.drawable.ic_check);
                    else c.setImageResource(0);
                if (!ChecklistBuilder.getCheckList().isEmpty())
                    ChecklistBuilder.changeColor(selectedColor);

            });
        }

    }

    private void saveNote(String s, boolean emergency) {
        if (note.getId() == 0 && emergency)
            note.setId(getIntent().getIntExtra("max", 100) + 1);

        if (note.getNote_type() != null)
            note.setNote_type(note.getNote_type());
        else note.setNote_type(NOTE_TYPE.MEMO);
        note.setTitle(binding.title.getText().toString());
        if (emergency && !s.trim().isEmpty())
            note.setText(s);
        else if (binding.noteText.getText() != null && !binding.noteText.getText().toString().trim().isEmpty())
            note.setText(binding.noteText.getText().toString());
        else {
            Toast.makeText(this, "Note text cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        note.setCheckList(ChecklistBuilder.getCheckList());
        ChecklistBuilder.clearChecklist();

        note.setColor(selectedColor);

        if (note.getDate() == null)
            note.setDate(Calendar.getInstance().getTime());

        if (date_reminder != null)
            setupWorker();
        if (!binding.tag.getText().toString().trim().isEmpty())
            note.setTag(binding.tag.getText().toString());

        if (note.getNote_type() == NOTE_TYPE.VAULT && note.getImage_path() != null)
            note.setImage_path(EncryptorFiles.encryptFile(this, note.getImage_path()));

        viewModel.addNote(note);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImage();
            else Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = result.getOriginalUri();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.noteImg.setImageBitmap(bitmap);
                        binding.noteImg.setVisibility(View.VISIBLE);
                        binding.deleteImg.setVisibility(View.VISIBLE);
                        //      saving the cropped image
                        new SaveInternallyAsync().execute();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else Toast.makeText(this, "Image data is null", Toast.LENGTH_SHORT).show();
    }


    private class SaveInternallyAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String name = String.valueOf(System.currentTimeMillis());
            String path = StorageUtils.saveToInternalStorage(bitmap, getApplicationContext(), name);
            note.setImage_path(path + "/" + name + ".jpg");
            return null;
        }

    }

}