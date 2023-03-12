package ru.shirko.telegram.InformingKubSU.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Document;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramApiClient {
    String URL;
    static String botToken;

    public TelegramApiClient(@Value("${telegram.api-url}") String URL,
                             @Value("${telegram.bot-token}") String botToken) {
        this.URL = URL;
        this.botToken = botToken;
    }


    public static void sendMessageTo(String chatId, String text, Document document) {
        TelegramBot bot = new TelegramBot(botToken);
        bot.execute(new SendMessage(chatId, text));
        if (document != null)
            bot.execute(new SendDocument(chatId, document.getFileId()));
    }



}
