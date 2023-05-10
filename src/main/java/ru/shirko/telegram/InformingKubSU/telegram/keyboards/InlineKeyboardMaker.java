package ru.shirko.telegram.InformingKubSU.telegram.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.shirko.telegram.InformingKubSU.constans.bot.ButtonNameEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.AccessRightEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.LoginStateEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.SendCommandStateEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Клавиатуры, формируемые в ленте Telegram
 */
@Component
public class InlineKeyboardMaker {

    public InlineKeyboardMarkup getChooseSendToButtons(String accessRightEnum) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        rowList.add(getButton(ButtonNameEnum.TO_ONLY_GROUP.getButtonName(), ButtonNameEnum.TO_ONLY_GROUP.name()));

        if (accessRightEnum.equals(AccessRightEnum.TEACHER.name()) || accessRightEnum.equals(AccessRightEnum.DEANERY_MEMBER.name())) {
            rowList.add(getButton(ButtonNameEnum.TO_ONLY_MEMBER.getButtonName(), ButtonNameEnum.TO_ONLY_MEMBER.name()));
        }

        if (accessRightEnum.equals(AccessRightEnum.DEANERY_MEMBER.name())) {
            rowList.add(getButton(ButtonNameEnum.TO_ALL_MEMBERS.getButtonName(), ButtonNameEnum.TO_ALL_MEMBERS.name()));
            rowList.add(getButton(ButtonNameEnum.TO_ALL_STUDENTS.getButtonName(), ButtonNameEnum.TO_ALL_STUDENTS.name()));
            rowList.add(getButton(ButtonNameEnum.TO_ALL_TEACHERS.getButtonName(), ButtonNameEnum.TO_ALL_TEACHERS.name()));
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


    public InlineKeyboardMarkup getChooseSendToGroupButtons() {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        rowList.add(getButton(ButtonNameEnum.TO_ENTRY_GROUP.getButtonName(), ButtonNameEnum.TO_ENTRY_GROUP.name()));
        rowList.add(getButton(ButtonNameEnum.TO_SUBGROUP.getButtonName(), ButtonNameEnum.TO_SUBGROUP.name()));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }



    public InlineKeyboardMarkup getChooseCategoryAllStudentButtons() {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        rowList.add(getButton(ButtonNameEnum.TO_CERTAIN_COURSE.getButtonName(), ButtonNameEnum.TO_CERTAIN_COURSE.name()));
        rowList.add(getButton(ButtonNameEnum.TO_ALL_COURSES.getButtonName(), ButtonNameEnum.TO_ALL_COURSES.name()));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


    public InlineKeyboardMarkup getChooseRoleButtons() {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        rowList.add(getButton("Студент", AccessRightEnum.STUDENT.name()));
        rowList.add(getButton("Преподаватель", AccessRightEnum.TEACHER.name()));
        rowList.add(getButton("Член деканата", AccessRightEnum.DEANERY_MEMBER.name()));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


    public InlineKeyboardMarkup backToChooseRoleButtons() {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//
//        rowList.add(getButton("Студент", AccessRightEnum.STUDENT.name()));
//        rowList.add(getButton("Преподаватель", AccessRightEnum.TEACHER.name()));
//        rowList.add(getButton("Член деканата", AccessRightEnum.DEANERY_MEMBER.name()));
        rowList.add(getButton("Назад", LoginStateEnum.INPUT_ROLE_STATE.name()));


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }


    public List<InlineKeyboardButton> getButton(String buttonName, String buttonCallbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonName);
        button.setCallbackData(buttonCallbackData);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(button);
        return keyboardButtonsRow;
    }



}
