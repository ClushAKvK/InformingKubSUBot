package ru.shirko.telegram.InformingKubSU.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
public class AccessConfig {
    @Value("${student-password}")
    String student_password;
    @Value("${teacher-password}")
    String teacher_password;
    @Value("${deanery-password}")
    String deanery_password;

    @Value("${database-path}")
    String DB_path;
}
