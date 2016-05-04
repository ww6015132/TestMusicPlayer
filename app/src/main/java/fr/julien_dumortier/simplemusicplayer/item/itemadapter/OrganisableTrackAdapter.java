package fr.julien_dumortier.simplemusicplayer.item.itemadapter;

import java.util.List;

import fr.julien_dumortier.simplemusicplayer.MainActivity;
import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.browsemanager.BrowseCover;
import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.item.Item.IOnPictureReady;
import fr.julien_dumortier.simplemusicplayer.playerview.TimeFormater;
import fr.julien_dumortier.simplemusicplayer.playlistview.MultiSelectManager;
import fr.julien_dumortier.simplemusicplayer.service.playlist.IMediaPlayerController;
import fr.julien_dumortier.simplemusicplayer.thememanager.DisplayManager2;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrganisableTrackAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Track> mTracks;
	private CurrentPlayAnimationManager mCurrentPlayAnimationManager;
	private IMediaPlayerController mMediaPlayer;
	private Typeface mFont;
	private MultiSelectManager mMultiSelectManager;
	
	public OrganisableTrackAdapter(MultiSelectManager multiSelectManager, final Context c, List<Track> tracks, final IOnPictureReady listener, IMediaPlayerController mediaPlayer, CurrentPlayAnimationManager currentPlayAnimationManager) {
		mInflater = LayoutInflater.from(c);
		mMultiSelectManager = multiSelectManager;
		mMediaPlayer = mediaPlayer;
		mContext = c;
		mFont = Typeface.createFromAsset(c.getAssets(), MainActivity.ROBOTO_TTF);
		mCurrentPlayAnimationManager = currentPlayAnimationManager;
		mTracks = tracks;		new Thread("thumbnail") { public void run() {
			for(int i =0; i<mTracks.size();i++) {
				Track art = (Track) mTracks.get(i);
				if(art.isDefaultPicture && art.mStringPicture!=null && !art.mStringPicture.equals("")) {
					Drawable dr = BrowseCover.getDrawableFromUri(c, art.mStringPicture, art.getDefaultDrawable());
					if(dr!=null) {
						art.setPicture(dr);
						listener.onPictureReady(art.getPicture(), i);
					}
					art.isDefaultPicture=false;
				}
			}
		}}.start();
	}

	public int getCount() {
		return mTracks.size();
	}

	public Object getItem(int position) {
		return null;
	}
		
	public long getItemId(int position) {
		return 0;
	}

	static int nb;
	
	public View getView(int position, View view, ViewGroup parent) {
		if(view==null || mTracks.get(position) == mMediaPlayer.getCurrentTrack() || Integer.toString(mCurrentPlayAnimationManager.getPos()).equals(view.getTag())) 
		{
			if(DisplayManager2.doUseDarkTextColor(mContext)) {
				view = mInflater.inflate(R.layout.dark_layout_item_track, null);
			}
			else
				view = mInflater.inflate(R.layout.layout_item_track, null);
		}
		((ImageView)view.findViewById(R.id.img)).setImageDrawable(mTracks.get(position).getPicture());
		((TextView)view.findViewById(R.id.txt)).setText(mTracks.get(position).getTrackName());
		((TextView)view.findViewById(R.id.txt)).setTypeface(mFont);
		String duration = TimeFormater.getFormatedTime(mTracks.get(position).getDuration());
		((TextView)view.findViewById(R.id.duration_and_artist)).setText(duration+" - "+mTracks.get(position).getArtistName());
		((TextView)view.findViewById(R.id.duration_and_artist)).setTypeface(mFont);
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 135);
		view.setLayoutParams(params);
		if(mTracks.get(position) == mMediaPlayer.getCurrentTrack()) {
			mCurrentPlayAnimationManager.addViewAndstartAnimation(view, position);
			view.setTag(Integer.toString(position));
		}
		if(mMultiSelectManager.isActiveMultiSelect()) {
			if(mMultiSelectManager.contains(position))
				view.setBackgroundColor(Color.rgb(183, 183, 183));
			else
				view.setBackgroundColor(Color.TRANSPARENT);
		} else {
			if(DisplayManager2.getActiveColorAlternate(mContext)) {
				if(DisplayManager2.getNbColumn(mContext)==2) {
					if(nb==0||nb==3) {
						if(DisplayManager2.doUseDarkTextColor(mContext))
							view.setBackgroundColor(-11184811);
						else
							view.setBackgroundColor(-2171170);
					}
					else
						view.setBackgroundColor(Color.TRANSPARENT);
					if(++nb>3)
						nb=0;
				} else {
					if(position%2==0)
						if(DisplayManager2.doUseDarkTextColor(mContext))
							view.setBackgroundColor(-11184811);
						else
							view.setBackgroundColor(-2171170);
					else
						view.setBackgroundColor(Color.TRANSPARENT);
				}
			} else 
				view.setBackgroundColor(Color.TRANSPARENT);
		}
		return view;
	}
}