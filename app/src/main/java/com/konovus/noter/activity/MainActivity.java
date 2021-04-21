package com.konovus.noter.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.konovus.noter.R;
import com.konovus.noter.adapter.FragmentsAdapter;
import com.konovus.noter.databinding.ActivityMainBinding;
import com.konovus.noter.viewmodel.AddNoteViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FragmentsAdapter fragmentsAdapter;
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        
        binding.addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewNoteActivity.class);
            intent.putExtra("note_type", binding.viewPager.getCurrentItem());
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
        });

        setTitle("NoteIt");
        setupViewPager();
        setupBottomNav();
    }

    private void setupBottomNav() {
        binding.bottomNav.setBackground(null);

        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.memos:
                    binding.viewPager.setCurrentItem(0);
                    break;
                case R.id.journal:
                    binding.viewPager.setCurrentItem(1);
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
                        break;
                    case 1:
                        binding.bottomNav.getMenu().findItem(R.id.journal).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}