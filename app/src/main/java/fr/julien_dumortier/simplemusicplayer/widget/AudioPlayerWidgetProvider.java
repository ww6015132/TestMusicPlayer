package fr.julien_dumortier.simplemusicplayer.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class AudioPlayerWidgetProvider extends AppWidgetProvider {
	
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		for (int x : appWidgetIds) {
			((AudioPlayerApplication) context.getApplicationContext()).deleteWidget(x);
		}
	}
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for (int x : appWidgetIds) {
			((AudioPlayerApplication) context.getApplicationContext()).createWidget(x);
		}
	}

	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		AudioPlayerApplication application = ((AudioPlayerApplication) context.getApplicationContext());
		if (intent.getAction().startsWith("fr.julien_dumortier") && application.getMusicPlayerService() != null) {
		//	int widgetId = intent.getIntExtra(WidgetView.WIDGET_ID, 0);
			int function = intent.getIntExtra(WidgetView.FUNCTION, -1);
			switch(function) {
				case WidgetView.ACTION_LAST:
					application.getMusicPlayerService().getMusicMediaPlayer().last();
					break;
				case WidgetView.ACTION_NEXT:
					application.getMusicPlayerService().getMusicMediaPlayer().next();
					break;
			}
		}
	}
}
