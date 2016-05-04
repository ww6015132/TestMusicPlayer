package fr.julien_dumortier.simplemusicplayer.playerview;

import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.MainActivity;
import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.item.Item.IOnPictureReady;
import fr.julien_dumortier.simplemusicplayer.service.playlist.IMediaPlayerController;
import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer;
import fr.julien_dumortier.simplemusicplayer.thememanager.DisplayManager2;

public class MiniPlayerViewController implements IPlayerViewController, IOnPictureReady {

	private MainActivity mContext;
	private TextView mTrackName;
	private TextView mArtistName;
	private ImageView mPlay, mTrackImage;
	private SeekBar mProgression;
	private boolean isPlayingMode, isShowingPlayer;
	private IMediaPlayerController mMediaPlayerController;
	private View mPlayerContainer;
	
	public MiniPlayerViewController(MainActivity act, View v, SeekBar progress, 
			IMediaPlayerController mediaPlayerController) {
		mContext = act;
		mPlayerContainer = v;
		isShowingPlayer = false;
		mTrackImage = ((ImageView)v.findViewById(R.id.musique_image));
        mTrackName = ((TextView)v.findViewById(R.id.mini_track_name));
        mArtistName = ((TextView)v.findViewById(R.id.mini_artist_name));
        mPlay = ((ImageView)v.findViewById(R.id.button_play));
		mPlay.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(MotionEvent.ACTION_DOWN == event.getAction()) {
					if(isPlayingMode)
						((ImageView)v).setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause_click));
					else
						((ImageView)v).setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_click));	
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
		mProgression = progress;
		updateThumbStyle();
		/*mProgression.setProgressDrawable(act.getResources().getDrawable(R.drawable.seekbar_color));/*
		LayerDrawable ld = (LayerDrawable) mProgression.getProgressDrawable();
		ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.progressshape);
		d1.setColorFilter(Color.parseColor("#202a36"), android.graphics.PorterDuff.Mode.SRC_IN);*/
		mProgression.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar arg0) {
				mMediaPlayerController.seekToPercent((mProgression.getProgress()*100)/mProgression.getMax());
			}
			
			public void onStartTrackingTouch(SeekBar arg0) {}			
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {}
		});
		mMediaPlayerController = mediaPlayerController;
	}
	
	public void updateThumbStyle() {
		mProgression.setProgressDrawable(DisplayManager2.getProgressStyleDrawable(mContext));
		mProgression.setThumb(DisplayManager2.getThumbStyleDrawable(mContext));
	}
	
	public void setPercentTime(int percentPlayed) {
		mProgression.setProgress((percentPlayed*mProgression.getMax())/100);
	}

	public void setTimePlayed(final long timePlayed, final long restTimePlayed) {
		
	}

	public void initView(final MusicMediaPlayer mediaPlayer) {
		mContext.runOnUiThread(new Runnable(){public void run(){
			Track track = mediaPlayer.getCurrentTrack();
			if(track!=null) {
				if(track.isDefaultPicture)
					track.initPicture();
				mTrackName.setText(track.getTrackName());
				mArtistName.setText(track.getArtistName());
				mTrackImage.setImageDrawable(track.getPicture());
				mProgression.setProgress((mediaPlayer.getPercentPlayed()*mProgression.getMax())/100); 
				if(mediaPlayer.isPlaying()) 
					mPlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause));
				else
					mPlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play));
				isPlayingMode = mediaPlayer.isPlaying();
				showPlayer();
			} else {
				dismissPlayer();
			}
		}});
	}

	public void play() {
		if(!isPlayingMode && mMediaPlayerController.getCurrentTrack()!=null)
		mContext.runOnUiThread(new Runnable() {
			public void run() {
				isPlayingMode = true;
				mProgression.setEnabled(true);
				mMediaPlayerController.resume();
				mPlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause));
			}
		});
	}

	
	public void pause() {
		if(isPlayingMode)
		mContext.runOnUiThread(new Runnable() {
			public void run() {
				isPlayingMode = false;
				mMediaPlayerController.pause();
				mPlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play));
			}
		});
	}

	public void setRepeat(int repeatMode) {}

	public void setRandom(boolean b) {}

	public void stop() {
		pause();
		clearView();
	}

	public void clearView() {
		mContext.runOnUiThread(new Runnable(){public void run(){
			mTrackName.setText("");
			mArtistName.setText("");
			mTrackImage.setImageDrawable(null);
			dismissPlayer();
		}});
	}

	private void showPlayer() {
		if(!isShowingPlayer) {
			int h = mPlayerContainer.getHeight();
			if(h<10)
				h=200;
			Animation anim = new TranslateAnimation(0, 0, h, 0);
			anim.setDuration(600);
			anim.setInterpolator(new DecelerateInterpolator());
			anim.setAnimationListener(new AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mPlayerContainer.setVisibility(View.VISIBLE);
				}			
				public void onAnimationRepeat(Animation animation) {}			
				public void onAnimationEnd(Animation animation) {
					//mProgression.setVisibility(View.VISIBLE);
					mProgression.setY(mPlayerContainer.getY()-mPlayerContainer.getHeight()-30);
					mProgression.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.FILL_PARENT, 15));
					mContext.updateGridViewLayoutParams(false);
				}
			});
			anim.setFillAfter(true);
			mPlayerContainer.clearAnimation();
			mPlayerContainer.startAnimation(anim);
		}
		isShowingPlayer = true;
	}
	
	private void dismissPlayer() {
		if(isShowingPlayer) {
			int h = mPlayerContainer.getHeight();
			if(h<10)
				h=200;
			Animation anim = new TranslateAnimation(0, 0, 0, h);
			anim.setDuration(600);
			anim.setInterpolator(new DecelerateInterpolator());
			anim.setAnimationListener(new AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mProgression.setVisibility(View.GONE);
				}			
				public void onAnimationRepeat(Animation animation) {}			
				public void onAnimationEnd(Animation animation) {
					mPlayerContainer.setVisibility(View.GONE);
					mContext.updateGridViewLayoutParams(true);
				}
			});
			anim.setFillAfter(true);
			mPlayerContainer.clearAnimation();
			mPlayerContainer.startAnimation(anim);
		}
		isShowingPlayer = false;
	}

	public void onPictureReady(final Drawable picture, int posFlag) {
		mContext.runOnUiThread(new Runnable() {
			public void run() {
				mTrackImage.setImageDrawable(picture);
			}
		});
	}
}
