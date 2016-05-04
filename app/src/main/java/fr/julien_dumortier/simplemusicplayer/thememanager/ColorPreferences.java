package fr.julien_dumortier.simplemusicplayer.thememanager;

import fr.julien_dumortier.simplemusicplayer.R;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ColorPreferences extends Preference {

	public ColorPreferences(Context context) {
		  super(context);
	 }
	 
	 public ColorPreferences(Context context, AttributeSet attrs) {
	  super(context, attrs);
	 }
	 
	 public ColorPreferences(Context context, AttributeSet attrs, int defStyle) {
	  super(context, attrs, defStyle);
	 }
	 
	 static LinearLayout mLinearLayout;
	 private static int [] pictures = {
		 R.drawable.classique_selected, R.drawable.bleu_vert_selected, R.drawable.orange_selected, 
			 R.drawable.rouge_selected, R.drawable.vert_pomme_selected, R.drawable.violet_selected
	 };
	 
	 protected View onCreateView(ViewGroup parent) {
		final HorizontalScrollView v = new HorizontalScrollView(getContext());
		v.setClickable(false);
		v.setHorizontalScrollBarEnabled(false);
		mLinearLayout = new LinearLayout(getContext());
		mLinearLayout.setClickable(false);
		int size = ((Activity)getContext()).getWindowManager().getDefaultDisplay().getWidth();
		int sizeH = ((Activity)getContext()).getWindowManager().getDefaultDisplay().getHeight();
		if(sizeH<size)
			size = sizeH;
		/*
		 * 0: classique
		 * 1: bleu-vert
		 * 2: orange
		 * 3: rouge
		 * 4: vert-pomme
		 * 5: violet
		 */
		mLinearLayout.addView(initImageColorView(size, 
				R.drawable.classique_default, R.drawable.classique_selected, 
				R.drawable.classique_pressed, 0));
		mLinearLayout.addView(initImageColorView(size, 
				R.drawable.bleu_vert_default, R.drawable.bleu_vert_selected, 
				R.drawable.bleu_vert_pressed, 1));
		mLinearLayout.addView(initImageColorView(size,
				R.drawable.orange_default, R.drawable.orange_selected, 
				R.drawable.orange_pressed, 2));
		mLinearLayout.addView(initImageColorView(size, 
				R.drawable.rouge_default, R.drawable.rouge_selected, 
				R.drawable.rouge_pressed, 3));
		mLinearLayout.addView(initImageColorView(size, 
				R.drawable.vert_pomme_default, R.drawable.vert_pomme_selected, 
				R.drawable.vert_pomme_pressed, 4));
		mLinearLayout.addView(initImageColorView(size, 
				R.drawable.violet_default, R.drawable.violet_selected, 
				R.drawable.violet_pressed, 5));
		v.addView(mLinearLayout);
		unselectAll();
		
		/* autoselect */
		int pos = DisplayManager2.getThemeColorPos(getContext());
		 int nbChild = mLinearLayout.getChildCount();
		 if(nbChild>pos) {
			 ImageView iv = (ImageView) mLinearLayout.getChildAt(pos).findViewWithTag("iv_color");
			 iv.setBackgroundResource(pictures[pos]);
			 iv.setSelected(true);
		 }
		/* ********** */
		
		v.postDelayed(new Runnable() {
		    public void run() {
		    	ObjectAnimator animator= ObjectAnimator.ofInt(v, "scrollX", v.getChildAt(0)
		                .getMeasuredWidth()-((Activity)getContext()).getWindowManager().getDefaultDisplay().getWidth());
		    	animator.setInterpolator(new AccelerateInterpolator());
		    	animator.setDuration(700);
		    	animator.addListener(new AnimatorListener() {
					public void onAnimationStart(Animator arg0) {}
					
					public void onAnimationRepeat(Animator arg0) {}
					
					public void onAnimationEnd(Animator arg0) {
				    	ObjectAnimator animator=ObjectAnimator.ofInt(v, "scrollX", 0);
				    	animator.setInterpolator(new DecelerateInterpolator());
				    	animator.setDuration(450);
				    	animator.start();
					}
					
					public void onAnimationCancel(Animator arg0) {}
				});
		    	animator.start();
		    }
		}, 200L);
		return v;
	 }

	 public View initImageColorView(int size, final int def, final int select, final int pressed, final int pos) {
			 RelativeLayout rl = new RelativeLayout(getContext());
			 rl.setPadding(0, 11, 0, 15);
			 rl.setLayoutParams(new LinearLayout.LayoutParams(size/4, LinearLayout.LayoutParams.WRAP_CONTENT));
			 rl.setGravity(Gravity.CENTER);
			 final ImageView iv = new ImageView(getContext());
			 iv.setTag("iv_color");
			 iv.setContentDescription(Integer.toString(def));
			 iv.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View arg0, MotionEvent event) {
					switch(event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							iv.setBackgroundResource(pressed);
							break;
						case MotionEvent.ACTION_UP:
								unselectAll();
								iv.setBackgroundResource(select);
								iv.setSelected(true);
								DisplayManager2.setSelectedColorPosition(getContext(), pos);
							break;
						case MotionEvent.ACTION_CANCEL:
							if(iv.isSelected())
								iv.setBackgroundResource(select);
							else
								iv.setBackgroundResource(def);
							break;
					}
					return true;
				}
			});
		 rl.addView(iv);
		 iv.postInvalidate();
		 return rl;
	 }
	 
	 private void unselectAll() {
		 int nbChild = mLinearLayout.getChildCount();
		 for(int i=0; i<nbChild; i++) {
			 ImageView iv = (ImageView) mLinearLayout.getChildAt(i).findViewWithTag("iv_color");
			 iv.setBackgroundResource(Integer.parseInt(iv.getContentDescription().toString()));
			 iv.setSelected(false);
		 }
	 }
}
