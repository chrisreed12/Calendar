package Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.text.ParseException;

public class DBCountries {

    public static ObservableList<String> getCountries() throws SQLException {
        String query = "SELECT country from country;";
        ObservableList<String> countries = FXCollections.observableArrayList();
        Connection connection = DBConnect.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()){
            String country = new String();
            country = (resultSet.getString("country"));
            countries.add(country);
        }
        statement.close();
        resultSet.close();
        connection.close();
        return countries;
    }

    public static int addCountry(String country) throws SQLException, ParseException {
        int countryId;
        String timeStamp = String.valueOf(TimeConverter.toUTC(new Timestamp(System.currentTimeMillis())));
        Connection connection = DBConnect.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();

        String query4 = "INSERT INTO country ("
                +"country,"
                +"createDate,"
                +"createdBy,"
                +"lastUpdate,"
                +"lastUpdateBy)"
                +"VALUES (?,?,?,?,?)";

        PreparedStatement statement4 = connection.prepareStatement(query4, Statement.RETURN_GENERATED_KEYS);
        statement4.setString(1, country);
        statement4.setString(2, timeStamp);
        statement4.setString(3, metaData.getUserName());
        statement4.setString(4, timeStamp);
        statement4.setString(5, metaData.getUserName());
        statement4.execute();
        ResultSet rs = statement4.getGeneratedKeys();
        rs.next();
        countryId = rs.getInt(1);
        statement4.close();
        connection.close();
        return countryId;
    }

    public static int getCountryID(String selectedItem) throws SQLException {
        String query = "SELECT countryID from country WHERE country = '" + selectedItem +"'";
        Connection connection = DBConnect.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();
        int countryId = resultSet.getInt("countryId");
        statement.close();
        resultSet.close();
        connection.close();
        return countryId;
    }
}
