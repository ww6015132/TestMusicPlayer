package fr.julien_dumortier.simplemusicplayer.playerview;

public class TimeFormater {

	public static String getFormatedTime(long time) {
		int seconds = (int) (time / 1000) % 60 ;
		String sec = "";
		if(seconds<10)
			sec="0";
		sec+=Integer.toString(seconds);
		int minutes = (int) ((time / (1000*60)) % 60);
		return minutes+":"+sec;
	}
}
