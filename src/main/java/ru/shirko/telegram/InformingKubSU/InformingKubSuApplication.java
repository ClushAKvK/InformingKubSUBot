package ru.shirko.telegram.InformingKubSU;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import ru.shirko.telegram.InformingKubSU.constans.user.LoginStateEnum;
import ru.shirko.telegram.InformingKubSU.constans.user.SendCommandStateEnum;
import ru.shirko.telegram.InformingKubSU.telegram.user.UserSendMessage;
import ru.shirko.telegram.InformingKubSU.telegram.user.UserUtils;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class InformingKubSuApplication {

	public static Map<String, UserUtils> users = new HashMap<>();

	public static Map<String, LoginStateEnum> userLoginStates = new HashMap<>();

	public static Map<String, SendCommandStateEnum> usersSendCommandStates = new HashMap<>();

	public static Map<String, UserSendMessage> userPreparingMessage = new HashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(InformingKubSuApplication.class, args);
	}

}
