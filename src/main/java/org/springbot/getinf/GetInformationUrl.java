package org.springbot.getinf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.springbot.bot.Bot;
import org.springbot.parsjson.FindJsonObject;
import org.telegram.telegrambots.meta.api.objects.Message;

import com.github.kevinsawicki.http.HttpRequest;

public class GetInformationUrl implements GetInformation{

	private FindJsonObject findJsonObject = new FindJsonObject();

	private Message message;

	private Bot bot;
	
	public GetInformationUrl(Message message, Bot bot) {
		this.message = message;
		this.bot = bot;
	}
	
	public String[] getInformation() {
		
		String scanId = getScanId();
		
		String url = "https://www.virustotal.com/gui/url/"+ scanId.substring(0, 64) +"/detection";

		JSONObject scans = getJsonScans(scanId);

		Map<String, JSONObject> jsonArray = getArrayJson(scans);

		String text = findJsonObject.getTextInformationUrl(jsonArray);
		
		String [] resource = new String [] {text, url};

		return resource;
	}
	
	//получает информацию об url
	public JSONObject getJsonScans(String scanId) {
		
		String urlVirusTotal = "https://www.virustotal.com/vtapi/v2/url/report";
		String key = bot.getVirusTotalToken();

	    HttpRequest request = HttpRequest.get(urlVirusTotal);
	    request.part("apikey", key);
	    request.part("resource", scanId);

		JSONObject jsonobject = new JSONObject(request.body());
		
		JSONObject scans = jsonobject.getJSONObject("scans");
			
		return scans;
	}
	
	public Map<String, JSONObject> getArrayJson(JSONObject scans) {

		Map<String, JSONObject> jsonArray = new HashMap<String, JSONObject>();
		Map<String, JSONObject> jsonArrayAll = FindJsonObject.findJsonOnject(scans);

		int count = 0;
		for (Entry<String, JSONObject> entry : jsonArrayAll.entrySet()) {

			if(entry.getValue().getBoolean("detected")) {
				jsonArray.put(entry.getKey(),entry.getValue());
				count++;
			}
			if (count >= 8) {
				break;
			}
		}
		return jsonArray;
	}
	
	//отправляет url для сканирования
	public String getScanId(){
		String urlResource = message.getText();
		String key = bot.getVirusTotalToken();
		String urlVirusTotal = "https://www.virustotal.com/vtapi/v2/url/scan";
    	
	    HttpRequest request = HttpRequest.post(urlVirusTotal);
	    request.part("apikey", key);
	    request.part("url", urlResource);

	    int status = request.code();
	    
	    String scanId = null;
	    
	    if(status == 200) {
	    	
	    	JSONObject jsonobject = new JSONObject(request.body());
	    	scanId = jsonobject.getString("scan_id");
	    	
	    }else {
	    	System.out.println("Server error " + status);
	    }
		
	    return scanId;
	}
}
