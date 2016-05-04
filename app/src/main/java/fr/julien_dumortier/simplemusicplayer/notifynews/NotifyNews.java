package fr.julien_dumortier.simplemusicplayer.notifynews;

import fr.julien_dumortier.simplemusicplayer.R;
import fr.julien_dumortier.simplemusicplayer.uiprompt.PromptShowText;
import android.content.Context;
import android.content.SharedPreferences.Editor;

public class NotifyNews {
	
	private static final String NEWS = "NEWS", ALREADY_SHOWING = "ALWAYS_SHOWING_", NEWS_NUMBER = "2",
								PURCHASE = "PURCHASE";
	
	private static final int NB_SHOW_PURCHASE = 250;
	
	public static void showNews(Context context) {
		if(!isAlwaysShowing(context)) {
			setAlwaysShowing(context);
			new PromptShowText(context, context.getResources().getString(R.string.news_title), context.getResources().getString(R.string.news_message)).show();
		} else {
			int nb = getNbPurchaseShowing(context);
			if(nb>=NB_SHOW_PURCHASE) {
				setNbPurchaseShowing(context, 0);
				showPurchase(context);
			} else {
				setNbPurchaseShowing(context, nb+1);
			}
		}
	}
	
	public static void showPurchase(Context context) {
		new PromptShowText(context, context.getResources().getString(R.string.purchase_title), 
					context.getResources().getString(R.string.purchase), 
					context.getResources().getString(R.string.cancel))
			.setSecondButtonLink(context.getResources().getString(R.string.go_to_playstore), 
					"https://play.google.com/store/apps/details?id=fr.julien_dumortier.audioplayer")
			.show();
	}
	
	private static boolean isAlwaysShowing(Context context) {
		return context.getSharedPreferences(NEWS, Context.MODE_PRIVATE).getBoolean(ALREADY_SHOWING+NEWS_NUMBER, false);
	}
	
	private static void setAlwaysShowing(Context context) {
		Editor editor = context.getSharedPreferences(NEWS, Context.MODE_PRIVATE).edit();
		editor.putBoolean(ALREADY_SHOWING+NEWS_NUMBER, true);
		editor.commit();
	}
	
	private static int getNbPurchaseShowing(Context context) {
		return context.getSharedPreferences(NEWS, Context.MODE_PRIVATE).getInt(PURCHASE, NB_SHOW_PURCHASE-5);
	}
	
	private static void setNbPurchaseShowing(Context context, int nb) {
		Editor editor = context.getSharedPreferences(NEWS, Context.MODE_PRIVATE).edit();
		editor.putInt(PURCHASE, nb);
		editor.commit();
	}
}
