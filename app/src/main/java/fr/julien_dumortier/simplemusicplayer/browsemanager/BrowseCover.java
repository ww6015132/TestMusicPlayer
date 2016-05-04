package fr.julien_dumortier.simplemusicplayer.browsemanager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Albums;

public class BrowseCover {
		
	public static Drawable getDrawableFromUri(Context c, Uri uri, Drawable defaultDrawable) {
		Bitmap bmp;
		try {
			bmp = MediaStore.Images.Media.getBitmap(c.getContentResolver(), uri);
		} catch (Throwable e) {
			e.printStackTrace();
			return defaultDrawable;
		}
		if(bmp==null)
			return defaultDrawable;
		try {
	        bmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
			return new BitmapDrawable(c.getResources(), bmp);
		} catch (Throwable e) {
			e.printStackTrace();
			return defaultDrawable;
		}
	}

	public static Uri getUriFromAlbumId(String albumId) {
		if(albumId==null || albumId.equals(""))
			return Uri.parse("");
		return Uri.parse("content://media/external/audio/albumart/"+albumId);
	}
	
	public static String getFirstAlbumIdByArtistName(Context c, String artistId) {
		try {
			String[] projection = new String[] { Albums._ID};			
		    
		    Uri urie = MediaStore.Audio.Artists.Albums.getContentUri("external",
                    Long.valueOf(artistId));
		    Cursor cursor = c.getContentResolver().query( urie,
        		projection, null, null, null);
			String url = "";
			if (cursor.moveToFirst()) {
				do {
					url = cursor.getString(0);
					if(url!=null && !url.equals("")) {
						cursor.close();
						return url;
					}
				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (Exception e) {}
		return "";
	}
}
