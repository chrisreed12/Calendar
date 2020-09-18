package Controller;

import Resources.Appointment;
import Resources.Customer;
import Utils.DBAppointments;
import Utils.DBCustomers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class AddApptController implements Initializable {

    @FXML
    Text titleError, descError, locError, custError, typeError, aptDateError, timeError, timeError2;
    @FXML
    Label label;
    @FXML
    TextField title, loc, url;
    @FXML
    TextArea desc;
    @FXML
    ComboBox<String> typeSelect, custSelect, startHR, startMin, endHR, endMin;
    @FXML
    DatePicker aptDay;
    @FXML
    Button saveBtn, cnclBtn;
    ObservableList<Customer> customers = FXCollections.observableArrayList();
    ObservableList<String> custDisplay = FXCollections.observableArrayList();
    ObservableList<String> hours = FXCollections.observableArrayList(
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"
    );
    ObservableList<String> minutes = FXCollections.observableArrayList(
            "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"
    );
    ObservableList<String> type = FXCollections.observableArrayList(
            "Initial",
            "Follow-up",
            "Presentation",
            "Scrum",
            "Wrap-up"
    );
    boolean recordUpdated = false;
    boolean isUpdate = false;
    Appointment addApt = new Appointment();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            customers = DBCustomers.getCustomers();
            for (Customer customer : customers) {
                custDisplay.add(customer.getCustomerId() + " - " + customer.getCustomerName());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        startHR.getItems().addAll(hours);
        endHR.getItems().addAll(hours);
        startMin.getItems().addAll(minutes);
        endMin.getItems().addAll(minutes);
        custSelect.getItems().addAll(custDisplay);
        typeSelect.getItems().addAll(type);
        /* A lambda expression is used here to allow the Java ActionEvent event handler make a call to the checkForm
        *  method once the saveBtn has been pressed */
        saveBtn.setOnAction(event -> {
            try {
                checkForm();
            } catch (SQLException | ParseException | IOException throwables) {
                throwables.printStackTrace();
            }
        });

        cnclBtn.setOnAction(event -> {
            closeWindow(cnclBtn);
        });

    }

    private void checkForm() throws SQLException, ParseException, IOException {
        clearErrors();

        Boolean errorFree = true;
        if (checkString(title.getText()) && title.getSelectedText().length() < 256) {
            addApt.setTitle(title.getText());
        } else {
            if (title.getText().length() > 255) {
                titleError.setText("Title more than 255");
            } else {
                titleError.setText("Title blank");
                errorFree = false;
            }
        }

        if (checkString(desc.getText())) {
            addApt.setDescription(desc.getText());
        } else {
            descError.setText("Description blank");
            errorFree = false;
        }

        if (checkString(loc.getText())) {
            addApt.setLocation(loc.getText());
        } else {
            locError.setText("Location blank");
            errorFree = false;
        }

        if (checkString(custSelect.getSelectionModel().toString())) {
            addApt.setCustomerId(parseCustID(custSelect.getSelectionModel().getSelectedItem()));
            addApt.setContact(parseCustName(custSelect.getSelectionModel().getSelectedItem()));
        } else {
            custError.setText("Customer blank");
            errorFree = false;
        }

        if (checkString(typeSelect.getSelectionModel().toString())) {
            addApt.setType(typeSelect.getValue());
        } else {
            typeError.setText("Type blank");
            errorFree = false;
        }

        if (checkString(url.getText())) {
            addApt.setUrl(url.getText());
        } else {
            addApt.setUrl("");
        }

        if (aptDay.getValue() != null) {
            Timestamp start = null;
            Timestamp stop = null;
            if (startHR.getSelectionModel().getSelectedItem() != null && startMin.getSelectionModel().getSelectedItem() != null) {
                start = geTimestamp(aptDay.getValue(), startHR.getValue(), startMin.getValue());
                if (start.toLocalDateTime().getDayOfWeek().toString().contains("SATURDAY") || start.toLocalDateTime().getDayOfWeek().toString().contains("SUNDAY")) {
                    errorFree = false;
                    timeError.setText("Outside of business hours");
                }
                if(start.before(new Timestamp(System.currentTimeMillis()))){
                    errorFree = false;
                    timeError.setText("Start time/day in the past");
                }
                if (endHR.getSelectionModel().getSelectedItem() != null && endMin.getSelectionModel().getSelectedItem() != null) {
                    stop = geTimestamp(aptDay.getValue(), endHR.getValue(), endMin.getValue());
                    if(stop.before(new Timestamp(start.getTime()+5))){
                        errorFree = false;
                        timeError.setText("End time cannot be before Start");
                    }
                } else {
                    timeError2.setText("End blank");
                    errorFree = false;
                }
            } else {
                timeError.setText("Start blank");
                errorFree = false;
            }
            try {
                if (DBAppointments.scheduled(addApt.getAppointmentId(), start, stop)) {
                    addApt.setStart(start);
                    addApt.setEnd(stop);
                } else {
                    timeError.setText("Appointment exists, pick a");
                    timeError2.setText("different time.");
                    errorFree = false;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (Integer.valueOf(startHR.getValue()) < 9) {
                timeError.setText("Outside of business hours");
                errorFree = false;
            }
            if (Integer.valueOf(endHR.getValue()) > 16 && Integer.valueOf(endMin.getValue()) > 0) {
                timeError.setText("Outside of business hours");
                errorFree = false;
            }
        } else {
            aptDateError.setText("Select a day");
            errorFree = false;
        }

        if (errorFree) {
            if (isUpdate) {
                updateAppointments();
                recordUpdated = DBAppointments.updateAppointment(addApt);
            } else {
                recordUpdated = DBAppointments.addAppointment(addApt);
            }
            isUpdated(recordUpdated);
        } else {
            alert();
        }
    }

    private void updateAppointments() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CalView.fxml"));
        loader.load();
        AppointmentController appointmentController = loader.getController();
        appointmentController.updateTable();
    }

    private void isUpdated(boolean recordUpdated) {
        if (recordUpdated) {
            closeWindow(saveBtn);

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Write to DB failed contact your system administrator.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    private void closeWindow(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    private void alert() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Please correct errors and try again.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    private void clearErrors() {
        titleError.setText("");
        descError.setText("");
        locError.setText("");
        custError.setText("");
        typeError.setText("");
        aptDateError.setText("");
        timeError.setText("");
        timeError2.setText("");
    }

    private Timestamp geTimestamp(LocalDate date, String hr, String min) {
        String string = date.toString() + " " + hr + ":" + min + ":00";
        return date == null ? null : Timestamp.valueOf(string);
    }

    private int parseCustID(String customer) {
        return Integer.parseInt(customer.substring(0, customer.indexOf("-")).trim());
    }

    private String parseCustName(String customer) {
        return customer.substring(customer.indexOf("-") + 1).trim();
    }

    public boolean checkString(String string) {
        return !(string == null) && !string.trim().isEmpty();
    }

    public void initData(Appointment selectedItem) {
        addApt = selectedItem;
        label.setText("Edit Appointment");
        isUpdate = true;
        title.setText(selectedItem.getTitle());
        desc.setText(selectedItem.getDescription());
        loc.setText(selectedItem.getLocation());
        custSelect.setValue(selectedItem.getCustomerId() + " - " + selectedItem.getCustomerName());
        typeSelect.setValue(selectedItem.getType());
        url.setText(selectedItem.getUrl());
        aptDay.setValue(LocalDate.from(selectedItem.getStart().toLocalDateTime()));
        startHR.setValue(parseHR(selectedItem.getStart()));
        startMin.setValue(parseMin(selectedItem.getStart()));
        endHR.setValue(parseHR(selectedItem.getEnd()));
        endMin.setValue(parseMin(selectedItem.getEnd()));
    }

    private String parseHR(Timestamp time) {
        Date date = new Date(time.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("kk");
        String formatTime = sdf.format(date);
        return formatTime;
    }

    private String parseMin(Timestamp time) {
        Date date = new Date(time.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("mm");
        String formatTime = sdf.format(date);
        return formatTime;
    }
}
