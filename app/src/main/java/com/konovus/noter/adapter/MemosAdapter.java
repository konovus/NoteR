package com.konovus.noter.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.konovus.noter.R;
import com.konovus.noter.databinding.ChecklistRowBinding;
import com.konovus.noter.databinding.ChecklistRowViewingBinding;
import com.konovus.noter.databinding.MemoLayoutItemBinding;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.NOTE_TYPE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import static com.konovus.noter.activity.MainActivity.TAG;

public class MemosAdapter extends RecyclerView.Adapter<MemosAdapter.MemosViewHolder> {

    private List<Note> notes;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnMemosClickListener clickListener;
    public static final int NOTE_TITLE = 0;
    public static final int NOTE_IMAGE = 1;
    public static final int NOTE_TITLE_IMAGE = 2;
    public static final int NOTE_TEXT = 3;

    public MemosAdapter(List<Note> notes, Context context, OnMemosClickListener clickListener) {
        this.notes = notes;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MemosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());
        MemoLayoutItemBinding binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.memo_layout_item, parent, false
        );
        return new MemosViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MemosViewHolder holder, int position) {
//        viewBinderHelper.bind(holder.binding.swipeLayout, String.valueOf(notes.get(position).getId()));
        switch (getItemViewType(position)) {
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
        if (note.getTitle() != null && !note.getTitle().trim().isEmpty() && note.getImage_path() == null)
            return NOTE_TITLE;
        else if (note.getImage_path() != null && (note.getTitle() == null || note.getTitle().trim().isEmpty()))
            return NOTE_IMAGE;
        else if ((note.getTitle() == null || note.getTitle().trim().isEmpty()) && note.getImage_path() == null)
            return NOTE_TEXT;
        else if (note.getTitle() != null && !note.getTitle().trim().isEmpty() && note.getImage_path() != null)
            return NOTE_TITLE_IMAGE;

        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public interface OnMemosClickListener {
        void OnMemoClick(Note note, int pos);
    }

    public class MemosViewHolder extends RecyclerView.ViewHolder {

        private MemoLayoutItemBinding binding;

        public MemosViewHolder(MemoLayoutItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note, boolean image, boolean title) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) binding.title.getLayoutParams();
            ViewGroup.MarginLayoutParams lp_text = (ViewGroup.MarginLayoutParams) binding.textNote.getLayoutParams();
            if (image) {
                Glide.with(context)
                        .load(note.getImage_path())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.imageNote);
                Glide.with(context).load(note.getImage_path())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.imageNoteCopy);
                binding.imageNote.setVisibility(View.VISIBLE);
                binding.imageNoteCopy.setVisibility(View.INVISIBLE);
                binding.imageNoteWrap.setCardBackgroundColor(Color.parseColor(
                        note.getColor() != null ? note.getColor() : "#1C2226"));

                lp.setMargins(10, 0, 0, 0);

            } else {
                lp.setMargins(10, 50, 0, 0);
            }

            if (title) {
                binding.title.setText(note.getTitle());
                binding.title.setVisibility(View.VISIBLE);
            } else lp_text.setMargins(30, 50, 30, 0);

            binding.textNote.setText(note.getText());
            binding.checklistWrapper.removeAllViews();
            if (note.getCheckList() != null) {
                binding.checklistWrapper.setVisibility(View.VISIBLE);
                for(int i = 0; i < note.getCheckList().size(); i++)
                    for (Map.Entry<String, String> entry : note.getCheckList().entrySet()) {
                        if(entry.getKey().contains("" + i)){
                            LinearLayout check_row_view = (LinearLayout) LayoutInflater.from(context)
                                    .inflate(R.layout.checklist_row_viewing, null);
                            binding.checklistWrapper.addView(check_row_view);
                            ChecklistRowViewingBinding checkRowBinding = DataBindingUtil.bind(check_row_view);
                            if(entry.getKey().contains("true"))
                                checkRowBinding.checkBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_checkbox));
                            checkRowBinding.textView.setText(entry.getValue());
                            if(entry.getKey().contains("true"))
                                checkRowBinding.textView.setPaintFlags(checkRowBinding.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            else
                                checkRowBinding.textView.setPaintFlags(checkRowBinding.textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                        }
                    }
            }

            GradientDrawable gradientDrawable = (GradientDrawable) binding.layoutNote.getBackground();
            if (note.getColor() != null && !note.getColor().trim().isEmpty())
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            else
                gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorSmokeBlack));

            SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd", Locale.ENGLISH);

            Date date = note.getDate();
            binding.dateNote.setText(sdf2.format(date));

            binding.layoutNote.setOnClickListener(v -> clickListener.OnMemoClick(note, getAdapterPosition()));
            binding.executePendingBindings();
        }
    }

    public void setData(List<Note> data) {
        Log.i("NoteR", "MemosAdapter - from setData");
        if (data != null) {
            notes = data;
            notifyDataSetChanged();
        }
    }

    public void insertNote(Note note) {
        Log.i(TAG, "insertNote: ");
        notes.add(0, note);
        notifyItemInserted(0);
    }

    public void updateNote(Note note, int pos) {
        notes.set(pos, note);
        notifyItemChanged(pos, note);
    }

    public void removeNote(int pos) {
        notes.remove(pos);
        notifyItemRemoved(pos);
    }


//    private static DiffUtil.ItemCallback<Note> DIFF_CALLBACK =
//            new DiffUtil.ItemCallback<Note>() {
//                @Override
//                public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
//                    return oldItem.getId() == newItem.getId();
//                }
//
//                @Override
//                public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
//                    return oldItem.getTitle().equals(newItem.getTitle()) &&
//                           oldItem.getColor().equals(newItem.getColor()) &&
//                           oldItem.getText().equals(newItem.getText()) &&
//                           oldItem.getImage_path().equals(newItem.getImage_path()) &&
//                           oldItem.getTag().equals(newItem.getTag());
//                }
//            };


//    @Override
//    public void submitList(@Nullable List<Note> list) {
//        super.submitList(list != null ? new ArrayList<>(list) : null);
//    }
//
//    static class NoteDiffCallback extends DiffUtil.ItemCallback<Note> {
//
//        @Override
//        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
//            return oldItem.getId() == newItem.getId();
//
//        }
//
//        @Override
//        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
//            return oldItem.equals(newItem);
//
//        }
//    }
}
