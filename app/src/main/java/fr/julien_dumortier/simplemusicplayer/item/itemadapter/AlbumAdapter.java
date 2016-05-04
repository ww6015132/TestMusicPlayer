package fr.julien_dumortier.simplemusicplayer.item.itemadapter;

import java.util.List;

import fr.julien_dumortier.simplemusicplayer.MainActivity;
import fr.julien_dumortier.simplemusicplayer.R;

import fr.julien_dumortier.simplemusicplayer.browsemanager.BrowseCover;
import fr.julien_dumortier.simplemusicplayer.item.Album;
import fr.julien_dumortier.simplemusicplayer.item.Item;
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

public class AlbumAdapter extends ItemAdapter {
	private Context mContext;
	private Typeface mFont;
	public AlbumAdapter(final Context c, List<Item> albums, final IOnPictureReady listener) {
		mInflater = LayoutInflater.from(c);
		mItems = albums;
		mContext = c;
		mFont = Typeface.createFromAsset(c.getAssets(), MainActivity.ROBOTO_TTF);
		new Thread("thumbnail") { public void run() {
			for(int i =0; i<mItems.size();i++) {
				Album art = (Album) mItems.get(i);
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
	static int nb;
	public View getView(int position, View view, ViewGroup parent) {
		if(view==null) {
			if(DisplayManager2.doUseDarkTextColor(mContext)) {
				view = mInflater.inflate(R.layout.dark_layout_item, null);
			}
			else
				view = mInflater.inflate(R.layout.layout_item, null);
		}
		((ImageView)view.findViewById(R.id.img)).setImageDrawable(((Album)mItems.get(position)).getPicture());
		((TextView)view.findViewById(R.id.txt)).setText(((Album)mItems.get(position)).getAlbumName());
		((TextView)view.findViewById(R.id.txt)).setTypeface(mFont);
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 125);		
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