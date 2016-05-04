package fr.julien_dumortier.simplemusicplayer.thememanager;

import fr.julien_dumortier.simplemusicplayer.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

public class DisplayManager2 {

	private static String BASE = "THEME_MANAGER", THEME_COLOR = "THEME_COLOR", NB_COLUMNS = "NB_COLUMNS",
			ACTIVE_LAST_PLAYLIST = "ACTIVE_LAST_PLAYLIST", ACTIVE_COLOR_ALTERNATE = "ACTIVE_COLOR_ALTERNATE", 
			ACTIVE_EDIT_COVER_MODE = "ACTIVE_EDIT_COVER_MODE", ACTIVE_DARK_THEME = "ACTIVE_DARK_THEME", 
			SORT_TYPE = "SORT_TYPE";
	private static final String CLASSIC_THEME = null;
	
/*				
		 * 0: classique
		 * 1: bleu-vert
		 * 2: orange
		 * 3: rouge
		 * 4: vert-pomme
		 * 5: violet
*/	
	public static final String START_THEME_COLOR[] = {
		CLASSIC_THEME,
		"#1AAEBC",
		"#F4A614",
		"#A51515",
		"#9AD214",
		"#8B3EFF"
	};

	public static final String END_THEME_COLOR[] = {
		CLASSIC_THEME,
		"#0195A3",
		"#F29210",
		"#911111",
		"#86BE00",
		"#7225E6"
	};
		
	public static final int SEEK_DRAWABLE[] = {R.drawable.custom_seekbar_dark, 
										R.drawable.custom_seekbar_turq, 
										R.drawable.custom_seekbar_orange, 
										R.drawable.custom_seekbar_red, 
										R.drawable.custom_seekbar_vert,
										R.drawable.custom_seekbar_violet, 
										R.drawable.custom_seekbar_bleu };
	
	public static final int THUMB_DRAWABLE[] = {R.drawable.thumb_dark, 
										R.drawable.thumb_turq, 
										R.drawable.thumb_orange, 
										R.drawable.thumb_red, 
										R.drawable.thumb_vert,
										R.drawable.thumb_violet, 
										R.drawable.thumb_bleu };
	
	public static final int SWITCH_DRAWABLE[] = {R.layout.switch_button_dark,
										R.layout.switch_button_turq, 
										R.layout.switch_button_orange, 
										R.layout.switch_button_red, 
										R.layout.switch_button_vert,
										R.layout.switch_button_violet,
										R.layout.switch_button_bleu };
	
	public static final int SPINNER_DRAWABLE[] =  {R.drawable.spinner_dark, 
										R.drawable.spinner_turq,
										R.drawable.spinner_orange, 
										R.drawable.spinner_red, 
										R.drawable.spinner_vert,
										R.drawable.spinner_violet,
										R.drawable.spinner_bleu };
	
	public static int getThemeColorPos(Context c) {
		SharedPreferences sp = c.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		return sp.getInt(THEME_COLOR, 0); //default is gray
	}
		
	public static void setSelectedColorPosition(Context c, int pos) {
		Editor editor = c.getSharedPreferences(BASE, Context.MODE_PRIVATE).edit();
		editor.putInt(THEME_COLOR, pos);
		editor.commit();
	}

	private static int nbColumn = -1; //cache	
	public static int getNbColumn(Context c) {
		if(nbColumn!=-1) 
			return nbColumn;
		SharedPreferences sp = c.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		nbColumn = sp.getInt(NB_COLUMNS, 1); //default is one
		return nbColumn;
	}
	
	public static void setNbColumn(Context c, int nb) {
		if(nb<1)
			nb=1;
		if(nb>3)
			nb=3;
		nbColumn = nb;
		Editor editor = c.getSharedPreferences(BASE, Context.MODE_PRIVATE).edit();
		editor.putInt(NB_COLUMNS, nb);
		editor.commit();
	}

	public static boolean getActiveEditCoverMode(Context c) {
		SharedPreferences sp = c.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		return sp.getBoolean(ACTIVE_EDIT_COVER_MODE, true);
	}
	
	public static void setActiveEditCoverMode(Context c, boolean b) {
		Editor editor = c.getSharedPreferences(BASE, Context.MODE_PRIVATE).edit();
		editor.putBoolean(ACTIVE_EDIT_COVER_MODE, b);
		editor.commit();
	}
	
	public static boolean getActiveLastPlaylist(Context c) {
		SharedPreferences sp = c.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		return sp.getBoolean(ACTIVE_LAST_PLAYLIST, true);
	}
	
	public static void setActiveLastPlaylist(Context c, boolean b) {
		Editor editor = c.getSharedPreferences(BASE, Context.MODE_PRIVATE).edit();
		editor.putBoolean(ACTIVE_LAST_PLAYLIST, b);
		editor.commit();
	}

	private static int isActive = -1; //cache	
	public static boolean getActiveColorAlternate(Context c) {
		if(isActive==0)
			return false;
		if(isActive==1)
			return true;
		SharedPreferences sp = c.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		boolean b = sp.getBoolean(ACTIVE_COLOR_ALTERNATE, false); 
		isActive = b ? 1:0;
		return b;
	}
	
	public static void setActiveColorAlternate(Context c, boolean b) {
		Editor editor = c.getSharedPreferences(BASE, Context.MODE_PRIVATE).edit();
		editor.putBoolean(ACTIVE_COLOR_ALTERNATE, b);
		editor.commit();
		isActive = b ? 1:0;
	}

	public static boolean doUseDarkTextColor(Context c) {
		return getActiveDarkTheme(c);
	}

	public static Drawable getProgressStyleDrawable(Context c) {
		int pos = getThemeColorPos(c);
		if(SEEK_DRAWABLE.length<pos)
			pos=0;
		return c.getResources().getDrawable(SEEK_DRAWABLE[pos]);
	}

	public static Drawable getSpinnerStyleDrawable(Context c) {
		int pos = getThemeColorPos(c);
		if(SPINNER_DRAWABLE.length<pos)
			pos=0;
		return c.getResources().getDrawable(SPINNER_DRAWABLE[pos]);
	}

	public static int getSwitchStyle(Context c) {
		int pos = getThemeColorPos(c);
		if(SWITCH_DRAWABLE.length<pos)
			pos=0;
		return SWITCH_DRAWABLE[pos];
	}

	public static Drawable getThumbStyleDrawable(Context c) {
		int pos = getThemeColorPos(c);
		if(THUMB_DRAWABLE.length<pos)
			pos=0;
		return c.getResources().getDrawable(THUMB_DRAWABLE[pos]);
	}

	public static boolean getActiveDarkTheme(Context c) {
		SharedPreferences sp = c.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		return sp.getBoolean(ACTIVE_DARK_THEME, true);
	}

	public static void setActiveDarkTheme(Context c, boolean b) {
		Editor editor = c.getSharedPreferences(BASE, Context.MODE_PRIVATE).edit();
		editor.putBoolean(ACTIVE_DARK_THEME, b);
		editor.commit();	
	}

	/* COLORS */
	public static Drawable getActionBarDrawable(Context c) {
		int colorPos = getThemeColorPos(c);
		if(START_THEME_COLOR.length<colorPos)
			colorPos = 0;
		int colorStart = 0;
		int colorEnd = 0;
		if(colorPos==0){
			colorStart = Color.parseColor("#4C5155");
			colorEnd = Color.parseColor("#383C3F");
		} else {
			colorStart = Color.parseColor(START_THEME_COLOR[colorPos]);
			colorEnd = Color.parseColor(END_THEME_COLOR[colorPos]);
		}
		GradientDrawable gd = new GradientDrawable(
	            GradientDrawable.Orientation.TOP_BOTTOM,
	            new int[] {colorStart,colorEnd});
		return gd;
	}

	public static Drawable getFullMiniPlayerbackground(Context c) {
		int colorStart = 0;
		int colorEnd = 0;
		colorStart = Color.parseColor("#4C5155");
		colorEnd = Color.parseColor("#383C3F");
		GradientDrawable gd = new GradientDrawable(
	            GradientDrawable.Orientation.TOP_BOTTOM,
	            new int[] {colorStart,colorEnd});
		return gd;
	}

	public static int getBackgroundColor(Context c) {
		return  getActiveDarkTheme(c) ? Color.parseColor("#383C3F") : Color.WHITE;
	}

	public static int getVisualizerColor(Context c) {
		int colorPos = getThemeColorPos(c);
		if(colorPos==0 || END_THEME_COLOR.length < colorPos) {
			return Color.WHITE;
		}
		return  Color.parseColor(END_THEME_COLOR[colorPos]);
	}

	public static int getCurrentPlayAnimationColor(Context c) {
		int colorPos = getThemeColorPos(c);
		if(colorPos==0 || END_THEME_COLOR.length < colorPos) {
			if(doUseDarkTextColor(c))
				return Color.WHITE;
			return Color.parseColor("#383C3F");
		}
		return  Color.parseColor(END_THEME_COLOR[colorPos]);
	}

	public static int getSortType(Context c) {
		SharedPreferences sp = c.getSharedPreferences(BASE, Context.MODE_PRIVATE);
		return sp.getInt(SORT_TYPE, 0); //default is number track
	}
	
	public static void setSortType(Context c, int nb) {
		if(nb<0 || nb > 1)
			nb=0;
		Editor editor = c.getSharedPreferences(BASE, Context.MODE_PRIVATE).edit();
		editor.putInt(SORT_TYPE, nb);
		editor.commit();
	}
}
