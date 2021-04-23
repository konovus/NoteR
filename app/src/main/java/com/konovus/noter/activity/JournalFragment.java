package com.konovus.noter.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konovus.noter.R;
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


public class JournalFragment extends Fragment {

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
//        setHasOptionsMenu(true);

        setupRecyclerView();
        setupSearch();
        observe();
    }

    private void setupSearch() {
        binding.search.setMaxWidth(Integer.MAX_VALUE);
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        viewModel.getAllNotes(NOTE_TYPE.JOURNAL).observe(requireActivity(), notesList -> {
            Log.i("NoteR", "JournalF - from observe");
            adapter.setData(notesList);
        });
    }

    private void setupRecyclerView() {
        adapter = new JournalAdapter(notes, requireContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_bar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void searchDB(String query){
        viewModel.searchNotes(query, NOTE_TYPE.JOURNAL).observe(requireActivity(), notesList -> {
            adapter.setData(notesList);
        });
    }
}