package fr.julien_dumortier.simplemusicplayer.uiprompt;

import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.thememanager.DisplayManager2;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptConfirmAddInNew.IOnConfirmAddInNew;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptConfirmDeletePlaylist.IOnConfirmDeletePlaylist;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptDefineCoverAlbum.IOnUrlImagePostedListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

public class PromptActionByType extends AlertDialog.Builder implements IOnConfirmAddInNew, IOnConfirmDeletePlaylist, IOnUrlImagePostedListener {

	private static final int [] ITEM_STRING_ID = {R.string.add_in_list, R.string.add_and_play_now, 
		R.string.add_in_new_and_play ,R.string.delete_playlist};
	private static final int ITEM_SET_COVER_MANUALLY = R.string.set_cover_manually, ITEM_SET_COVER_AUTO = R.string.set_cover_auto;
	private IManageItemAction mManager;
	private Activity mContext;
	
	public PromptActionByType(final Activity context, final boolean isPlaylist, boolean isSettableCoverAlbum, IManageItemAction manager) {
		super(context);
		mContext = context;
		setTitle(context.getResources().getText(R.string.title_action_playlist));
		setIcon(R.drawable.ic_action);
		mManager = manager;
		String [] items = null;
		int nbToAdd = 0;
		if(isSettableCoverAlbum && DisplayManager2.getActiveEditCoverMode(mContext))
			nbToAdd+=2;
		
		if(!isPlaylist) {
			items = new String[ITEM_STRING_ID.length-1+nbToAdd];
			for(int i=0; i<ITEM_STRING_ID.length-1; i++)
				items[i] = context.getResources().getText(ITEM_STRING_ID[i]).toString();
		} else {
			items = new String[ITEM_STRING_ID.length+nbToAdd];
			for(int i=0; i<ITEM_STRING_ID.length; i++)
				items[i] = context.getResources().getText(ITEM_STRING_ID[i]).toString();
		}
		
		if(isSettableCoverAlbum && DisplayManager2.getActiveEditCoverMode(mContext)) {
			items[items.length-2] = context.getResources().getText(ITEM_SET_COVER_MANUALLY).toString();
			items[items.length-1] = context.getResources().getText(ITEM_SET_COVER_AUTO).toString();
		}
		
		setItems(items, new OnClickListener() {
			public void onClick(DialogInterface arg0, int pos) {
				switch(pos) {
					case 0:
						mManager.addInList();
						break;
					case 1:
						mManager.addAndPlayNow();
						break;
					case 2:
						new PromptConfirmAddInNew(context, PromptActionByType.this).show();
						break;
					case 3:
						if(isPlaylist)
							new PromptConfirmDeletePlaylist(context, PromptActionByType.this).show();
						else
							new PromptDefineCoverAlbum(context, PromptActionByType.this).show();
						break;
					case 4:
						if(isPlaylist)
							new PromptDefineCoverAlbum(context, PromptActionByType.this).show();
						else 
							mManager.startDefineCoverAuto();
						break;
					case 5:
						mManager.startDefineCoverAuto();
						break;
				}
			}
		});
	}

	public interface IManageItemAction {
		public void addAndPlayNow();
		public void addInNewAndPlay();
		public void addInList();
		public void defineCover(String url);
		public void startDefineCoverAuto();
		public void deletePlaylist();
	}

	public void onConfirmAddInNew() {
		mManager.addInNewAndPlay();
	}

	public void onConfirmDeletePlaylist() {
		mManager.deletePlaylist();
	}

	public void onPostImageUrl(String url) {
		mManager.defineCover(url);
	}

	public void onPostBadImageUrl() {
		mContext.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getContext(), getContext().getResources().getString(R.string.bad_url_to_image), Toast.LENGTH_LONG).show();
				new PromptDefineCoverAlbum(getContext(), PromptActionByType.this).show();
			}
		});
	}
}
