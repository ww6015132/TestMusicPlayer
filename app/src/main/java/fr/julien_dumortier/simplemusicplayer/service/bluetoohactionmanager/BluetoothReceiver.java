package fr.julien_dumortier.simplemusicplayer.service.bluetoohactionmanager;

import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class BluetoothReceiver extends BroadcastReceiver{

	public static MusicMediaPlayer mMusicMediaPlayer;

	
	public void onReceive(Context context, Intent intent) {
	    final KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
	    if (event.getAction() != KeyEvent.ACTION_DOWN) return;
	    if(mMusicMediaPlayer!=null)
	    switch (event.getKeyCode()) {
	        case KeyEvent.KEYCODE_MEDIA_STOP:
	        case KeyEvent.KEYCODE_HEADSETHOOK:
	        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
	        	if(mMusicMediaPlayer.isPlaying())
	        		mMusicMediaPlayer.pause();
	        	else
	        		mMusicMediaPlayer.resume();
	            break;
	        case KeyEvent.KEYCODE_MEDIA_NEXT:
	        	mMusicMediaPlayer.next();
	            break;
	        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
	            mMusicMediaPlayer.last();
	            break;
	    }
	}
}
