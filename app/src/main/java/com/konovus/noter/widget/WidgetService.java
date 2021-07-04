package com.konovus.noter.widget;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.konovus.noter.R;
import com.konovus.noter.entity.Note;
import com.konovus.noter.util.ChecklistBuilder;
import com.konovus.noter.util.StaticM;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetItemFactory(getApplicationContext(), intent);
    }

    class WidgetItemFactory implements RemoteViewsFactory {

        private final Context context;
        private final int appWidgetId;
        private List<Note> notes = new ArrayList<>();
        private List<Note> widgetNotes = new ArrayList<>();

        public WidgetItemFactory(Context context, Intent intent) {
            this.context = context;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        }

        @Override
        public void onCreate() {
            notes = StaticM.loadNotesFromPhone("notes", context);
            for(Note note: notes)
                if(note.getImage_path() == null || note.getImage_path().isEmpty())
                    widgetNotes.add(note);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("appWidgetId", appWidgetId).apply();
        }

        @Override
        public void onDataSetChanged() {
            notes = StaticM.loadNotesFromPhone("notes", context);
            widgetNotes.clear();
            for(Note note: notes)
                if(note.getImage_path() == null || note.getImage_path().isEmpty())
                    widgetNotes.add(note);
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return widgetNotes.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_single_item);

            setBackGroundResource(views, widgetNotes.get(position).getColor());

            SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
            Date date = widgetNotes.get(position).getDate();
            views.setTextViewText(R.id.dateWidget, sdf2.format(date));

            views.setTextViewText(R.id.descriptionWidget, widgetNotes.get(position).getText());
            Intent clickIntent = new Intent();
            clickIntent.putExtra("note", widgetNotes.get(position));
            clickIntent.putExtra("fromWidget", true);
            for(Note note: notes)
                if(note.getId() == widgetNotes.get(position).getId())
                    clickIntent.putExtra("pos", notes.indexOf(note));

            if (widgetNotes.get(position).getCheckList() != null) {
                views.setInt(R.id.checklist_wrapper_widget, "setVisibility", View.VISIBLE);
                HashMap<String, String> checklist = widgetNotes.get(position).getCheckList();
                views.removeAllViews(R.id.checklist_wrapper_widget);


                for (Map.Entry<String, String> entry: checklist.entrySet()) {
                    RemoteViews row_views = new RemoteViews(context.getPackageName(), R.layout.checklist_row_widget);
                    row_views.setTextViewText(R.id.text, entry.getValue());
                    if (entry.getKey().contains("true")) {
                        row_views.setInt(R.id.text, "setPaintFlags",
                                Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                        row_views.setInt(R.id.check_btn, "setImageResource", R.drawable.check);
                    }
                    views.addView(R.id.checklist_wrapper_widget, row_views);
                }
            } else {
                views.removeAllViews(R.id.checklist_wrapper_widget);
                views.setInt(R.id.checklist_wrapper_widget, "setVisibility", View.GONE);
            }

            views.setOnClickFillInIntent(R.id.widget_wrapper, clickIntent);
            return views;
        }

        private void setBackGroundResource(RemoteViews views, String color) {
            switch (color) {
                case "#1C2226":
                    views.setInt(R.id.widget_wrapper, "setBackgroundResource",
                            R.drawable.bg_note);
                    break;
                case "#F9A825":
                    views.setInt(R.id.widget_wrapper, "setBackgroundResource",
                            R.drawable.bg_note_c1);
                    break;
                case "#2E7D32":
                    views.setInt(R.id.widget_wrapper, "setBackgroundResource",
                            R.drawable.bg_note_c2);
                    break;
                case "#ba5049":
                    views.setInt(R.id.widget_wrapper, "setBackgroundResource",
                            R.drawable.bg_note_c3);
                    break;
                case "#00838F":
                    views.setInt(R.id.widget_wrapper, "setBackgroundResource",
                            R.drawable.bg_note_c4);
                    break;
                case "#6A1B9A":
                    views.setInt(R.id.widget_wrapper, "setBackgroundResource",
                            R.drawable.bg_note_c5);
                    break;
                case "#f08080":
                    views.setInt(R.id.widget_wrapper, "setBackgroundResource",
                            R.drawable.bg_note_c6);
                    break;
            }
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {return 1; }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
