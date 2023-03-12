package ru.shirko.telegram.InformingKubSU.initialization;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import ru.shirko.telegram.InformingKubSU.database.DBUtils;

import static ru.shirko.telegram.InformingKubSU.InformingKubSuApplication.users;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class InitializingBeanImpl implements InitializingBean {

    DBUtils dbUtils;

    @Override
    public void afterPropertiesSet() throws Exception {
        dbUtils.openConnection();
        users = DBUtils.getUsersData();
    }
}
