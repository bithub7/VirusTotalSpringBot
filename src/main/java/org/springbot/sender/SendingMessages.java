package org.springbot.sender;

import java.util.ArrayList;
import java.util.List;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.springbot.bot.Bot;
import org.springbot.getinf.GetInformation;

public class SendingMessages {

	private SendMessage sendMessage = new SendMessage();
	
	private Message message;

	private Bot bot;
	
	public SendingMessages(Message message, Bot bot) {
		this.message = message;
		this.bot = bot;
	}
	
	public void sendMsg(String text) {
		
		sendMessage.enableMarkdown(true);
		sendMessage.setChatId(message.getChatId());
		sendMessage.setText(text);
			
		try {
			bot.execute(sendMessage); //?
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public void sendingInformation(GetInformation getInformation) {
				
		String [] resource = getInformation.getInformation();
		String text = resource[0];
		String url = resource[1];

		System.out.println(text + "\n" + url);

		sendMsg(text, url);
	}

	public void sendMsg(String text, String url) {

		sendMessage.enableMarkdown(true);
		sendMessage.setChatId(message.getChatId());
		sendMessage.setText(text);

		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

		List <List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
		List <InlineKeyboardButton> rowInline = new ArrayList <InlineKeyboardButton>();
		rowInline.add(new InlineKeyboardButton().setText("View full details").setUrl(url));
		rowsInline.add(rowInline);

		markupInline.setKeyboard(rowsInline);
		sendMessage.setReplyMarkup(markupInline);

		try {
			bot.execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
