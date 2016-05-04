package fr.julien_dumortier.simplemusicplayer.widget;

import java.util.Enumeration;
import java.util.Hashtable;
import fr.julien_dumortier.simplemusicplayer.service.MusicPlayerService;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.res.Configuration;
public class AudioPlayerApplication extends Application {
	
	private static Hashtable<Integer, WidgetView> mWidgetViews = new Hashtable<Integer, WidgetView>();
	private MusicPlayerService mService;
	private AudioPlayerWidgetViewController mAudioPlayerWidgetViewController;
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void onCreate() {
		super.onCreate();
		mAudioPlayerWidgetViewController = new AudioPlayerWidgetViewController(this);
		updateAllWidgets();
	}

	public AudioPlayerWidgetViewController getAudioPlayerWidgetViewController() {
		return mAudioPlayerWidgetViewController;
	}

	public void updateAllWidgets() {
		AppWidgetManager man = AppWidgetManager.getInstance(this);
		int[] ids = man.getAppWidgetIds(new ComponentName(this, AudioPlayerWidgetProvider.class));
		for (int x : ids) {
			createWidget(x);
		}
	}

	public void deleteWidget(int widgetId) {
		mWidgetViews.remove(widgetId);
	}

	public void createWidget(int widgetId) {
		if (!mWidgetViews.containsKey(widgetId)) {
			WidgetView view = new WidgetView(this, widgetId);
			mWidgetViews.put(widgetId, view);
		}
	}

	public WidgetView getView(int widgetId) {
		createWidget(widgetId);
		return mWidgetViews.get(widgetId);
	}

	public Enumeration<WidgetView> getAllViews() {
		return mWidgetViews.elements();
	}

	public void serviceCreated(MusicPlayerService musicPlayerService) {
		mService = musicPlayerService;
	}

	public void serviceDestroyed() {
		mService = null;
	}
	
	public MusicPlayerService getMusicPlayerService() {
		return mService;
	}
	
	public boolean isDisplayable() {
		if(mService==null || mService.getMusicMediaPlayer() == null)
			return false;
		return mService.getMusicMediaPlayer().getCurrentTrack()!=null;
	}
}
