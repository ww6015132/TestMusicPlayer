package fr.julien_dumortier.simplemusicplayer.browsemanager;

import java.util.ArrayList;
import java.util.List;

import fr.julien_dumortier.simplemusicplayer.item.Album;
import fr.julien_dumortier.simplemusicplayer.item.Artist;
import fr.julien_dumortier.simplemusicplayer.item.Genre;
import fr.julien_dumortier.simplemusicplayer.item.Item;
import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.thememanager.DisplayManager2;
import fr.julien_dumortier.simplemusicplayer.R;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.Artists;
import android.provider.MediaStore.Audio.Media;

public class BrowseManager {	
	
	public static final String /*UNKNOW_ALBUM_FORMATED = "Album inconnu",*/ UNKNOW_ALBUM_KEY = "music",
			/*UNKNOW_ARTIST_FORMATED = "Artiste inconnu",*/ UNKNOW_ARTIST_KEY = MediaStore.UNKNOWN_STRING;
	
	public static void browseArtist(final Context	c, final OnBrowseListener listener)  {
		new Thread("browser") { public void run() {
		try {
		String[] projection = new String[] { Artists._ID, Artists.ARTIST, Artists.NUMBER_OF_ALBUMS, Artists.NUMBER_OF_TRACKS };
		String sortOrder = Media.ARTIST + " ASC";

		ArrayList<Item> artists = new ArrayList<Item>();
		int unknowAlbum = 0;
		/*******************************/
        Uri urie = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        
        Cursor cursor = c.getContentResolver().query(urie,
        		projection, null , null, sortOrder);
        
		/*******************************/
		if (cursor.moveToFirst()) {
			do {
				try {
					String id = cursor.getString(0);
					String artist = cursor.getString(1);
					int nbAlbums = cursor.getInt(2);
					int nbTracks = cursor.getInt(3);
					if(artist.equals(MediaStore.UNKNOWN_STRING)) {
						unknowAlbum += nbAlbums;
					} else {
					//	String albumId = BrowseCover.getFirstAlbumIdByArtistName(c, artist);
					//	Uri uri = BrowseCover.getUriFromAlbumId(albumId);
						artists.add(new Artist(c, id, artist, nbTracks, nbAlbums, null, null));
					}

				} catch (Throwable e) {}
			} while (cursor.moveToNext());
		}
		if(unknowAlbum>0) {
			artists.add(0, new Artist(c, "-1", c.getResources().getString(R.string.unknow_artist), -1, unknowAlbum, null, null));
		}
		cursor.close();
		listener.onBrowseArtistFinish(artists);

		} catch (Exception e) {}
		}}.start();
	}
	
	public static void browseAlbum(final Context c, final OnBrowseListener listener)  {	
		new Thread("browser") { public void run() {
		try {
		String[] projection = new String[] { Albums._ID, Albums.ALBUM, Albums.ARTIST,  Albums.NUMBER_OF_SONGS };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = Media.ALBUM + " ASC";
		Cursor cursor = c.getContentResolver().query(Albums.EXTERNAL_CONTENT_URI, 
				projection, selection, selectionArgs, sortOrder);

		ArrayList<Item> artists = new ArrayList<Item>();
		int unknowAlbum = 0;
		if (cursor.moveToFirst()) {
			do {
				try {
					String id = cursor.getString(0);
					String album = cursor.getString(1);
					String artist = cursor.getString(2);
					int nbTracks = cursor.getInt(3);
					if(album.equals("music")) {
						unknowAlbum += nbTracks;
					} else {
						Uri uri = BrowseCover.getUriFromAlbumId(id);
						artists.add(new Album(c, id, album, artist, nbTracks, uri, null));
					}
				} catch (Throwable e) {}
			} while (cursor.moveToNext());
		}
		if(unknowAlbum>0) {
			artists.add(0, new Album(c, "-1", c.getResources().getString(R.string.unknow_album), "", unknowAlbum, null, null));
		}
		cursor.close();
		listener.onBrowseAlbumFinish(artists);
		} catch (Exception e) {}
		}}.start();
	}
	
	public static void browseGenre(final Context context, final OnBrowseListener listener)  {
		new Thread("browser") { public void run() {
			try {
	        String GENRE_ID = MediaStore.Audio.Genres._ID;
	        String GENRE_NAME = MediaStore.Audio.Genres.NAME;
			ArrayList<Item> genres = new ArrayList<Item>();			
	        Cursor cursor = context.getContentResolver().query(
	        		MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
	            new String[] { GENRE_ID, GENRE_NAME },
	            null, null, MediaStore.Audio.Genres.NAME+" ASC");
	        if (cursor.moveToFirst()) {
				do {
					try {
					String id = cursor.getString(0);
					String genreName = cursor.getString(1);
					Cursor c = context.getContentResolver().query(
			                makeGenreUri(id),
			                new String[] { MediaStore.Audio.Media.DATA }, null,
			                null, null);
					int nbTracks = c.getCount();
					c.close();
					if(nbTracks>0 && !genreName.equals(""))
						genres.add(new Genre(context, id, genreName, nbTracks, null, null));
					} catch (Throwable e) {}
				} while (cursor.moveToNext());
			}
			cursor.close();
			listener.onBrowseGenreFinish(genres);
			} catch (Exception e) {}
		}}.start();
	}
	
	public static void browseTrackByGenreId(final Context context, final OnBrowseListener listener, final Item item) {
		new Thread("browser") { public void run() {
			try {
	        ArrayList<Item> items = new ArrayList<Item>();
	            Cursor c = context.getContentResolver().query(
	                makeGenreUri(item.getId()),
	                new String[] {Media._ID, Media.ALBUM, Media.ARTIST, Media.ALBUM_ID, Media.ARTIST_ID, Media.DURATION, Media.TITLE, Media.DATA },
		            null,
		            null, null);
		        if (c.moveToFirst()) {
					do {
						try  {
						String id = c.getString(0);
						String album = c.getString(1);
						String artist = c.getString(2);
						String albumId = c.getString(3);
						String artistId = c.getString(4);
						String duration = c.getString(5);
						String trackName = c.getString(6);
						String data = c.getString(7);
						Uri uri = BrowseCover.getUriFromAlbumId(albumId);
						Long du = (long) 0;
						if(duration != null)
							du = Long.parseLong(duration);
						items.add(new Track(context, id, data, album, albumId, artist, artistId, trackName, du, uri, item));
						} catch (Throwable e) {}
					} while (c.moveToNext());
				}
				c.close();
				listener.onBrowseTrackFinish(items);
			} catch (Exception e) {}
		}}.start();
	}
	
	public static void browseTrackByAlbum(final Context c, final OnBrowseListener listener, final Album album) {
		new Thread("browser") { public void run() {
			try {
			String[] projection = new String[] { Media._ID, Media.ALBUM, Media.ARTIST, Media.ALBUM_ID, Media.ARTIST_ID, Media.DURATION, Media.TITLE, Media.DATA };
			/*String where = android.provider.MediaStore.Audio.Media.IS_MUSIC + "!= 0";
			if(album != null && album.getId() != null && album.getId().equals("-1")) {
				if(album.getArtistName().equals("") || album.getArtistName().equals(c.getResources().getString(R.string.unknow_artist))) { 
					where = android.provider.MediaStore.Audio.Media.ALBUM + " = \""+UNKNOW_ALBUM_KEY+"\"";
					if(album.getArtistName().equals(c.getResources().getString(R.string.unknow_artist)))
						where += "AND " + android.provider.MediaStore.Audio.Media.ARTIST + " = \"" + MediaStore.UNKNOWN_STRING + "\"";
				}
				else
					where = android.provider.MediaStore.Audio.Media.ARTIST + " = \""+album.getArtistName()+"\" AND " + android.provider.MediaStore.Audio.Media.ALBUM + " = \""+UNKNOW_ALBUM_KEY+"\"";
			}
			else if(album != null && album.getId()!=null && !album.getId().equals(""))
				where = android.provider.MediaStore.Audio.Media.ALBUM_ID + " = "+album.getId();*/
			StringBuilder where = new StringBuilder();
	        where.append(MediaStore.Audio.Media.TITLE + " != ''"); 
	        
			if (album != null && album.getId() != null && !album.getId().equals("-1")) {
	        	where.append(" AND " + MediaStore.Audio.Media.ALBUM_ID + "=" + album.getId());
	        }
			
	        if (album != null && album.getArtistName() != null) {
	        	if(album.getArtistName().equals(UNKNOW_ARTIST_KEY))
	        		where.append(" AND " + MediaStore.Audio.Media.ARTIST + "=\"" + UNKNOW_ARTIST_KEY +"\"");
	        	else
	        		where.append(" AND " + MediaStore.Audio.Media.ARTIST + "= \"" + album.getArtistName() +"\"");
	        }
	        where.append(" AND " + MediaStore.Audio.Media.IS_MUSIC + "=1");
	        
			String sortOrder = Media.TRACK + " ASC";
			if(DisplayManager2.getSortType(c) == 1) //1 => sort title
				sortOrder = Media.TITLE + " ASC";
			Cursor cursor = c.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, 
					projection, where.toString(), null, sortOrder);

			ArrayList<Item> tracks = new ArrayList<Item>();
			if (cursor.moveToFirst()) {
				do {
					try {
					String id = cursor.getString(0);
					String albumName = cursor.getString(1);
					String artist = cursor.getString(2);
					String albumId = cursor.getString(3);
					String artistId = cursor.getString(4);
					String duration = cursor.getString(5);
					String trackName = cursor.getString(6);
					String data = cursor.getString(7);
					Uri uri = BrowseCover.getUriFromAlbumId(albumId);
					tracks.add(new Track(c, id, data, albumName, albumId, artist, artistId, trackName, Long.parseLong(duration), uri, album));
					} catch (Throwable e) {}
				} while (cursor.moveToNext());
			}
			cursor.close();
			listener.onBrowseTrackFinish(tracks);
			} catch (Exception e) {}
			}}.start();
	}
	
	public static void browseAlbumByArtistName(final Context c, final OnBrowseListener listener, final Artist artist) {
		new Thread("browser") { public void run() {
			try {
				String[] projection = new String[] { Albums._ID, Albums.ALBUM, Albums.ARTIST, Albums.NUMBER_OF_SONGS };
				String sortOrder = Albums.ALBUM + " ASC";
				ArrayList<Item> artists = new ArrayList<Item>();
				int unknowAlbum = 0;
				
				Uri urie = MediaStore.Audio.Artists.Albums.getContentUri("external", Long.valueOf(artist.getId()));
	            Cursor cursor = null;
	            
	            if(Long.valueOf(artist.getId())>0)
	            	cursor = c.getContentResolver().query( urie, projection, null, null, sortOrder);
	            else 
	            	cursor = c.getContentResolver().query(
	            		    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
	            		    projection, 
	            		    MediaStore.Audio.Media.ARTIST + "=\"" + UNKNOW_ARTIST_KEY+"\"",
	            		    null, 
	            		    sortOrder);
	            
				if (cursor.moveToFirst()) {
					do {
						try {
						String id = cursor.getString(0);
						String album = cursor.getString(1);
						String artistName = cursor.getString(2);
						int nbTracks = cursor.getInt(3);
						if(album.equals(UNKNOW_ALBUM_KEY)) {
							unknowAlbum += nbTracks;
						} else {
							Uri uri = BrowseCover.getUriFromAlbumId(id);
							artists.add(new Album(c, id, album, artistName, nbTracks, uri, artist));
						}
	
						} catch (Throwable e) {}
					} while (cursor.moveToNext());
				}
				if(unknowAlbum>0) {
					artists.add(0, new Album(c, "-1", c.getResources().getString(R.string.unknow_album), artist.getArtistName(), unknowAlbum, null, artist));
				}
				cursor.close();
				listener.onBrowseAlbumFinish(artists);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}}.start();
		
	}
	
	public static void browseTrack(final Context c, boolean b, final OnBrowseListener listener)  {
		if(b) {
			browseTrackByAlbum(c, new OnBrowseListener() {
				public void onTrackBrowsed(ArrayList<Item> tracks) {}
				public void onBrowseGenreFinish(ArrayList<Item> items) {}				
				public void onBrowseArtistFinish(ArrayList<Item> items) {}				
				public void onBrowseAlbumFinish(ArrayList<Item> items) {}
				
				public void onBrowseTrackFinish(final ArrayList<Item> items) {
					items.add(0, new Track(c, "-5", "", "", "", "", "", c.getResources().getString(R.string.play_all), 0, null, null));	
					listener.onBrowseTrackFinish(items);
				}	
			}, null);
		}			
		else
			browseTrackByAlbum(c, listener, null);
	}

	public static void browseTracksByIds(final Context context, final List<String> ids, final OnTrackBrowseListener listener, final Item item) {
		new Thread("browser") { public void run() {
			try{
			ArrayList<Item> tracks = new ArrayList<Item>();
			for(String idTrack: ids) {
				String[] projection = new String[] { Media._ID, Media.ALBUM, Media.ARTIST, Media.ALBUM_ID, Media.ARTIST_ID, Media.DURATION, Media.TITLE, Media.DATA };
				String where = android.provider.MediaStore.Audio.Media._ID + " = \""+idTrack+"\"";
				Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, 
						projection, where, null, null);
				if (cursor.moveToFirst()) {
					do {
						try {
						String id = cursor.getString(0);
						String albumName = cursor.getString(1);
						String artist = cursor.getString(2);
						String albumId = cursor.getString(3);
						String artistId = cursor.getString(4);
						String duration = cursor.getString(5);
						String trackName = cursor.getString(6);
						String data = cursor.getString(7);
						Uri uri = BrowseCover.getUriFromAlbumId(albumId);
						tracks.add(new Track(context, id, data, albumName, albumId, artist, artistId, trackName, Long.parseLong(duration), uri, item));

						} catch (Throwable e) {}
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
			listener.onTrackBrowsed(tracks);

			} catch (Exception e) {}
			}}.start();
	}
	
	

	private static Uri makeGenreUri(String genreId) {
        String CONTENTDIR = MediaStore.Audio.Genres.Members.CONTENT_DIRECTORY;
        return Uri.parse(
            new StringBuilder()
            .append(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI.toString())
            .append("/")
            .append(genreId)
            .append("/")
            .append(CONTENTDIR)
            .toString());
    }
	
	public interface OnTrackBrowseListener {
		public void onTrackBrowsed(ArrayList<Item> tracks);
	}
	
	public interface OnBrowseListener extends OnTrackBrowseListener {
		public void onBrowseAlbumFinish(ArrayList<Item> items);
		public void onBrowseGenreFinish(ArrayList<Item> items);
		public void onBrowseTrackFinish(ArrayList<Item> items);
		public void onBrowseArtistFinish(ArrayList<Item> items);
	}

	public static void browseTrackByArtistName(final Context c, final OnTrackBrowseListener listener, final Artist artist) {
		new Thread("browser") { public void run() {
			try {
			String[] projection = new String[] { Media._ID, Media.ALBUM, Media.ARTIST, Media.ALBUM_ID, Media.ARTIST_ID, Media.DURATION, Media.TITLE, Media.DATA };
			String where = null;
			
			if(!artist.getId().equals(UNKNOW_ARTIST_KEY))
				where = android.provider.MediaStore.Audio.Media.IS_MUSIC + "!= 0 AND " + android.provider.MediaStore.Audio.Media.ARTIST_ID + " = \""+artist.getId()+"\"";	
			else
				where = android.provider.MediaStore.Audio.Media.IS_MUSIC + "!= 0 AND " + android.provider.MediaStore.Audio.Media.ARTIST + " = \""+UNKNOW_ARTIST_KEY+"\"";
			
			String sortOrder = Media.TITLE + " ASC";
			Cursor cursor = c.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, 
					projection, where, null, sortOrder);
			ArrayList<Item> tracks = new ArrayList<Item>();
			if (cursor.moveToFirst()) {
				do {
					try {
					String id = cursor.getString(0);
					String albumName = cursor.getString(1);
					String artist = cursor.getString(2);
					String albumId = cursor.getString(3);
					String artistId = cursor.getString(4);
					String duration = cursor.getString(5);
					String trackName = cursor.getString(6);
					String data = cursor.getString(7);
					Uri uri = BrowseCover.getUriFromAlbumId(albumId);
					tracks.add(new Track(c, id, data, albumName, albumId, artist, artistId, trackName, Long.parseLong(duration), uri, null));

					} catch (Throwable e) {}
				} while (cursor.moveToNext());
			}
			cursor.close();
			listener.onTrackBrowsed(tracks);
			} catch (Exception e) {}
		}}.start();
	}
}