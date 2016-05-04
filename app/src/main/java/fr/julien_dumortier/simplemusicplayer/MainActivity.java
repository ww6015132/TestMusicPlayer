package fr.julien_dumortier.simplemusicplayer;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.browsemanager.BrowseManager;
import fr.julien_dumortier.simplemusicplayer.browsemanager.BrowseManager.OnBrowseListener;
import fr.julien_dumortier.simplemusicplayer.coveralbumeditor.CoverAlbumEditor.IOnUpdateFinishListener;
import fr.julien_dumortier.simplemusicplayer.equalizer.EqualizerActivity;
import fr.julien_dumortier.simplemusicplayer.item.Item;
import fr.julien_dumortier.simplemusicplayer.item.Playlist;
import fr.julien_dumortier.simplemusicplayer.item.Item.IOnPictureReady;
import fr.julien_dumortier.simplemusicplayer.item.itemadapter.AlbumAdapter;
import fr.julien_dumortier.simplemusicplayer.item.itemadapter.ArtistAdapter;
import fr.julien_dumortier.simplemusicplayer.item.itemadapter.GenreAdapter;
import fr.julien_dumortier.simplemusicplayer.item.itemadapter.ItemAdapter;
import fr.julien_dumortier.simplemusicplayer.item.itemadapter.ItemClicManager;
import fr.julien_dumortier.simplemusicplayer.item.itemadapter.PlayListAdapter;
import fr.julien_dumortier.simplemusicplayer.item.itemadapter.TrackAdapter;
import fr.julien_dumortier.simplemusicplayer.notifynews.NotifyNews;
import fr.julien_dumortier.simplemusicplayer.playerview.FullScreenPlayerViewController;
import fr.julien_dumortier.simplemusicplayer.playerview.MiniPlayerViewController;
import fr.julien_dumortier.simplemusicplayer.playlistview.PlayListManagerActivity;
import fr.julien_dumortier.simplemusicplayer.playlistview.SavedPlaylistManager;
import fr.julien_dumortier.simplemusicplayer.service.BackgroundBinder;
import fr.julien_dumortier.simplemusicplayer.service.MusicPlayerService;
import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer;
import fr.julien_dumortier.simplemusicplayer.thememanager.DisplayManager2;
import fr.julien_dumortier.simplemusicplayer.thememanager.ManageDisplayView;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptConfirmExit;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptSuppressConfirm;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptSuppressConfirm.IOnSuppressConfirm;
import android.os.Bundle;
import android.os.IBinder;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnBrowseListener, IMusicMediaControllerGetter, 
	IOnPictureReady, IOnSuppressConfirm, IOnUpdateFinishListener {
	public static final String ROBOTO_TTF = "roboto_regular.ttf";
	public GridView mGridView;
	private View mMiniPlayerLayout, mEmptyText;
	private ServiceConnection mConnection;		 
    private MusicPlayerService mService;
	private MiniPlayerViewController mMiniPlayerViewController;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    public SoftReference<GridView> mLastGridView;
	public static boolean isStartByNotification;
    public static Item selectedItem;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.content_frame).setBackgroundColor(DisplayManager2.getBackgroundColor(this));
		findViewById(R.id.mini_player).setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
		initMainMenuListener();
		ActionBar actionBar = getActionBar();
		
		if(actionBar!=null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
			actionBar.setDisplayShowTitleEnabled(false);
		}
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new DrawerListAdapter(this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mEmptyText = findViewById(R.id.empty_text);
		if(DisplayManager2.doUseDarkTextColor(this)) 
			((TextView)mEmptyText).setTextColor(Color.WHITE);
		else
			((TextView)mEmptyText).setTextColor(Color.BLACK);
		Typeface font = Typeface.createFromAsset(getAssets(), ROBOTO_TTF);
		((TextView)mEmptyText).setTypeface(font);
		mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                0,  /* "open drawer" description for accessibility */
                0  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

		mConnection = new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {}
			public void onServiceConnected(ComponentName name,
					IBinder service) {	
					mService  = ((BackgroundBinder)service).getService();
					mService.getMusicMediaPlayer().removeAllViewController();
					mMiniPlayerViewController = new MiniPlayerViewController(MainActivity.this, 
							mMiniPlayerLayout,
							(SeekBar) findViewById(R.id.progression_seek), 
							mService.getMusicMediaPlayer());
					mService.getMusicMediaPlayer().addViewControler(mMiniPlayerViewController);    		    	
			}
		};
		startService(new Intent(this, MusicPlayerService.class));
		Intent intent = new Intent(this, MusicPlayerService.class);          
		bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(20);
        selectItem(2);
	}

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    
    public void finishApplication(){
    	if(mService!=null) {
    		mService.getMusicMediaPlayer().stop(true);
    	}
    	finish();
    }
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(selectedItem!=null && animIsEnd) {
        		
        		if(mLastGridView!=null && mLastGridView.get() != null)
        		{
        			GridView last = mLastGridView.get();
        			mEmptyText.setVisibility(View.GONE);
        			mGridView.setVisibility(View.INVISIBLE);
        			last.setVisibility(View.VISIBLE);
    				mGridView = last;
    				selectedItem = selectedItem.getParent();
    				LinearLayout rl = (LinearLayout) findViewById(R.id.grid_layout);
    				rl.removeAllViewsInLayout();
    				rl.addView(mGridView);
    				mLastGridView.clear();
    				mLastGridView = null;
        			animationShow();
        		}
        		else
        			ItemClicManager.startParentBrowseByItemType(this, selectedItem, this);
        	}
			else if(selectedItem == null)
				new PromptConfirmExit(this).show();
	        return true;
	    }
		return super.onKeyDown(keyCode, event);
	}
	
	public void initNavigationDrawer() {

		  	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	        mDrawerList = (ListView) findViewById(R.id.left_drawer);
	        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	        mDrawerList.setAdapter(new DrawerListAdapter(this));
	        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
			mEmptyText = findViewById(R.id.empty_text);
			
			mDrawerToggle = new ActionBarDrawerToggle(
	                this,                  /* host Activity */
	                mDrawerLayout,         /* DrawerLayout object */
	                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
	                0,  /* "open drawer" description for accessibility */
	                0  /* "close drawer" description for accessibility */
	                ) {
	            public void onDrawerClosed(View view) {
	                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	            }

	            public void onDrawerOpened(View drawerView) {
	                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	            }
	        };
	        mDrawerLayout.setDrawerListener(mDrawerToggle);
	        mDrawerToggle.syncState();
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
		ActionBar actionBar = getActionBar();
		if(actionBar!=null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
			actionBar.setDisplayShowTitleEnabled(false);
		}
	    GridView last = mGridView;
	    if(mService!=null) {
	    	try {
	    		mService.getMusicMediaPlayer().removeViewController(mMiniPlayerViewController);
			} catch(Exception e){}
	    }
	    setContentView(R.layout.activity_main);
		findViewById(R.id.mini_player).setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
		initMainMenuListener();
		mEmptyText = findViewById(R.id.empty_text);
		if(DisplayManager2.doUseDarkTextColor(this)) 
			((TextView)mEmptyText).setTextColor(Color.WHITE);
		else
			((TextView)mEmptyText).setTextColor(Color.BLACK);
		Typeface font = Typeface.createFromAsset(getAssets(), ROBOTO_TTF);
		((TextView)mEmptyText).setTypeface(font);
		mGridView.setOnItemClickListener(last.getOnItemClickListener());
		mGridView.setOnItemLongClickListener(last.getOnItemLongClickListener());
		if(last!=null && last.getAdapter() != null && last.getAdapter().getCount()==0) {
			mEmptyText.setVisibility(View.VISIBLE);
			mGridView.setVisibility(View.INVISIBLE); 
		} else
			startUpdateRightLayout((ItemAdapter) last.getAdapter());
		if(mService!=null) {
			mMiniPlayerViewController = new MiniPlayerViewController(MainActivity.this, 
					mMiniPlayerLayout,
					(SeekBar) findViewById(R.id.progression_seek), 
					mService.getMusicMediaPlayer());
			mService.getMusicMediaPlayer().addViewControler(mMiniPlayerViewController); 
		}
		invalidateOptionsMenu();
		mDrawerToggle.onConfigurationChanged(newConfig);
		initNavigationDrawer();
		mDrawerList.invalidateViews();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (requestCode == ManageDisplayView.MANAGE_DISPLAY_VIEW) {
		     updateDisplay();
		  }
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
	    if(position==0) {
	    		selectedItem=null;
				onPlaylistBrowse(SavedPlaylistManager.getAllPlaylistName(MainActivity.this, true));
		}
	    if(position==1) {
				selectedItem=null;
				BrowseManager.browseAlbum(MainActivity.this, MainActivity.this);
		}
	    if(position==2) {
				selectedItem=null;
				BrowseManager.browseArtist(MainActivity.this, MainActivity.this);
		}
	    if(position==3) {
				selectedItem=null;
				BrowseManager.browseGenre(MainActivity.this, MainActivity.this);
	    }
	    if(position==4) {
				selectedItem=null;
				BrowseManager.browseTrack(MainActivity.this, true, MainActivity.this);
		}
    }
    
	public void updateDisplay() {
		findViewById(R.id.mini_player).setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
	    if(DisplayManager2.doUseDarkTextColor(this)) 
			((TextView)mEmptyText).setTextColor(Color.WHITE);
		else
			((TextView)mEmptyText).setTextColor(Color.BLACK);
		ActionBar actionBar = getActionBar();
		if(actionBar!=null)
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
		mGridView.setNumColumns(DisplayManager2.getNbColumn(this));
		invalidateOptionsMenu();
		findViewById(R.id.content_frame).setBackgroundColor(13487566);
		if(mMiniPlayerViewController!=null)
			mMiniPlayerViewController.updateThumbStyle();
		mGridView.invalidateViews();
	}
	
	public void onResume() {
		super.onResume();
		if(mService!=null && mMiniPlayerViewController != null) {
			mService.getMusicMediaPlayer().addViewControler(mMiniPlayerViewController);
			mMiniPlayerViewController.initView(getMusicMediaPlayer());
		}
		ActionBar actionBar = getActionBar();
		if(actionBar!=null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
			actionBar.setDisplayShowTitleEnabled(false);
		}
		findViewById(R.id.mini_player).setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
		if(DisplayManager2.doUseDarkTextColor(this)) 
			((TextView)mEmptyText).setTextColor(Color.WHITE);
		else
			((TextView)mEmptyText).setTextColor(Color.BLACK);
		mGridView.invalidateViews();
		invalidateOptionsMenu(); 
		NotifyNews.showNews(this);
	}
	
	public void onStop() {
		super.onStop();
		if(mService!=null && mMiniPlayerViewController != null) {
			try {
				mService.getMusicMediaPlayer().removeViewController(mMiniPlayerViewController);
			} catch(Exception e){}
		}
	}
	
	public void onDestroy() {
		if(mService!=null && mMiniPlayerViewController != null) {
			try {
				mService.getMusicMediaPlayer().removeViewController(mMiniPlayerViewController);
				mService.getMusicMediaPlayer().stop(true);
			} catch(Exception e){}
		}
		if(mService!=null) {
			try {
				unbindService(mConnection);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		ImageView v = (ImageView) findViewById(android.R.id.home);
		v.setImageResource(R.drawable.ic_casque);
		findViewById(R.id.content_frame).setBackgroundColor(DisplayManager2.getBackgroundColor(this));
		if(mMiniPlayerViewController!=null)
			mMiniPlayerViewController.updateThumbStyle();
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	            	if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
	            		mDrawerLayout.closeDrawer(GravityCompat.START);
	            	else
	            		mDrawerLayout.openDrawer(GravityCompat.START);
	                return true;
                case R.id.menu_equa:
	                startActivity(new Intent(this, EqualizerActivity.class));
	                return true;
		        case R.id.menu_playlist:
	                startActivity(new Intent(this, PlayListManagerActivity.class));
	                return true;
		        case R.id.menu_parent_dir:
		        	if(selectedItem!=null && animIsEnd) {
		        		
		        		if(mLastGridView!=null && mLastGridView.get() != null)
		        		{
		        			GridView last = mLastGridView.get();
		        			mEmptyText.setVisibility(View.GONE);
		        			mGridView.setVisibility(View.INVISIBLE);
		        			last.setVisibility(View.VISIBLE);
	        				mGridView = last;
	        				selectedItem = selectedItem.getParent();
	        				LinearLayout rl = (LinearLayout) findViewById(R.id.grid_layout);
	        				rl.removeAllViewsInLayout();
	        				rl.addView(mGridView);
	        				mLastGridView.clear();
	        				mLastGridView = null;
		        			animationShow();
		        		}
		        		else
		        			ItemClicManager.startParentBrowseByItemType(this, selectedItem, this);
		        	}
	                return true;
		        case R.id.menu_remove_all:
					new PromptSuppressConfirm(this, this).show();
	                return true;
		        case R.id.action_settings:
		               	startActivityForResult(new Intent(this, ManageDisplayView.class), ManageDisplayView.MANAGE_DISPLAY_VIEW);
		                return true;
		        default:
		                return super.onOptionsItemSelected(item);
	        }
	}
			
	public void updatePlaylist() {
		selectedItem=null;
		onPlaylistBrowse(SavedPlaylistManager.getAllPlaylistName(MainActivity.this, true));
	}
	
	protected void initMainMenuListener() {
		findViewById(R.id.mini_player).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(new Intent(MainActivity.this, FullScreenPlayerViewController.class), 0);	
			}
		});
		mMiniPlayerLayout = findViewById(R.id.mini_player);
		mGridView = new GridView(this);
		LinearLayout rl = (LinearLayout) findViewById(R.id.grid_layout);
		rl.removeAllViewsInLayout();
		rl.addView(mGridView);
		mGridView.setNumColumns(DisplayManager2.getNbColumn(this));
	}

	public void onPlaylistBrowse(String[] playlists) {
		final ArrayList<Item> list = new ArrayList<Item>();
		for(String string : playlists) {
			Playlist pl = new Playlist(this, string);
			list.add(pl);
		}
		runOnUiThread(new Runnable() { public void run() {
			ItemClicManager listener = new ItemClicManager(MainActivity.this, list, MainActivity.this, MainActivity.this);
			if(mGridView!=null)
				mGridView.setVisibility(View.INVISIBLE);
			mGridView = new GridView(MainActivity.this);
			LinearLayout rl = (LinearLayout) findViewById(R.id.grid_layout);
			rl.removeAllViewsInLayout();
			rl.addView(mGridView);
			mGridView.setNumColumns(DisplayManager2.getNbColumn(MainActivity.this));
			mGridView.setOnItemClickListener(listener);
			mGridView.setOnItemLongClickListener(listener);
			if(list.size()==0) {
				mEmptyText.setVisibility(View.VISIBLE);
				mGridView.setVisibility(View.INVISIBLE);
			} else
				startUpdateRightLayout(new PlayListAdapter(MainActivity.this, list, MainActivity.this));
		}});
	}


	public void onTrackBrowsed(ArrayList<Item> tracks) {
		onBrowseTrackFinish(tracks);
	}	
	
	public void onBrowseAlbumFinish(final ArrayList<Item> items) {
		runOnUiThread(new Runnable() { public void run() {
			ItemClicManager listener = new ItemClicManager(MainActivity.this, items, MainActivity.this, MainActivity.this);
			if(mGridView!=null)
				mGridView.setVisibility(View.INVISIBLE);
			mGridView = new GridView(MainActivity.this);
			LinearLayout rl = (LinearLayout) findViewById(R.id.grid_layout);
			rl.removeAllViewsInLayout();
			rl.addView(mGridView);
			mGridView.setNumColumns(DisplayManager2.getNbColumn(MainActivity.this));
			mGridView.setOnItemClickListener(listener);
			mGridView.setOnItemLongClickListener(listener);
			if(items.size()==0) {
				mEmptyText.setVisibility(View.VISIBLE);
				mGridView.setVisibility(View.INVISIBLE); 
			} else {
				startUpdateRightLayout(new AlbumAdapter(MainActivity.this, items, MainActivity.this));
			}
		}});
	} 
	
	public void onBrowseArtistFinish(final ArrayList<Item> items) {
		runOnUiThread(new Runnable() { public void run() {
			ItemClicManager listener = new ItemClicManager(MainActivity.this, items, MainActivity.this, MainActivity.this);
			if(mGridView!=null)
				mGridView.setVisibility(View.INVISIBLE);
			mGridView = new GridView(MainActivity.this);
			LinearLayout rl = (LinearLayout) findViewById(R.id.grid_layout);
			rl.removeAllViewsInLayout();
			rl.addView(mGridView);
			mGridView.setNumColumns(DisplayManager2.getNbColumn(MainActivity.this));
			mGridView.setOnItemClickListener(listener);
			mGridView.setOnItemLongClickListener(listener);
			if(items.size()==0) {
				mEmptyText.setVisibility(View.VISIBLE);
				mGridView.setVisibility(View.INVISIBLE);
			} else
				startUpdateRightLayout(new ArtistAdapter(MainActivity.this, items, MainActivity.this));
		}});
	}
	
	public void onBrowseTrackFinish(final ArrayList<Item> items) {
		runOnUiThread(new Runnable() { public void run() {
			ItemClicManager listener = new ItemClicManager(MainActivity.this, items, MainActivity.this, MainActivity.this);
			if(mGridView!=null)
				mGridView.setVisibility(View.INVISIBLE);
			mGridView = new GridView(MainActivity.this);
			LinearLayout rl = (LinearLayout) findViewById(R.id.grid_layout);
			rl.removeAllViewsInLayout();
			rl.addView(mGridView);
			mGridView.setNumColumns(DisplayManager2.getNbColumn(MainActivity.this));
			mGridView.setOnItemClickListener(listener);
			mGridView.setOnItemLongClickListener(listener);
			if(items.size()==0) {
				mEmptyText.setVisibility(View.VISIBLE);
				mGridView.setVisibility(View.INVISIBLE);
			} else
				startUpdateRightLayout(new TrackAdapter(MainActivity.this, items, MainActivity.this));
		}});	
	}
	
	public void onBrowseGenreFinish(final ArrayList<Item> items) {
		runOnUiThread(new Runnable() { public void run() {
			ItemClicManager listener = new ItemClicManager(MainActivity.this, items, MainActivity.this, MainActivity.this);
			if(mGridView!=null)
				mGridView.setVisibility(View.INVISIBLE);
			mGridView = new GridView(MainActivity.this);
			LinearLayout rl = (LinearLayout) findViewById(R.id.grid_layout);
			rl.removeAllViewsInLayout();
			rl.addView(mGridView);
			mGridView.setNumColumns(DisplayManager2.getNbColumn(MainActivity.this));
			mGridView.setOnItemClickListener(listener);
			mGridView.setOnItemLongClickListener(listener);
			selectedItem=null;
			if(items.size()==0) {
				mEmptyText.setVisibility(View.VISIBLE);
				mGridView.setVisibility(View.INVISIBLE); 
			} else
				startUpdateRightLayout(new GenreAdapter(MainActivity.this, items, MainActivity.this));
		}});	
	}
	
	private void startUpdateRightLayout(final ItemAdapter adapter) {
		mGridView.setVisibility(View.VISIBLE);
		mEmptyText.setVisibility(View.GONE);
		mGridView.setAdapter(adapter);
		animationShow();
	}

	public MusicMediaPlayer getMusicMediaPlayer() {
		if(mService==null)
			return null;
		return mService.getMusicMediaPlayer();
	}

	public void updateGridViewLayoutParams(boolean fullScreen) {
		LinearLayout.LayoutParams params = null;
		View v = findViewById(R.id.grid_layout);
		if(fullScreen)
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		else
			params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, v.getHeight() - mMiniPlayerLayout.getHeight());
		v.setLayoutParams(params);
		mGridView.invalidateViews();
	}

	public void onPictureReady(final Drawable picture, final int posFlag) {
		runOnUiThread(new Thread()
	    	{
	    		public void run()
	    		{
		    		if(picture!=null && mGridView != null)
					{	
						final int numVisibleChildren = mGridView.getChildCount();
						final int firstVisiblePosition = mGridView.getFirstVisiblePosition();

						View ll=null;
						for ( int i = 0; i < numVisibleChildren; i++ ) {
						    int positionOfView = firstVisiblePosition + i;

						    if (positionOfView == posFlag) {
						    	ll = mGridView.getChildAt(i);
						    }
						}
						if(ll!=null) {
							ImageView iv = (ImageView) ll.findViewById(R.id.img);
							iv.setImageDrawable(picture);	
							mGridView.invalidateViews();
						}
					}
	    		}
	    	});
	}
	
	public void onSuppress() {
		if(mService!=null) 
			mService.getMusicMediaPlayer().getPlayListManager().removeAllTracks(true);
	}
	
	private Animation animShow;
	private boolean animIsEnd;
	public void animationShow() {
		animIsEnd = false;
		if(animShow==null) {
			animShow = new AlphaAnimation(0, 1);
			animShow.setDuration(444);
			animShow.setAnimationListener(new AnimationListener() {
				public void onAnimationStart(Animation animation) {}
				
				public void onAnimationRepeat(Animation animation) {}
				
				public void onAnimationEnd(Animation animation) {
					animIsEnd = true;
				}
			});
		} 
		findViewById(R.id.grid_layout).startAnimation(animShow);
	}

	public void onCoverUpdate(final boolean success) {
		runOnUiThread(new Runnable() {
			public void run() {
				if(success)
					Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.good_url_to_image),
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.bad_url_to_image),
							Toast.LENGTH_LONG).show();
			}
		});
	}
}