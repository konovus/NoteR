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


public class MemosFragment extends Fragment implements MemosAdapter.OnMemosClickListener {

    private FragmentMemosBinding binding;
    private MemosAdapter adapter;
    private List<Note> notes = new ArrayList<>();
    private FragmentsViewModel viewModel;
    private SubMenu tags;

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

        setupDrawer();
        setupRecyclerView();
        setupSearch();
        observe();
    }


    private void setupDrawer() {
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(requireActivity(), drawerLayout, binding.toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    private void setupDrawerNumbers(List<Note> noteList){
        NavigationView navigationView = requireActivity().findViewById(R.id.navView);

        // get menu from navigationView
        Menu menu = navigationView.getMenu();
        if(tags == null){
            tags = menu.addSubMenu("Tags");
            MenuItem trash = menu.add("Trash");
            trash.setIcon(R.drawable.ic_trash);
            MenuItem cloudSave = menu.add("Save to Cloud");
            cloudSave.setIcon(R.drawable.ic_cloud_save);
        }
        Set<String> tags_distinct = new HashSet<>();
        for(Note note : noteList)
            if(note.getTag() != null && !note.getTag().trim().isEmpty())
                tags_distinct.add(note.getTag());
        for(String tag : tags_distinct)
            tags.add(tag);
        if(tags_distinct.size() == 0){
            tags.add("Empty");
            tags.add("Empty");
        }


        // find MenuItem you want to change
        MenuItem nav_memos = menu.findItem(R.id.memos);
        TextView memosTV;

        memosTV = (TextView) nav_memos.getActionView();
        memosTV.setGravity(Gravity.CENTER_VERTICAL);
        memosTV.setTypeface(null, Typeface.BOLD);
        memosTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorMyAccent));
        memosTV.setText(String.valueOf(noteList.size()));
    }

    private void setupSearch() {
        SearchView search = getView().findViewById(R.id.search);
        search.setMaxWidth(Integer.MAX_VALUE);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query != null && !query.isEmpty())
                    searchDB(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null )
                    searchDB(newText);
                return true;
            }
        });
    }

    private void observe() {
        viewModel.getAllNotes(NOTE_TYPE.MEMO).observe(requireActivity(), notesList -> {
            Log.i("NoteR", "MemosF - from observe");
            adapter.setData(notesList);
            setupDrawerNumbers(notesList);
        });
    }

    private void setupRecyclerView() {
        adapter = new MemosAdapter(notes, requireContext(), this);
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setItemViewCacheSize(100);
    }

    private void searchDB(String query){
        viewModel.searchNotes(query, NOTE_TYPE.MEMO).observe(requireActivity(), notesList -> {
            adapter.setData(notesList);
        });
    }

    @Override
    public void OnMemoClick(Note note) {
        Intent intent = new Intent(requireContext(), NewNoteActivity.class);
        intent.putExtra("note", note);
        startActivity(intent);
    }
}