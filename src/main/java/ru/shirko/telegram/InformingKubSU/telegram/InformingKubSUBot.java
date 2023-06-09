package ru.shirko.telegram.InformingKubSU.telegram;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import ru.shirko.telegram.InformingKubSU.constans.bot.BotMessageEnum;
import ru.shirko.telegram.InformingKubSU.telegram.handlers.CallbackQueryHandler;
import ru.shirko.telegram.InformingKubSU.telegram.handlers.MessageHandler;
import java.io.IOException;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InformingKubSUBot extends SpringWebhookBot {
    String botPath;
    String botUsername;
    String botToken;

    MessageHandler messageHandler;
    CallbackQueryHandler callbackQueryHandler;

    public InformingKubSUBot(SetWebhook setWebhook, MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler) {
        super(setWebhook);
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
    }


    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            return handleUpdate(update);
        }
        catch (IllegalArgumentException e) {
            return new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE.getMessage());
        }
        catch (Exception e) {
            return new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.EXCEPTION_WHAT_THE_HELL.getMessage());
        }
    }

    private BotApiMethod<?> handleUpdate(Update update) throws IOException {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        }
        else {
            Message message = update.getMessage();
            if (message != null) {
                return messageHandler.answerMessage(update.getMessage());
            }
        }
        return null;
    }

}
