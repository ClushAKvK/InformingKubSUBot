package ru.shirko.telegram.InformingKubSU.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import ru.shirko.telegram.InformingKubSU.telegram.InformingKubSUBot;
import ru.shirko.telegram.InformingKubSU.telegram.handlers.CallbackQueryHandler;
import ru.shirko.telegram.InformingKubSU.telegram.handlers.MessageHandler;

@Configuration
@AllArgsConstructor
public class SpringConfig {
    TelegramConfig telegramConfig;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(telegramConfig.getWebhookPath()).build();
    }


    @Bean
    public InformingKubSUBot springWebhookBot(SetWebhook setWebhook,
                                              MessageHandler messageHandler,
                                              CallbackQueryHandler callbackQueryHandler) {
        InformingKubSUBot bot = new InformingKubSUBot(setWebhook, messageHandler, callbackQueryHandler);

        bot.setBotPath(telegramConfig.getWebhookPath());
        bot.setBotUsername(telegramConfig.getBotName());
        bot.setBotToken(telegramConfig.getBotToken());

        return bot;
    }

}
