package com.konovus.noter.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.konovus.noter.R;
import com.konovus.noter.databinding.FragmentJournalBinding;
import com.konovus.noter.databinding.MemoLayoutItemBinding;
import com.konovus.noter.entity.Note;
import com.konovus.noter.viewmodel.JournalViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private List<Note> notes;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnJournalClickListener clickListener;
    public static final int NOTE_TITLE = 0;
    public static final int NOTE_IMAGE = 1;
    public static final int NOTE_TITLE_IMAGE = 2;
    public static final int NOTE_TEXT = 3;

    public JournalAdapter(List<Note> notes, Context context, OnJournalClickListener clickListener) {
        this.notes = notes;
        this.context = context;
        this.clickListener = clickListener;
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
        switch (getItemViewType(position)){
            case NOTE_TEXT:
                holder.bind(notes.get(position), false, false);
                break;
            case NOTE_IMAGE:
                holder.bind(notes.get(position), true, false);
                break;
            case NOTE_TITLE:
                holder.bind(notes.get(position), false, true);
                break;
            case NOTE_TITLE_IMAGE:
                holder.bind(notes.get(position), true, true);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Note note = notes.get(position);
        if(note.getTitle() != null && !note.getTitle().trim().isEmpty() && note.getImage_path() == null)
            return NOTE_TITLE;
        else if(note.getImage_path() != null && (note.getTitle() == null || note.getTitle().trim().isEmpty()))
            return NOTE_IMAGE;
        else if((note.getTitle() == null || note.getTitle().trim().isEmpty())&& note.getImage_path() == null)
            return NOTE_TEXT;
        else if(note.getTitle() != null && !note.getTitle().trim().isEmpty() && note.getImage_path() != null)
            return NOTE_TITLE_IMAGE;

        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public interface OnJournalClickListener{
        void OnJournalClick(Note note);
    }

    class JournalViewHolder extends RecyclerView.ViewHolder{

        MemoLayoutItemBinding binding;

        public JournalViewHolder(MemoLayoutItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note, boolean image, boolean title){
            ViewGroup.MarginLayoutParams lp =  (ViewGroup.MarginLayoutParams)binding.title.getLayoutParams();
            ViewGroup.MarginLayoutParams lp_text =  (ViewGroup.MarginLayoutParams)binding.textNote.getLayoutParams();
            if(image){
                Glide.with(context).load(note.getImage_path()).into(binding.imageNote);
                binding.imageNote.setVisibility(View.VISIBLE);
                binding.imageNoteWrap.setCardBackgroundColor(Color.parseColor(
                        note.getColor() != null ? note.getColor() : "#1C2226"));

                lp.setMargins(10, 0, 0, 0);

            } else {
                lp.setMargins(10, 50, 0, 0);
            }
            binding.title.setLayoutParams(lp);

            if(title){
                binding.title.setText(note.getTitle());
                binding.title.setVisibility(View.VISIBLE);
            } else lp_text.setMargins(30, 50, 30, 0);

            binding.textNote.setLayoutParams(lp_text);

            binding.textNote.setText(note.getText());

            GradientDrawable gradientDrawable = (GradientDrawable) binding.layoutNote.getBackground();
            if(note.getColor() != null && !note.getColor().trim().isEmpty())
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            else gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorSmokeBlack));

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd 'at' HH:mm a", Locale.ENGLISH);
            SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd", Locale.ENGLISH);

            Date date = note.getDate();
            binding.dateNote.setText(sdf2.format(date));

            binding.executePendingBindings();
            binding.layoutNote.setOnClickListener(v -> clickListener.OnJournalClick(note));
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
