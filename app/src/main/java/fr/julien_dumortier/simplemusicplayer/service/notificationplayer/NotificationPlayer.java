package fr.julien_dumortier.simplemusicplayer.service.notificationplayer;

import fr.julien_dumortier.simplemusicplayer.R;

import fr.julien_dumortier.simplemusicplayer.MainActivity;
import fr.julien_dumortier.simplemusicplayer.playerview.IPlayerViewController;
import fr.julien_dumortier.simplemusicplayer.service.MusicPlayerService;
import fr.julien_dumortier.simplemusicplayer.service.playlist.MusicMediaPlayer;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

@SuppressWarnings("deprecation")
public class NotificationPlayer implements IPlayerViewController {

	public static final String LAST = "last", NEXT = "next", PLAY = "play", DELETE = "delete";
	private MusicPlayerService mContext;
	private RemoteViews mRemoteViews;
	private NotificationManager mNotificationManager;
	private boolean isPlayingMode;
	
	public NotificationPlayer(MusicPlayerService context) {
		try {
		mContext = context;
		
		mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout); 
		
		Intent intent = new Intent(context, NotificationManagerService.class); 
		
		intent.setAction(LAST);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 1);
		mRemoteViews.setOnClickPendingIntent(R.id.button_last, pendingIntent);

		Intent intent2 = new Intent(context, NotificationManagerService.class); 
		intent2.setAction(NEXT);
		pendingIntent = PendingIntent.getService(context, 1, intent2, 1);
		mRemoteViews.setOnClickPendingIntent(R.id.button_next, pendingIntent);

		Intent intent3 = new Intent(context, NotificationManagerService.class); 
		intent3.setAction(PLAY);
		pendingIntent = PendingIntent.getService(context, 2, intent3, 1);
		mRemoteViews.setOnClickPendingIntent(R.id.button_play, pendingIntent);
		
		BitmapDrawable dr = (BitmapDrawable) mContext.getMusicMediaPlayer().getCurrentTrack().getPicture();
				
		mRemoteViews.setImageViewBitmap(R.id.image, dr.getBitmap());
		mRemoteViews.setTextViewText(R.id.mini_track_name, mContext.getMusicMediaPlayer().getCurrentTrack().getTrackName());
		mRemoteViews.setTextViewText(R.id.mini_artist_name, mContext.getMusicMediaPlayer().getCurrentTrack().getArtistName());

		
	//	Intent intentResumeActivity = new Intent(context, MainActivity.class);
		PendingIntent resumePlayerIntent = PendingIntent.getActivity(context, 3, new Intent(context, MainActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_CANCEL_CURRENT);// PendingIntent.getActivity(mContext, 0, intentResumeActivity, 0);
		
		Intent intentDeleteActivity = new Intent(context, NotificationManagerService.class);
		intentDeleteActivity.setAction(DELETE);
		PendingIntent deletePlayerIntent = PendingIntent.getActivity(mContext, 4, intentDeleteActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_CANCEL_CURRENT);
		
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		.setContent(mRemoteViews).setSmallIcon(R.drawable.ic_casque_mini).setContentIntent(resumePlayerIntent).setDeleteIntent(deletePlayerIntent);
		
		mNotificationManager = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 

		mNotificationManager.notify(20, mBuilder.getNotification());
		NotificationManagerService.mMusicMediaPlayer = mContext.getMusicMediaPlayer();
		mContext.getMusicMediaPlayer().addViewControler(this);
		}catch(Exception e){}
	}

	public void initView(MusicMediaPlayer mediaPlayer) {
		try{
			if(mediaPlayer.getCurrentTrack()!=null) {
				mRemoteViews.setTextViewText(R.id.mini_track_name, mediaPlayer.getCurrentTrack().getTrackName());
				mRemoteViews.setTextViewText(R.id.mini_artist_name, mediaPlayer.getCurrentTrack().getArtistName());
				BitmapDrawable dr = (BitmapDrawable) mediaPlayer.getCurrentTrack().getPicture();
				mRemoteViews.setImageViewBitmap(R.id.image, dr.getBitmap());
				if(mediaPlayer.isPlaying()) 
					mRemoteViews.setImageViewResource(R.id.button_play, R.drawable.pause_button);
				else
					mRemoteViews.setImageViewResource(R.id.button_play, R.drawable.play_button);
				isPlayingMode = mediaPlayer.isPlaying();
				
				Intent intentResumeActivity = new Intent(mContext, MainActivity.class);
				PendingIntent resumePlayerIntent = PendingIntent.getActivity(mContext, 5, intentResumeActivity, 0);
				
				Intent intent = new Intent(mContext, NotificationManagerService.class); 			
				intent.setAction(DELETE);
				PendingIntent pendingIntent = PendingIntent.getService(mContext, 6, intent, 1);
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
				.setContent(mRemoteViews).setSmallIcon(R.drawable.ic_casque_mini)
				.setContentIntent(resumePlayerIntent).setDeleteIntent(pendingIntent);
				
				mNotificationManager = 
						(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE); 
				mNotificationManager.notify(20, mBuilder.getNotification());
				NotificationManagerService.mMusicMediaPlayer = mediaPlayer;
			}
		}catch(Exception e){}
	}

	public void play() {
		try {
			if(!isPlayingMode && mContext.getMusicMediaPlayer().getCurrentTrack()!=null)
			{
				isPlayingMode = true;
				mContext.getMusicMediaPlayer().resume();
				mRemoteViews.setImageViewResource(R.id.button_play, R.drawable.pause_button);
	
				Intent intentResumeActivity = new Intent(mContext, MainActivity.class);
				PendingIntent resumePlayerIntent = PendingIntent.getActivity(mContext, 7, intentResumeActivity, 0);
				
				Intent intent = new Intent(mContext, NotificationManagerService.class); 			
				intent.setAction(DELETE);
				PendingIntent pendingIntent = PendingIntent.getService(mContext, 8, intent, 1);
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
				.setContent(mRemoteViews).setSmallIcon(R.drawable.ic_casque_mini)
				.setContentIntent(resumePlayerIntent).setDeleteIntent(pendingIntent);
				
				mNotificationManager = 
						(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE); 
				mNotificationManager.notify(20, mBuilder.getNotification());
			}
		}catch(Exception e){}
	}

	public void pause() {
		try {
			if(isPlayingMode)
			{
				isPlayingMode = false;
				mContext.getMusicMediaPlayer().pause();
				mRemoteViews.setImageViewResource(R.id.button_play, R.drawable.play_button);
	
				Intent intentResumeActivity = new Intent(mContext, MainActivity.class);
				PendingIntent resumePlayerIntent = PendingIntent.getActivity(mContext, 9, intentResumeActivity, 0);
				
				Intent intent = new Intent(mContext, NotificationManagerService.class); 			
				intent.setAction(DELETE);
				PendingIntent pendingIntent = PendingIntent.getService(mContext, 10, intent, 1);
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
				.setContent(mRemoteViews).setSmallIcon(R.drawable.ic_casque_mini)
				.setContentIntent(resumePlayerIntent).setDeleteIntent(pendingIntent);
				
				mNotificationManager = 
						(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE); 
				mNotificationManager.notify(20, mBuilder.getNotification());
			}
		}catch(Exception e){}
	}

	public void clearView() {
		dismiss();
	}

	public void stop() {}

	public void setPercentTime(int percentPlayed) {}

	public void setTimePlayed(long timePlayed, long restTimePlayed) {}

	public void setRepeat(int repeatMode) {}

	public void setRandom(boolean b) {}

	public void dismiss() {
		try {
			mContext.getMusicMediaPlayer().removeViewController(this);
			mNotificationManager.cancel(20);
		}catch(Exception e){}
	}
}
