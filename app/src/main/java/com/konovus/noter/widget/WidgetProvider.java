package com.konovus.noter.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.konovus.noter.R;
import com.konovus.noter.activity.NewNoteActivity;
import com.konovus.noter.activity.NewNoteWidgetActivity;

public class WidgetProvider extends AppWidgetProvider {
    public static final String ACTION_CLICK = "actionClick";
    public static final String EXTRA_ITEM_POSITION = "itemPosition";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds){
            Intent intent = new Intent(context, NewNoteWidgetActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            Intent serviceIntent = new Intent(context, WidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            Intent clickIntent = new Intent(context, WidgetProvider.class);
            clickIntent.setAction(ACTION_CLICK);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, clickIntent, 0);


            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);
            views.setOnClickPendingIntent(R.id.textView_widget, pendingIntent);
            views.setRemoteAdapter(R.id.listView, serviceIntent);
            views.setEmptyView(R.id.listView, R.id.empty_view);

            // for click action on each list item
            views.setPendingIntentTemplate(R.id.listView, pendingIntent1);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(ACTION_CLICK.equals(intent.getAction())){
            Intent editIntent = new Intent(context, NewNoteActivity.class);
            editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(intent.getSerializableExtra("note") != null)
                editIntent.putExtra("note", intent.getSerializableExtra("note"));
            editIntent.putExtra("fromWidget", intent.getBooleanExtra("fromWidget", false));
            editIntent.putExtra("pos", intent.getIntExtra("pos", 0));
            context.startActivity(editIntent);
        }
    }
}
