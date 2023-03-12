package ru.shirko.telegram.InformingKubSU.telegram.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import ru.shirko.telegram.InformingKubSU.constans.bot.BotMessageEnum;
import ru.shirko.telegram.InformingKubSU.constans.bot.ButtonNameEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.AccessRightEnum;
import ru.shirko.telegram.InformingKubSU.telegram.TelegramApiClient;

import static ru.shirko.telegram.InformingKubSU.InformingKubSuApplication.users;

@Component
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserSendMessage {
    String chatId;

    String text;

    Document document;

    String group;

    String course;

    String fullName;

    String shortName;

    String messageTo;

    //TelegramApiClient client;

    public void sendMessageExecute() throws IllegalArgumentException {

        if (text == null)
            text = "...";

        if (messageTo.equals(ButtonNameEnum.TO_ONLY_GROUP.name()) ||
                messageTo.equals(ButtonNameEnum.TO_ENTRY_GROUP.name()) ||
                messageTo.equals(ButtonNameEnum.TO_SUBGROUP.name())
        ) {
            sendMessageToGroupExecute();
        }
        else if (messageTo.equals(ButtonNameEnum.TO_ALL_MEMBERS.name())) {
            sendMessageToAllExecute();
        }
        else if (messageTo.equals(ButtonNameEnum.TO_ALL_STUDENTS.name())) {
            if (course == null)
                sendMessageToAllStudentsExecute();
            else
                sendMessageToCertainCourseStudentExecute();
        }
        else if (messageTo.equals(ButtonNameEnum.TO_ALL_TEACHERS.name())) {
            sendMessageToTeachersExecute();
        }
        else if (messageTo.equals(ButtonNameEnum.TO_ONLY_MEMBER.name())) {
            if (fullName != null)
                sendMessageToOnlyMemberFullNameExecute();
            else if (shortName != null)
                sendMessageToOnlyMemberShortNameExecute();
        }

    }

    private void sendMessageToGroupExecute() {

        for (String chatId : users.keySet()) {
            if (chatId.equals(this.chatId)) continue;

            UserUtils user = users.get(chatId);
            if (user.getRole().equals(AccessRightEnum.STUDENT.name())) {
                String [] param = group.split("/");
                if (param[0].equals(user.getStudentGroup()) &&
                        (param[1].equals("0") || param[1].equals(user.getStudentSubGroup()))) {
                    SendMessage sendMessage = new SendMessage(chatId,
                            "Сообщение от: " + users.get(this.chatId).getFullName() + "\n\n" + text);

                    //TODO
                    TelegramApiClient.sendMessageTo(chatId, sendMessage.getText(), document);

                }
            }
        }
    }


    private void sendMessageToAllExecute() {

        for (String chatId : users.keySet()) {
            if (chatId.equals(this.chatId)) continue;

            UserUtils user = users.get(chatId);
            SendMessage sendMessage = new SendMessage(chatId,
                    "Сообщение от: " + users.get(this.chatId).getFullName() + "\n\n" + text);

            //TODO
            TelegramApiClient.sendMessageTo(chatId, sendMessage.getText(), document);


        }
    }


    private void sendMessageToAllStudentsExecute() {

        for (String chatId : users.keySet()) {
            if (chatId.equals(this.chatId)) continue;

            UserUtils user = users.get(chatId);
            if (user.getRole().equals(AccessRightEnum.STUDENT.name())) {
                SendMessage sendMessage = new SendMessage(chatId,
                        "Сообщение от: " + users.get(this.chatId).getFullName() + "\n\n" + text);

                //TODO
                TelegramApiClient.sendMessageTo(chatId, sendMessage.getText(), document);

            }

        }
    }


    private void sendMessageToCertainCourseStudentExecute() {
        for (String chatId : users.keySet()) {
            if (chatId.equals(this.chatId)) continue;

            UserUtils user = users.get(chatId);
            if (user.getRole().equals(AccessRightEnum.STUDENT.name()) &&
                    user.getStudentCourse().equals(this.course)) {
                SendMessage sendMessage = new SendMessage(chatId,
                        "Сообщение от: " + users.get(this.chatId).getFullName() + "\n\n" + text);

                //TODO
                TelegramApiClient.sendMessageTo(chatId, sendMessage.getText(), document);

            }

        }
    }


    private void sendMessageToTeachersExecute() {
        for (String chatId : users.keySet()) {
            if (chatId.equals(this.chatId)) continue;

            UserUtils user = users.get(chatId);
            if (user.getRole().equals(AccessRightEnum.TEACHER.name())) {
                SendMessage sendMessage = new SendMessage(chatId,
                        "Сообщение от: " + users.get(this.chatId).getFullName() + "\n\n" + text);

                //TODO
                TelegramApiClient.sendMessageTo(chatId, sendMessage.getText(), document);

            }

        }
    }


    private void sendMessageToOnlyMemberFullNameExecute() {
        for (String chatId : users.keySet()) {
            if (chatId.equals(this.chatId)) continue;

            UserUtils user = users.get(chatId);
            if (user.getFullName().equals(fullName)) {
                SendMessage sendMessage = new SendMessage(chatId,
                        "Сообщение от: " + users.get(this.chatId).getFullName() + "\n\n" + text);

                //TODO
                TelegramApiClient.sendMessageTo(chatId, sendMessage.getText(), document);
                return;
            }

        }
        throw new IllegalArgumentException(BotMessageEnum.EXCEPTION_NOT_FOUND_MEMBER.getMessage());
    }


    private void sendMessageToOnlyMemberShortNameExecute() {
        for (String chatId : users.keySet()) {
            if (chatId.equals(this.chatId)) continue;

            UserUtils user = users.get(chatId);
            if (user.getShortName().equals(shortName)) {
                SendMessage sendMessage = new SendMessage(chatId,
                        "Сообщение от: " + users.get(this.chatId).getFullName() + "\n\n" + text);

                //TODO
                TelegramApiClient.sendMessageTo(chatId, sendMessage.getText(), document);
                return;
            }

        }
    }

}
