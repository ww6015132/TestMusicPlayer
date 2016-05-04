package fr.julien_dumortier.simplemusicplayer.item;

import fr.julien_dumortier.simplemusicplayer.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class Album extends Item {
	
	private String mAlbumName, mArtistName;
	private int mNbTrack;
	private static Drawable mDefaultDrawable;
	
	public Album(Context context, String id, String albumName, String artistName, int nbTrack, Uri picture, Item parent) {
		super(context, id, parent, picture);
		mAlbumName = albumName;
		mArtistName = artistName;
		mNbTrack = nbTrack;
		mPicture = getDefaultDrawable();//.getConstantState().newDrawable();
	}
		
	public String getAlbumName() {
		return mAlbumName;
	}

	public String getArtistName() {
		return mArtistName;
	}
	
	public int getNbTrack() {
		return mNbTrack;
	}
	
	public Drawable getDefaultDrawable() {
		try {
			if(mDefaultDrawable==null)
				mDefaultDrawable = mContext.getResources().getDrawable(R.drawable.ic_album);
		}catch(Throwable e) {}
		return mDefaultDrawable;
	}
}
