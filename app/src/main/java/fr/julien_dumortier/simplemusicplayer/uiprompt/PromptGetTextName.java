package fr.julien_dumortier.simplemusicplayer.uiprompt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import fr.julien_dumortier.simplemusicplayer.R;

public class PromptGetTextName extends AlertDialog.Builder {

	public PromptGetTextName(Context context, final IOnTextSetted listener) {
		super(context);
		setMessage(context.getResources().getText(R.string.enter_playlist_name));
		final EditText edit = new EditText(context);
		setView(edit);
		setPositiveButton(context.getResources().getText(R.string.yes), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				listener.onTextSetted(edit.getText().toString());
			}
		});
		setNegativeButton(context.getResources().getText(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}});
	}

	public interface IOnTextSetted {
		public void onTextSetted(String name);
	}
}