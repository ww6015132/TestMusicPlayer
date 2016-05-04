package fr.julien_dumortier.simplemusicplayer.widget;

import fr.julien_dumortier.simplemusicplayer.MainActivity;
import fr.julien_dumortier.simplemusicplayer.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RemoteViews;

public class WidgetView {

	public static final String WIDGET_ID = "widgetId", FUNCTION = "function", 
			INTENT_ON_CLICK_FORMAT = "fr.julien_dumortier.simplemusicplayer.widget.id.%d.click";
	public static final int ACTION_NEXT = 1, ACTION_LAST = 2;
	
	private int mWidgetId;
	private boolean mButtonVisible;
	
	public WidgetView(Context context, int widgetId) {
		mWidgetId = widgetId;
		drawWidget(context, null);
	}
	
	public void drawWidget(Context context, Bitmap bitmap) {
		mButtonVisible = ((AudioPlayerApplication)context.getApplicationContext()).isDisplayable();
		RemoteViews rviews = new RemoteViews(context.getPackageName(), R.layout.audio_player_widget_v2);
		rviews.setOnClickPendingIntent(R.id.fronground_widget, PendingIntent.getActivity(context, 17, new Intent(context, MainActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_CANCEL_CURRENT));
		if(mButtonVisible) {
			rviews.setViewVisibility(R.id.last_widget, View.VISIBLE);
			rviews.setViewVisibility(R.id.next_widget, View.VISIBLE);
			rviews.setOnClickPendingIntent(R.id.last_widget, prepareIntent(ACTION_LAST, context));
			rviews.setOnClickPendingIntent(R.id.next_widget, prepareIntent(ACTION_NEXT, context));
		} else {
			rviews.setViewVisibility(R.id.last_widget, View.INVISIBLE);
			rviews.setViewVisibility(R.id.next_widget, View.INVISIBLE);
		}
		if(bitmap!=null) {
			try {
				rviews.setImageViewBitmap(R.id.fronground_widget, combineImages(bitmap, BitmapFactory.decodeResource(context.getResources(), R.drawable.frontground_widget)));
			} catch(OutOfMemoryError e) {
				rviews.setImageViewResource(R.id.fronground_widget, R.drawable.frontground_widget);
			}
		}else 
			rviews.setImageViewResource(R.id.fronground_widget, R.drawable.frontground_widget);
		AppWidgetManager.getInstance(context).updateAppWidget(mWidgetId, rviews);
	}
	
	private PendingIntent prepareIntent(int action, Context context) {
		Intent intent = new Intent(String.format(INTENT_ON_CLICK_FORMAT, mWidgetId));
		intent.setClass(context, AudioPlayerWidgetProvider.class);
		intent.putExtra(WIDGET_ID, mWidgetId);
		intent.putExtra(FUNCTION, action);
		return PendingIntent.getBroadcast(context, action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	public void switchVisibility(Context context, boolean visible, Bitmap bitmap) {
		mButtonVisible = visible;
		drawWidget(context, bitmap);
	}
	
	public Bitmap combineImages(Bitmap cover, Bitmap frontground) throws OutOfMemoryError { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom 
			Bitmap bitmap = Bitmap.createBitmap(frontground.getWidth(), frontground.getHeight(), Config.ARGB_8888);
			
	        Canvas c = new Canvas(bitmap);
	
	
	        Drawable drawable1 = new BitmapDrawable(cover);
	        Drawable drawable2 = new BitmapDrawable(frontground);
	
	        int marge = getMargin(frontground.getWidth());
	        
	        drawable1.setBounds(marge, marge, frontground.getWidth()-marge, frontground.getHeight()-marge);
	        drawable2.setBounds(0, 0, frontground.getWidth(), frontground.getHeight());
	        drawable1.draw(c);
	        drawable2.draw(c);
	        return bitmap;
	} 
	
	public int getMargin(int origin) {
		return (int)((origin*9.43f)/100);
	}
}
