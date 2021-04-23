package com.konovus.noter.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konovus.noter.R;
import com.konovus.noter.databinding.FragmentJournalBinding;
import com.konovus.noter.databinding.MemoLayoutItemBinding;
import com.konovus.noter.entity.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private List<Note> notes;
    private Context context;
    private LayoutInflater layoutInflater;

    public JournalAdapter(List<Note> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());
        MemoLayoutItemBinding binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.memo_layout_item, parent, false
        );
        return new JournalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class JournalViewHolder extends RecyclerView.ViewHolder{

        MemoLayoutItemBinding binding;

        public JournalViewHolder(MemoLayoutItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note){

            if(note.getImage_path() != null){
                binding.imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImage_path()));
                binding.imageNote.setVisibility(View.VISIBLE);
            } else binding.imageNote.setVisibility(View.GONE);

            if(note.getTitle() != null) {
                binding.title.setText(note.getTitle());
                binding.title.setVisibility(View.VISIBLE);
            } else binding.title.setVisibility(View.GONE);

            binding.textNote.setText(note.getText());

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd 'at' HH:mm a", Locale.ENGLISH);
            Date date;
            try {
                date = sdf.parse(note.getDate());
                binding.dateNote.setText(sdf.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void setData(List<Note> data) {
        Log.i("NoteR", "JournalAdapter - from setData");
        if(data != null){
            notes.clear();
            notes.addAll(data);
            notifyDataSetChanged();
        }
    }
    public void insertNote(Note note){
        notes.add(note);
        notifyItemInserted(notes.size() - 1);
    }
    public void updateNote(Note note, int pos){
        notes.set(pos, note);
        notifyItemChanged(pos, note);
    }
}
