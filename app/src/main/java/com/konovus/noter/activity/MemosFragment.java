package com.konovus.noter.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.konovus.noter.R;
import com.konovus.noter.adapter.FragmentsAdapter;
import com.konovus.noter.adapter.MemosAdapter;
import com.konovus.noter.databinding.FragmentMemosBinding;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;
import com.konovus.noter.viewmodel.FragmentsViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static androidx.core.view.MenuItemCompat.getActionView;


public class MemosFragment extends Fragment implements MemosAdapter.OnMemosClickListener, FragmentsAdapter.UpdateableFragment {

    private FragmentMemosBinding binding;
    private MemosAdapter adapter;
    private List<Note> notes = new ArrayList<>();
    private FragmentsViewModel viewModel;
    private int max_id;

    public MemosFragment() {
        // Required empty public constructor
    }

    public static MemosFragment newInstance() {
        return new MemosFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);

        Log.i("NoteR", "MemosF - from onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_memos, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        observe();
    }

    private void observe() {
        viewModel.getAllNotes(NOTE_TYPE.MEMO).observe(requireActivity(), notesList -> {
            Log.i("NoteR", "MemosF - from observe");
            adapter.setData(notesList);
        });
    }



    private void setupRecyclerView() {
        adapter = new MemosAdapter(notes, requireContext(), this);
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setItemViewCacheSize(100);

    }

    @Override
    public void OnMemoClick(Note note) {
        Intent intent = new Intent(requireContext(), NewNoteActivity.class);
        intent.putExtra("note_type", 0);
        intent.putExtra("note", note);
        startActivity(intent);
    }

    @Override
    public void update(List<Note> noteList, NOTE_TYPE note_type) {
        if(note_type == NOTE_TYPE.MEMO || note_type == NOTE_TYPE.TRASH)
            adapter.setData(noteList);
    }
}