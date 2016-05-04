package fr.julien_dumortier.simplemusicplayer.playerview;

import fr.julien_dumortier.simplemusicplayer.MainActivity;
import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.equalizer.EqualizerActivity;
import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.playlistview.PlayListManagerActivity;
import fr.julien_dumortier.simplemusicplayer.service.BackgroundBinder;
import fr.julien_dumortier.simplemusicplayer.service.MusicPlayerService;
import fr.julien_dumortier.simplemusicplayer.service.playlist.IMediaPlayerController;
import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer;
import fr.julien_dumortier.simplemusicplayer.service.playlist.PlayListManager;
import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer.IToastManager;
import fr.julien_dumortier.simplemusicplayer.thememanager.DisplayManager2;
import fr.julien_dumortier.simplemusicplayer.thememanager.ManageDisplayView;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptSuppressConfirm;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptSuppressConfirm.IOnSuppressConfirm;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class FullScreenPlayerViewController extends Activity implements IPlayerViewController, IToastManager, IOnSuppressConfirm {
	
	private ServiceConnection mConnection;		 
    private MusicPlayerService mService;
	private TextView mTrackName;
	private TextView mArtistName;
	private TextView mAlbumName;
	private ImageView mPlay, mTrackImage, mNext, mLast, mRepeat, mShuffle;
	private SeekBar mProgression;
	private TextView mTimePlayed, mTotalTime;
	private boolean isPlayingMode;
	private IMediaPlayerController mMediaPlayerController;
	private String mLastToastMessage;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_screen_player);
		findViewById(R.id.mini_player).setBackgroundDrawable(DisplayManager2.getFullMiniPlayerbackground(this));
		ActionBar actionBar = getActionBar();
		if(actionBar!=null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
			actionBar.setDisplayShowCustomEnabled(true);
            View cView = LayoutInflater.from(this).inflate(R.layout.action_bar_track_detail, null);
    		Typeface font = Typeface.createFromAsset(getAssets(), MainActivity.ROBOTO_TTF);
            mTrackName = ((TextView)cView.findViewById(R.id.track_name_full));
            mTrackName.setTypeface(font);
            mArtistName = ((TextView)cView.findViewById(R.id.artist_name_full));
            mArtistName.setTypeface(font);
            mAlbumName = ((TextView)cView.findViewById(R.id.album_name_full));
            mAlbumName.setTypeface(font);
            actionBar.setCustomView(cView);
		} 
		initListener();
		mConnection = new ServiceConnection() { 	

			public void onServiceDisconnected(ComponentName name) {}

			public void onServiceConnected(ComponentName name,
					IBinder service) {	
					mService  = ((BackgroundBinder)service).getService();
					mService.getMusicMediaPlayer().setToastManager(FullScreenPlayerViewController.this);
					mService.getMusicMediaPlayer().addViewControler(FullScreenPlayerViewController.this);
					mMediaPlayerController = mService.getMusicMediaPlayer();
			}  
		};
	}
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
		ActionBar actionBar = getActionBar();
		if(actionBar!=null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
			actionBar.setDisplayShowTitleEnabled(false);
            View cView = LayoutInflater.from(this).inflate(R.layout.action_bar_track_detail, null);
    		Typeface font = Typeface.createFromAsset(getAssets(), MainActivity.ROBOTO_TTF);
            mTrackName = ((TextView)cView.findViewById(R.id.track_name_full));
            mTrackName.setTypeface(font);
            mArtistName = ((TextView)cView.findViewById(R.id.artist_name_full));
            mArtistName.setTypeface(font);
            mAlbumName = ((TextView)cView.findViewById(R.id.album_name_full));
            mAlbumName.setTypeface(font);
            actionBar.setCustomView(cView);
		}
		setContentView(R.layout.full_screen_player);
		findViewById(R.id.mini_player).setBackgroundDrawable(DisplayManager2.getFullMiniPlayerbackground(this));
		initListener();
		initView(getMusicMediaPlayer());
	}
		
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (requestCode == ManageDisplayView.MANAGE_DISPLAY_VIEW) {
		    	 updateDisplay();
		  }
	}
	
	public void updateDisplay() {
		findViewById(R.id.mini_player).setBackgroundDrawable(DisplayManager2.getFullMiniPlayerbackground(this));
		ActionBar actionBar = getActionBar();
		if(actionBar!=null) {
			actionBar.setBackgroundDrawable(DisplayManager2.getActionBarDrawable(this));
			invalidateOptionsMenu();
		} 
	}
	public void initListener() {
		mTrackImage = ((ImageView)findViewById(R.id.musique_image_full));
        mPlay = ((ImageView)findViewById(R.id.button_play_full));
		mPlay.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(MotionEvent.ACTION_DOWN == event.getAction()) {
					if(isPlayingMode)
						mPlay.setImageDrawable(FullScreenPlayerViewController.this.getResources().getDrawable(R.drawable.pause_click));
					else
						mPlay.setImageDrawable(FullScreenPlayerViewController.this.getResources().getDrawable(R.drawable.play_click));	
				}
				if(MotionEvent.ACTION_UP == event.getAction()) {
					if(isPlayingMode)
						pause();
					else
						play();		
				}
				return true;
			}
		});
		mNext = ((ImageView)findViewById(R.id.button_next_full));
		mNext.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(MotionEvent.ACTION_DOWN == event.getAction())
					mNext.setImageDrawable(FullScreenPlayerViewController.this.getResources().getDrawable(R.drawable.next_click));
				if(MotionEvent.ACTION_UP == event.getAction()) {
					mNext.setImageDrawable(FullScreenPlayerViewController.this.getResources().getDrawable(R.drawable.next));
					if(mMediaPlayerController!=null)
						mMediaPlayerController.next();
				}
				return true;
			}
		});
		mLast = ((ImageView)findViewById(R.id.button_last_full));
		mLast.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(MotionEvent.ACTION_DOWN == event.getAction())
					mLast.setImageDrawable(FullScreenPlayerViewController.this.getResources().getDrawable(R.drawable.previous_click));
				if(MotionEvent.ACTION_UP == event.getAction()) {
					mLast.setImageDrawable(FullScreenPlayerViewController.this.getResources().getDrawable(R.drawable.previous));
					if(mMediaPlayerController!=null)
						mMediaPlayerController.last();
				}
				return true;
			}
		});
		mRepeat = ((ImageView)findViewById(R.id.button_repeat_full));
		mRepeat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(mMediaPlayerController!=null)
					mMediaPlayerController.getPlayListManager().setRepeatMode(
							mMediaPlayerController.getPlayListManager().getRepeatMode()+1);
			}
		});
		mShuffle = ((ImageView)findViewById(R.id.button_shuffle_full));
		mShuffle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(mMediaPlayerController!=null)
					mMediaPlayerController.getPlayListManager().setRandom(!mMediaPlayerController.getPlayListManager().isRandomMode());
			}
		});
		mProgression = (SeekBar) findViewById(R.id.progression_seek);
		mProgression.setProgressDrawable(DisplayManager2.getProgressStyleDrawable(this));
		mProgression.setThumb(DisplayManager2.getThumbStyleDrawable(this));
		mProgression.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar arg0) {
				if(mMediaPlayerController!=null)
					mMediaPlayerController.seekToPercent((mProgression.getProgress()*100)/mProgression.getMax());
			}			
			public void onStartTrackingTouch(SeekBar arg0) {}			
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {}
		});
		mTimePlayed = ((TextView)findViewById(R.id.time_played));
		mTimePlayed.setTextColor(Color.rgb(92, 221, 252));
		mTimePlayed.setShadowLayer(1, 1, 1, Color.BLACK);
        mTotalTime = ((TextView)findViewById(R.id.total_time));		
        mTotalTime.setTextColor(Color.rgb(92, 221, 252));
        mTotalTime.setShadowLayer(1, 1, 1, Color.BLACK);
	}

	public void onResume() {
		super.onResume();
		Intent intent = new Intent(this, MusicPlayerService.class);          
		bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
		invalidateOptionsMenu();
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
		getMenuInflater().inflate(R.menu.full_menu, menu);
		int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView actionBarTextView = (TextView)findViewById(actionBarTitleId); 
		if(actionBarTextView!=null)
			actionBarTextView.setText("");
		ImageView v = (ImageView) findViewById(android.R.id.home);
		v.setImageResource(R.drawable.ic_casque);
		findViewById(R.id.bg).setBackgroundColor(DisplayManager2.getBackgroundColor(this));
		mProgression.setProgressDrawable(DisplayManager2.getProgressStyleDrawable(this));
		mProgression.setThumb(DisplayManager2.getThumbStyleDrawable(this));
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.menu_equa:
	                startActivity(new Intent(this, EqualizerActivity.class));
	                return true;
	            case android.R.id.home:
	            	this.finish();
	                return true;
		        case R.id.menu_playlist:
	                startActivity(new Intent(this, PlayListManagerActivity.class));
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
	
	public MusicMediaPlayer getMusicMediaPlayer() {
		if(mService==null)
			return null;
		return mService.getMusicMediaPlayer();
	}

	////////////////////// IPlayerViewControllerMethods /////////////////
	public void setPercentTime(int percentPlayed) {
		mProgression.setProgress((percentPlayed*mProgression.getMax())/100);
	}

	public void setTimePlayed(final long timePlayed, final long restTimePlayed) {
		runOnUiThread(new Runnable() {
			public void run() {
				mTimePlayed.setText(TimeFormater.getFormatedTime(timePlayed));
				mTotalTime.setText(TimeFormater.getFormatedTime(restTimePlayed));
			}
		});
	}

	
	public void initView(final MusicMediaPlayer mediaPlayer) {
		runOnUiThread(new Runnable(){public void run(){
			Track track = mediaPlayer.getCurrentTrack();
			if(track!=null) {
				if(mTrackName!=null)
					mTrackName.setText(track.getTrackName());
				if(mArtistName!=null)
					mArtistName.setText(track.getArtistName());
				if(mAlbumName!=null)
					mAlbumName.setText(track.getAlbumName());
				
				mTrackImage.setImageDrawable(track.getHdPicture());
				mTimePlayed.setText(TimeFormater.getFormatedTime(mediaPlayer.getTimePlayed()));
				mTotalTime.setText(TimeFormater.getFormatedTime(mediaPlayer.getRestTimePlayed()));
				mProgression.setProgress((mediaPlayer.getPercentPlayed()*mProgression.getMax())/100);
				if(mediaPlayer.isPlaying()) {
					isPlayingMode = true;
					mPlay.setImageDrawable(getResources().getDrawable(R.drawable.pause));
				}
				if(mediaPlayer.getPlayListManager().isRandomMode())
					mShuffle.setImageDrawable(FullScreenPlayerViewController.this.getResources().getDrawable(R.drawable.shuffle_on));
				switch(mediaPlayer.getPlayListManager().getRepeatMode()) {
					case PlayListManager.NO_REPEAT:
						mRepeat.setImageDrawable(getResources().getDrawable(R.drawable.repeat));
						break;
					case PlayListManager.REPEAT_ALL:
						mRepeat.setImageDrawable(getResources().getDrawable(R.drawable.repeat_on));
						break;
					case PlayListManager.REPEAT_ONE:
						mRepeat.setImageDrawable(getResources().getDrawable(R.drawable.repeat_on_one));
						break;
				}
			}
		}});
	}

	public void play() {
		if(!isPlayingMode && mMediaPlayerController != null && mMediaPlayerController.getCurrentTrack()!=null)
		runOnUiThread(new Runnable() {
			public void run() {
				isPlayingMode = true;
				mProgression.setEnabled(true);
				mMediaPlayerController.resume();
				mPlay.setImageDrawable(getResources().getDrawable(R.drawable.pause));
			}
		});
	}
	
	public void pause() {
		if(isPlayingMode && mMediaPlayerController != null)
		runOnUiThread(new Runnable() {
			public void run() {
				isPlayingMode = false;
				mMediaPlayerController.pause();
				mPlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
			}
		});
	}

	public void setRepeat(final int repeatMode) {
		runOnUiThread(new Runnable() {
			public void run() {
				switch(repeatMode) {
					case PlayListManager.NO_REPEAT:
						mRepeat.setImageDrawable(getResources().getDrawable(R.drawable.repeat));
						break;
					case PlayListManager.REPEAT_ALL:
						mRepeat.setImageDrawable(getResources().getDrawable(R.drawable.repeat_on));
						break;
					case PlayListManager.REPEAT_ONE:
						mRepeat.setImageDrawable(getResources().getDrawable(R.drawable.repeat_on_one));
						break;
				}
			}
		});
	}

	public void setRandom(final boolean b) {
		runOnUiThread(new Runnable() {
			public void run() {
				if(b)
					mShuffle.setImageDrawable(getResources().getDrawable(R.drawable.shuffle_on));
				else
					mShuffle.setImageDrawable(getResources().getDrawable(R.drawable.shuffle));
			}
		});
	}

	public void clearView() {
		finish();
	}

	public void stop() {
		pause();
	}	

	public void showMessage(final String message) {
		runOnUiThread(new Runnable() {
			public void run() {
				if(message.equals(mLastToastMessage)) {
					new Thread(){ public void run() {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {}
						mLastToastMessage = "";
					}}.start();
				} else {
					Toast.makeText(FullScreenPlayerViewController.this, message, Toast.LENGTH_SHORT).show();
					mLastToastMessage = message;
				}
			}
		});
	}
	////////////////////// IPlayerViewControllerMethods /////////////////
	

	public void onSuppress() {
		mMediaPlayerController.getPlayListManager().removeAllTracks(true);
		this.finish();
	}
}
