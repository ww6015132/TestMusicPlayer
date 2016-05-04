package fr.julien_dumortier.simplemusicplayer.service.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.playlistview.SavedPlaylistManager;
import fr.julien_dumortier.simplemusicplayer.thememanager.DisplayManager2;

public class PlayListManager {

	public static final int NO_REPEAT = 0, REPEAT_ALL = 1, REPEAT_ONE = 2;
	private List<Track> mTracks;
	private List<Integer> mPositionTrack;
	private boolean isRandom;
	private int isRepeat;//0 => pas de rŽpŽtition, 1 => toutes, 2 => juste la courante
	private MusicMediaPlayer mMusicMediaPlayer;
	
	public PlayListManager(MusicMediaPlayer musicMediaPlayer) {
		mTracks = new ArrayList<Track>();
		mPositionTrack = new ArrayList<Integer>();
		mMusicMediaPlayer = musicMediaPlayer;
	}
	
	private Track getNextTrackByPos(int realPos) {
		try {
			int posCurrentTrack = mPositionTrack.indexOf(Integer.valueOf(realPos));
			if(posCurrentTrack!=-1 && posCurrentTrack+1 < mPositionTrack.size()) {
				return mTracks.get(mPositionTrack.get(posCurrentTrack+1));
			}
		} catch(Exception e) {}
		return null;
	}
	
	private Track getPreviousTrackByPos(int realPos) {
		try {
			int posCurrentTrack = mPositionTrack.indexOf(Integer.valueOf(realPos));
			if(posCurrentTrack!=-1 && posCurrentTrack-1 < mPositionTrack.size() && posCurrentTrack > 0) {
				return mTracks.get(mPositionTrack.get(posCurrentTrack-1));
			}
		} catch(Exception e) {}
		return null;
	}
	
	public Track getNextTrack(Track track) throws EndOfPlayListException {
		try {
			//rŽpŽter un seul
			if(isRepeat == REPEAT_ONE)
				return track;
			
			Track nextTrack = getNextTrackByPos(mTracks.indexOf(track));
			if(nextTrack!=null)
				return nextTrack;
			
			//rŽpŽter tous
			if(isRepeat == REPEAT_ALL && mTracks.size()>0)
				return mTracks.get(mPositionTrack.get(0)); //retourne le premier

		} catch(Exception e) {}
		throw new EndOfPlayListException();
	}
	
	public Track getPreviousTrack(Track track) throws EndOfPlayListException {
		try {
			//rŽpŽter un seul
			if(isRepeat == REPEAT_ONE)
				return track;
			
			Track previousTrack = getPreviousTrackByPos(mTracks.indexOf(track));
			if(previousTrack!=null)
				return previousTrack;
	
			//repeter tous
			if(isRepeat == REPEAT_ALL && mTracks.size()>0)
				return mTracks.get(mTracks.size()-1); //retourne le dernier

		} catch(Exception e) {}
		throw new EndOfPlayListException();
	}
	
	/** @return toutes les piste de la playlist */
	public List<Track> getAllTracks() {
		return mTracks;
	}
	
	/** ajoute une piste et la li immediatement */
	public void addTrackAndRun(Track track) {
		Track tmp = addTrack(track);
		if(tmp!=null)
			mMusicMediaPlayer.playTrack(tmp);
	}
	
	/** supprime toutes les pistes de la playliste et arrete la lecture 
	 * @param withClearView */
	public void removeAllTracks(boolean withClearView) {
		if(withClearView && mTracks.size()>0 && DisplayManager2.getActiveLastPlaylist(mMusicMediaPlayer.mContext)) {
			SavedPlaylistManager.saveLastPlayList(mMusicMediaPlayer.mContext, mTracks);
		}
		mMusicMediaPlayer.stop(withClearView);
		mTracks = new ArrayList<Track>();
		mPositionTrack = new ArrayList<Integer>();
	}
	
	/** supprime une piste de la liste de lecture */
	public void removeTrack(Track track) {
		if(mMusicMediaPlayer.getCurrentTrack()==track) {
			try {
				mMusicMediaPlayer.playTrack(getNextTrack(track));
			} catch (EndOfPlayListException e) {
				mMusicMediaPlayer.stop(true);
			    if(mMusicMediaPlayer.mToastManager!=null)
			    	mMusicMediaPlayer.mToastManager.showMessage(mMusicMediaPlayer.mContext.getResources().getText(R.string.end_playlist).toString());
			}
		}
		int pos = mTracks.indexOf(track);
		mTracks.remove(track);
		mPositionTrack.remove(Integer.valueOf(pos));
	}
	
	/** ajoute une piste a la liste */
	public Track addTrack(Track track) {
		boolean isEmpty = mTracks.size()==0;
		if(mTracks.contains(track))
			track = new Track(track);
		mTracks.add(track);
		updateRandom();
		if(isEmpty) {
			mMusicMediaPlayer.playTrack(track);
			return null;
		} else
			return track;
	}
	
	/** ajoute plusieurs pistes ˆ la liste */
	public boolean addTracks(List<Track> tracks) {
		boolean isEmpty = mTracks.size()==0;
		mTracks.addAll(tracks);
		updateRandom();
		if(isEmpty) {
			if(mTracks.size()>0)
				mMusicMediaPlayer.playTrack(mTracks.get(0));
		}
		return isEmpty;
	}
	
	
	public void setRepeatMode(int repeat) {
		if(repeat>REPEAT_ONE) {
			repeat=NO_REPEAT;
		}
		isRepeat = repeat;
		mMusicMediaPlayer.refreshRepeatViews(repeat);
	}
	
	public int getRepeatMode() {
		return isRepeat;
	}
	
	public void setRandom(boolean random) {
		isRandom = random;
		mMusicMediaPlayer.refreshRandomViews(isRandom);
		updateRandom();
	}
	
	public void updateRandom() {
		if(isRandom)
			activeRandom();
		else 
			disableRandom();
	}
	
	private void disableRandom() {
		List<Integer> pos = new ArrayList<Integer>();
		for(int i = 0; i<mTracks.size(); i++)
			pos.add(i);
		mPositionTrack.clear();
		mPositionTrack=pos;
	}
	
	private void activeRandom() {
		List<Integer> pos = new ArrayList<Integer>();
		for(int i = 0; i<mTracks.size(); i++)
			pos.add(i);
		Collections.shuffle(pos);
		mPositionTrack.clear();
		mPositionTrack=pos;
	}
	
	public boolean isRandomMode() {
		return isRandom;
	}

	public void moveTrack(Track selectedTrack, int newPos) {
		if(mTracks.remove(selectedTrack)) {
			try {
				mTracks.add(newPos, selectedTrack);
			} catch(IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
	}

	public void removeTracks(List<Track> selectedTracks) {
		for(Track track:selectedTracks)
			removeTrack(track);
	}
}
