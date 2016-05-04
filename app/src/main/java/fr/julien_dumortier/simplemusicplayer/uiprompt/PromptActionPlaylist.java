package fr.julien_dumortier.simplemusicplayer.uiprompt;

import java.util.List;

import fr.julien_dumortier.simplemusicplayer.R;

import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptMoveTrack.IOnNewPosSelected;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptSuppressConfirm.IOnSuppressConfirm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class PromptActionPlaylist extends AlertDialog.Builder implements IOnSuppressConfirm, IOnNewPosSelected {

	private static final int [] ITEM_STRING_ID = {R.string.play_now, 
		R.string.move, R.string.delete};
	private static final int [] ITEM_STRING_ID_MULTI_SELECT = {R.string.delete};	
	private IManagePlaylistItemAction mManager;
	private Track mSelectedTrack;
	private List<Track> mSelectedTracks;
	
	public PromptActionPlaylist(final Context context, Track selectedTrack, final int nbMax, IManagePlaylistItemAction manager) {
		super(context);
		setTitle(context.getResources().getText(R.string.title_action_playlist));
		setIcon(R.drawable.ic_action);
		mSelectedTrack = selectedTrack;
		mManager = manager;
		String [] items = new String[ITEM_STRING_ID.length];
		for(int i=0; i<ITEM_STRING_ID.length; i++)
			items[i] = context.getResources().getText(ITEM_STRING_ID[i]).toString();
		setItems(items, new OnClickListener() {
			public void onClick(DialogInterface arg0, int pos) {
				switch(pos) {
					case 0:
						mManager.playNow(mSelectedTrack);
						break;
					case 1:
						new PromptMoveTrack(context, nbMax, PromptActionPlaylist.this).show();
						break;
					case 2:
						new PromptSuppressConfirm(context, mSelectedTrack, PromptActionPlaylist.this).show();
						break;
				}
			}
		});
	}
	
	public PromptActionPlaylist(final Context context, List<Track> selectedTracks, IManagePlaylistItemAction manager) {
		super(context);
		setTitle(context.getResources().getText(R.string.title_action_playlist));
		setIcon(R.drawable.ic_action);
		mSelectedTracks = selectedTracks;
		mManager = manager;
		String [] items = new String[ITEM_STRING_ID_MULTI_SELECT.length];
		for(int i=0; i<ITEM_STRING_ID_MULTI_SELECT.length; i++)
			items[i] = context.getResources().getText(ITEM_STRING_ID_MULTI_SELECT[i]).toString();
		setItems(items, new OnClickListener() {
			public void onClick(DialogInterface arg0, int pos) {
				switch(pos) {
					case 0:
						new PromptSuppressConfirm(context, mSelectedTracks, PromptActionPlaylist.this).show();
						break;
				}
			}
		});
	}

	public interface IManagePlaylistItemAction {
		public void playNow(Track selectedTrack);
		public void move(Track selectedTrack, int newPos);
		public void delete(Track selectedTrack);
		public void delete(List<Track> mSelectedTracks);
	}

	public void onSuppress() {
		if(mSelectedTrack!=null)
			mManager.delete(mSelectedTrack);
		else if(mSelectedTracks!=null) {
			mManager.delete(mSelectedTracks);
		}
	}

	public void onPositionSelected(int pos) {
		mManager.move(mSelectedTrack, pos);
	}
}
