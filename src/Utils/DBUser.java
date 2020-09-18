package Utils;

import Resources.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DBUser {
    public ObservableList<User> getUsers() throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();
        String querry = "SELECT * from users";
        Connection connection = DBConnect.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(querry);
        while (resultSet.next()) {
            User user = new User();
            user.setUserId(resultSet.getInt("userId"));

        }
        return users;
    }

    public User getUser(int userId) {
        User user = new User();

        return user;
    }
}
