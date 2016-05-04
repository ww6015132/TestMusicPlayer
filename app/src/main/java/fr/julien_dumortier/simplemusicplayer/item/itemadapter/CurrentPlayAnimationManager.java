package fr.julien_dumortier.simplemusicplayer.item.itemadapter;

import fr.julien_dumortier.simplemusicplayer.R;

import fr.julien_dumortier.simplemusicplayer.thememanager.DisplayManager2;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

public class CurrentPlayAnimationManager {
	private View mCurrentViewAnimated;
	private View v, v2, v3;
	private int mCurrentPos;
	private AnimationListener mList, mList2 ,mList3;
	private Context mContext;
	
	public CurrentPlayAnimationManager(Context context) {
		mCurrentPos = -1;
		mContext = context;
	}

	public void addViewAndstartAnimation(View v, int pos) {
		stopAnimation();
		mCurrentPos = pos;
		LinearLayout rl = (LinearLayout) v.findViewById(R.id.play_now);
		if(rl!=null) {
			rl.clearAnimation();
			startAnimation(rl);
		}
	}
	
	private void startAnimation(View vv) {
		mCurrentViewAnimated = vv;
		mCurrentViewAnimated.setVisibility(View.VISIBLE);
		v = mCurrentViewAnimated.findViewById(R.id.image1);
		v.setBackgroundColor(DisplayManager2.getCurrentPlayAnimationColor(mContext));
		mList = new AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			
			public void onAnimationEnd(Animation anim) {
			v.clearAnimation();
				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.scale_anim1);
				animation.setAnimationListener(mList);
				v.startAnimation(animation);
			}
		};
		
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.scale_anim1);
		animation.setAnimationListener(mList);
		v.startAnimation(animation);
		
		v2 = mCurrentViewAnimated.findViewById(R.id.image2);
		v2.setBackgroundColor(DisplayManager2.getCurrentPlayAnimationColor(mContext));

		mList2 = new AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			
			public void onAnimationEnd(Animation anim) {
				v2.clearAnimation();
				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.scale_anim2);
				animation.setAnimationListener(mList2);
				v2.startAnimation(animation);
			}
		};
		
		Animation animation2 = AnimationUtils.loadAnimation(mContext, R.anim.scale_anim2);
		animation2.setAnimationListener(mList2);
		v2.startAnimation(animation2);
		

		
		v3 = mCurrentViewAnimated.findViewById(R.id.image3);
		v3.setBackgroundColor(DisplayManager2.getCurrentPlayAnimationColor(mContext));

		mList3 = new AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			
			public void onAnimationEnd(Animation anim) {
				v3.clearAnimation();
				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.scale_anim3);
				animation.setAnimationListener(mList3);
				v3.startAnimation(animation);
			}
		};
		
		Animation animation3 = AnimationUtils.loadAnimation(mContext, R.anim.scale_anim3);
		animation3.setAnimationListener(mList3);
		v3.startAnimation(animation3);
	}
	
	
	public void stopAnimation() {
		if(mCurrentViewAnimated!=null) {
			mCurrentViewAnimated.clearAnimation();
			v.clearAnimation();
			v2.clearAnimation();
			v3.clearAnimation();
			mCurrentViewAnimated.setVisibility(View.GONE);
			mCurrentViewAnimated = null;
		}
		mCurrentPos = -1;
	}

	public boolean isCurrentAnimation() {
		return mCurrentViewAnimated != null;
	}

	public int getPos() {
		return mCurrentPos;
	}

	public void resumeAnimation() {
		
	}

	public void pauseAnimation() {
		
	}
}
