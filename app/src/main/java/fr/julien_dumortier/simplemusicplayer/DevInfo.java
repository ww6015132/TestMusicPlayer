package fr.julien_dumortier.simplemusicplayer;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DevInfo extends AlertDialog {

	public DevInfo(final Context context) {
		super(context);
		setTitle(context.getResources().getString(R.string.dev_info_title));
		final View v = LayoutInflater.from(context).inflate(R.layout.dev_info, null);
		v.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
			public void onClick(View vv) {
				String subject = "AudioPlayer: "+((EditText)v.findViewById(R.id.subject)).getText().toString();
				String from = ((EditText)v.findViewById(R.id.from)).getText().toString();
				String body = ((EditText)v.findViewById(R.id.body)).getText().toString();
				if(!isEmailValid(from))
					Toast.makeText(context, context.getResources().getString(R.string.email_error), Toast.LENGTH_LONG).show();
				else if(body.length()>5) {
					sendMessage(from, body, subject);
					Toast.makeText(context, context.getResources().getString(R.string.thank_you), Toast.LENGTH_LONG).show();
					v.findViewById(R.id.contact).setVisibility(View.GONE);
				}
			}
		});
		setView(v);
		setButton(context.getResources().getString(R.string.ok), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	}
	
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}

	private void sendMessage(final String from, final String body, final String subject) {
		new Thread("Send message") {
			public void run() {
				HttpClient httpclient = new DefaultHttpClient();
			    try { 
			    	HttpPost httppost = new HttpPost("http://audioplayer.julien-dumortier.fr/send.php?subject="+URLEncoder.encode(subject, "UTF-8")+"&body="+URLEncoder.encode(body+" \n\n\nDe "+from, "UTF-8"));
					httpclient.execute(httppost);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
