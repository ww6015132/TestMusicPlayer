package fr.julien_dumortier.simplemusicplayer.item;

import fr.julien_dumortier.simplemusicplayer.R;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class Playlist extends Item {

	private String mName;
	private static Drawable mDefaultDrawable;
	
	public Playlist(Context context, String playlistName) {
		super(context, "-80", null, null);
		mName = playlistName;
		mPicture = getDefaultDrawable();//.getConstantState().newDrawable();
		isDefaultPicture = false;
	}
	
	public String getPlayListName() {
		return mName;
	}
	
	public boolean isLastPlaylist() {
		return mName.equals(mContext.getResources().getString(R.string.last_playlist));
	}
	

	public Drawable getDefaultDrawable() {
		try {
			if(mDefaultDrawable==null)
				mDefaultDrawable = mContext.getResources().getDrawable(R.drawable.ic_playlist);
		}catch(Throwable e) {}
		return mDefaultDrawable;
	}
}
