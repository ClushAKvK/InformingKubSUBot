package ru.shirko.telegram.InformingKubSU.constans.bot;

/**
 * Названия кнопок основной и инлайн клавиатруры
 */
public enum ButtonNameEnum {
    // Основаная клавиатура
    SEND_MESSAGE("Отправить сообщение"),
    SEND_TO_MY_OWN_GROUP("Отправить моей группе"),
    HELP("Помощь"),

    // Инлайн клавиатура
    TO_ALL_MEMBERS("Всем"),
    TO_ALL_STUDENTS("Всем студентам..."),
    TO_ALL_TEACHERS("Всем преподавателям"),
    TO_ONLY_MEMBER("Участнику"),
    TO_ONLY_GROUP("Группе"),

    TO_ENTRY_GROUP("Всей группе"),
    TO_SUBGROUP("Моей подгруппе"),

    TO_CERTAIN_COURSE("Ввести курс"),
    TO_ALL_COURSES("Все курсы")
    ;


    private final String buttonName;

    ButtonNameEnum(String buttonName) { this.buttonName = buttonName; }

    public String getButtonName() {
        return buttonName;
    }
}
