package fr.julien_dumortier.simplemusicplayer.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import fr.julien_dumortier.simplemusicplayer.R;

public class Genre extends Item {
	private String mGenreName;
	private int mNbAlbums;
	private static Drawable mDefaultDrawable;
	
	public Genre(Context context, String id, String genreName, int nbAlbums, Uri picture, Item parent) {
		super(context, id, parent, picture);
		mGenreName = genreName;
		mNbAlbums = nbAlbums;
		mPicture = getDefaultDrawable();//.getConstantState().newDrawable();
	}
		
	public String getGenreName() {
		return mGenreName;
	}
	
	public int getNbAlbums() {
		return mNbAlbums;
	}	
	
	public Drawable getDefaultDrawable() {
		try{
			if(mDefaultDrawable==null)
				mDefaultDrawable = mContext.getResources().getDrawable(R.drawable.ic_genre);
		}catch(Throwable e) {}
		return mDefaultDrawable;
	}
}
