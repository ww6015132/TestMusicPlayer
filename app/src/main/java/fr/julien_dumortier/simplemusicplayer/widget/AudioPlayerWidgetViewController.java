package fr.julien_dumortier.simplemusicplayer.widget;

import java.util.Enumeration;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import fr.julien_dumortier.simplemusicplayer.item.Track;

public class AudioPlayerWidgetViewController {

	private AudioPlayerApplication mAudioPlayerApplication;

	public AudioPlayerWidgetViewController(AudioPlayerApplication application) {
		mAudioPlayerApplication = application;
	}
	
	public void showViews(Track currentTrack) {
		Enumeration<WidgetView> views = mAudioPlayerApplication.getAllViews();
		while(views.hasMoreElements()) {
			WidgetView view = views.nextElement();
			view.switchVisibility(mAudioPlayerApplication, true, getRoundedCornerBitmap(((BitmapDrawable)currentTrack.getHdPicture()).getBitmap()));
		}
	}
	
	public void hideViews() {
		Enumeration<WidgetView> views = mAudioPlayerApplication.getAllViews();
		while(views.hasMoreElements()) {
			WidgetView view = views.nextElement();
			view.switchVisibility(mAudioPlayerApplication, false, null);
		}
	}
	

	private static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
