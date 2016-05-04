package fr.julien_dumortier.simplemusicplayer.uiprompt;

import java.net.URL;
import java.net.URLConnection;
import fr.julien_dumortier.simplemusicplayer.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

public class PromptDefineCoverAlbum extends AlertDialog.Builder {

	public interface IOnUrlImagePostedListener {
		public void onPostImageUrl(String url);
		public void onPostBadImageUrl();
	}
	
	private IOnUrlImagePostedListener mListener;
	
	public PromptDefineCoverAlbum(Context context, IOnUrlImagePostedListener listener) {
		super(context);
		mListener = listener;
		setMessage(context.getResources().getText(R.string.enter_url_to_image));
		final EditText edit = new EditText(context);
		setView(edit);
		setPositiveButton(context.getResources().getText(R.string.ok), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				new Thread("validation url thread") { public void run() {
					String url = edit.getText().toString();
					if(validImageUrl(url))
						mListener.onPostImageUrl(url);
					else
						mListener.onPostBadImageUrl();
				}}.start();
			}
		});
		setNegativeButton(context.getResources().getText(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}});
	}
	
	private static boolean validImageUrl(String url) {
		URLConnection connection;
		try {
			connection = new URL(url).openConnection();
			String contentType = connection.getHeaderField("Content-Type");
			return contentType.startsWith("image/");
		} catch (Exception e) {
			return false;
		}
	}
}
