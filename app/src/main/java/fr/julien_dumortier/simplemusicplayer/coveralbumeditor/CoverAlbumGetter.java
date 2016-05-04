package fr.julien_dumortier.simplemusicplayer.coveralbumeditor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import fr.julien_dumortier.simplemusicplayer.item.Album;
import fr.julien_dumortier.simplemusicplayer.item.Item;
import fr.julien_dumortier.simplemusicplayer.item.Track;

public class CoverAlbumGetter {
	
	public interface IOnLinksGettedListener {
		public void onLinkGetted(List<String> links);
		public void error();
	}

	public void startGetCoversLinksByItem(final Item item, final IOnLinksGettedListener listener) {
		new Thread("get cover from google thread") { 
			public void run() {
				try {
					String formatedRequest = formatRequestByItem(item);
					JSONObject jso = getJsonResponse(formatedRequest, 0);
					List<String> links = getAllCoverLinkFromJson(jso);
					jso = getJsonResponse(formatedRequest, links.size());
					links.addAll(getAllCoverLinkFromJson(jso));
					listener.onLinkGetted(links);
				} catch(Exception e) {
					try {
						String formatedRequest = formatRequestByItem(item);
						JSONObject jso = getJsonResponse(formatedRequest, 0);
						List<String> links = getAllCoverLinkFromJson(jso);
						jso = getJsonResponse(formatedRequest, links.size());
						links.addAll(getAllCoverLinkFromJson(jso));
						listener.onLinkGetted(links);
					} catch(Exception ee) {
						try {
							String formatedRequest = formatRequestByItem(item);
							JSONObject jso = getJsonResponse(formatedRequest, 0);
							List<String> links = getAllCoverLinkFromJson(jso);
							jso = getJsonResponse(formatedRequest, links.size());
							links.addAll(getAllCoverLinkFromJson(jso));
							listener.onLinkGetted(links);
						} catch(Exception eee) {
							eee.printStackTrace();
							listener.error();
						}
					}
				}
			}
		}.start();
	}
	
	private static String formatRequestByItem(Item item) throws Exception  {
		String formatedRequest = "";
		if(item instanceof Album) {
			String albumName = ((Album)item).getAlbumName();
			String artistName = ((Album)item).getArtistName();
			formatedRequest = artistName+" "+albumName;
			formatedRequest = java.net.URLEncoder.encode(formatedRequest,"UTF-8");
		}
		else if(item instanceof Track) {
			String albumName = ((Track)item).getAlbumName();
			String artistName = ((Track)item).getArtistName();
			String trackName = ((Track)item).getTrackName();
			formatedRequest = artistName+" "+albumName+" "+trackName;
		} else
			throw new Exception("type de l'item non pris en charge");
		return formatedRequest.replaceAll(" ", "%20");
	}
	
	private static JSONObject getJsonResponse(String formatedRequest, int offset) throws Exception {
		if(offset>0)
			formatedRequest+="&start="+Integer.toString(offset);
		URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +
                "v=1.0&q=cover%20cd%20"+formatedRequest+"&tbs=isz:m");
		URLConnection connection = url.openConnection();
		connection.addRequestProperty("Referer", "mywebsite");
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}
		
		return new JSONObject(builder.toString());
	}
	
	private static List<String> getAllCoverLinkFromJson(JSONObject jso) throws Exception {
		List<String> links = new ArrayList<String>();
		JSONArray jsa = jso.getJSONObject("responseData").getJSONArray("results");
		for(int i=0; i<jsa.length(); i++) {
			links.add(jsa.getJSONObject(i).getString("url"));
		}
		return links;
	}
}
