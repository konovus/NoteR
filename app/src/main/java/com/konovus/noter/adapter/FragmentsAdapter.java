package com.konovus.noter.adapter;

import com.konovus.noter.activity.JournalFragment;
import com.konovus.noter.activity.MemosFragment;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;

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
    List<Note> noteList = new ArrayList<>();
    private NOTE_TYPE note_type;

    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String name){
        tab_names.add(name);
        fragments.add(fragment);
    }

    public interface UpdateableFragment {
        void update(List<Note> noteList, NOTE_TYPE note_type);
    }
    public void update(List<Note> noteList, NOTE_TYPE note_type) {
        this.noteList = noteList;
        this.note_type = note_type;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof UpdateableFragment) {
            ((UpdateableFragment) object).update(noteList, note_type);
        }
        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);
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
