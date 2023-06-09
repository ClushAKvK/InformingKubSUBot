package ru.shirko.telegram.InformingKubSU.telegram.handlers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.shirko.telegram.InformingKubSU.config.AccessConfig;
import ru.shirko.telegram.InformingKubSU.constans.bot.BotMessageEnum;
import ru.shirko.telegram.InformingKubSU.constans.bot.ButtonNameEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.AccessRightEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.LoginStateEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.SendCommandStateEnum;
import ru.shirko.telegram.InformingKubSU.database.DBUtils;
import ru.shirko.telegram.InformingKubSU.database.UserEntity;
import ru.shirko.telegram.InformingKubSU.telegram.keyboards.InlineKeyboardMaker;
import ru.shirko.telegram.InformingKubSU.telegram.keyboards.ReplyKeyboardMaker;
import ru.shirko.telegram.InformingKubSU.telegram.user.UserSendMessage;
import ru.shirko.telegram.InformingKubSU.telegram.user.UserUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import static ru.shirko.telegram.InformingKubSU.InformingKubSuApplication.*;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {

    AccessConfig accessConfig;

    InlineKeyboardMaker inlineKeyboardMaker;
    ReplyKeyboardMaker replyKeyboardMaker;

    List<String> updateUser = new ArrayList<>();

    Map<String, String> kubsuLogin = new HashMap<>();

    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString();

        if (message.hasDocument()) {
            if (usersSendCommandStates.containsKey(chatId) &&
                    usersSendCommandStates.get(chatId).equals(SendCommandStateEnum.INPUT_MESSAGE)) {

                userPreparingMessage.get(chatId).setDocument(message.getDocument());
                userPreparingMessage.get(chatId).setText(message.getCaption());

                try {
                    userPreparingMessage.get(chatId).sendMessageExecute();
                }
                catch (IllegalArgumentException e) {
                    return getExceptionMessage(chatId);
                }
                finally {
                    usersSendCommandStates.remove(chatId);
                }

                return getSendSuccessMessage(chatId);

            }
        }

        String inputText = message.getText();
        if (inputText == null) {
            throw new IllegalArgumentException();
        }
        else if (inputText.equals("/start")) {
            return getStartMessage(chatId);
        }
        else if (inputText.equals("/login")) {
            return getLoginStartMessage(chatId);
        }
        else if (inputText.equals("/update")) {
            updateUser.add(chatId);
            return getLoginStartMessage(chatId);
        }
        /**
         * Авторизация нового пользователя
         */
        else if (userLoginStates.containsKey(chatId)) {
            if (userLoginStates.get(chatId).equals(LoginStateEnum.INPUT_FULLNAME_STATE)) {
                users.get(chatId).setFullName(inputText.trim().toUpperCase());
//                return getLoginChooseRole(chatId);
                return getLoginEnterPasswordMessage(chatId);
            }
            else if (userLoginStates.get(chatId).equals(LoginStateEnum.INPUT_KUBSU_LOGIN_STATE)) {
                kubsuLogin.put(chatId, inputText.trim());
                if (users.get(chatId).getRole().equals(AccessRightEnum.STUDENT.name())) {
                    return getLoginStudentAuthorizationPassMessage(chatId);
                }
                else if (users.get(chatId).getRole().equals(AccessRightEnum.TEACHER.name())) {
//                    return getLoginStudentAuthorizationPassMessage(chatId);
                    if (inputText.trim().charAt(0) == 's') {
                        kubsuLogin.remove(chatId);
                        return getWrongLoginTeacherAuthorizationPassMessage(chatId);
                    }
                    else if (inputText.trim().equals("admin")) {
                        kubsuLogin.remove(chatId);
                        return getLoginTruthPasswordMessage(chatId);
                    }
                    return getLoginTeacherAuthorizationPassMessage(chatId);
                }
            }
            else if (userLoginStates.get(chatId).equals(LoginStateEnum.INPUT_KUBSU_STUDENT_PASSWORD_STATE)) {
                String [] studentAuthData = new String[] { kubsuLogin.get(chatId), inputText.trim() };
                kubsuLogin.remove(chatId);

                try {
                    List<String> studentKubSUData = loginKubSUStudent(studentAuthData);
                    users.get(chatId).setFullName(studentKubSUData.get(0));
                    users.get(chatId).setStudentGroup(studentKubSUData.get(1));
                }
                catch (IllegalArgumentException e) {
                    userLoginStates.replace(chatId, LoginStateEnum.INPUT_KUBSU_LOGIN_STATE);
                    return new SendMessage(chatId, e.getMessage() + "\n" + BotMessageEnum.LOGIN_INPUT_KUBSU_LOGIN_MESSAGE.getMessage());
                }

                return getLoginStudentUnderGroupMessage(chatId);
            }
            else if (userLoginStates.get(chatId).equals(LoginStateEnum.INPUT_STUDENT_UNDER_GROUP_STATE)) {
                String course = users.get(chatId).getStudentCourse();
                String group = users.get(chatId).getStudentGroup();
                String under_group = inputText.trim();

                users.get(chatId).setStudentGroup(course + "/" + group + "/" + under_group);

                return getLoginTruthPasswordMessage(chatId);
            }
            else if (userLoginStates.get(chatId).equals(LoginStateEnum.INPUT_KUBSU_TEACHER_PASSWORD_STATE)) {
                String [] teacherAuthData = new String[] { kubsuLogin.get(chatId), inputText.trim() };
//                String teacherAuthData;
                kubsuLogin.remove(chatId);

                try {
                    String teacherKubSUData = loginKubSUTeacher(teacherAuthData);
                    users.get(chatId).setFullName(teacherKubSUData);
//                    users.get(chatId).setStudentGroup(studentKubSUData.get(1));
                }
                catch (IllegalArgumentException e) {
                    userLoginStates.replace(chatId, LoginStateEnum.INPUT_KUBSU_LOGIN_STATE);
                    return new SendMessage(chatId, e.getMessage() + "\n" + BotMessageEnum.LOGIN_INPUT_KUBSU_LOGIN_MESSAGE.getMessage());
                }

                return getLoginTruthPasswordMessage(chatId);
            }
//            else if (userLoginStates.get(chatId).equals(LoginStateEnum.INPUT_STUDENT_GROUP_STATE)) {
//                inputText = inputText.trim().replace(" ", "/");
//                users.get(chatId).setStudentGroup(inputText);
//                if (!checkInputStudentGroup(inputText)) {
//                    return new SendMessage(chatId, "Неправильный формат ввода! Повторите попытку.");
//                }
//                return getLoginEnterPasswordMessage(chatId);
//            }
            else if (userLoginStates.get(chatId).equals(LoginStateEnum.INPUT_ENTER_PASSWORD_STATE)) {
                if (checkInputPassword(chatId, inputText)) {
                    return getLoginTruthPasswordMessage(chatId);
                }
                return new SendMessage(chatId, BotMessageEnum.LOGIN_WRONG_PASSWORD_MESSAGE.getMessage());
            }
        }
        else if (inputText.equals(ButtonNameEnum.HELP.getButtonName())) {
            return getHelpMessage(chatId);
        }
        else if (inputText.equals(ButtonNameEnum.SEND_MESSAGE.getButtonName())) {
            return getSendCommandMessage(chatId);
        }
        else if (inputText.equals(ButtonNameEnum.SEND_TO_MY_OWN_GROUP.getButtonName())) {
            return getSendToMyOwnGroupMessage(chatId);
        }
        /**
         * Пользователь вводит сообщение
         */
        if (usersSendCommandStates.containsKey(chatId)) {
            if (usersSendCommandStates.get(chatId).equals(SendCommandStateEnum.INPUT_GROUP)) {
                return getSendCommandInputGroupMessage(chatId, inputText);
            }
            else if (usersSendCommandStates.get(chatId).equals(SendCommandStateEnum.INPUT_NAME_MEMBER)) {
                return getSendCommandInputNameMemberMessage(chatId, inputText);
            }
            else if (usersSendCommandStates.get(chatId).equals(SendCommandStateEnum.INPUT_COURSE)) {
                return getSendCommandInputCourseMessage(chatId, inputText);
            }
            else if (usersSendCommandStates.get(chatId).equals(SendCommandStateEnum.INPUT_MESSAGE)) {
                userPreparingMessage.get(chatId).setText(inputText);

                try {
                    userPreparingMessage.get(chatId).sendMessageExecute();
                }
                catch (IllegalArgumentException e) {
                    return getExceptionMessage(chatId);
                }
                finally {
                    usersSendCommandStates.remove(chatId);
                }

                return getSendSuccessMessage(chatId);
            }
        }

        return new SendMessage(chatId, BotMessageEnum.NON_COMMAND_MESSAGE.getMessage());
    }


    /**
     * Реализации ответов
     */

    public SendMessage getStartMessage(String chatId) {
        if (!users.containsKey(chatId))
            users.put(chatId, new UserUtils(chatId));

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.START_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public SendMessage getLoginStartMessage(String chatId) {
        if (!userLoginStates.containsKey(chatId))
//            userLoginStates.put(chatId, LoginStateEnum.INPUT_FULLNAME_STATE);
            userLoginStates.put(chatId, LoginStateEnum.INPUT_ROLE_STATE);
        else
            userLoginStates.replace(chatId, LoginStateEnum.INPUT_ROLE_STATE);
//            userLoginStates.replace(chatId, LoginStateEnum.INPUT_FULLNAME_STATE);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.LOGIN_CHOOSE_ROLE_MESSAGE.getMessage());
//        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.LOGIN_START_MESSAGE.getMessage());
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getChooseRoleButtons());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public SendMessage getLoginChooseRole(String chatId) {
        userLoginStates.replace(chatId, LoginStateEnum.INPUT_ROLE_STATE);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.LOGIN_CHOOSE_ROLE_MESSAGE.getMessage());
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getChooseRoleButtons());
        return sendMessage;
    }


    public SendMessage getLoginStudentAuthorizationPassMessage(String chatId) {
        userLoginStates.replace(chatId, LoginStateEnum.INPUT_KUBSU_STUDENT_PASSWORD_STATE);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.LOGIN_INPUT_KUBSU_PASS_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public SendMessage getLoginTeacherAuthorizationPassMessage(String chatId) {
        userLoginStates.replace(chatId, LoginStateEnum.INPUT_KUBSU_TEACHER_PASSWORD_STATE);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.LOGIN_INPUT_KUBSU_PASS_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public SendMessage getWrongLoginTeacherAuthorizationPassMessage(String chatId) {
//        userLoginStates.replace(chatId, LoginStateEnum.INPUT_KUBSU_TEACHER_PASSWORD_STATE);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.LOGIN_WRONG_INPUT_KUBSU_LOGIN_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public SendMessage getLoginStudentUnderGroupMessage(String chatId) {
        userLoginStates.replace(chatId, LoginStateEnum.INPUT_STUDENT_UNDER_GROUP_STATE);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.LOGIN_INPUT_STUDENT_UNDER_GROUP.getMessage());
        return sendMessage;
    }


    public SendMessage getLoginEnterPasswordMessage(String chatId) {
        userLoginStates.replace(chatId, LoginStateEnum.INPUT_ENTER_PASSWORD_STATE);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.LOGIN_ENTER_PASSWORD_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public SendMessage getLoginTruthPasswordMessage(String chatId) {
        userLoginStates.remove(chatId);

        if (updateUser.contains(chatId)) {
            DBUtils.updateUserEntity(new UserEntity(users.get(chatId)));
            updateUser.remove(chatId);
        }
        else {
            try {
                DBUtils.insertUserEntity(new UserEntity(users.get(chatId)));
            }
            catch (SQLException e) {
                DBUtils.updateUserEntity(new UserEntity(users.get(chatId)));
            }
        }


        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.LOGIN_TRUTH_PASSWORD_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard(users.get(chatId).getRole()));
        return sendMessage;
    }


    public SendMessage getSendCommandMessage(String chatId) {
        UserSendMessage userSendMessage = new UserSendMessage();
        userSendMessage.setChatId(chatId);

        userPreparingMessage.put(chatId, userSendMessage);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.SEND_COMMAND_MESSAGE.getMessage());
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getChooseSendToButtons(users.get(chatId).getRole()));
        return sendMessage;
    }


    public SendMessage getSendToMyOwnGroupMessage(String chatId) {
        UserSendMessage userSendMessage = new UserSendMessage();
        userSendMessage.setChatId(chatId);

        userPreparingMessage.put(chatId, userSendMessage);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.SEND_COMMAND_MESSAGE.getMessage());
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getChooseSendToGroupButtons());
        return sendMessage;
    }


    public SendMessage getSendCommandInputGroupMessage(String chatId, String group) {
        usersSendCommandStates.replace(chatId, SendCommandStateEnum.INPUT_MESSAGE);

        //UserSendMessage userSendMessage = new UserSendMessage();
        //userPreparingMessage.get(chatId).setChatId(chatId);
        userPreparingMessage.get(chatId).setGroup(groupToMessage(group));

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.SEND_COMMAND_INPUT_TEXT_MESSAGE.getMessage());
        return sendMessage;
    }


    public SendMessage getSendCommandInputNameMemberMessage(String chatId, String FIO) {
        usersSendCommandStates.replace(chatId, SendCommandStateEnum.INPUT_MESSAGE);

        boolean isFullName = FIO.trim().split(" ").length == 3;
        if (isFullName)
            userPreparingMessage.get(chatId).setFullName(FIO.toUpperCase());
        else
            userPreparingMessage.get(chatId).setShortName(FIO.toUpperCase());

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.SEND_COMMAND_INPUT_TEXT_MESSAGE.getMessage());
        return sendMessage;
    }


    public SendMessage getSendCommandInputCourseMessage(String chatId, String course) {
        usersSendCommandStates.replace(chatId, SendCommandStateEnum.INPUT_MESSAGE);

        userPreparingMessage.get(chatId).setCourse(course);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.SEND_COMMAND_INPUT_TEXT_MESSAGE.getMessage());
        return sendMessage;
    }


    public SendMessage getHelpMessage(String chatId) {
        if (userPreparingMessage.containsKey(chatId)) userPreparingMessage.remove(chatId);
        if (usersSendCommandStates.containsKey(chatId)) usersSendCommandStates.remove(chatId);

        UserUtils user = users.get(chatId);

        String role = accessToRole(user.getRole());
        String group = groupToView(user.getGroup());

        String message = String.format(BotMessageEnum.HELP_MESSAGE.getMessage(), user.getFullName(), role, group);
        SendMessage sendMessage = new SendMessage(chatId, message);
        return sendMessage;
    }


    public SendMessage getSendSuccessMessage(String chatId) {
        userPreparingMessage.remove(chatId);
        usersSendCommandStates.remove(chatId);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.SEND_SUCCESS_MESSAGE.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }


    public SendMessage getExceptionMessage(String chatId) {
        userPreparingMessage.remove(chatId);
        usersSendCommandStates.remove(chatId);

        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.EXCEPTION_NOT_FOUND_MEMBER.getMessage());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }





    private boolean checkInputStudentGroup(String text) {
        return text.split("/").length == 3;
    }


    private boolean checkInputPassword(String chatId, String text) {
        if (users.get(chatId).getRole().equals(AccessRightEnum.STUDENT.name()) &&
                text.equals(accessConfig.getStudent_password())) {
            return true;
        }
        else if (users.get(chatId).getRole().equals(AccessRightEnum.TEACHER.name()) &&
                text.equals(accessConfig.getTeacher_password())) {
            return true;
        }
        else if (users.get(chatId).getRole().equals(AccessRightEnum.DEANERY_MEMBER.name()) &&
                text.equals(accessConfig.getDeanery_password())) {
            return true;
        }
        return false;
    }


    private String groupToMessage(String group) {
        group = group.trim();
        if (group.split("/").length == 1) {
            return group + "/0";
        }
        else {
            return group;
        }
    }


    private String groupToView(String group) {
        if (group == null) return "Отсутствует";

        String [] vGroup = group.split("/");

        String ans = vGroup[1];
        if (!vGroup[2].equals("0"))
            ans += "/" + vGroup[2];

        return ans;
    }


    private String accessToRole(String access) {
        if (access.equals(AccessRightEnum.STUDENT.name()))
            return "Студент";
        else if (access.equals(AccessRightEnum.TEACHER.name()))
            return "Преподаватель";
        else
            return "Член деканата";
    }


    private List<String> loginKubSUStudent(String [] data) {
        org.jsoup.nodes.Document doc = null;
        List<String> studentData = new ArrayList<>();
        try {
            Connection.Response response = Jsoup.connect("https://kubsu.ru/user/")
                    .method(Connection.Method.GET)
                    .execute();
            response = Jsoup.connect("https://kubsu.ru/user/")
                    .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 YaBrowser/23.1.4.778 Yowser/2.5 Safari/537.36")
                    .data("name", data[0],
                            "pass", data[1],
                            "form_build_id", "form-6WaXgfgNZ-mR5iIyNjJ1cazdjmfi1aO9HjwkGxI3A0Y",
                            "form_id", "user_login",
                            "op", "Войти")
                    .method(Connection.Method.POST)
                    .timeout(10000).execute();


            doc = Jsoup.connect("https://www.kubsu.ru/public-portfolio").cookies(response.cookies()).get();
//            Element blockRega = doc.select("div.foot").first();
            String [] fio = doc.select("head > title").text().split(Pattern.quote("|"));

            if (fio[0].trim().equals("Гость")) throw new IllegalArgumentException("Неверный логин или пароль");

            studentData.add(fio[0].trim());

//            System.out.println(fio[0].trim());
//            System.out.println(doc.select("head > meta"));
//            System.out.println(doc.select("head > meta").get(2).attr("content"));
            for (Element el : doc.select("head > meta")) {
                if (el.attr("about").equals("/ru/taxonomy/term/2103")) {
                    String group = el.attr("content");
//                    System.out.println(el.attr("content"));
                    studentData.add(group.charAt(0) + "/" + group + "/0");
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return studentData;
    }

    private String loginKubSUTeacher(String [] data) {
        org.jsoup.nodes.Document doc = null;
//        List<String> teacherData = new ArrayList<>();
        String teacherData = null;
        try {
            Connection.Response response = Jsoup.connect("https://kubsu.ru/user/")
                    .method(Connection.Method.GET)
                    .execute();
            response = Jsoup.connect("https://kubsu.ru/user/")
                    .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 YaBrowser/23.1.4.778 Yowser/2.5 Safari/537.36")
                    .data("name", data[0],
                            "pass", data[1],
                            "form_build_id", "form-6WaXgfgNZ-mR5iIyNjJ1cazdjmfi1aO9HjwkGxI3A0Y",
                            "form_id", "user_login",
                            "op", "Войти")
                    .method(Connection.Method.POST)
                    .timeout(10000).execute();


            doc = Jsoup.connect("https://www.kubsu.ru/public-portfolio").cookies(response.cookies()).get();
//            Element blockRega = doc.select("div.foot").first();
            String [] fio = doc.select("head > title").text().split(Pattern.quote("|"));

            if (fio[0].trim().equals("Гость")) throw new IllegalArgumentException("Неверный логин или пароль");

            teacherData = fio[0].trim();
//            teacherData.add(fio[0].trim());

        } catch (IOException e) {

            e.printStackTrace();
        }

        return teacherData;
    }
}
