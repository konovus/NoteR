package com.konovus.noter.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konovus.noter.R;
import com.konovus.noter.databinding.MemoLayoutItemBinding;
import com.konovus.noter.entity.Note;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class MemosAdapter extends ListAdapter<Note, MemosAdapter.MemosViewHolder> {

    private List<Note> notes;
    private Context context;
    private LayoutInflater layoutInflater;

    public MemosAdapter(List<Note> notes, Context context, LayoutInflater layoutInflater) {
        super(DIFF_CALLBACK);
        this.notes = notes;
        this.context = context;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public MemosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());
        MemoLayoutItemBinding binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.memo_layout_item, parent, false
        );
        return new MemosViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MemosViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class MemosViewHolder extends RecyclerView.ViewHolder{

        private MemoLayoutItemBinding binding;

        public MemosViewHolder(MemoLayoutItemBinding binding) {
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
            binding.dateNote.setText(sdf.format(note.getDate()));
        }
    }

    public void setData(List<Note> data) {
        submitList(data);
    }

    private static DiffUtil.ItemCallback<Note> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Note>() {
                @Override
                public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle()) &&
                           oldItem.getColor().equals(newItem.getColor()) &&
                           oldItem.getText().equals(newItem.getText()) &&
                           oldItem.getImage_path().equals(newItem.getImage_path()) &&
                           oldItem.getTag().equals(newItem.getTag());
                }
            };
}
