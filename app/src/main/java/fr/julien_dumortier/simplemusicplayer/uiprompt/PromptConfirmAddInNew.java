package fr.julien_dumortier.simplemusicplayer.uiprompt;

import fr.julien_dumortier.simplemusicplayer.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class PromptConfirmAddInNew  extends AlertDialog.Builder {

	public PromptConfirmAddInNew(Context context, final IOnConfirmAddInNew listener) {
		super(context);
		setTitle(context.getResources().getText(R.string.add_in_new_and_play));
		setMessage(context.getResources().getText(R.string.add_in_new_and_play_quest));
		setIcon(R.drawable.ic_menu_delete);
		setPositiveButton(context.getResources().getText(R.string.yes), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				listener.onConfirmAddInNew();
			}
		});
		setNegativeButton(context.getResources().getText(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	}

	public interface IOnConfirmAddInNew {
		public void onConfirmAddInNew();
	}
}
