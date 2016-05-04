package fr.julien_dumortier.simplemusicplayer.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import fr.julien_dumortier.simplemusicplayer.R;

public abstract class Item {
	private String mId;
	private Item mParentItem;
	protected Context mContext;
	protected Drawable mPicture;
	public Uri mStringPicture;
	public boolean isDefaultPicture;
	
	public Item(Context c, String id, Item parentItem, Uri picture) {
		mId = id;
		mParentItem = parentItem;
		mContext = c;
		mStringPicture = picture;
		isDefaultPicture = true;
	}

	public abstract Drawable getDefaultDrawable();
	
	public String getId() {
		return mId;
	}

	public Item getParent() {
		return mParentItem;
	}
			
	public final Drawable getPicture() {
	//	initPicture(listener, posFlag);
		return mPicture;
	}
//	
//	private void initPicture(final IOnPictureReady listener, final int posFlag) {
//		if(isDefaultPicture && mStringPicture!=null && !mStringPicture.equals(""))
//		{
//			new Thread("browse") { public void run(){			
//					mPicture = BrowseCover.getDrawableFromUri(mContext, mStringPicture);
//					listener.onPictureReady(mPicture, posFlag);
//					isDefaultPicture=false;
//			}}.start();
//		}
//	}
	
	public final Drawable getHdPicture() {
		Bitmap bmp;
		try {
			bmp = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mStringPicture);
		} catch (Exception e) {
			e.printStackTrace();
			return mContext.getResources().getDrawable(R.drawable.ic_track_hd);
		}
		return new BitmapDrawable(mContext.getResources(), bmp);
	}
	
	protected Context getContext() {
		return mContext;
	}
	
	protected Uri getStringPicture() {
		return mStringPicture;
	}

	public void setPicture(Drawable drawableFromUri) {
		mPicture = drawableFromUri;
	}
	
	public interface IOnPictureReady {
		public void onPictureReady(Drawable mPicture, int posFlag);
	}
}
