package com.konovus.noter.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konovus.noter.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemosFragment extends Fragment {

    public MemosFragment() {
        // Required empty public constructor
    }

    public static MemosFragment newInstance() {

        return new MemosFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_memos, container, false);
    }
}