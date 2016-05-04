package fr.julien_dumortier.simplemusicplayer.service;

import fr.julien_dumortier.simplemusicplayer.service.bluetoohactionmanager.BluetoothReceiver;
import fr.julien_dumortier.simplemusicplayer.service.notificationplayer.NotificationPlayer;
import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer;
import fr.julien_dumortier.simplemusicplayer.widget.AudioPlayerApplication;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.audiofx.Equalizer;
import android.os.IBinder;

public class MusicPlayerService extends Service {
	 private MusicMediaPlayer mMusicMediaPlayer;
	 private BackgroundBinder mBinder;
	 private NotificationPlayer mNotification;
	 private ComponentName mBluetoothReceiverComponent;
	
	 public IBinder onBind(Intent arg0)
	 {
		 return mBinder;
	 }	 
	 
	 public void onCreate()
	 {
		 super.onCreate();
		 mBinder = new BackgroundBinder(this);
		 mMusicMediaPlayer = new MusicMediaPlayer(this);
		 BluetoothReceiver.mMusicMediaPlayer = mMusicMediaPlayer;
		 mBluetoothReceiverComponent = new ComponentName(this, BluetoothReceiver.class);
		 ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).registerMediaButtonEventReceiver(mBluetoothReceiverComponent);
		 ((AudioPlayerApplication)getApplicationContext()).serviceCreated(this);
	 }
	 
	 public int onStartCommand(Intent intent, int flags, int startId)
	 {
		 return super.onStartCommand(intent, flags, startId);
	 }
	 
	 public void onDestroy()
	 {
		 mMusicMediaPlayer.getEqualizer().release();
		 ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).unregisterMediaButtonEventReceiver(mBluetoothReceiverComponent);
		 mMusicMediaPlayer.stop(true);
		 mMusicMediaPlayer = null;
		 ((AudioPlayerApplication)getApplicationContext()).serviceDestroyed();
		 super.onDestroy();
	 }
	 
	 
	 public MusicMediaPlayer getMusicMediaPlayer() {
		 return mMusicMediaPlayer;
	 }

	public void showNotification() {
		mNotification = new NotificationPlayer(this);
	}

	public void dismissNotification() {
		if(mNotification!=null)
			mNotification.dismiss();
		mNotification = null;
	}


	public Equalizer getEqualizer() {
		return mMusicMediaPlayer.getEqualizer();
	}
}
