package Utils;

import Resources.Report;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DBReport {
    public static ObservableList<Report> getAptByMon() throws SQLException {
        ObservableList<Report> rptAptByMon = FXCollections.observableArrayList();
        String query = "Select MONTHNAME(start) month, type, count(type) from appointment group by month, type";
        Connection connection = DBConnect.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()){
            Report report = new Report();
            report.setFirstCol(resultSet.getString("month"));
            report.setSecondCol(resultSet.getString("type"));
            report.setThirdCol(resultSet.getString("count(type)"));
            rptAptByMon.add(report);
        }
        return rptAptByMon;
    }

    public static ObservableList<Report> getCnsltSched() throws SQLException {
        ObservableList<Report> cnsltRpt = FXCollections.observableArrayList();
        String query = "SELECT contact, DATE_FORMAT(start, '%a, %c/%e %I:%i %p') as start, DATE_FORMAT(end, '%a, %c/%e %I:%i %p') as end, customerName from appointment, customer where (appointment.customerId = customer.customerId) and MONTH(start) = MONTH(CURDATE())";
        Connection connection = DBConnect.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()){
            Report report = new Report();
            report.setFirstCol(resultSet.getString("contact"));
            report.setSecondCol(resultSet.getString("start"));
            report.setThirdCol(resultSet.getString("end"));
            report.setFourthCol(resultSet.getString("customerName"));
            cnsltRpt.add(report);
        }

        return cnsltRpt;
    }

    public static ObservableList<Report> getAptByLoc() throws SQLException {
        ObservableList<Report> rptByLoc = FXCollections.observableArrayList();
        String query = "Select contact, location, count(location) from appointment group by contact, location";
        Connection connection = DBConnect.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()){
            Report report = new Report();
            report.setFirstCol(resultSet.getString("contact"));
            report.setSecondCol(resultSet.getString("location"));
            report.setThirdCol(resultSet.getString("count(location)"));
            rptByLoc.add(report);
        }
        return rptByLoc;
    }
}
