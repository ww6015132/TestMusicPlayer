package fr.julien_dumortier.simplemusicplayer.playlistview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import fr.julien_dumortier.simplemusicplayer.R;

import fr.julien_dumortier.simplemusicplayer.item.Track;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SavedPlaylistManager {

	private static String BASE = "simpleMusicPlaylist", PLAYLIST_NAMES_LIST = "playlist_names_base_save_sauvegarde", 
			LAST_PLAYLIST = "last_playlist_base_sauvegarde_save", LAST_PLAYLIST_EMPTY = "last_playlist_is_empty";
	
	public static String[] getAllPlaylistName(Context context, boolean addLastPlaylist) {
		SharedPreferences sp = context.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		Set<String> empty = new HashSet<String>();
		Set<String> set = sp.getStringSet(PLAYLIST_NAMES_LIST, empty);
		Object [] tab  = set.toArray();
		String st[] = null;
		if(addLastPlaylist && !lastPlaylistIsEmpty(context)) {
			st = new String [tab.length+1];
			st[0] = context.getResources().getString(R.string.last_playlist);
			for(int i =1; i<tab.length+1;i++)
				st[i]= tab[i-1].toString();
		} else {
			st = new String [tab.length];
			for(int i =0; i<tab.length;i++)
				st[i]= tab[i].toString();
		}
		
		return st; 
	}

	public static String[] getTracksIdByPlaylistName(Context context, String namePlaylist) {
		SharedPreferences sp = context.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		String[] elems = sp.getString(namePlaylist, "").split("--");		
		return elems;
	}
	
	public static void savePlaylist(Context context, String namePlaylist, List<Track> tracks) {
		addPlaylistName(context, namePlaylist);
		String ids = "";
		for(Track track:tracks)
			ids += track.getId()+"--";
		SharedPreferences sp = context.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(namePlaylist, ids);
		editor.commit();
	}	
	
	public static void addPlaylistName(Context context, String newName) {
		SharedPreferences sp = context.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		Set<String> empty = new HashSet<String>();
		Set<String> set = sp.getStringSet(PLAYLIST_NAMES_LIST, empty);
		set.remove(newName);
		set.add(newName);
		Editor editor = sp.edit();
		editor.putStringSet(PLAYLIST_NAMES_LIST, set);
		editor.commit();
	}
	
	public static void removePlaylistByName(Context context, String name) {
		SharedPreferences sp = context.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		Set<String> empty = new HashSet<String>();
		Set<String> set = sp.getStringSet(PLAYLIST_NAMES_LIST, empty);
		set.remove(name);
		Editor editor = sp.edit();
		editor.putStringSet(PLAYLIST_NAMES_LIST, set);
		editor.commit();
		editor.remove(name);
		editor.commit();
	}
	
	public static void saveLastPlayList(Context context, List<Track> tracks) {
		Editor editor = context.getSharedPreferences(BASE, Context.MODE_PRIVATE).edit();
		editor.putBoolean(LAST_PLAYLIST_EMPTY, false);
		editor.commit();
		String ids = "";
		for(Track track:tracks)
			ids += track.getId()+"--";
		editor.putString(LAST_PLAYLIST, ids);
		editor.commit();
	}
	
	public static String[] getTracksIdInLastPlayList(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		String[] elems = sp.getString(LAST_PLAYLIST, "").split("--");		
		return elems;
	}
	
	private static boolean lastPlaylistIsEmpty(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		return sp.getBoolean(LAST_PLAYLIST_EMPTY, true);
	}

	public static void removeLastPlaylist(Context context) {
		Editor editor = context.getSharedPreferences(BASE, Context.MODE_PRIVATE).edit();
		editor.putBoolean(LAST_PLAYLIST_EMPTY, true);
		editor.commit();
		editor.putString(LAST_PLAYLIST, "");
		editor.commit();
	}
}
