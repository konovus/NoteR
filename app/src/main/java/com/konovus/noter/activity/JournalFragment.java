package com.konovus.noter.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.konovus.noter.R;
import com.konovus.noter.adapter.FragmentsAdapter;
import com.konovus.noter.adapter.JournalAdapter;
import com.konovus.noter.adapter.MemosAdapter;
import com.konovus.noter.databinding.FragmentJournalBinding;
import com.konovus.noter.databinding.FragmentMemosBinding;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;
import com.konovus.noter.viewmodel.FragmentsViewModel;
import com.konovus.noter.viewmodel.JournalViewModel;

import java.util.ArrayList;
import java.util.List;


public class JournalFragment extends Fragment implements FragmentsAdapter.UpdateableFragment, JournalAdapter.OnJournalClickListener {

    private FragmentJournalBinding binding;
    private JournalAdapter adapter;
    private List<Note> notes = new ArrayList<>();
    private JournalViewModel viewModel;

    public JournalFragment() {
        // Required empty public constructor
    }

    public static JournalFragment newInstance() {return new JournalFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(JournalViewModel.class);
        Log.i("NoteR", "JournalF - from onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_journal, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        setupDrawer();

        setupRecyclerView();
//        setupSearch();
        observe();
    }

//    private void setupDrawer() {
//        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(requireActivity(), drawerLayout, binding.toolbar,
//                R.string.nav_drawer_open, R.string.nav_drawer_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//    }
    private void setupDrawerNumbers(List<Note> noteList){
        NavigationView navigationView = requireActivity().findViewById(R.id.navView);

        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        // find MenuItem you want to change
        MenuItem nav_journal = menu.findItem(R.id.journal);
        TextView journalTV;
//These lines should be added in the OnCreate() of your main activity
        journalTV = (TextView) nav_journal.getActionView();
        journalTV.setGravity(Gravity.CENTER_VERTICAL);
        journalTV.setTypeface(null, Typeface.BOLD);
        journalTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorMyAccent));
        journalTV.setText(String.valueOf(noteList.size()));
    }
//    private void setupSearch() {
//        SearchView search = getView().findViewById(R.id.search);
//        search.setMaxWidth(Integer.MAX_VALUE);
//        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if(query != null && !query.isEmpty())
//                    searchDB(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if(newText != null )
//                    searchDB(newText);
//                return true;
//            }
//        });
//    }
    private void observe() {
        viewModel.getAllNotes(NOTE_TYPE.JOURNAL).observe(requireActivity(), notesList -> {
            Log.i("NoteR", "JournalF - from observe");
            adapter.setData(notesList);
//            setupDrawerNumbers(notesList);
        });
    }

    private void setupRecyclerView() {
        adapter = new JournalAdapter(notes, requireContext(), this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.top_bar_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//
//        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if(query != null && !query.isEmpty())
//                    searchDB(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if(newText != null )
//                    searchDB(newText);
//                return true;
//            }
//        });
//    }
//
//    private void searchDB(String query){
//        viewModel.searchNotes(query, NOTE_TYPE.JOURNAL).observe(requireActivity(), notesList -> {
//            adapter.setData(notesList);
//        });
//    }

    @Override
    public void update(List<Note> noteList, NOTE_TYPE note_type) {
        if(note_type == NOTE_TYPE.JOURNAL)
            adapter.setData(noteList);
    }

    @Override
    public void OnJournalClick(Note note) {
        Intent intent = new Intent(requireContext(), NewNoteActivity.class);
        intent.putExtra("note_type", 1);
        intent.putExtra("note", note);
        startActivity(intent);
    }
}