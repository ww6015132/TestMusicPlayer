package fr.julien_dumortier.simplemusicplayer.coveralbumeditor;

import java.util.List;
import fr.julien_dumortier.simplemusicplayer.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class PromptCovers extends AlertDialog {
	
	private Activity mContext; 
    private ViewFlipper viewFlipper;
    private float lastX;
    private List<String> mUrls;
    private IOnCoverSelectedListener mListener;
    
    public interface IOnCoverSelectedListener {
    	public void onCoverSelected(Bitmap bmp);
    }

	public PromptCovers(Activity context, List<String> coverUrls, IOnCoverSelectedListener listener) {
		super(context);
		mListener = listener;
		mContext = context;
		mUrls = coverUrls;
		setTitle(context.getResources().getString(R.string.cover_choice));
		viewFlipper = new ViewFlipper(mContext);
		Toast.makeText(mContext, mContext.getResources().getString(R.string.swipe_to_choice), Toast.LENGTH_LONG).show();
		for(int i=0; i<mUrls.size(); i++) {
			View v = LayoutInflater.from(context).inflate(R.layout.prompt_covers, null);
			viewFlipper.addView(v, i);
		}

		startSetCoverInImageView();
		
		setView(viewFlipper);
		setButton(context.getResources().getText(R.string.ok), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(viewFlipper!=null) {
					View v = viewFlipper.getCurrentView();
					if(v!=null) {
						ImageView iv = (ImageView) v.findViewById(R.id.cover_image);
						Bitmap bmp = null;
						if(iv.getDrawable()!=null)
							bmp = ((BitmapDrawable)iv.getDrawable()).getBitmap();
						
						if(bmp == null) {
							int realPos = 0;
							int size = viewFlipper.getChildCount();
							for(int pos = 0;pos<size;pos++) {
								if(v.equals(getChildPos(pos)))
									realPos=pos;	
							}
							try {
								bmp = getBitmapFromUrl(mUrls.get(realPos));
							} catch (Exception e) {}
						}
						if(bmp!=null)
							mListener.onCoverSelected(bmp);
					}
				}
			}
		});
		setButton2(context.getResources().getText(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}});
		
	}
	
	public boolean onTouchEvent(MotionEvent touchevent) 
    {
             switch (touchevent.getAction())
             {
                    // when user first touches the screen to swap
                     case MotionEvent.ACTION_DOWN: 
                     {
                         lastX = touchevent.getX();
                         break;
                    }
                     case MotionEvent.ACTION_UP: 
                     {
                         float currentX = touchevent.getX();
                         
                         // if left to right swipe on screen
                         if (lastX < currentX-20) 
                         {
                              // If no more View/Child to flip
                             if (viewFlipper.getDisplayedChild() == 0)
                                 break;
                             
                             // set the required Animation type to ViewFlipper
                             // The Next screen will come in form Left and current Screen will go OUT from Right 
                             viewFlipper.setInAnimation(mContext, R.anim.in_from_left);
                             viewFlipper.setOutAnimation(mContext, R.anim.out_to_right);
                             // Show the next Screen
                             viewFlipper.showNext();
                         }
                         
                         // if right to left swipe on screen
                         if (lastX > currentX+20)
                         {
                             if (viewFlipper.getDisplayedChild() == 1)
                                 break;
                             // set the required Animation type to ViewFlipper
                             // The Next screen will come in form Right and current Screen will go OUT from Left 
                             viewFlipper.setInAnimation(mContext, R.anim.in_from_right);
                             viewFlipper.setOutAnimation(mContext, R.anim.out_to_left);
                             // Show The Previous Screen
                             viewFlipper.showPrevious();
                         }
                         break;
                     }
             }
             return false;
    }
	
	private int mCurrentPos;
	private Bitmap mTmpBitmap;
	private void startSetCoverInImageView() {
		new Thread("Thread get cover at pos"+mCurrentPos) {
			public void run() {
				int size = viewFlipper.getChildCount();
				for(int pos = 0; pos<size;pos++) {
					View v = getChildPos(pos);
					final ImageView coverImage = (ImageView)v.findViewById(R.id.cover_image);
					final ImageView loadImage = (ImageView) v.findViewById(R.id.load);
					
					mContext.runOnUiThread(new Runnable() {
						public void run() {
							loadImage.setVisibility(View.VISIBLE);
							final Animation rotateAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_animation);
							rotateAnimation.setRepeatCount(Animation.INFINITE);
							loadImage.startAnimation(rotateAnimation);
						}
					});
					try {
						mTmpBitmap = getBitmapFromUrl(mUrls.get(pos));
					} catch (Exception e) {
						mTmpBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.failed_job);
					}
					mContext.runOnUiThread(new Runnable() {
						public void run() {
							coverImage.setImageBitmap(mTmpBitmap);
							loadImage.setVisibility(View.INVISIBLE);
							loadImage.clearAnimation();
						}
					});
				}
			}
		}.start();
	}
	
	private View getChildPos(int pos) {
		if(pos==0)
			return viewFlipper.getChildAt(0);
		return viewFlipper.getChildAt(viewFlipper.getChildCount()-pos);
	}
	
	private static Bitmap getBitmapFromUrl(String url) throws Exception {
		byte [] data = CoverAlbumEditor.downloadUrlToByteArray(url);
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
}
