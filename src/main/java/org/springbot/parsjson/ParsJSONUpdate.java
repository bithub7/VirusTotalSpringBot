package org.springbot.parsjson;

import org.json.JSONObject;

public class ParsJSONUpdate {

	private static String update;

	public ParsJSONUpdate(String update) {
		this.update = update;
	}

	private static String[] arrNameTypeMessage = {"text", "audio", "document", "photo", "sticker",
			"video", "contact", "location", "animation", "voice"};


	public static String[] getUpadateInf() {

		JSONObject jsonObject = new JSONObject(update); //to do 

		JSONObject message = jsonObject.getJSONObject("message");
		String messageId = message.getString("messageId");
		String id = message.getString("id");
		String firstName = message.getString("firstName");
		String lastName = message.getString("lastName");
		String userName = message.getString("userName");
		String languageCode = message.getString("languageCode");

		String type = null;
		for (String element : arrNameTypeMessage) {

			if (message.getString(element) != null) {
				type = message.getString(element);
			}

		}

		String arrUpdateInf[] = {messageId, id, firstName, lastName, userName, languageCode, type};

		return arrUpdateInf;
	}
}