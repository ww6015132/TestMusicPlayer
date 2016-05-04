package fr.julien_dumortier.simplemusicplayer.uiprompt;

import fr.julien_dumortier.simplemusicplayer.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.NumberPicker;

public class PromptMoveTrack extends AlertDialog.Builder {

	
	private NumberPicker mNumberPicker;

	public PromptMoveTrack(final Context context, int nbMax, final IOnNewPosSelected listener) {
		super(context);
		setMessage(context.getResources().getText(R.string.new_pos));
		mNumberPicker = new NumberPicker(context);
		mNumberPicker.setMinValue(1);
		mNumberPicker.setMaxValue(nbMax);
		setView(mNumberPicker);
		setPositiveButton(context.getResources().getText(R.string.ok), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				listener.onPositionSelected(mNumberPicker.getValue()-1);
			}
		});
		setNegativeButton(context.getResources().getText(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	}

	public interface IOnNewPosSelected {
		public void onPositionSelected(int pos);
	}
}
