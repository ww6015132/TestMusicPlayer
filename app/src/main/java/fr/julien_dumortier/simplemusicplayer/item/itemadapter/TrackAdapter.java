package fr.julien_dumortier.simplemusicplayer.item.itemadapter;

import java.util.List;

import fr.julien_dumortier.simplemusicplayer.MainActivity;
import fr.julien_dumortier.simplemusicplayer.R;

import fr.julien_dumortier.simplemusicplayer.browsemanager.BrowseCover;
import fr.julien_dumortier.simplemusicplayer.item.Item;
import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.item.Item.IOnPictureReady;
import fr.julien_dumortier.simplemusicplayer.thememanager.DisplayManager2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

public class TrackAdapter extends ItemAdapter {	
	private Context mContext;
	private Typeface mFont;
	public TrackAdapter(final Context c, List<Item> tracks, final IOnPictureReady listener) {
		mItems = tracks;	
		mInflater = LayoutInflater.from(c);
		mContext = c;
		mFont = Typeface.createFromAsset(c.getAssets(), MainActivity.ROBOTO_TTF);
		new Thread("thumbnail") { public void run() {
			for(int i =0; i<mItems.size();i++) {
				Track art = (Track) mItems.get(i);
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
	
	public TrackAdapter(Context c, List<Item> tracks) {
		mInflater = LayoutInflater.from(c);
		mItems = tracks;		
	}

	static int nb=0;
	public View getView(int position, View view, ViewGroup parent) {
		if(view==null) 
		{
			if(DisplayManager2.doUseDarkTextColor(mContext)) {
				view = mInflater.inflate(R.layout.dark_layout_item_track, null);
			}
			else
				view = mInflater.inflate(R.layout.layout_item_track, null);
		}
		((ImageView)view.findViewById(R.id.img)).setImageDrawable(mItems.get(position).getPicture());
		((TextView)view.findViewById(R.id.txt)).setText(((Track)mItems.get(position)).getTrackName());
		((TextView)view.findViewById(R.id.txt)).setTypeface(mFont);
		((TextView)view.findViewById(R.id.duration_and_artist)).setText(((Track)mItems.get(position)).getArtistName());
		((TextView)view.findViewById(R.id.duration_and_artist)).setTypeface(mFont);
		
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 135);
		view.setLayoutParams(params);
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
		}
		return view;
	}
	
}