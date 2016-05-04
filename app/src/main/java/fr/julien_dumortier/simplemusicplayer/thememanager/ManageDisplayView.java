package fr.julien_dumortier.simplemusicplayer.thememanager;

import fr.julien_dumortier.simplemusicplayer.DevInfo;
import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.notifynews.NotifyNews;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class ManageDisplayView extends PreferenceActivity {
	
	public static final int MANAGE_DISPLAY_VIEW = 24;
	private ListPreference mListPrefNbColumn, mListPrefSortType;
	private CheckBoxPreference mAutoSave, mColorAlternate, mDarkTheme, mCoverEditor;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		if(actionBar!=null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
		} 
		addPreferencesFromResource(R.xml.settings);
		PreferenceManager prefs = getPreferenceManager();
		
		/* ColorPreference */
		/*mColorPreference = (ColorPreference) prefs.findPreference("theme_color");*/
		/* Nombre de colonnes */
		mListPrefNbColumn = (ListPreference) prefs.findPreference("nb_column");
		CharSequence[] entryValues = mListPrefNbColumn.getEntryValues();
		int lastNbColunm = DisplayManager2.getNbColumn(this);
		
		for(int i=0; i<entryValues.length; i++)
			if(entryValues[i].equals(Integer.toString(lastNbColunm)))
				mListPrefNbColumn.setValueIndex(i);
		
		mListPrefNbColumn.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				DisplayManager2.setNbColumn(ManageDisplayView.this, Integer.parseInt(newValue.toString()));	
				return true;
			}
		});
		
		
		/* Enregistrement automatique */
		mAutoSave = (CheckBoxPreference) prefs.findPreference("active_last_playlist");
		mAutoSave.setChecked(DisplayManager2.getActiveLastPlaylist(this));
		mAutoSave.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				DisplayManager2.setActiveLastPlaylist(ManageDisplayView.this, (Boolean) newValue);
				return true;
			}
		});
		
		/* Alterner les couleurs */
		mColorAlternate = (CheckBoxPreference) prefs.findPreference("active_color_alternate");
		mColorAlternate.setChecked(DisplayManager2.getActiveColorAlternate(this));
		mColorAlternate.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				DisplayManager2.setActiveColorAlternate(ManageDisplayView.this, (Boolean) newValue);
				return true;
			}
		});
		
		/* Theme sombre */
		mDarkTheme = (CheckBoxPreference) prefs.findPreference("active_dark_theme");
		mDarkTheme.setChecked(DisplayManager2.getActiveDarkTheme(this));
		mDarkTheme.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				DisplayManager2.setActiveDarkTheme(ManageDisplayView.this, (Boolean) newValue);
				return true;
			}
		});
		
		/* Editeur de jaquette */
		mCoverEditor = (CheckBoxPreference) prefs.findPreference("show_cover_menu");
		mCoverEditor.setChecked(DisplayManager2.getActiveEditCoverMode(this));
		mCoverEditor.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				DisplayManager2.setActiveEditCoverMode(ManageDisplayView.this, (Boolean) newValue);
				return true;
			}
		});

		/* Type de tris */
		mListPrefSortType = (ListPreference) prefs.findPreference("sort_type");
		CharSequence[] sortEntryValues = mListPrefSortType.getEntryValues();
		int lastSortType = DisplayManager2.getSortType(this);
		final String [] entries = ManageDisplayView.this.getResources().getStringArray(R.array.sort_entries);
		
		for(int i=0; i<sortEntryValues.length; i++)
			if(sortEntryValues[i].equals(entries[lastSortType]))
				mListPrefSortType.setValueIndex(i);
		
		mListPrefSortType.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				
				final String val = newValue.toString();
				int pos = 0;
				for(int i=0;i<entries.length;i++) {
					if(val.equals(entries[i])) {
						pos = i;
					}
				}
				DisplayManager2.setSortType(ManageDisplayView.this, pos);	
				return true;
			}
		});
		

		/* A propos de */
		Preference pa = prefs.findPreference("purchase"); 
		pa.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				NotifyNews.showPurchase(ManageDisplayView.this);
				return false;
			}
		});
		
		/* A propos de */
		Preference p = prefs.findPreference("about"); 
		p.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				new DevInfo(ManageDisplayView.this).show();
				return false;
			}
		});
	}

	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
		ActionBar actionBar = getActionBar();
		if(actionBar!=null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
		} 
	}
		
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.nothing, menu);
		ImageView v = (ImageView) findViewById(android.R.id.home);
		v.setImageResource(R.drawable.ic_casque);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
					finish();
	                return true;
		        default:
		                return super.onOptionsItemSelected(item);
	        }
	}
}
