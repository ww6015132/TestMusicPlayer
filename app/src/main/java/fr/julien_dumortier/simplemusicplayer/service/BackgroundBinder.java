package fr.julien_dumortier.simplemusicplayer.service;

import android.os.Binder;

public class BackgroundBinder extends Binder {
	
  	private MusicPlayerService service = null; 
  
    public BackgroundBinder(MusicPlayerService service) { 
        super(); 
        this.service = service; 
    } 
 
    public MusicPlayerService getService() { 
        return service; 
    } 
}
