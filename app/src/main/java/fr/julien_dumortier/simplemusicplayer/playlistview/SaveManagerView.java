package fr.julien_dumortier.simplemusicplayer.playlistview;

import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import fr.julien_dumortier.simplemusicplayer.R;

import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptConfirmReplacePlaylist;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptGetTextName;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptPlaylistName;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptConfirmReplacePlaylist.IOnConfirmReplacePlaylist;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptGetTextName.IOnTextSetted;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptPlaylistName.IOnNamePlaylistSelected;

public class SaveManagerView extends AlertDialog.Builder implements IOnNamePlaylistSelected, IOnConfirmReplacePlaylist, IOnTextSetted {
	
	private Context mContext;
	private List<Track> mTracks;
	private String mNamePlaylist;
	
	public SaveManagerView(Context context, List<Track> tracks) {
		super(context);
		mContext = context;
		mTracks = tracks;
		if(SavedPlaylistManager.getAllPlaylistName(context, false).length==0) {
			new PromptGetTextName(mContext, SaveManagerView.this).show();
		} else {
			setTitle(mContext.getResources().getText(R.string.save));
			setMessage(mContext.getResources().getText(R.string.save_replace_or_new));
			setPositiveButton(mContext.getResources().getText(R.string.new_playlist), new OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					new PromptGetTextName(mContext, SaveManagerView.this).show();
				}
			});
			setNegativeButton(mContext.getResources().getText(R.string.replace_playlist), new OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					new PromptPlaylistName(mContext, SaveManagerView.this).show();
				}
			});
			this.show();
		}
	}

	public void onNamePlaylistSelected(String name) {
		mNamePlaylist = name;
		new PromptConfirmReplacePlaylist(mContext, this).show();
	}

	public void onConfirm() {
		SavedPlaylistManager.savePlaylist(mContext, mNamePlaylist, mTracks);
	}

	public void onTextSetted(String name) {
		if(name.length()<3) {
			Toast.makeText(mContext, mContext.getResources().getText(R.string.short_name), Toast.LENGTH_LONG).show();
			new PromptGetTextName(mContext, SaveManagerView.this).show();
		} else if(name.length()>34) {
			Toast.makeText(mContext, mContext.getResources().getText(R.string.long_name), Toast.LENGTH_LONG).show();
			new PromptGetTextName(mContext, SaveManagerView.this).show();
		} else if(name.equals(mContext.getResources().getString(R.string.last_playlist))) {
			Toast.makeText(mContext, mContext.getResources().getText(R.string.default_name), Toast.LENGTH_LONG).show();
			new PromptGetTextName(mContext, SaveManagerView.this).show();
		} else				
			SavedPlaylistManager.savePlaylist(mContext, name, mTracks);
	}
}
