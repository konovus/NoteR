package com.konovus.noter.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

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
import com.konovus.noter.viewmodel.MemosViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ObjIntConsumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemosFragment extends Fragment {

    private FragmentMemosBinding binding;
    private MemosAdapter adapter;
    private List<Note> notes = new ArrayList<>();
    private MemosViewModel viewModel;

    public MemosFragment() {
        // Required empty public constructor
    }

    public static MemosFragment newInstance() {
        return new MemosFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MemosViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_memos, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        setupRecyclerView();
        observe();
    }

    private void observe() {
        viewModel.getAllNotes(NOTE_TYPE.MEMO.toString()).observe(requireActivity(), notesList -> {
            adapter.setData(notesList);
        });
    }

    private void setupRecyclerView() {
        adapter = new MemosAdapter(notes, requireContext());
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
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
        viewModel.searchNotes(query, NOTE_TYPE.MEMO.toString()).observe(requireActivity(), notesList -> {
            adapter.setData(notesList);
        });
    }
}