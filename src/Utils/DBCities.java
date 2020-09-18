package Utils;

import Resources.City;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DBCities {
    public static ObservableList<City> cities;

    public static ObservableList<City> getCities() throws SQLException {
        ObservableList<City> cities = FXCollections.observableArrayList();
        String query = "Select * from city";
        Connection connection = DBConnect.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()){
            City city = new City();
            city.setCityId(resultSet.getInt("cityId"));
            city.setCity(resultSet.getString("city"));
            cities.add(city);
        }
        resultSet.close();
        statement.close();
        connection.close();
        return cities;
    }
}
