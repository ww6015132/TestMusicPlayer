package fr.julien_dumortier.simplemusicplayer.service.playlist;

import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer.IToastManager;

public interface IMediaPlayerController {
	
	/** fait un "resume" sur le MediaPlayer appel "play" sur les vues*/
	public void resume();

	public void pause();
	
	public boolean isPlaying();	
	
	public PlayListManager getPlayListManager();
	
	public void playTrack(Track track);		
		
	/** deplace la lecture jusqu'au poucentage de la piste*/
	public void seekToPercent(int percent);
	
	public Track getCurrentTrack();

	public void next();
	
	public void last();
	
	public void setToastManager(IToastManager toastManager);
}
