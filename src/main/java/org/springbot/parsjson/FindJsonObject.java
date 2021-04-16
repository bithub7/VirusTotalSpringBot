package org.springbot.parsjson;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class FindJsonObject {
	
	/*
	 *  In the json "scans" object,
	 *  the objects are just listed,
	 *  not listed in the array. 
	 *  This method parses all objects 
	 *  that are in the "scans" json object.
	 */
	
	public static Map<String, JSONObject> findJsonOnject(JSONObject jsonObject ){
		
		Map<String, JSONObject> jsonarr = new HashMap<String, JSONObject>();
		
		char startAny [] = new char [] {'}',',','"'};
		char endAny [] = new char [] {'"',':','{',};
		
		String jsonStr = jsonObject.toString();
		
		jsonStr = jsonStr.substring(1);
		jsonStr = jsonStr.substring(1);
		jsonStr = new String(startAny) + jsonStr;
		
		char arr [] = jsonStr.toCharArray();
		char tempArr [] = new char[] {arr[0], arr[1], arr[2]};
		
		for (int i = 3; i<=arr.length;i++) {
			
			if (Arrays.equals(tempArr, startAny)) {
				
				int startVolume = i;
				
				for (; i<=arr.length;i++) {
					
					if (Arrays.equals(tempArr, endAny)) {
						
						String kayName = new String(Arrays.copyOfRange(arr, startVolume, i - endAny.length));
						
						JSONObject volumeName = jsonObject.getJSONObject(kayName);
						
						jsonarr.put(kayName, volumeName);

						break;
					}
					
					if(i > arr.length - 1) {
						
						break;
						
					}
					tempArr[0] = tempArr[1];
					tempArr[1] = tempArr[2];
					tempArr[2] = arr[i];
				}		
			}
			
			if(i > arr.length - 1) {

				break;
				
			}
			tempArr[0] = tempArr[1];
			tempArr[1] = tempArr[2];
			tempArr[2] = arr[i];
		}
	
		return jsonarr;
	
	}
	
	public String getTextInformationUrl(Map<String, JSONObject> jsonarr) {
		
		String text = "";
		
		if (jsonarr.toString() == "{}") {
			text = "No viruses detected";
		}else {
			text = "The following threats were detected:\n\n";
		    for (Entry<String, JSONObject> entry : jsonarr.entrySet()) {
		    	text += entry.getKey()+" :"+"\n";
				text += "\t" + "detected: " + entry.getValue().getBoolean("detected")+"\n";
				text += "\t" + "result: " + entry.getValue().get("result").toString()+"\n" + "\n"; 
		    }
		}

		text =  text.replace("_", "\\_");
		
		return text;
	}
	
	public String getTextInformationFile(Map<String, JSONObject> jsonarr) {
		
		String text = "";
		
		if (jsonarr.toString() == "{}") {
			text = "No viruses detected";
		}else {
			text = "The following threats were detected:\n\n";
		    for (Entry<String, JSONObject> entry : jsonarr.entrySet()) {
		    	text += entry.getKey()+" :"+"\n";
				text += "\t" + "detected: " + entry.getValue().getBoolean("detected")+"\n";
				text += "\t" + "result: " + entry.getValue().get("result").toString()+"\n";
				text += "\t" + "update: " + entry.getValue().getString("update")+"\n";
				text += "\t" + "version: " + entry.getValue().getString("version")+"\n" + "\n"; 
		    }
		}
		
		text =  text.replace("_", "\\_");
		
		return text;
	}
	

}
