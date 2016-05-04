package fr.julien_dumortier.simplemusicplayer.playlistview;

import java.util.ArrayList;
import java.util.List;
import fr.julien_dumortier.simplemusicplayer.item.Track;

public class MultiSelectManager {
	private List<Integer> mSelectedTracks;
	private boolean isActiveMultiSelect;
	
	public MultiSelectManager() {
		mSelectedTracks = new ArrayList<Integer>();
	}
	
	public void removeTrack(int pos) {
		mSelectedTracks.remove(Integer.valueOf(pos));
	}
	
	public void addSelectedTrack(int pos) {
		Integer tmp = Integer.valueOf(pos);
		if(!mSelectedTracks.contains(tmp))
			mSelectedTracks.add(tmp);
	}
	
	public void clearSelectedTracks() {
		mSelectedTracks.clear();
	}
	
	public List<Track> getSelectedTracks(List<Track> allTrack) {
		List<Track> selectedTrack = new ArrayList<Track>();
		for(Integer pos:mSelectedTracks)
			selectedTrack.add(allTrack.get(pos));
		return selectedTrack;
	}

	public boolean contains(int pos) {
		return mSelectedTracks.contains(Integer.valueOf(pos));
	}

	public boolean isActiveMultiSelect() {
		return isActiveMultiSelect;
	}
	
	public void setActiveMultiSelect(boolean b) {
		isActiveMultiSelect = b;
	}
}
