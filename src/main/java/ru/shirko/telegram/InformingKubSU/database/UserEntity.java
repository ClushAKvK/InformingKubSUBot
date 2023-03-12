package ru.shirko.telegram.InformingKubSU.database;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.shirko.telegram.InformingKubSU.telegram.user.UserUtils;


@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    long id;
    String chatId;
    String role;
    String fullName;
    String group;

    public UserEntity(UserUtils userUtils) {
        this.chatId = userUtils.getChatId();
        this.role = userUtils.getRole();
        this.fullName = userUtils.getFullName();
        this.group = userUtils.getGroup();
    }

}
