package fr.julien_dumortier.simplemusicplayer.equalizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class EqualizerConfigurationDataBase {

	public static class EqualizerConfigurationNotFoundException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	public static final int INACTIVE_PRESET = -1;
	private static final String BAND_LEVEL = "band_level", ITEM_BAND = "band_level_", IS_ACTIVE ="is_active", PRESET = "PRESET";
	
	public static void setBandLevel(Context c, int band, int level) {
		Editor editor = c.getSharedPreferences(BAND_LEVEL, Context.MODE_PRIVATE).edit();
		editor.putInt(ITEM_BAND+band, level);
		editor.commit();
	}
	
	public static int getBandLevel(Context c, int band, int defaultValue) {
		SharedPreferences sp = c.getSharedPreferences(BAND_LEVEL, Context.MODE_PRIVATE);
		return sp.getInt(ITEM_BAND+band, defaultValue);
	}

	public static int getPreset(Context c) {
		return c.getSharedPreferences(BAND_LEVEL, Context.MODE_PRIVATE).getInt(PRESET, INACTIVE_PRESET);
	}
	
	public static void setPreset(Context c, int posPreset) {
		Editor editor = c.getSharedPreferences(BAND_LEVEL, Context.MODE_PRIVATE).edit();
		editor.putInt(PRESET, posPreset);
		editor.commit();
	}
	
	public static boolean isActiveEqualizer(Context c) {
		return c.getSharedPreferences(BAND_LEVEL, Context.MODE_PRIVATE).getBoolean(IS_ACTIVE, false);
	}
	
	public static void setActiveEqualizer(Context c, boolean b) {
		Editor editor = c.getSharedPreferences(BAND_LEVEL, Context.MODE_PRIVATE).edit();
		editor.putBoolean(IS_ACTIVE, b);
		editor.commit();
	}
}
