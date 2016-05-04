package fr.julien_dumortier.simplemusicplayer.playlistview;

import java.util.List;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.equalizer.EqualizerActivity;
import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.item.Item.IOnPictureReady;
import fr.julien_dumortier.simplemusicplayer.item.itemadapter.CurrentPlayAnimationManager;
import fr.julien_dumortier.simplemusicplayer.item.itemadapter.OrganisableTrackAdapter;
import fr.julien_dumortier.simplemusicplayer.playerview.IPlayerViewController;
import fr.julien_dumortier.simplemusicplayer.service.BackgroundBinder;
import fr.julien_dumortier.simplemusicplayer.service.MusicPlayerService;
import fr.julien_dumortier.simplemusicplayer.service.playlist.IMediaPlayerController;
import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer;
import fr.julien_dumortier.simplemusicplayer.thememanager.DisplayManager2;
import fr.julien_dumortier.simplemusicplayer.thememanager.ManageDisplayView;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptActionPlaylist;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptSuppressConfirm;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptActionPlaylist.IManagePlaylistItemAction;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptSuppressConfirm.IOnSuppressConfirm;

public class PlayListManagerActivity extends Activity implements IManagePlaylistItemAction, IOnSuppressConfirm, IOnPictureReady, IPlayerViewController {
	private List<Track> mTracks;
	private ServiceConnection mConnection;		 
    private MusicPlayerService mService;
	private IMediaPlayerController mMediaPlayerController;
	private GridView mListView;
	private View mEmptyText;
	private CurrentPlayAnimationManager mCurrentPlayAnimationManager;
	private MultiSelectManager mMultiSelectManager;
	private Menu mMenu;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist_manager);
		mMultiSelectManager = new MultiSelectManager();
		mEmptyText = findViewById(R.id.empty_text);
		if(DisplayManager2.doUseDarkTextColor(this))
			((TextView)mEmptyText).setTextColor(Color.WHITE);
		mCurrentPlayAnimationManager = new CurrentPlayAnimationManager(this);
		ActionBar actionBar = getActionBar();
		if(actionBar!=null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
            actionBar.setTitle(this.getResources().getString(R.string.playlist_title));
		} 
		initListener();
		mConnection = new ServiceConnection() { 
			public void onServiceDisconnected(ComponentName name) {}
			public void onServiceConnected(ComponentName name, IBinder service) {	
				mService  = ((BackgroundBinder)service).getService();
				mMediaPlayerController = mService.getMusicMediaPlayer();
				mTracks = mMediaPlayerController.getPlayListManager().getAllTracks();
				mService.getMusicMediaPlayer().addViewControler(PlayListManagerActivity.this);
				if(mTracks.size()>0) {
					mEmptyText.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
					mListView.setAdapter(new OrganisableTrackAdapter(mMultiSelectManager, PlayListManagerActivity.this, mTracks, PlayListManagerActivity.this, mMediaPlayerController, mCurrentPlayAnimationManager));
				} else {
					mEmptyText.setVisibility(View.VISIBLE);
					mListView.setVisibility(View.GONE);
				}
			}
		};
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
		ActionBar actionBar = getActionBar();
		if(actionBar!=null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
            actionBar.setTitle(this.getResources().getString(R.string.playlist_title));
		} 
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (requestCode == ManageDisplayView.MANAGE_DISPLAY_VIEW) {
		     updateDisplay();
		  }
	}
	
	
	public void updateDisplay() {
		ActionBar actionBar = getActionBar();
		if(actionBar!=null) {
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
			invalidateOptionsMenu();
		} 
	}
	public void initListener() {
		mListView = (GridView) findViewById(R.id.grid_layout);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if(mMultiSelectManager.isActiveMultiSelect())
				{
					if(mMultiSelectManager.contains(pos))
						mMultiSelectManager.removeTrack(pos);
					else
						mMultiSelectManager.addSelectedTrack(pos);
					mListView.invalidateViews();
				} else
					mMediaPlayerController.playTrack(mTracks.get(pos)); 
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if(mMultiSelectManager.isActiveMultiSelect() && !mMultiSelectManager.contains(pos))
						mMultiSelectManager.addSelectedTrack(pos);
				if(mMultiSelectManager.isActiveMultiSelect()) {
					mListView.invalidateViews();
					List<Track> selectedTracks = mMultiSelectManager.getSelectedTracks(mTracks);
					new PromptActionPlaylist(PlayListManagerActivity.this, selectedTracks, PlayListManagerActivity.this).show();
				}
				else
					new PromptActionPlaylist(PlayListManagerActivity.this, mTracks.get(pos), mTracks.size(), PlayListManagerActivity.this).show();
				return true;
			}
		});
	}

	public void onResume() {
		super.onResume();
		Intent intent = new Intent(this, MusicPlayerService.class);          
		bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
	}

	public void onStop() {
		if(mService!=null) {
			try {
				mService.getMusicMediaPlayer().removeViewController(this);
				unbindService(mConnection);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		super.onStop();
	}
		
	public boolean onCreateOptionsMenu(Menu menu) {
		mMenu = menu;
		getMenuInflater().inflate(R.menu.playlist_menu, menu);
		ImageView v = (ImageView) findViewById(android.R.id.home);
		v.setImageResource(R.drawable.ic_casque);
		findViewById(R.id.bg).setBackgroundColor(DisplayManager2.getBackgroundColor(this));
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	            	this.finish();
	            	return true;
	            case R.id.menu_equa:
	                startActivity(new Intent(this, EqualizerActivity.class));
	                return true;
	            case R.id.multi_select:
	            	mMultiSelectManager.setActiveMultiSelect(!mMultiSelectManager.isActiveMultiSelect());
	            	if(mMultiSelectManager.isActiveMultiSelect())
	            		mMenu.findItem(R.id.multi_select).setIcon(R.drawable.ic_multi_select_on);
	            	else {
	            		mMenu.findItem(R.id.multi_select).setIcon(R.drawable.ic_multi_select);
	            		clearMultiSelectManager();
	            	}
	            	return true;
	            case R.id.save:
	            	if(mTracks.size()>0)
	            		new SaveManagerView(this, mTracks);
	            	return true;
		        case R.id.menu_remove_all:
					new PromptSuppressConfirm(this, PlayListManagerActivity.this).show();
	                return true;
		        case R.id.action_settings:
	               	startActivityForResult(new Intent(this, ManageDisplayView.class), ManageDisplayView.MANAGE_DISPLAY_VIEW);
	               	return true;
		        default:
		            return super.onOptionsItemSelected(item);
	        }
	}

	public void playNow(Track selectedTrack) {
		mMediaPlayerController.playTrack(selectedTrack); 
	}

	public void move(Track selectedTrack, int newPos) {
		mMediaPlayerController.getPlayListManager().moveTrack(selectedTrack, newPos);
		mTracks = mMediaPlayerController.getPlayListManager().getAllTracks();
		if(mTracks.size()>0) {
			mEmptyText.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			mListView.setAdapter(new OrganisableTrackAdapter(mMultiSelectManager, PlayListManagerActivity.this, mTracks, PlayListManagerActivity.this, mMediaPlayerController, mCurrentPlayAnimationManager));
		} else {
			mEmptyText.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}

	public void delete(Track selectedTrack) {
		mMediaPlayerController.getPlayListManager().removeTrack(selectedTrack);
		mTracks = mMediaPlayerController.getPlayListManager().getAllTracks();
		if(mTracks.size()>0) {
			mEmptyText.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			mListView.setAdapter(new OrganisableTrackAdapter(mMultiSelectManager, PlayListManagerActivity.this, mTracks, PlayListManagerActivity.this, mMediaPlayerController, mCurrentPlayAnimationManager));
		} else {
			mEmptyText.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}

	public void delete(List<Track> selectedTracks) {
		mMediaPlayerController.getPlayListManager().removeTracks(selectedTracks);
		mMultiSelectManager.clearSelectedTracks();
		mMultiSelectManager.setActiveMultiSelect(false);
		mMenu.findItem(R.id.multi_select).setIcon(R.drawable.ic_multi_select);
		mTracks = mMediaPlayerController.getPlayListManager().getAllTracks();
		if(mTracks.size()>0) {
			mEmptyText.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			mListView.setAdapter(new OrganisableTrackAdapter(mMultiSelectManager, PlayListManagerActivity.this, mTracks, PlayListManagerActivity.this, mMediaPlayerController, mCurrentPlayAnimationManager));
		} else {
			mEmptyText.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}
	
	public void onSuppress() {
		mMediaPlayerController.getPlayListManager().removeAllTracks(true);
		this.finish();
	}

	public void onPictureReady(final Drawable picture, final int posFlag) {
		runOnUiThread(new Thread()
    	{
    		public void run()
    		{
	    		if(picture!=null && mListView != null && mListView.getChildAt(posFlag)!=null)
				{	
					final int numVisibleChildren = mListView.getChildCount();
					final int firstVisiblePosition = mListView.getFirstVisiblePosition();

					View ll=null;
					for ( int i = 0; i < numVisibleChildren; i++ ) {
					    int positionOfView = firstVisiblePosition + i;

					    if (positionOfView == posFlag) {
					    	ll = mListView.getChildAt(i);
					    }
					}
					if(ll!=null) {
						ImageView iv = (ImageView) ll.findViewById(R.id.img);
						iv.setImageDrawable(picture);	
						mListView.invalidateViews();
					}
				}
    		}
    	});
	}

	public void initView(MusicMediaPlayer mediaPlayer) {
		mCurrentPlayAnimationManager.stopAnimation();
		mListView.invalidateViews();
	}

	public void play() {
		mCurrentPlayAnimationManager.resumeAnimation();
	}

	public void pause() {
		mCurrentPlayAnimationManager.pauseAnimation();
	}
	
	public void clearView() {
		mCurrentPlayAnimationManager.stopAnimation();
	}

	public void stop() {
		mCurrentPlayAnimationManager.stopAnimation();
	}

	public void setRepeat(int repeatMode) {}
	public void setRandom(boolean b) {}
	public void setPercentTime(int percentPlayed) {}
	public void setTimePlayed(long timePlayed, long restTimePlayed) {}
	
	public void clearMultiSelectManager() {
		mMultiSelectManager.clearSelectedTracks();
		mMultiSelectManager.setActiveMultiSelect(false);
		mListView.invalidateViews();
	}
}