package org.springbot.getinf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.springbot.bot.Bot;
import org.springbot.parsjson.FindJsonObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import com.github.kevinsawicki.http.HttpRequest;

@Component
public class GetInformationFile implements GetInformation{
	
	private Message message;

	private FindJsonObject findJsonObject = new FindJsonObject();

	private Bot bot;

	public GetInformationFile () {}

	public GetInformationFile(Message message, Bot bot) {
		this.message = message;
		this.bot = bot;
	}
	
	public String [] getInformation() {
		
		String pathFile = downloadTelegramFile();
		
		String scanId = getScanId(pathFile);
		
		String url = "https://www.virustotal.com/gui/file/" + scanId.substring(0, 64) + "/detection";
		
		JSONObject scans = getJsonScans(scanId);
		
		Map<String, JSONObject> jsonArray = getArrayJson(scans);
		
		String text = findJsonObject.getTextInformationFile(jsonArray);

		String [] resource = new String [] {text, url};

		return resource;
	}

	public  String downloadTelegramFile() {

	    String upPath = System.getProperty("user.dir") + "\\src\\tmp\\";

		String url = "https://api.telegram.org/bot"+ bot.getBotToken() +"/getFile?file_id="+ message.getDocument().getFileId();

		try {
		    HttpRequest request = HttpRequest.post(url);
		    int status = request.code();
		    JSONObject jsonResult = null;

		    if(status == 200) {
				jsonResult = new JSONObject(request.body());
		    	
		    }else {
		    	System.out.println("Server error " + status);
		    }

			JSONObject path = jsonResult.getJSONObject("result");
		    String file_path = path.getString("file_path");
		    URL downoload = new URL("https://api.telegram.org/file/bot" + bot.getBotToken() + "/" + file_path);

		    FileOutputStream fos = new FileOutputStream(upPath + message.getDocument().getFileName());
		    ReadableByteChannel rbc = Channels.newChannel(downoload.openStream());
		    
		    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		    fos.close();
		    rbc.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
		return upPath + message.getDocument().getFileName();
	}
	
	public String getScanId(String pathFile){

		File file = new File(pathFile);
		String url = "https://www.virustotal.com/vtapi/v2/file/scan";
		
	    HttpRequest request = HttpRequest.post(url);
	    request.part("apikey", bot.getVirusTotalToken());
	    request.part("file","file", file);
	    
	    file.delete();
	    
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
	
	public JSONObject getJsonScans(String scanId) {
		JSONObject scans = null;

		System.out.println("Waiting for the result to be received from the server...");
		for (int i = 0; i<10;i++) {
			
			HttpRequest request = HttpRequest.get("https://www.virustotal.com/vtapi/v2/file/report");
			request.part("apikey", bot.getVirusTotalToken());
			request.part("resource", scanId);
			scans = new JSONObject(request.body());
			
			int response_code = scans.getInt("response_code");

			if (response_code == 1) {
				
				scans = scans.getJSONObject("scans");
				break;
			}
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
}
