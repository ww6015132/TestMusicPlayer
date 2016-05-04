package fr.julien_dumortier.simplemusicplayer.item;

import fr.julien_dumortier.simplemusicplayer.R;

import fr.julien_dumortier.simplemusicplayer.browsemanager.BrowseCover;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class Track extends Item {
	private String mAlbumName, mAlbumId, mArtistName, mArtistId, mTrackName, mDataTrack;
	private long mDuration;
	private static Drawable mDefaultDrawable;
	
	public Track(Context context, String id, String dataTrack, String albumName, String albumId, 
			String artistName, String artistId, String trackName, long duration, Uri picture, Item parent) {
		super(context, id, parent, picture);
		mDataTrack = dataTrack;
		mAlbumName = albumName;
		mAlbumId = albumId;
		mArtistName = artistName;
		mArtistId = artistId;
		mTrackName = trackName;
		mDuration = duration;
		mPicture = getDefaultDrawable();//.getConstantState().newDrawable();
	}

	public Track(Track track) {
		this(track.getContext(), track.getId(), track.getDataTrack(), track.getAlbumName(), track.getAlbumId(),
				track.getArtistName(), track.getArtistId(), track.getTrackName(), track.getDuration(),
				track.getStringPicture(), track.getParent());
	}


	public String getDataTrack() {
		return mDataTrack;
	}

	public String getAlbumName() {
		return mAlbumName;
	}

	public String getArtistName() {
		return mArtistName;
	}
	
	public String getAlbumId() {
		return mAlbumId;
	}

	public String getArtistId() {
		return mArtistId;
	}

	public String getTrackName() {
		return mTrackName;
	}
	
	public long getDuration() {
		return mDuration;
	}

	public void initPicture() {
		if(isDefaultPicture && mStringPicture!=null && !mStringPicture.equals("")) {
			Drawable dr = BrowseCover.getDrawableFromUri(mContext, mStringPicture, getDefaultDrawable());
			if(dr!=null)
				mPicture = dr;
			isDefaultPicture=false;
		}
	}

	public Drawable getDefaultDrawable() {
		try {
		if(mDefaultDrawable==null)
			mDefaultDrawable = mContext.getResources().getDrawable(R.drawable.ic_track);
		}catch(Throwable e) {}
		return mDefaultDrawable;
	}
}
