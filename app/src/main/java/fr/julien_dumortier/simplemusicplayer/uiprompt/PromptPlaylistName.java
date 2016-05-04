package fr.julien_dumortier.simplemusicplayer.uiprompt;

import fr.julien_dumortier.simplemusicplayer.R;

import fr.julien_dumortier.simplemusicplayer.playlistview.SavedPlaylistManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class PromptPlaylistName extends AlertDialog.Builder {

	public PromptPlaylistName(Context context, final IOnNamePlaylistSelected listener) {
		super(context);
		setTitle(context.getResources().getText(R.string.select_playlist));
		final String [] names = SavedPlaylistManager.getAllPlaylistName(context, false);
		setItems(names, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				listener.onNamePlaylistSelected(names[which]);
			}
		});
	}

	public interface IOnNamePlaylistSelected {
		public void onNamePlaylistSelected(String name);
	}
}
