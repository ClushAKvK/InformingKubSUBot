package ru.shirko.telegram.InformingKubSU.telegram.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.shirko.telegram.InformingKubSU.constans.bot.ButtonNameEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.AccessRightEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Основная клавиатура, находящаяся под строкой ввода текста в Telegram
 */
@Component
public class ReplyKeyboardMaker {

    public ReplyKeyboardMarkup getMainMenuKeyboard(String accessRightEnum) {
        KeyboardRow row1 = new KeyboardRow();

        if (accessRightEnum.equals(AccessRightEnum.STUDENT.name()))
            row1.add(new KeyboardButton(ButtonNameEnum.SEND_TO_MY_OWN_GROUP.getButtonName()));
        else
            row1.add(new KeyboardButton(ButtonNameEnum.SEND_MESSAGE.getButtonName()));

        row1.add(new KeyboardButton(ButtonNameEnum.HELP.getButtonName()));

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        return replyKeyboardMarkup;
    }
}
