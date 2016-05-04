package fr.julien_dumortier.simplemusicplayer.item.itemadapter;

import java.util.List;
import fr.julien_dumortier.simplemusicplayer.item.Item;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class ItemAdapter extends BaseAdapter {
	protected LayoutInflater mInflater;
	protected List<Item> mItems;
	
	public int getCount() {
		return mItems.size();
	}

	public Object getItem(int position) {
		return null;
	}
		
	public long getItemId(int position) {
		return 0;
	}
}
