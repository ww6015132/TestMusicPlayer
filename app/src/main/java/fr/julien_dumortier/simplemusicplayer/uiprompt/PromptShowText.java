package fr.julien_dumortier.simplemusicplayer.uiprompt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import fr.julien_dumortier.simplemusicplayer.R;

public class PromptShowText extends AlertDialog.Builder {

	public PromptShowText(Context context, String title, String message) {
		super(context);
		setTitle(title);
		setMessage(message);
		setPositiveButton(context.getResources().getText(R.string.ok), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	}
	
	public PromptShowText(Context context, String title, String message, String textButton) {
		super(context);
		setTitle(title);
		setMessage(message);
		setPositiveButton(textButton, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	}

	public PromptShowText setSecondButtonLink(final String title, final String link) {
		setNegativeButton(title, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
				PromptShowText.this.getContext().startActivity(browserIntent);
			}
		});
		return this;
	}
}