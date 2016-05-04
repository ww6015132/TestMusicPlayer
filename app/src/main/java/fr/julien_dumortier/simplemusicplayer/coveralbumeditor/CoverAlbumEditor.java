package fr.julien_dumortier.simplemusicplayer.coveralbumeditor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import fr.julien_dumortier.simplemusicplayer.item.Album;
import fr.julien_dumortier.simplemusicplayer.item.Item;
import fr.julien_dumortier.simplemusicplayer.item.Track;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class CoverAlbumEditor {

	public interface IOnUpdateFinishListener {
		public void onCoverUpdate(boolean success);
	}
	
	public static void startUpdateCoverAlbum(final Context context, final String pictureUrl, final Item item, final IOnUpdateFinishListener listener) {
		new Thread("update cover thread") {
			public void run() {
				try {
					listener.onCoverUpdate(updateCoverAlbum(context, pictureUrl, item));
				} catch (Exception e) {
					listener.onCoverUpdate(false);
				}
			}
		}.start();
	}
	
	public static boolean updateCoverAlbum(Context context, Bitmap bmp, Item item) throws NumberFormatException, IOException {
		

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		
		if(item instanceof Album && byteArray!=null)	
			return updateArtworkCoverAlbumInDb(context, byteArray, Long.parseLong(((Album)item).getId()));
		else if(item instanceof Track && byteArray!=null) {
			return setID3ArtworkCoverAlbum(byteArray, ((Track)item).getDataTrack()) &&
			updateArtworkCoverAlbumInDb(context, byteArray, Long.parseLong(((Track)item).getAlbumId()));
		}
		return false;
	}
	
	public static boolean updateCoverAlbum(Context context, String pictureUrl, Item item) throws Exception {
		
		byte[] byteArray = null;
		
		if(item instanceof Album || item instanceof Track)
			byteArray = downloadUrlToByteArray(pictureUrl); 
		
		if(item instanceof Album && byteArray!=null)	
			return updateArtworkCoverAlbumInDb(context, byteArray, Long.parseLong(((Album)item).getId()));
		else if(item instanceof Track && byteArray!=null) {
			return setID3ArtworkCoverAlbum(byteArray, ((Track)item).getDataTrack()) &&
			updateArtworkCoverAlbumInDb(context, byteArray, Long.parseLong(((Track)item).getAlbumId()));
		}
		return false;
	}
	
	private static boolean setID3ArtworkCoverAlbum(byte[] picture, String filePath) {
			try {
				//set id3 tag
				File file = new File(filePath);
				MP3File f = (MP3File) AudioFileIO.read(file);		
				ID3v23Tag tag = new ID3v23Tag();
		        f.getTag().addField(tag.createArtworkField(picture, "image/png"));		        
		        f.commit();     				
		        return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	}
		
	private static boolean updateArtworkCoverAlbumInDb(Context context, byte[] picture, long albumId) throws IOException {
			//update media player db
			Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
			Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
			ContentResolver res = context.getContentResolver();
			OutputStream in = res.openOutputStream(uri);
			in.write(picture);
			in.close();
			return true;
	}
		
	public static byte[] downloadUrlToByteArray(String url) throws Exception {
		try {
		URL toDownload = new URL(url);
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    byte[] chunk = new byte[4096];
	    int bytesRead;
	    InputStream stream = toDownload.openStream();

	    while ((bytesRead = stream.read(chunk)) > 0) {
	        outputStream.write(chunk, 0, bytesRead);
	    }
	    byte [] data = outputStream.toByteArray();
	    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	    if (bitmap !=null) {
	        int h = bitmap.getHeight();
	        int w = bitmap.getWidth();
	        if(h>500||w>500) {
		        h = 500*h/w;
		        w = 500;
	        }
	        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
	    }
	    ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
	    bitmap.compress(Bitmap.CompressFormat.PNG, 100, streamOut);
	    return streamOut.toByteArray();
		} catch(OutOfMemoryError e) {
			throw new Exception("out of memory");
		}
	}
}
