package com.konovus.noter.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.konovus.noter.R;
import com.konovus.noter.adapter.FragmentsAdapter;
import com.konovus.noter.databinding.ActivityMainBinding;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;
import com.konovus.noter.viewmodel.AddNoteViewModel;
import com.konovus.noter.viewmodel.FragmentsViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private FragmentsAdapter fragmentsAdapter;
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    private FragmentsViewModel viewModel;
    private SubMenu tags;
    private SearchView search;
    private List<Note> memoList;
    private List<Note> journalList;
    private String[] tags_distinct_arr;
    private NavigationView navigationView;
    private List<Note> old_notes = new ArrayList<>();
    private int max_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);

        binding.addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewNoteActivity.class);
            intent.putExtra("note_type", binding.viewPager.getCurrentItem());
            intent.putExtra("max", max_id);
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
        });

        setupDrawer();
        setupSearch();
        setupViewPager();
        setupBottomNav();

        if(getIntent().getIntExtra("note_type", -1) == 0) {
            binding.viewPager.setCurrentItem(0);
            binding.bottomNav.setSelectedItemId(0);
        } else if(getIntent().getIntExtra("note_type", -1) == 1){
            binding.viewPager.setCurrentItem(1);
            binding.bottomNav.setSelectedItemId(1);
        }
    }
    private void getMaxId(List<Note> notesList) {
        for (Note note: notesList)
            if(note.getId() > max_id)
                max_id = note.getId();
    }
    private void setupSearch() {
        search = findViewById(R.id.search);
        search.setMaxWidth(Integer.MAX_VALUE);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query != null && !query.isEmpty())
                    if(binding.viewPager.getCurrentItem() == 0)
                        searchDB(query, NOTE_TYPE.MEMO);
                    else searchDB(query, NOTE_TYPE.JOURNAL);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null )
                    if(binding.viewPager.getCurrentItem() == 0)
                        searchDB(newText, NOTE_TYPE.MEMO);
                    else searchDB(newText, NOTE_TYPE.JOURNAL);
                return true;
            }
        });
    }

    private void searchDB(String query, final NOTE_TYPE note_type){
        viewModel.searchNotes(query, note_type).observe(this, notesList -> {
                fragmentsAdapter.update(notesList, note_type);
        });
    }

    private void setupDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navView);
        // get menu from navigationView
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.memos);

        Menu menu = navigationView.getMenu();
        if(tags == null){
            tags = menu.addSubMenu("Tags");
            MenuItem trash = menu.add(Menu.NONE, R.drawable.ic_trash, Menu.NONE, "Trash");
            trash.setIcon(R.drawable.ic_trash);
            MenuItem cloudSave = menu.add(Menu.NONE, R.drawable.ic_cloud_save, Menu.NONE, "Save to Cloud");
            cloudSave.setIcon(R.drawable.ic_cloud_save);
            MenuItem fromCloud = menu.add(Menu.NONE, R.drawable.ic_cloud_save + 1, Menu.NONE, "Get from Cloud");
            fromCloud.setIcon(R.drawable.ic_cloud_save);
        }
        viewModel.getAllNotes(NOTE_TYPE.MEMO).observe(this, noteList -> {
            memoList = noteList;
            getMaxId(noteList);

            Set<String> tags_distinct = new HashSet<>();
            for(Note note : noteList)
                if(note.getTag() != null && !note.getTag().trim().isEmpty())
                    tags_distinct.add(note.getTag());
            tags_distinct_arr = tags_distinct.toArray(new String[tags_distinct.size()]);
            for(int i = 0; i < tags_distinct_arr.length; i++){
                tags.add(Menu.NONE, i + 123, Menu.NONE, tags_distinct_arr[i]);
            }
            if(tags_distinct.size() == 0){
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
        viewModel.getAllNotes(NOTE_TYPE.JOURNAL).observe(this, noteList -> {
            journalList = noteList;
            getMaxId(noteList);
            MenuItem nav_journal = menu.findItem(R.id.journal);
            TextView journalTV;
            journalTV = (TextView) nav_journal.getActionView();
            journalTV.setGravity(Gravity.CENTER_VERTICAL);
            journalTV.setTypeface(null, Typeface.BOLD);
            journalTV.setTextColor(ContextCompat.getColor(this, R.color.colorMyAccent));
            journalTV.setText(String.valueOf(noteList.size()));
        });
        setupDrawerNumbers();
    }

    private void setupDrawerNumbers() {
    }

    private void setupBottomNav() {
        binding.bottomNav.setBackground(null);
        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.memos:
                    binding.viewPager.setCurrentItem(0);
                    navigationView.setCheckedItem(R.id.memos);

                    break;
                case R.id.journal:
                    binding.viewPager.setCurrentItem(1);
                    navigationView.setCheckedItem(R.id.journal);
                    break;
            }
            return true;
        });
    }

    private void setupViewPager() {
        fragmentsAdapter = new FragmentsAdapter(getSupportFragmentManager());
        fragmentsAdapter.addFragment(MemosFragment.newInstance(), "Memos");
        fragmentsAdapter.addFragment(JournalFragment.newInstance(), "Journal");

        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.setAdapter(fragmentsAdapter);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        binding.bottomNav.getMenu().findItem(R.id.memos).setChecked(true);
                        binding.toolbar.setTitle("Memos");
                        search.setQuery("", false);
                        search.setIconified(true);
                        break;
                    case 1:
                        binding.bottomNav.getMenu().findItem(R.id.journal).setChecked(true);
                        binding.toolbar.setTitle("Journal");
                        search.setQuery("", false);
                        search.setIconified(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.memos:
                binding.viewPager.setCurrentItem(0);
                fragmentsAdapter.update(memoList, NOTE_TYPE.MEMO);
                break;
            case R.id.journal:
                binding.viewPager.setCurrentItem(1);
                fragmentsAdapter.update(journalList, NOTE_TYPE.JOURNAL);
                break;
            case R.id.vault:
                Toast.makeText(this, "Vault: In development", Toast.LENGTH_SHORT).show();
                break;
            case R.drawable.ic_trash:
                Toast.makeText(this, "Trash: In development", Toast.LENGTH_SHORT).show();
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
        for(int i = 0; i < tags_distinct_arr.length; i++)
            if(item.getItemId() == i + 123)
                filterNotesByTag(tags_distinct_arr[i]);

        return true;
    }

    private void filterNotesByTag(String s) {
        List<Note> filteredMemos = new ArrayList<>();
        List<Note> filteredJournal = new ArrayList<>();
        for(Note note: memoList)
            if(note.getTag() != null && note.getTag().equals(s))
                filteredMemos.add(note);
        for(Note note: journalList)
            if(note.getTag() != null && note.getTag().equals(s))
                filteredJournal.add(note);
        if(!filteredMemos.isEmpty())
            fragmentsAdapter.update(filteredMemos, NOTE_TYPE.MEMO);
        if(!filteredJournal.isEmpty())
            fragmentsAdapter.update(filteredJournal, NOTE_TYPE.JOURNAL);
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
}