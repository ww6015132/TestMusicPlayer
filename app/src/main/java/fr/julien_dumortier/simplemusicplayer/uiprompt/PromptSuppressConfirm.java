package fr.julien_dumortier.simplemusicplayer.uiprompt;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import fr.julien_dumortier.simplemusicplayer.R;

import fr.julien_dumortier.simplemusicplayer.item.Track;
public class PromptSuppressConfirm  extends AlertDialog.Builder {

	public PromptSuppressConfirm(Context context, final Track selectedTrack, final IOnSuppressConfirm listener) {
		super(context);
		setTitle(context.getResources().getText(R.string.delete_title));
		setMessage(context.getResources().getText(R.string.delete_track_from_playlist));
		setIcon(R.drawable.ic_menu_delete);
		setPositiveButton(context.getResources().getText(R.string.yes), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				listener.onSuppress();
			}
		});
		setNegativeButton(context.getResources().getText(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	}

	public PromptSuppressConfirm(Context context,  final IOnSuppressConfirm listener) {
		super(context);
		setTitle(context.getResources().getText(R.string.delete_title));
		setMessage(context.getResources().getText(R.string.delete_all_track_from_playlist));
		setIcon(R.drawable.ic_menu_delete);
		setPositiveButton(context.getResources().getText(R.string.yes), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				listener.onSuppress();
			}
		});
		setNegativeButton(context.getResources().getText(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	}

	public PromptSuppressConfirm(Context context, List<Track> mSelectedTracks, final IOnSuppressConfirm listener) {
		super(context);
		setTitle(context.getResources().getText(R.string.delete_title));
		setMessage(context.getResources().getText(R.string.delete_tracks_from_playlist));
		setIcon(R.drawable.ic_menu_delete);
		setPositiveButton(context.getResources().getText(R.string.yes), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				listener.onSuppress();
			}
		});
		setNegativeButton(context.getResources().getText(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	}

	public interface IOnSuppressConfirm {
		public void onSuppress();
	}
}
