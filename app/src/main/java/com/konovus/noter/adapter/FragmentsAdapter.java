package com.konovus.noter.adapter;

import com.konovus.noter.activity.JournalFragment;
import com.konovus.noter.activity.MemosFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentsAdapter extends FragmentPagerAdapter {

    List<String> tab_names = new ArrayList<>();
    List<Fragment> fragments = new ArrayList<>();

    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String name){
        tab_names.add(name);
        fragments.add(fragment);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return MemosFragment.newInstance();
        else return JournalFragment.newInstance();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tab_names.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
