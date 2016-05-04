package fr.julien_dumortier.simplemusicplayer.service.playlist;

import java.util.ArrayList;
import java.util.List;
import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.equalizer.EqualizerConfigurationDataBase;
import fr.julien_dumortier.simplemusicplayer.equalizer.preset.Preset;
import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.playerview.IPlayerViewController;
import fr.julien_dumortier.simplemusicplayer.service.MusicPlayerService;
import fr.julien_dumortier.simplemusicplayer.service.phonestatemanager.PhoneCallStateManager;
import fr.julien_dumortier.simplemusicplayer.widget.AudioPlayerApplication;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Equalizer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MusicMediaPlayer implements IMediaPlayerController {
	public MusicPlayerService mContext;
	private MediaPlayer mMediaPlayer;
	private PlayListManager mPlayListManager;
	private Track mCurrentTrack;
	private List<IPlayerViewController> mPlayerViewController;
	private Thread mUpdateTimePlayedThread;
	private boolean isPause, isDestroyed;
	private PhoneCallStateManager mPhoneStateListener;
	public IToastManager mToastManager;
	private Equalizer mEqualizer;
	
	public MusicMediaPlayer(MusicPlayerService context) {
		mContext = context;
		isDestroyed = false;
		isPause = false;
		mMediaPlayer = new MediaPlayer();
		initEqualizer();
		mPlayerViewController = new ArrayList<IPlayerViewController>();
		mPlayListManager = new PlayListManager(this);
		mPhoneStateListener = new PhoneCallStateManager(mContext, this);
		initMediaPlayerListener();
		mUpdateTimePlayedThread = new Thread() {
			public void run() {
				try {
					while(!isDestroyed) {
						if(!isPause) {
							try {
								for(IPlayerViewController pl:mPlayerViewController) {
									pl.setPercentTime(MusicMediaPlayer.this.getPercentPlayed());
									pl.setTimePlayed(MusicMediaPlayer.this.getTimePlayed(), MusicMediaPlayer.this.getRestTimePlayed());
								} 
							} catch(Exception e) {/*concurrent modification exception*/}
						}
						Thread.sleep(1000);//pause d'une seconde
					}
				} catch (InterruptedException e) {
					return;
				}
			}
		};
		mUpdateTimePlayedThread.setName("UpdateTimePlayedThread");
		mUpdateTimePlayedThread.start(); 
	}
	
	public interface IToastManager {
		public void showMessage(String message);
	}
	
	public void setToastManager(IToastManager toastManager) {
		mToastManager = toastManager;
	}
	
	public void playTrack(Track track) {
		realPlayTrack(track);
		mPlayListManager.updateRandom();
	}
	
	private Track mTmpTrack;
	
	public void realPlayTrack(Track track) {
		if(track!=null) {
			
			stop(false);
			mTmpTrack = track;
			initMediaPlayerListener();
			try {
				mMediaPlayer.setDataSource(mTmpTrack.getDataTrack());
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				isPause = false;
				mCurrentTrack = mTmpTrack;
				((AudioPlayerApplication)mContext.getApplicationContext()).getAudioPlayerWidgetViewController().showViews(mCurrentTrack);
				for(IPlayerViewController pl:mPlayerViewController) {
					pl.initView(this);
					pl.play();
				}
				mTmpTrack = null;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("2ND TEST PREPARE AND START MEDIA PLAYER");
				try { //retest
					mMediaPlayer.setDataSource(mTmpTrack.getDataTrack());
					mMediaPlayer.prepare();
					mMediaPlayer.start();
					isPause = false;
					mCurrentTrack = mTmpTrack;
					((AudioPlayerApplication)mContext.getApplicationContext()).getAudioPlayerWidgetViewController().showViews(mCurrentTrack);
					for(IPlayerViewController pl:mPlayerViewController) {
						pl.initView(this);
						pl.play();
					}
					mTmpTrack = null;
				} catch (Exception e2) {
					endOfPlaylist();
				}
			}
		} else 
			System.out.println("track NULL !");
	}
	
	public void resume() {
		if(mCurrentTrack!=null)
		try {
			mMediaPlayer.start();
			for(IPlayerViewController pl:mPlayerViewController)
				pl.play();
			isPause = false;
		} catch(Exception e) {}
	}
	
	public void pause() {
		if(mCurrentTrack!=null)
		try { 
			mMediaPlayer.pause();
			for(IPlayerViewController pl:mPlayerViewController)
				pl.pause();
			isPause = true;
		} catch(Exception e) {}
	}
	
	public void stop(boolean withStopView) {
		if(mEqualizer != null) 
			try { mEqualizer.release(); } catch(Exception e) {}
		if(mCurrentTrack!=null)
			try { mMediaPlayer.stop(); } catch(Exception e) {}

		if(mCurrentTrack!=null)
			try { 	mMediaPlayer.release(); } catch(Exception e) {}
		mCurrentTrack = null;	
		if(withStopView) {
			for(IPlayerViewController pl:mPlayerViewController)
				pl.stop();		
			((AudioPlayerApplication)mContext.getApplicationContext()).getAudioPlayerWidgetViewController().hideViews();
			mContext.dismissNotification();
		}
	}
	
	public void destroy() {
		TelephonyManager mgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		if(mgr != null) {
		    mgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		stop(true);
		isDestroyed = true;
		mUpdateTimePlayedThread.interrupt();
		for(IPlayerViewController pl:mPlayerViewController)
			pl.stop();
	}
	
	/** stop et supprime la musique courante */
	public void clearCurrentTrack() {
		//update la vue des mini player (mini, gros, notif, widget)		
		stop(true);
		for(IPlayerViewController pl:mPlayerViewController) {
			if(mTmpTrack==null)
				pl.clearView();
			pl.setTimePlayed(0, 0);
			pl.setPercentTime(1);
			pl.stop();
		}
		mCurrentTrack = null;	
	}
	

	public void initMediaPlayerListener() {
		mMediaPlayer = new MediaPlayer();
		initEqualizer();
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				try {
					realPlayTrack(mPlayListManager.getNextTrack(mCurrentTrack));
				} catch (EndOfPlayListException e) {
					endOfPlaylist();
				}
			}
		});
	}
	
	private void endOfPlaylist() {
		try {
			realPlayTrack(mPlayListManager.getNextTrack(mCurrentTrack));
		} catch (EndOfPlayListException e) {
		    clearCurrentTrack();
		    if(mToastManager!=null)
		    	mToastManager.showMessage(mContext.getResources().getText(R.string.end_playlist).toString());
		}
	}

	public void addViewControler(IPlayerViewController controller) {
		if(!mPlayerViewController.contains(controller))
			mPlayerViewController.add(controller);
		controller.initView(this);
		mContext.dismissNotification();
	}

	public void removeAllViewController() {
		mPlayerViewController.clear();
		//if(mCurrentTrack!=null)
		//	mContext.showNotification();
	}
	
	public void removeViewController(IPlayerViewController controller) {
		mPlayerViewController.remove(controller);
		if(mCurrentTrack!=null && mPlayerViewController.size()==0)
			mContext.showNotification();
	}
	
	public boolean isPlaying() {
		try {
			return mMediaPlayer.isPlaying();
		} catch(Exception e) {
			return false;
		}
	}
	
	/** recupere le pourcentage du temp passe*/
	public int getPercentPlayed() {
		if(mCurrentTrack==null) return 0;
		try {
			int duration = mMediaPlayer.getDuration();
			if(duration>0)
				return (mMediaPlayer.getCurrentPosition()*100)/mMediaPlayer.getDuration();
		}catch(Exception e) {};
		return 0;
	}

	/**recupere le temps passe de la lecture en cours*/
	public long getTimePlayed() {
		if(mCurrentTrack==null) return 0;
		try {
		return mMediaPlayer.getCurrentPosition();
		}catch(Exception e) {return 0;}
	}
	
	/**recupere le temps restant de la lecture en cours*/
	public long getRestTimePlayed() {
		if(mCurrentTrack==null) return 0;
		try {
			return mMediaPlayer.getDuration()-mMediaPlayer.getCurrentPosition();
		} catch(Exception e) {
			return 0;
		}
	}
	
	/** deplace la lecture jusqu'au poucentage de la piste*/
	public void seekToPercent(int percent) {
		if(mCurrentTrack!=null)
		try {
			long millis = (mMediaPlayer.getDuration()*percent)/100;
			mMediaPlayer.seekTo((int) millis);
		} catch(Exception e) {}
	}

	public Track getCurrentTrack() {
		return mCurrentTrack;
	}

	public PlayListManager getPlayListManager() {
		return mPlayListManager;
	}

	public void next() {
		try {
			if(mCurrentTrack!=null)
				realPlayTrack(mPlayListManager.getNextTrack(mCurrentTrack));
		} catch (EndOfPlayListException e) {
			//toast fin de liste
			if(mToastManager!=null)
		    	mToastManager.showMessage(mContext.getResources().getString(R.string.no_next));
		}
	}

	public void last() {
		try {
			if(mCurrentTrack!=null)
				realPlayTrack(mPlayListManager.getPreviousTrack(mCurrentTrack));
		} catch (EndOfPlayListException e) {
			if(mToastManager!=null)
		    	mToastManager.showMessage(mContext.getResources().getString(R.string.no_last));
		}
	}

	public void refreshRandomViews(boolean isRandom) {
		for(IPlayerViewController pl:mPlayerViewController)
			pl.setRandom(isRandom);
	}

	public void refreshRepeatViews(int repeat) {
		for(IPlayerViewController pl:mPlayerViewController)
			pl.setRepeat(repeat);
	}
	
	public MediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}
	
	 private void initEqualizer() {
		 	try {
		 		if(mEqualizer!=null)
		 			mEqualizer.release();
			 	mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
		        boolean active = EqualizerConfigurationDataBase.isActiveEqualizer(mContext);
			 	mEqualizer.setEnabled(active);
			 	if(active) 
			 	{
				    final short bands = mEqualizer.getNumberOfBands();
			        final short minEQLevel = mEqualizer.getBandLevelRange()[0];
			        final short maxEQLevel = mEqualizer.getBandLevelRange()[1];
			      /*  final short maxEQLevel = (short) ( minEQLevel + 
			        		(( maxLevel +  ( minEQLevel < 0 ? -minEQLevel:minEQLevel )) /2 ));*/
			        int defaultValue = ((maxEQLevel-( minEQLevel > 0 ? minEQLevel:-minEQLevel )/2));
				 	int mPreset = EqualizerConfigurationDataBase.getPreset(mContext);
				 	int[] mBandPreset = null;
					if(active && mPreset!=EqualizerConfigurationDataBase.INACTIVE_PRESET)
				 		mBandPreset = new Preset(mPreset).formatPreset(bands, minEQLevel, maxEQLevel);
			        
			        for (short i = 0; i < bands; i++) {
			            final short band = i;
			            int level = defaultValue;
			            if(active) {
			            	if(mPreset==EqualizerConfigurationDataBase.INACTIVE_PRESET)
			            		level = EqualizerConfigurationDataBase.getBandLevel(mContext, i, defaultValue)+(minEQLevel < 0 ? -minEQLevel:minEQLevel);
			            	else
			            		level = /*(minEQLevel < 0 ? -minEQLevel:minEQLevel)-*/(mBandPreset[i] < 0 ? -mBandPreset[i]:mBandPreset[i]);
			            }
			            mEqualizer.setBandLevel(band, (short) (level + minEQLevel));
			        }
			 	}
		 	} catch(Exception e) {}
	 }

	public Equalizer getEqualizer() {
		return mEqualizer;
	}
}
