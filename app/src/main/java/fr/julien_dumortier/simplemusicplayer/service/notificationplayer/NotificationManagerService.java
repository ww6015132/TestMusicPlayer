package fr.julien_dumortier.simplemusicplayer.service.notificationplayer;

import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationManagerService extends Service {

	public static MusicMediaPlayer mMusicMediaPlayer;
	
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) { 
		if(intent !=null && mMusicMediaPlayer!=null) {
			String action = intent.getAction();
			if(NotificationPlayer.DELETE.equals(action))
				mMusicMediaPlayer.stop(true);
			if(NotificationPlayer.LAST.equals(action))
				mMusicMediaPlayer.last();
			if(NotificationPlayer.NEXT.equals(action))
				mMusicMediaPlayer.next();
			if(NotificationPlayer.PLAY.equals(action))
				if(mMusicMediaPlayer.isPlaying())
					mMusicMediaPlayer.pause();
				else
					mMusicMediaPlayer.resume();
		}
		return super.onStartCommand(intent, flags, startId);
	}
}
