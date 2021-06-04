package com.konovus.noter.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.aghajari.zoomhelper.ZoomHelper;
import com.google.android.material.navigation.NavigationView;
import com.konovus.noter.R;
import com.konovus.noter.adapter.MemosAdapter;
import com.konovus.noter.databinding.ActivityMainBinding;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;
import com.konovus.noter.viewmodel.FragmentsViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MemosAdapter.OnMemosClickListener {

    private ActivityMainBinding binding;
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    private FragmentsViewModel viewModel;
    private MemosAdapter adapter;
    private final List<Note> notes = new ArrayList<>();

    private SubMenu tags;
    private List<Note> memoList;
    private List<Note> trashList = new ArrayList<>();
    private HashMap<String, Integer> tags_labels = new HashMap<>();
    private List<Note> old_notes = new ArrayList<>();
    private int max_id;
    private static int rv_pos;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);

        binding.addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewNoteActivity.class);
            intent.putExtra("max", max_id);
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
        });
        setupDrawer();
        setupSearch();
        setupRecyclerView();
        observe();
        checkInTrash();


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ZoomHelper zoomHelper = ZoomHelper.Companion.getInstance();
        zoomHelper.setLayoutTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        return zoomHelper.dispatchTouchEvent(ev,this) || super.dispatchTouchEvent(ev);

    }

    private void observe() {
        Log.i("NoteR", "MemosF - from observe");

        viewModel.getAllNotes(NOTE_TYPE.MEMO).observe(this, notesList -> {
            notes.addAll(notesList);
            adapter.setData(notes);
            if(rv_pos != 0)
                binding.recyclerView.post(() -> binding.recyclerView.scrollToPosition(rv_pos));

        });
    }

    private void setupRecyclerView() {
        Log.i("NoteR", "MemosF - from rvSetup");

        adapter = new MemosAdapter(notes, this, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setItemViewCacheSize(100);
        binding.recyclerView.setAdapter(adapter);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkInTrash() {
        viewModel.getAllNotes(NOTE_TYPE.TRASH).observe(this, noteList -> {
            trashList = noteList;
            checkForExpiredNotes(noteList);
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkForExpiredNotes(List<Note> noteList) {
        LocalDate localDate = LocalDate.now().minusDays(1);
        Date current = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        for(Note note: noteList){
            if(!note.getRemoval_date().after(current)){
                if(note.getImage_path() != null && !note.getImage_path().trim().isEmpty())
                    new File(note.getImage_path()).delete();
                viewModel.deleteNote(note);
            }
        }
    }

    private void getMaxId(List<Note> notesList) {
        for (Note note: notesList)
            if(note.getId() > max_id)
                max_id = note.getId();
    }
    private void setupSearch() {
        SearchView search = findViewById(R.id.search);
        search.setMaxWidth(Integer.MAX_VALUE);
        binding.title.setOnClickListener(v -> search.requestFocus());
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query != null && !query.isEmpty())
                        searchDB(query, NOTE_TYPE.MEMO);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null )
                        searchDB(newText, NOTE_TYPE.MEMO);
                return true;
            }
        });
    }

    private void searchDB(String query, final NOTE_TYPE note_type){
        viewModel.searchNotes(query, note_type).observe(this, notesList -> {
            adapter.setData(notesList);
            Log.i("NoteR", "MemosF - from searchDB");

        });
    }

    private void setupDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        // get menu from navigationView
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.memos);

        Menu menu = navigationView.getMenu();
        if(tags == null){
            tags = menu.addSubMenu("Tags");
            MenuItem cloudSave = menu.add(Menu.NONE, R.drawable.ic_cloud_save, Menu.NONE, "Save to Cloud");
            cloudSave.setIcon(R.drawable.ic_cloud_save);
            MenuItem fromCloud = menu.add(Menu.NONE, R.drawable.ic_cloud_save + 1, Menu.NONE, "Get from Cloud");
            fromCloud.setIcon(R.drawable.ic_cloud_save);
        }
        List<String> all_tags = new ArrayList<>();

        viewModel.getAllNotes(NOTE_TYPE.MEMO).observe(this, noteList -> {
            memoList = noteList;
            getMaxId(noteList);

            for(Note note : noteList)
                if(note.getTag() != null && !note.getTag().trim().isEmpty())
                    all_tags.add(note.getTag());
            for(String s : all_tags)
                tags_labels.putIfAbsent(s, Collections.frequency(all_tags, s));
            for(Map.Entry<String, Integer> entry : tags_labels.entrySet())
                tags.add(Menu.NONE, entry.getValue() + 123 , Menu.NONE, entry.getKey() + " (" +
                        entry.getValue() + ")");

            if(tags_labels.keySet().size() == 0){
                tags.add("Empty");
                tags.add("Empty");
            }
            MenuItem nav_memos = menu.findItem(R.id.memos);
            TextView memosTV;

            memosTV = (TextView) nav_memos.getActionView();
            memosTV.setGravity(Gravity.CENTER_VERTICAL);
            memosTV.setTypeface(null, Typeface.BOLD);
            memosTV.setTextColor(ContextCompat.getColor(this, R.color.colorMyAccent));
            memosTV.setText(String.valueOf(noteList.size()));
        });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.memos:
                binding.title.setText("Notes");
                adapter.setData(notes);
                break;
            case R.id.vault:
                Toast.makeText(this, "Vault: In development", Toast.LENGTH_SHORT).show();
                break;
            case R.id.trash:
                binding.title.setText("Trash");
                adapter.setData(trashList);
                break;
            case R.drawable.ic_cloud_save:
                Toast.makeText(this, "Cloud: In development", Toast.LENGTH_SHORT).show();
                break;
            case R.drawable.ic_cloud_save + 1:
                Toast.makeText(this, "Getting notes from Storage", Toast.LENGTH_SHORT).show();
                getNotesToApp();
                for(Note note : old_notes)
                    viewModel.addNote(note);
                break;
        }
        for(Map.Entry<String, Integer> entry : tags_labels.entrySet())
            if(item.getItemId() == entry.getValue() + 123)
                filterNotesByTag(entry.getKey());

        return true;
    }

    private void filterNotesByTag(String s) {
        List<Note> filteredMemos = new ArrayList<>();
        for(Note note: notes)
            if(note.getTag() != null && note.getTag().equals(s))
                filteredMemos.add(note);
        if(!filteredMemos.isEmpty())
            adapter.setData(filteredMemos);
    }

    private void getNotesToApp() {
        BufferedReader fr = null;
        Note  note = new Note();
        try {
            fr = new BufferedReader(new InputStreamReader(new FileInputStream(
                    new File(getExternalFilesDir("/").getAbsolutePath() + "/saved_notes.txt"))));
            String line = "";
            StringBuilder text = new StringBuilder();
            StringBuilder img_path = new StringBuilder();
            while((line = fr.readLine()) != null) {
                if(line.equals("<title>")) {
                    line = fr.readLine();
                    if(!line.trim().isEmpty() && !line.equals("null"))
                        note.setTitle(line);
                    line = fr.readLine();
                    line = fr.readLine();
                    if (line.equals("<text>"))
                        do {
                            line = fr.readLine();
                            if(!line.equals("</text>"))
                                text.append(line);
                        } while (!line.equals("</text>"));
                    note.setText(text.toString());
                    text.setLength(0);
                    line = fr.readLine();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd 'at' HH:mm a yyyy", Locale.ENGLISH);

                    Date date = sdf.parse(line);
                    note.setDate(date);
                    line = fr.readLine();
                    if (!line.equals("null")) {
                        do {
                            img_path.append(line);
                            line = fr.readLine();

                        } while (line != null && !line.equals("end_note"));
                        note.setImage_path(img_path.toString());
                        img_path.setLength(0);
                    }
                    note.setNote_type(NOTE_TYPE.MEMO);
                    List<String> colors = new ArrayList<>();
                    colors.addAll(Arrays.asList("#1C2226","#F9A825","#2E7D32",
                            "#C62828","#00838F", "#6A1B9A", "#EF6C00"));
                    note.setColor(colors.get((int)(Math.random() * 7)));
                    old_notes.add(note);
                    note = new Note();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void OnMemoClick(Note note) {
        Intent intent = new Intent(this, NewNoteActivity.class);
        intent.putExtra("note_type", note.getNote_type());
        intent.putExtra("note", note);
        rv_pos = notes.indexOf(note) + 1;
        startActivity(intent);
    }
}