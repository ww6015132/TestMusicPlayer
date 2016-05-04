package fr.julien_dumortier.simplemusicplayer.uiprompt;

import fr.julien_dumortier.simplemusicplayer.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class PromptConfirmDeletePlaylist  extends AlertDialog.Builder {

	public PromptConfirmDeletePlaylist(Context context, final IOnConfirmDeletePlaylist listener) {
		super(context);
		setTitle(context.getResources().getText(R.string.delete_playlist));
		setMessage(context.getResources().getText(R.string.delete_playlist_quest));
		setIcon(R.drawable.ic_menu_delete);
		setPositiveButton(context.getResources().getText(R.string.yes), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				listener.onConfirmDeletePlaylist();
			}
		});
		setNegativeButton(context.getResources().getText(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	}

	public interface IOnConfirmDeletePlaylist{
		public void onConfirmDeletePlaylist();
	}
}
