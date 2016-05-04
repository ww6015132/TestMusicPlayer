package fr.julien_dumortier.simplemusicplayer.item.itemadapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.IMusicMediaControllerGetter;
import fr.julien_dumortier.simplemusicplayer.MainActivity;
import fr.julien_dumortier.simplemusicplayer.browsemanager.BrowseManager;
import fr.julien_dumortier.simplemusicplayer.browsemanager.BrowseManager.OnBrowseListener;
import fr.julien_dumortier.simplemusicplayer.browsemanager.BrowseManager.OnTrackBrowseListener;
import fr.julien_dumortier.simplemusicplayer.coveralbumeditor.CoverAlbumEditor;
import fr.julien_dumortier.simplemusicplayer.coveralbumeditor.CoverAlbumGetter;
import fr.julien_dumortier.simplemusicplayer.coveralbumeditor.CoverAlbumGetter.IOnLinksGettedListener;
import fr.julien_dumortier.simplemusicplayer.coveralbumeditor.PromptCovers;
import fr.julien_dumortier.simplemusicplayer.coveralbumeditor.PromptCovers.IOnCoverSelectedListener;
import fr.julien_dumortier.simplemusicplayer.item.Album;
import fr.julien_dumortier.simplemusicplayer.item.Artist;
import fr.julien_dumortier.simplemusicplayer.item.Genre;
import fr.julien_dumortier.simplemusicplayer.item.Item;
import fr.julien_dumortier.simplemusicplayer.item.Playlist;
import fr.julien_dumortier.simplemusicplayer.item.Track;
import fr.julien_dumortier.simplemusicplayer.playlistview.SavedPlaylistManager;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptActionByType;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptConfirmAddInNew;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptActionByType.IManageItemAction;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class ItemClicManager implements OnItemClickListener, OnItemLongClickListener, IManageItemAction, 
	IOnLinksGettedListener, IOnCoverSelectedListener {

	private MainActivity mMainActivity;
	private List<Item> mItems;
	private OnBrowseListener mOnBrowseListener;
	private IMusicMediaControllerGetter mMusicMediaController;
	private Item mSelectedOnLongClickItem;
	private Handler mHandler;
	private ProgressDialog mProgress;
	private ProgressDialog mProgressDialog;
	
	@SuppressLint("HandlerLeak")
	public ItemClicManager(MainActivity context, List<Item> items, IMusicMediaControllerGetter getter, OnBrowseListener onBrowseListener) {
		mMainActivity = context;
		mItems = items;
		mOnBrowseListener = onBrowseListener;
		mMusicMediaController = getter;
		mSelectedOnLongClickItem = null;
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				if(msg.what==43) {				
					mMusicMediaController.getMusicMediaPlayer().getPlayListManager().removeAllTracks(false);
					mMusicMediaController.getMusicMediaPlayer().getPlayListManager().addTrackAndRun((Track) msg.obj);
					mProgress.dismiss();
				}
				if(msg.what==44) {
					@SuppressWarnings("unchecked")
					final List<Track> items = (List<Track>) msg.obj;					
					mMusicMediaController.getMusicMediaPlayer().getPlayListManager().addTracks(items);
				}
			}
		};
	}
	
	
	/*
	 * Genre -> tracks
	 * Artist -> Albums
	 * Albums -> tracks
	 * Tracks -> proposition
	 */
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		if(!(pos==0 && mItems.get(pos).getId().equals("-5"))) {
			mSelectedOnLongClickItem = mItems.get(pos);
			new PromptActionByType(mMainActivity, (mSelectedOnLongClickItem instanceof Playlist), (mSelectedOnLongClickItem instanceof Album || mSelectedOnLongClickItem instanceof Track),  this).show();
		}
		return true;
	}
	
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		Item item = mItems.get(pos);
		if(mMainActivity.mLastGridView != null)
			mMainActivity.mLastGridView.clear();
		mMainActivity.mLastGridView = null;
		if(!(item instanceof Track)) {
			mMainActivity.mLastGridView = new SoftReference<GridView>(mMainActivity.mGridView);
		}
		if(pos==0 && item.getId().equals("-5")) //lire tout
		{
			new PromptConfirmAddInNew(mMainActivity, new PromptConfirmAddInNew.IOnConfirmAddInNew() {
				
				public void onConfirmAddInNew() {
					mProgress = new ProgressDialog(mMainActivity);
					mProgress.setMessage(mMainActivity.getResources().getString(R.string.load));
					mProgress.show();
					mProgress.setCanceledOnTouchOutside(false);
					new Thread("browse all track") { public void run() {
						BrowseManager.browseTrack(mMainActivity, false, new OnBrowseListener() {
							public void onTrackBrowsed(ArrayList<Item> tracks) {}
							public void onBrowseGenreFinish(ArrayList<Item> items) {}				
							public void onBrowseArtistFinish(ArrayList<Item> items) {}				
							public void onBrowseAlbumFinish(ArrayList<Item> items) {}
							
							public void onBrowseTrackFinish(final ArrayList<Item> items) {
								List<Track> tr = new ArrayList<Track>();
								int nb = 0;
								for(Item it:items) {
									((Track)it).initPicture();
									tr.add((Track)it);
									if(++nb==1) {
										Message msg = new Message();
										msg.what = 43;
										msg.obj = it;
										mHandler.sendMessage(msg);
									}
								}
								if(tr.size()>0)
									tr.remove(0);
								Message msg = new Message();
								msg.what = 44;
								msg.obj = tr;
								mHandler.sendMessage(msg);					
							}				
						});
					}}.start();
				}
			}).show();
			
		}
		else {
			startBrowseBySelectedItem(item, mOnBrowseListener);
		}
	}
	
	public void startBrowseBySelectedItem(Item item, OnBrowseListener listener) {
		//playlist
		if(item instanceof Playlist) {

			MainActivity.selectedItem = item;
			String[] set = null;
			if(((Playlist)item).isLastPlaylist())
				set = SavedPlaylistManager.getTracksIdInLastPlayList(mMainActivity);
			else
				set = SavedPlaylistManager.getTracksIdByPlaylistName(mMainActivity, ((Playlist)item).getPlayListName());
			List<String> list = new ArrayList<String>();
			for(String tmp:set) {
				list.add(tmp);
			}
			BrowseManager.browseTracksByIds(mMainActivity, list, listener, item);
		}
		//album
		if(item instanceof Album) {
			MainActivity.selectedItem = item;
			BrowseManager.browseTrackByAlbum(mMainActivity, listener, (Album)item);
		}
		
		//artist
		if(item instanceof Artist) {
			MainActivity.selectedItem = item;
			BrowseManager.browseAlbumByArtistName(mMainActivity, listener, ((Artist)item));
		}
		
		//genre
		if(item instanceof Genre) {
			MainActivity.selectedItem = item;
			BrowseManager.browseTrackByGenreId(mMainActivity, listener, item);
		}
		
		//track
		if(item instanceof Track && mMusicMediaController.getMusicMediaPlayer()!=null) {
			mMusicMediaController.getMusicMediaPlayer().getPlayListManager().addTrackAndRun((Track) item);
		}
	}
	
	public static boolean startParentBrowseByItemType(MainActivity mainActivity, Item item, OnBrowseListener listener) {
		//album
		if(item instanceof Album) {
			if(item.getParent()!=null) {
				BrowseManager.browseAlbumByArtistName(mainActivity, listener, ((Artist)item.getParent()));
				MainActivity.selectedItem = item.getParent();
			}
			else {
				BrowseManager.browseAlbum(mainActivity, listener);
				MainActivity.selectedItem = null;
			}
			return true;
		}
		//artist
		if(item instanceof Artist) {
			BrowseManager.browseArtist(mainActivity, listener);
			MainActivity.selectedItem = null;
			return true;
		}
		//genre
		if(item instanceof Genre) {
			BrowseManager.browseGenre(mainActivity, listener);
			MainActivity.selectedItem = null;
			return true;
		}
		
		//playlist
		if(item instanceof Playlist) {
			MainActivity.selectedItem = null;
			mainActivity.onPlaylistBrowse(SavedPlaylistManager.getAllPlaylistName(mainActivity, true));
			return true;
		}
		
		//track
		if(item instanceof Track) {
			Item parent = item.getParent();
			//album
			if(parent instanceof Album) {
				if(parent.getParent()!=null) {
					BrowseManager.browseAlbumByArtistName(mainActivity, listener, ((Artist)parent.getParent()));
					MainActivity.selectedItem = parent.getParent();
				}
				else {
					BrowseManager.browseAlbum(mainActivity, listener);
					MainActivity.selectedItem = null;
				}
				return true;
			}
			//genre
			if(parent instanceof Genre) {
				BrowseManager.browseTrackByGenreId(mainActivity, listener, parent);
				MainActivity.selectedItem = null;
				return true;
			}
			
			//playlist
			if(parent instanceof Playlist) {
				MainActivity.selectedItem = null;
				mainActivity.onPlaylistBrowse(SavedPlaylistManager.getAllPlaylistName(mainActivity, true));
				return true;
			}
		}
		return true;
	}

	public void addAndPlayNow() {
		getTracksByItem(mSelectedOnLongClickItem, new OnTrackBrowsed() {
			public void onTracksBrowsed(List<Track> tracks) {
				mMusicMediaController.getMusicMediaPlayer().getPlayListManager().addTracks(tracks);
				if(tracks.size()>0)
					mMusicMediaController.getMusicMediaPlayer().playTrack(tracks.get(0));
			}
		});
	}

	public void addInNewAndPlay() {
		getTracksByItem(mSelectedOnLongClickItem, new OnTrackBrowsed() {
			public void onTracksBrowsed(List<Track> tracks) {
				mMusicMediaController.getMusicMediaPlayer().getPlayListManager().removeAllTracks(false);
				mMusicMediaController.getMusicMediaPlayer().getPlayListManager().addTracks(tracks);
			}
		});
	}

	public void addInList() {
		getTracksByItem(mSelectedOnLongClickItem, new OnTrackBrowsed() {
			public void onTracksBrowsed(List<Track> tracks) {
				mMusicMediaController.getMusicMediaPlayer().getPlayListManager().addTracks(tracks);	
			}
		});	
	}

	public void deletePlaylist() {
		if(mSelectedOnLongClickItem instanceof Playlist) {
			if(((Playlist)mSelectedOnLongClickItem).isLastPlaylist())
				SavedPlaylistManager.removeLastPlaylist(mMainActivity);
			else
				SavedPlaylistManager.removePlaylistByName(mMainActivity, ((Playlist)mSelectedOnLongClickItem).getPlayListName());				
			mMainActivity.updatePlaylist();
		}
	}
	
	private void getTracksByItem(Item item, final OnTrackBrowsed listener) {
		//playlist
		if(item instanceof Playlist) {
			String[] set = null;
			if(((Playlist)item).isLastPlaylist())
				set = SavedPlaylistManager.getTracksIdInLastPlayList(mMainActivity);
			else
				set = SavedPlaylistManager.getTracksIdByPlaylistName(mMainActivity, ((Playlist)item).getPlayListName());
			List<String> list = new ArrayList<String>();
			for(String tmp:set) {
				list.add(tmp);
			}
			BrowseManager.browseTracksByIds(mMainActivity, list, new OnTrackBrowseListener() {
				public void onTrackBrowsed(ArrayList<Item> tracks) {
					List<Track> tr = new ArrayList<Track>();
					for(Item it:tracks) {
						((Track)it).initPicture();
						tr.add((Track)it);
					}
					listener.onTracksBrowsed(tr);
				}
			}, null);
		}
		//album
		if(item instanceof Album) {
			BrowseManager.browseTrackByAlbum(mMainActivity, new ListenerBrowse(listener), (Album)item);
		}
		
		//artist
		if(item instanceof Artist) {
			BrowseManager.browseTrackByArtistName(mMainActivity, new ListenerBrowse(listener), ((Artist)item));
		}
		//genre
		if(item instanceof Genre) {
			BrowseManager.browseTrackByGenreId(mMainActivity, new ListenerBrowse(listener), item);
		}
		
		//track
		if(item instanceof Track) {
			ArrayList<Track> list = new ArrayList<Track>();
			list.add((Track)item);
			listener.onTracksBrowsed(list);
		}
	}
	
	public interface OnTrackBrowsed {
		public void onTracksBrowsed(List<Track> tracks);
	}
	
	public class ListenerBrowse implements OnBrowseListener {

		private OnTrackBrowsed mListenerListenerBrowse;
		
		public ListenerBrowse(OnTrackBrowsed listenerListenerBrowse) {
			mListenerListenerBrowse = listenerListenerBrowse;
		}
		
		public void onTrackBrowsed(ArrayList<Item> tracks) {
			List<Track> tr = new ArrayList<Track>();
			for(Item it:tracks) {
				((Track)it).initPicture();
				tr.add((Track)it);
			}
			mListenerListenerBrowse.onTracksBrowsed(tr);
		}

		public void onBrowseTrackFinish(ArrayList<Item> tracks) {
			List<Track> tr = new ArrayList<Track>();
			for(Item it:tracks) {
				((Track)it).initPicture();
				tr.add((Track)it);
			}
			mListenerListenerBrowse.onTracksBrowsed(tr);
		}

		public void onBrowseAlbumFinish(ArrayList<Item> items) {}

		public void onBrowseGenreFinish(ArrayList<Item> items) {}

		public void onBrowseArtistFinish(ArrayList<Item> items) {}
		
	}

	public void defineCover(String url) {
		CoverAlbumEditor.startUpdateCoverAlbum(mMainActivity, url, mSelectedOnLongClickItem, mMainActivity);
	}

	public void startDefineCoverAuto() {
		mProgressDialog = ProgressDialog.show(mMainActivity, mMainActivity.getResources().getString(R.string.load), 
				mMainActivity.getResources().getString(R.string.get_jacket));
		mProgressDialog.setCanceledOnTouchOutside(false);
		new CoverAlbumGetter().startGetCoversLinksByItem(mSelectedOnLongClickItem, this);
	}

	public void onLinkGetted(final List<String> links) {
		mProgressDialog.cancel();
		mMainActivity.runOnUiThread(new Runnable() {
			public void run() {
				new PromptCovers(mMainActivity, links, ItemClicManager.this).show();
			}
		});
	}


	public void onCoverSelected(Bitmap bmp) {
		try {
			CoverAlbumEditor.updateCoverAlbum(mMainActivity, bmp, mSelectedOnLongClickItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void error() {
		mProgressDialog.cancel();
	}
}
