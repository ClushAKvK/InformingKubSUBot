package ru.shirko.telegram.InformingKubSU.database;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.shirko.telegram.InformingKubSU.config.AccessConfig;
import ru.shirko.telegram.InformingKubSU.telegram.user.UserUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;


@Setter @Getter
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class DBUtils {

    AccessConfig accessConfig;

    static Connection connection;

    public void openConnection() {
        String path = accessConfig.getDB_path();
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + path;
            System.out.println(url);

            if (connection == null)
                connection = DriverManager.getConnection(url, "", "");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void closeConnection() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void insertUserEntity(UserEntity userEntity) throws SQLException {
        String sql = "INSERT INTO User VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(2, userEntity.getChatId());
        ps.setString(3, userEntity.getRole());
        ps.setString(4, userEntity.getFullName());
        ps.setString(5, userEntity.getGroup());
        ps.executeUpdate();
        ps.close();

    }


    public static void updateUserEntity(UserEntity userEntity) {
        String sql = "UPDATE User SET role=?, full_name=?, group_name=? WHERE chat_id=?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userEntity.getRole());
            ps.setString(2, userEntity.getFullName());
            ps.setString(3, userEntity.getGroup());
            ps.setString(4, userEntity.getChatId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static Map<String, UserUtils> getUsersData() {
        Map<String, UserUtils> map = new HashMap<>();

        Statement statement = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM User";

        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                UserEntity obj = new UserEntity();
                obj.setId(rs.getInt("id"));
                obj.setChatId(rs.getString("chat_id"));
                obj.setRole(rs.getString("role"));
                obj.setFullName(rs.getString("full_name"));
                obj.setGroup(rs.getString("group_name"));

                map.put(obj.getChatId(), new UserUtils(obj));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

}
