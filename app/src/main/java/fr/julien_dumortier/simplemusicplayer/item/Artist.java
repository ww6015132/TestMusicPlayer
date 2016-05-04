package fr.julien_dumortier.simplemusicplayer.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import fr.julien_dumortier.simplemusicplayer.R;

public class Artist extends Item{

	private String mArtistName;
	private int mNbTrack, mNbAlbums;
	private static Drawable mDefaultDrawable;
	
	public Artist(Context context, String id, String artistName, int nbTrack, int nbAlbums, Uri picture, Item parent) {
		super(context, id, parent, picture);
		mArtistName = artistName;
		mNbTrack = nbTrack;
		mNbAlbums = nbAlbums;
		mPicture = getDefaultDrawable();//.getConstantState().newDrawable();
	}
		
	public String getArtistName() {
		return mArtistName;
	}
	
	public int getNbTrack() {
		return mNbTrack;
	}
	
	public int getNbAlbums() {
		return mNbAlbums;
	}

	public void setUri(Uri uri) {
		mStringPicture = uri;
	}

	public Drawable getDefaultDrawable() {
		try {
			if(mDefaultDrawable==null)
				mDefaultDrawable = mContext.getResources().getDrawable(R.drawable.ic_artist);
		}catch(Throwable e) {}
		return mDefaultDrawable;
	}
}
