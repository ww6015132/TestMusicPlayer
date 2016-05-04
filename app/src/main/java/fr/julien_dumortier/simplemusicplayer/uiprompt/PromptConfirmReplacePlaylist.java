package fr.julien_dumortier.simplemusicplayer.uiprompt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import fr.julien_dumortier.simplemusicplayer.R;

public class PromptConfirmReplacePlaylist extends AlertDialog.Builder {

	public PromptConfirmReplacePlaylist(Context context, final IOnConfirmReplacePlaylist listener) {
		super(context);
		setTitle(context.getResources().getText(R.string.replace_playlist));
		setMessage(context.getResources().getText(R.string.replace_playlist_quest));
		setPositiveButton(context.getResources().getText(R.string.yes), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				listener.onConfirm();
			}
		});
		setNegativeButton(context.getResources().getText(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}});
	}

	public interface IOnConfirmReplacePlaylist {
		public void onConfirm();
	}
}