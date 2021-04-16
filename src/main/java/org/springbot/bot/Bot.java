package org.springbot.bot;

import org.apache.commons.validator.routines.UrlValidator;
import org.springbot.getinf.GetInformationFile;
import org.springbot.getinf.GetInformationUrl;
import org.springbot.sender.SendingMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.google.common.net.InetAddresses;

@Component
public class Bot extends TelegramLongPollingBot {
	
	@Value("${bot.name}")
    private String botUsername;

	@Value("${bot.token}")
    private String botToken;

	@Value("${bot.vt_token}")
	private String virusTotalToken;

    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getVirusTotalToken() {
    	return virusTotalToken;
	}

	public void onUpdateReceived(Update update) {

		Message message = update.getMessage(); 
		
		SendingMessages sendingMessages = new SendingMessages(message, this);

    	UrlValidator urlValidator = new UrlValidator();
    			
		if(message.hasText()) {
    		
	    	if(urlValidator.isValid(message.getText())||
	    	   urlValidator.isValid("https://" + message.getText())) {
				sendingMessages.sendingInformation(new GetInformationUrl(message, this));
				
	    	}else if(InetAddresses.isInetAddress(message.getText())) {
	    		sendingMessages.sendMsg("This is the api address");
	    		
	    	}else {
	    		sendingMessages.sendMsg("This is not a site domain");
	    		
	    	}
		}else if(message.hasDocument()){
			sendingMessages.sendMsg("File scanning may take some time");
			sendingMessages.sendingInformation(new GetInformationFile(message, this));
			
		}else {
			sendingMessages.sendMsg("This content is not supported");
		}
	}
}




