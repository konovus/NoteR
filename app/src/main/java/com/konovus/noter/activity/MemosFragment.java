package com.konovus.noter.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konovus.noter.R;
import com.konovus.noter.adapter.MemosAdapter;
import com.konovus.noter.databinding.FragmentMemosBinding;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;
import com.konovus.noter.viewmodel.FragmentsViewModel;

import java.util.ArrayList;
import java.util.List;


public class MemosFragment extends Fragment {

    private FragmentMemosBinding binding;
    private MemosAdapter adapter;
    private List<Note> notes = new ArrayList<>();
    private FragmentsViewModel viewModel;

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
//        setHasOptionsMenu(true);

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
        });
    }

    private void setupRecyclerView() {
        adapter = new MemosAdapter(notes, requireContext());
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerView.setAdapter(adapter);
    }

    private void searchDB(String query){
        viewModel.searchNotes(query, NOTE_TYPE.MEMO).observe(requireActivity(), notesList -> {
            adapter.setData(notesList);
        });
    }
}