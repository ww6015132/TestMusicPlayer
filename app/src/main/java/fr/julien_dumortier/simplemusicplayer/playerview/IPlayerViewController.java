package fr.julien_dumortier.simplemusicplayer.playerview;

import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer;

public interface IPlayerViewController {	
	void setPercentTime(int percentPlayed);
	void setTimePlayed(long timePlayed, long restTimePlayed);
	void initView(MusicMediaPlayer mediaPlayer);
	void play();
	void pause();
	void setRepeat(int repeatMode);
	void setRandom(boolean b);
	void clearView();
	void stop();
}
