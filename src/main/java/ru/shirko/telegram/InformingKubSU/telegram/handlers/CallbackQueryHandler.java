package ru.shirko.telegram.InformingKubSU.telegram.handlers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.shirko.telegram.InformingKubSU.constans.bot.BotMessageEnum;
import ru.shirko.telegram.InformingKubSU.constans.bot.ButtonNameEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.AccessRightEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.LoginStateEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.SendCommandStateEnum;
import ru.shirko.telegram.InformingKubSU.telegram.keyboards.InlineKeyboardMaker;

import java.io.IOException;

import static ru.shirko.telegram.InformingKubSU.InformingKubSuApplication.*;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CallbackQueryHandler {

    InlineKeyboardMaker inlineKeyboardMaker;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) throws IOException {
        final String chatId = buttonQuery.getMessage().getChatId().toString();

        String data = buttonQuery.getData();

        if (userPreparingMessage.containsKey(chatId) &&
                !(data.equals(ButtonNameEnum.TO_ALL_COURSES.name()) || data.equals(ButtonNameEnum.TO_CERTAIN_COURSE.name()))
        ) { userPreparingMessage.get(chatId).setMessageTo(data); }

        if (userLoginStates.containsKey(chatId) && userLoginStates.get(chatId).equals(LoginStateEnum.INPUT_ROLE_STATE)) {
            users.get(chatId).setRole(data);
            if (data.equals(AccessRightEnum.STUDENT.name())) {
                return getLoginStudentGroupMessage(chatId);
            }
            return getLoginEnterPasswordMessage(chatId);
        }
        else if (data.equals(ButtonNameEnum.TO_ONLY_GROUP.name())) {
            return getSendChooseGroupMessage(chatId);
        }
        else if (data.equals(ButtonNameEnum.TO_ONLY_MEMBER.name())) {
            return getSendToOnlyMemberMessage(chatId);
        }
        else if (data.equals(ButtonNameEnum.TO_ALL_STUDENTS.name())) {
            Integer messageId = buttonQuery.getMessage().getMessageId();
            return getSendChooseCategoryAllStudentMessage(chatId, messageId);
        }
        else if (data.equals(ButtonNameEnum.TO_ALL_TEACHERS.name())) {
            return getSendDefaultMessage(chatId);
        }
        else if (data.equals(ButtonNameEnum.TO_ALL_MEMBERS.name())) {
            return getSendDefaultMessage(chatId);
        }
        else if (data.equals(ButtonNameEnum.TO_ALL_COURSES.name())) {
            return getSendDefaultMessage(chatId);
        }
        else if (data.equals(ButtonNameEnum.TO_CERTAIN_COURSE.name())) {
            return getSendInputCourseMessage(chatId);
        }
        else if (data.equals(ButtonNameEnum.TO_ENTRY_GROUP.name())) {
            String group = users.get(chatId).getStudentGroup() + "/0";
            userPreparingMessage.get(chatId).setGroup(group);
            return getSendDefaultMessage(chatId);
        }
        else if (data.equals(ButtonNameEnum.TO_SUBGROUP.name())) {
            String group = users.get(chatId).getStudentGroup() + "/" +users.get(chatId).getStudentSubGroup();
            userPreparingMessage.get(chatId).setGroup(group);
            return getSendDefaultMessage(chatId);
        }
        return null;
    }


    /**
     * Обработка инлайн кнопок
     */

    public SendMessage getLoginStudentGroupMessage(String chatId) {
        userLoginStates.replace(chatId, LoginStateEnum.INPUT_STUDENT_GROUP_STATE);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.LOGIN_INPUT_GROUP_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public SendMessage getLoginEnterPasswordMessage(String chatId) {
        userLoginStates.replace(chatId, LoginStateEnum.INPUT_ENTER_PASSWORD_STATE);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.LOGIN_ENTER_PASSWORD_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public SendMessage getSendChooseGroupMessage(String chatId) {
        usersSendCommandStates.put(chatId, SendCommandStateEnum.INPUT_GROUP);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.SEND_COMMAND_INPUT_GROUP_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public SendMessage getSendToOnlyMemberMessage(String chatId) {
        usersSendCommandStates.put(chatId, SendCommandStateEnum.INPUT_NAME_MEMBER);

        SendMessage sendMessage = new SendMessage(chatId,
                BotMessageEnum.SEND_COMMAND_INPUT_NAME_MEMBER_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public EditMessageText getSendChooseCategoryAllStudentMessage(String chatId, Integer messageId) {

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);

        editMessageText.setText(BotMessageEnum.SEND_COMMAND_CHOOSE_CATEGORY_STUDENT_ALL.getMessage());

        editMessageText.setReplyMarkup(inlineKeyboardMaker.getChooseCategoryAllStudentButtons());

        editMessageText.enableMarkdown(true);

        return editMessageText;
    }


    public SendMessage getSendInputCourseMessage(String chatId) {
        usersSendCommandStates.put(chatId, SendCommandStateEnum.INPUT_COURSE);


        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.SEND_COMMAND_INPUT_COURSE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public SendMessage getSendDefaultMessage(String chatId) {
        usersSendCommandStates.put(chatId, SendCommandStateEnum.INPUT_MESSAGE);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.SEND_COMMAND_INPUT_TEXT_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }
}
