package fr.julien_dumortier.simplemusicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerListAdapter extends BaseAdapter {

    private static String [] mTitles;
    public static int [] ID_PICTURES = {R.drawable.ic_playlist, R.drawable.ic_album, R.drawable.ic_artist, R.drawable.ic_genre, R.drawable.ic_track};
    private static Bitmap [] mPicturesCache;
    private LayoutInflater mInflater;
    
    public DrawerListAdapter(Context c) {
    	mInflater = LayoutInflater.from(c);
    	mTitles = c.getResources().getStringArray(R.array.menu_list);
    	if(mPicturesCache==null) {
    		mPicturesCache = new Bitmap[ID_PICTURES.length];
	    	for(int i=0;i<ID_PICTURES.length; i++) {
	    		try {
		    		Bitmap bmp = BitmapFactory.decodeResource(c.getResources(), ID_PICTURES[i]);
		    		mPicturesCache[i] = Bitmap.createScaledBitmap(bmp, 120, 120, true);
		    		bmp.recycle();
	    		} catch (Throwable e) {}
	    	}
    	}
    }
    
	public int getCount() {
		return mTitles.length;
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int pos, View v, ViewGroup arg2) {
		if(v==null)
			v = mInflater.inflate(R.layout.drawer_list_item, null);
		((TextView)v.findViewById(R.id.title)).setText(mTitles[pos]);
		((ImageView)v.findViewById(R.id.picture)).setImageBitmap(mPicturesCache[pos]);
		return v;
	}
}
