package ru.shirko.telegram.InformingKubSU.telegram.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;
import ru.shirko.telegram.InformingKubSU.constans.user.AccessRightEnum;
import ru.shirko.telegram.InformingKubSU.database.UserEntity;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserUtils {
    final String chatId;
    String role;
    String fullName;

    /**
     * @studentGroup format: "<course>/<group>/<subGroup>"
     */
    @Nullable
    String studentGroup;

    public UserUtils(UserEntity userEntity) {
        this.chatId =userEntity.getChatId();
        this.role = userEntity.getRole();
        this.fullName = userEntity.getFullName().toUpperCase();
        this.studentGroup = userEntity.getGroup();
    }

    public String getGroup() {
        return studentGroup;
    }

    public String getShortName() {
        String [] FIO = fullName.split(" ");
        String shortName =
                FIO[1].charAt(0) + "." +
                FIO[2].charAt(0) + "." +
                FIO[0];
        return shortName;
    }

    public String getStudentCourse() {
        String [] studentInfo = studentGroup.split("/");
        return studentInfo[0];
    }

    public String getStudentGroup() {
        String [] studentInfo = studentGroup.split("/");
        return studentInfo[1];
    }

    public String getStudentSubGroup() {
        String [] studentInfo = studentGroup.split("/");
        return studentInfo[2];
    }
}
