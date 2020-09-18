package Utils;

import Resources.City;
import Resources.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.text.ParseException;

public class DBCustomers {
    static String timeStamp;

    static {
        try {
            timeStamp = String.valueOf(TimeConverter.toUTC(new Timestamp(System.currentTimeMillis())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Customer> getCustomers() throws SQLException {
        String query = selectCustomers();
        return getCustFromDB(query);
    }

    public static Customer getCustomer(int custId) throws SQLException {
        String query = selectCustomers() + " WHERE customerId = " + custId;
        Customer thisCustomer = getCustFromDB(query).get(0);
        return thisCustomer;
    }

    private static ObservableList<Customer> getCustFromDB(String givenQuery) throws SQLException {
        ObservableList<Customer> customersFromDB = FXCollections.observableArrayList();
        Connection connection = DBConnect.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(givenQuery);
        while (resultSet.next()) {
            Customer customer = new Customer();
            customer.setCustomerId(resultSet.getInt("customerId"));
            customer.setCustomerName(resultSet.getString("customerName"));
            customer.setAddressId(resultSet.getInt("addressId"));
            customer.setAddress(resultSet.getString("address"));
            customer.setAddress2(resultSet.getString("address2"));
            customer.setPostalCode(resultSet.getString("postalCode"));
            customer.setPhone(resultSet.getString("phone"));
            customer.setCityId(resultSet.getInt("cityId"));
            customer.setCity(resultSet.getString("city"));
            customer.setCountryId(resultSet.getInt("countryId"));
            customer.setCountry(resultSet.getString("country"));
            customer.setActive(resultSet.getInt("active"));
            if (!(customer == null)) {
                customersFromDB.add(customer);
            }
        }
        connection.close();
        return customersFromDB;
    }

    private static String selectCustomers() {
        String selectAll = "select customerId, customerName, c.addressId, address, address2, postalCode, phone, a.cityId, city, cty.countryId, country, active, c.createDate, c.createdBy, c.lastUpdate, c.LastUpdateBy " +
                "from customer c " +
                "join address a on c.addressId = a.addressId " +
                "join city cty on a.cityId = cty.cityId " +
                "join country cnty on cty.countryId = cnty.countryId";
        return selectAll;
    }

    public static void delete(Customer customer) throws SQLException {
        String query = "delete from customer where customerId = " + customer.getCustomerId();
        Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.execute();
        connection.close();
    }

    public static Boolean updateCustomer(Customer customer) throws SQLException {
        ObservableList<City> cities = DBCities.getCities();
        Boolean addCity = true;
        Connection connection = DBConnect.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        for (City city : cities) {
            if (city.getCity().equals(customer.getCity())){
                customer.setCityId(city.getCityId());
                addCity = false;
            }
        }

        if(addCity){
            String insertCity = "INSERT INTO city ("
                    +"city,"
                    +"countryId,"
                    +"createDate,"
                    +"createdBy,"
                    +"lastUpdate,"
                    +"lastUpdateBy) "
                    +"VALUES (?,?,?,?,?,?)";
            PreparedStatement insertCityStatement = connection.prepareStatement(insertCity, Statement.RETURN_GENERATED_KEYS);
            insertCityStatement.setString(1, customer.getCity());
            insertCityStatement.setInt(2, customer.getCountryId());
            insertCityStatement.setString(3, timeStamp);
            insertCityStatement.setString(4, metaData.getUserName());
            insertCityStatement.setString(5, timeStamp);
            insertCityStatement.setString(6, metaData.getUserName());
            insertCityStatement.executeUpdate();
            ResultSet rs3 = insertCityStatement.getGeneratedKeys();
            rs3.next();
            customer.setCityId(rs3.getInt(1));
        }

        if(customer.getAddressId() != 0){
            String updateAddress = "UPDATE address SET "
                    +"address = ?,"
                    +"address2 = ?,"
                    +"cityId = ?,"
                    +"postalCode = ?,"
                    +"phone = ?,"
                    +"lastUpdate = ?,"
                    +"lastUpdateBy = ? "
                    +"WHERE addressId = " + customer.getAddressId();
            PreparedStatement updateAddressStatement = connection.prepareStatement(updateAddress);
            updateAddressStatement.setString(1, customer.getAddress());
            updateAddressStatement.setString(2, customer.getAddress2());
            updateAddressStatement.setInt(3, customer.getCityId());
            updateAddressStatement.setString(4, customer.getPostalCode());
            updateAddressStatement.setString(5, customer.getPhone());
            updateAddressStatement.setString(6, timeStamp);
            updateAddressStatement.setString(7, metaData.getUserName());

        }

        int result = 0;

        if(customer.getCustomerId() != 0){
            String updateCustomer = "UPDATE customer SET "
                    +"customerName = ?,"
                    +"addressId = ?,"
                    +"lastUpdate = ?,"
                    +"lastUpdateBy = ? "
                    +"WHERE customerId = " + customer.getCustomerId();
            PreparedStatement updateCustomerStatement = connection.prepareStatement(updateCustomer);
            updateCustomerStatement.setString(1, customer.getCustomerName());
            updateCustomerStatement.setInt(2, customer.getAddressId());
            updateCustomerStatement.setString(3, timeStamp);
            updateCustomerStatement.setString(4, metaData.getUserName());
            result = updateCustomerStatement. executeUpdate();
            connection.close();
        }
        return result > 0;
    }

    public static Boolean addCustomer(Customer customer) throws SQLException {
        int count = 0;

        String query1 = "INSERT INTO customer ("
                +"customerName,"
                +"addressId,"
                +"active,"
                +"createDate,"
                +"createdBy,"
                +"lastUpdate,"
                +"lastUpdateBy)"
                +" VALUES (?,?,?,?,?,?,?)";

        String query2 = "INSERT INTO address ("
                +"address,"
                +"address2,"
                +"cityId,"
                +"postalCode,"
                +"phone,"
                +"createDate,"
                +"createdBy,"
                +"lastUpdate,"
                +"lastUpdateBy) "
                +"VALUES (?,?,?,?,?,?,?,?,?)";

        String query3 = "INSERT INTO city ("
                +"city,"
                +"countryId,"
                +"createDate,"
                +"createdBy,"
                +"lastUpdate,"
                +"lastUpdateBy) "
                +"VALUES (?,?,?,?,?,?)";

        Connection connection = DBConnect.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        try {

            PreparedStatement statement3 = connection.prepareStatement(query3, Statement.RETURN_GENERATED_KEYS);
            statement3.setString(1, customer.getCity());
            statement3.setInt(2, customer.getCountryId());
            statement3.setString(3, timeStamp);
            statement3.setString(4, metaData.getUserName());
            statement3.setString(5, timeStamp);
            statement3.setString(6, metaData.getUserName());
            count = count + statement3.executeUpdate();
            ResultSet rs3= statement3.getGeneratedKeys();
            rs3.next();
            customer.setCityId(rs3.getInt(1));

            PreparedStatement statement2 = connection.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS);
            statement2.setString(1, customer.getAddress());
            statement2.setString(2, customer.getAddress2());
            statement2.setInt(3, customer.getCityId());
            statement2.setString(4, customer.getPostalCode());
            statement2.setString(5, customer.getPhone());
            statement2.setString(6, timeStamp);
            statement2.setString(7, metaData.getUserName());
            statement2.setString(8, timeStamp);
            statement2.setString(9, metaData.getUserName());
            count = count + statement2.executeUpdate();
            ResultSet rs2 = statement2.getGeneratedKeys();
            rs2.next();
            customer.setAddressId(rs2.getInt(1));

            PreparedStatement statement1 = connection.prepareStatement(query1);
            statement1.setString(1, customer.getCustomerName());
            statement1.setInt(2, customer.getAddressId());
            statement1.setInt(3, customer.getActive());
            statement1.setString(4, timeStamp);
            statement1.setString(5, metaData.getUserName());
            statement1.setString(6, timeStamp);
            statement1.setString(7, metaData.getUserName());
            count = count + statement1.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count == 3;
    }
}
