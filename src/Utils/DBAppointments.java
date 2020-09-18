package Utils;

import Resources.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DBAppointments {

    public static ObservableList<Appointment> getMonthAppointment(LocalDate tempDate) throws SQLException, ParseException {
        ObservableList<Appointment> monthApt;
        Date monthStart = Date.valueOf(tempDate.withDayOfMonth(1));
        Date monthEnd = Date.valueOf(tempDate.plusMonths(1).withDayOfMonth(1));
        monthApt = getAptFromDB(new Timestamp(monthStart.getTime()), new Timestamp(monthEnd.getTime()));
        return monthApt;
    }

    public static ObservableList<Appointment> getWeekApt(LocalDate tempDate) throws SQLException, ParseException {
        ObservableList<Appointment> weekApt;
        TemporalField getWeek = WeekFields.of(Locale.getDefault()).dayOfWeek();
        Date weekStart = Date.valueOf(tempDate.with(getWeek, 1));
        Date weekEnd = Date.valueOf(tempDate.with(getWeek, 7).plusDays(1));
        weekApt = getAptFromDB(new Timestamp(weekStart.getTime()), new Timestamp(weekEnd.getTime()));
        return weekApt;
    }

    public static void deleteApt(Appointment appointment) throws SQLException {
        String query = "delete from appointment where appointmentId = " + appointment.getAppointmentId();
        Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.execute();
        connection.close();
    }

    private static ObservableList<Appointment> getAptFromDB(Timestamp start, Timestamp end) throws SQLException, ParseException {
        ObservableList<Appointment> appointmentsFromDB = FXCollections.observableArrayList();
        Connection connection = DBConnect.getConnection();
        Statement statement = connection.createStatement();
        String selectAllAppointment = "select appointmentId, userId, customer.customerId, customerName, title, description, location, contact, type, url, start, end from appointment, customer where appointment.customerId = customer.customerId";
        ResultSet resultSet = statement.executeQuery(selectAllAppointment + " AND start >= '" + start + "' and start <= '" + end + "'");
        while (resultSet.next()) {
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(resultSet.getInt("appointmentId"));
            appointment.setUserId(resultSet.getInt("userId"));
            appointment.setCustomerId(resultSet.getInt("customerId"));
            appointment.setCustomerName(resultSet.getString("customerName"));
            appointment.setTitle(resultSet.getString("title"));
            appointment.setDescription(resultSet.getString("description"));
            appointment.setLocation(resultSet.getString("location"));
            appointment.setContact(resultSet.getString("contact"));
            appointment.setType(resultSet.getString("type"));
            appointment.setUrl(resultSet.getString("url"));
            appointment.setStart(TimeConverter.fromUTC(resultSet.getTimestamp("start")));
            appointment.setEnd(TimeConverter.fromUTC(resultSet.getTimestamp("end")));
            if (!(appointment == null)) {
                appointmentsFromDB.add(appointment);
            }
        }
        connection.close();
        return appointmentsFromDB;
    }

    public static boolean scheduled(int appointmentId, Timestamp start, Timestamp end) throws SQLException, ParseException {
        System.out.println("AppointmentId from appt being edited: " + appointmentId);
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Connection connection = DBConnect.getConnection();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM appointment WHERE (start BETWEEN '" + TimeConverter.toUTC(start) + "' AND '" + TimeConverter.toUTC(end) + "') " +
                "OR (end BETWEEN '" + TimeConverter.toUTC(start) + "' AND '" + TimeConverter.toUTC(end) + "')";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()){
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(resultSet.getInt("appointmentId"));
            appointments.add(appointment);
        }
        for (Appointment appointment : appointments) {
            System.out.println("AppointmentID from appt from DB: " + appointment.getAppointmentId());
            if (appointment.getAppointmentId() == appointmentId){
                return true;
            }
        }
        return appointments.size() < 1;
    }

    public static boolean addAppointment(Appointment addApt) throws SQLException, ParseException {
        String query = "INSERT INTO appointment ("
                + "customerId,"
                + "userId,"
                + "title,"
                + "description,"
                + "location,"
                + "contact,"
                + "type,"
                + "url,"
                + "start,"
                + "end,"
                + "createDate,"
                + "createdBy,"
                + "lastUpdate,"
                + "lastUpdateBy) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int count = 0;
        Connection addAptCon = DBConnect.getConnection();
        DatabaseMetaData metaData = addAptCon.getMetaData();
        int userID = getUserID(addAptCon, metaData.getUserName());
        PreparedStatement statement = addAptCon.prepareStatement(query);
        statement.setString(1, String.valueOf(addApt.getCustomerId()));
        statement.setInt(2, userID);
        statement.setString(3, addApt.getTitle());
        statement.setString(4, addApt.getDescription());
        statement.setString(5, addApt.getLocation());
        statement.setString(6, addApt.getContact());
        statement.setString(7, addApt.getType());
        statement.setString(8, addApt.getUrl());
        statement.setString(9, String.valueOf(TimeConverter.toUTC(addApt.getStart())));
        statement.setString(10, String.valueOf(TimeConverter.toUTC(addApt.getEnd())));
        statement.setString(11, String.valueOf(TimeConverter.toUTC(new Timestamp(System.currentTimeMillis()))));
        statement.setString(12, metaData.getUserName());
        statement.setString(13, String.valueOf(TimeConverter.toUTC(new Timestamp(System.currentTimeMillis()))));
        statement.setString(14, metaData.getUserName());
        count = statement.executeUpdate();
        addAptCon.close();
        return count > 0;

    }

    private static int getUserID(Connection getUIDCon, String userName) throws SQLException {
        int result = 0;
        String user = userName.substring(0, userName.indexOf("@"));
        Statement statement = getUIDCon.createStatement();
        ResultSet resultSet = statement.executeQuery("Select userId from user where userName = \"" + user + "\"");
        while (resultSet.next()) {
            result = resultSet.getInt("userId");
        }
        return result;
    }

    public static boolean updateAppointment(Appointment addApt) throws SQLException {
        String query = "UPDATE appointment SET "
                + "title = ?, "
                + "description = ?, "
                + "location = ?, "
                + "contact = ?, "
                + "type = ?, "
                + "url = ?, "
                + "start = ?, "
                + "end = ?, "
                + "lastUpdate = ?, "
                + "lastUpdateBy = ? "
                + "WHERE appointmentId = " + addApt.getAppointmentId();

        Connection updateAptCon = DBConnect.getConnection();
        DatabaseMetaData metaData = updateAptCon.getMetaData();
        try {
            PreparedStatement statement = updateAptCon.prepareStatement(query);
            statement.setString(1, addApt.getTitle());
            statement.setString(2, addApt.getDescription());
            statement.setString(3, addApt.getLocation());
            statement.setString(4, addApt.getContact());
            statement.setString(5, addApt.getType());
            statement.setString(6, addApt.getUrl());
            statement.setString(7, String.valueOf(TimeConverter.toUTC(addApt.getStart())));
            statement.setString(8, String.valueOf(TimeConverter.toUTC(addApt.getEnd())));
            statement.setString(9, String.valueOf(TimeConverter.toUTC(new Timestamp(System.currentTimeMillis()))));
            statement.setString(10, metaData.getUserName());
            int result = statement.executeUpdate();
            updateAptCon.close();
            return result > 0;

        } catch (SQLException | ParseException se) {
            se.printStackTrace();
            return false;
        }
    }
}

