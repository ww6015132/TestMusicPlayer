package fr.julien_dumortier.simplemusicplayer.uiprompt;

import fr.julien_dumortier.simplemusicplayer.MainActivity;
import fr.julien_dumortier.simplemusicplayer.R;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;

public class PromptConfirmExit extends Dialog {
	public PromptConfirmExit(final MainActivity c) {
		super(c);
	    setTitle(c.getResources().getString(R.string.exit_title));
	    View v = LayoutInflater.from(c).inflate(R.layout.exit_layout, null);
	    setContentView(v);
	    v.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				c.finishApplication();
			}
		});
	    v.findViewById(R.id.cancel).setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				PromptConfirmExit.this.cancel();
			}
		});
	    getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
	    
	}

}
