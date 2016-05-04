package fr.julien_dumortier.simplemusicplayer.service.phonestatemanager;

import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneCallStateManager extends PhoneStateListener {

    	private boolean stopPhone;
    	private MusicMediaPlayer mMusicMediaPlayer;
    	
		public PhoneCallStateManager(Context c, MusicMediaPlayer musicMediaPlayer) {
			mMusicMediaPlayer = musicMediaPlayer;
			TelephonyManager mgr = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
			if(mgr != null) {
			    mgr.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
			}
		}
		
		public void onCallStateChanged(int state, String incomingNumber) {
	        if (state == TelephonyManager.CALL_STATE_RINGING) {
	        	stopPhone =mMusicMediaPlayer.isPlaying();
	        	mMusicMediaPlayer.pause(); //appel entrant
	        } else if(state == TelephonyManager.CALL_STATE_IDLE) {
	        	if(stopPhone) {
	        		stopPhone = false;
	        		mMusicMediaPlayer.resume(); //t�l�phone raccroch�
	        	}
	        } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
	        	stopPhone =mMusicMediaPlayer.isPlaying();
	        	mMusicMediaPlayer.pause(); //t�l�phone d�ccroch�
	        }
	        super.onCallStateChanged(state, incomingNumber);
	    }
}
