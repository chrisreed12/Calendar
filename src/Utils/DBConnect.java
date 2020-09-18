package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
//* Allows for actually connecting to database with a user name and password supplied by the user   *//
//    public static Connection getConnection(String user, String pass) {
//        Connection connection = null;
//        String url = "jdbc:mysql://3.227.166.251/U04xzx";
//        String userName = user;
//        String passWord = pass;
//
//        try {
//            connection = DriverManager.getConnection(url, userName, passWord);
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//        return connection;
//    }

    public static Connection getConnection() {
        Connection connection = null;
        String url = "jdbc:mysql://3.227.166.251/U04xzx";
        String userName = "U04xzx";
        String passWord = "53688377209";

        try {
            connection = DriverManager.getConnection(url, userName, passWord);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }
}
