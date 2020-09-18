package Controller;

import Resources.Appointment;
import Resources.Customer;
import Utils.DBAppointments;
import Utils.DBCustomers;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {
    @FXML
    Button addAptBtn, dltAptBtn, cusViewBtn, viewAptBtn;
    @FXML
    TableView<Appointment> aptTable;
    @FXML
    TableColumn<Appointment, String> titleCol, descCol, typeCol, custCol, dayCol, startCol, endCol;
    @FXML
    TableColumn<Appointment, Button> viewCol;
    @FXML
    ComboBox<String> calSelect;
    ObservableList<String> aptSelect = FXCollections.observableArrayList(
            "Week",
            "Month"
    );
    private final LocalDate date = LocalDate.now();
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private AddApptController addApptController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        custCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        dayCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        calSelect.getItems().addAll(aptSelect);
        calSelect.getSelectionModel().selectFirst();
        try {
            appointments = DBAppointments.getWeekApt(date);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        aptTable.setItems(appointments);
        formatTable();
        addButtonToTable();

        aptRemind(appointments);

        calSelect.setOnAction(event -> {
            updateCal();
        });

        dltAptBtn.setOnAction(event -> {
            try {
                Appointment dltApt = aptTable.getSelectionModel().getSelectedItem();
                aptTable.getItems().remove(dltApt);
                DBAppointments.deleteApt(dltApt);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });

        addAptBtn.setOnAction(event -> {
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader();
                root = FXMLLoader.load(getClass().getResource("/View/AddApt.fxml"));
                addApptController = loader.getController();
                Stage stage = new Stage();
                stage.setTitle("Smart Calendar - Add Appointment");
                stage.setScene(new Scene(root, 600, 400));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        viewAptBtn.setOnAction(event -> {
            if (aptTable.getSelectionModel().getSelectedItem() != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AddApt.fxml"));
                try {
                    Parent root = loader.load();
                    AddApptController apptController = loader.getController();
                    apptController.initData(aptTable.getSelectionModel().getSelectedItem());
                    Stage stage = new Stage();
                    stage.setTitle("Smart Calendar - Edit Appointment");
                    stage.setScene(new Scene(root, 600, 400));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void aptRemind(ObservableList<Appointment> appointments) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        for (Appointment appointment : appointments) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long minutes = 900000;
            if (appointment.getStart().getDay() == timestamp.getDay()
                    && timestamp.after(new Timestamp(appointment.getStart().getTime()-minutes))

                    && timestamp.before(appointment.getEnd())
                ){
                String start = sdf.format(appointment.getStart());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have the appointment "+ appointment.getTitle() + " with " + appointment.getCustomerName() + " at " + start);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }
        }
    }

    public void updateCal() {
        aptTable.getItems().clear();
        if (calSelect.getValue().equals("Month")) {
            try {
                appointments = DBAppointments.getMonthAppointment(date);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                appointments = DBAppointments.getWeekApt(date);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        aptTable.setItems(appointments);
        formatTable();
        addButtonToTable();
    }

    private void formatTable() {

        /* A lambda expression is used here to assign the CellValueFactory of the table column to the associated value
        *  in the Appointment class object.  In this below instances we are using the Appointment class get method to
        *  retrieve the date and convert them into a more visibly friendly format.*/
        dayCol.setCellValueFactory(
                Appointment -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd");
                    property.setValue(dateFormat.format(Appointment.getValue().getStart()));
                    return property;
                });
        startCol.setCellValueFactory(
                Appointment -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    property.setValue(dateFormat.format(Appointment.getValue().getStart()));
                    return property;
                });
        endCol.setCellValueFactory(
                Appointment -> {
                    SimpleStringProperty property = new SimpleStringProperty();
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    property.setValue(dateFormat.format(Appointment.getValue().getEnd()));
                    return property;
                });
    }

    private void addButtonToTable() {

        Callback<TableColumn<Appointment, Button>, TableCell<Appointment, Button>> cellFactory = new Callback<TableColumn<Appointment, Button>, TableCell<Appointment, Button>>() {
            @Override
            public TableCell<Appointment, Button> call(final TableColumn<Appointment, Button> param) {
                final TableCell<Appointment, Button> cell = new TableCell<Appointment, Button>() {

                    private final Button btn = new Button("View");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Appointment appointment = getTableView().getItems().get(getIndex());
                            Customer customer = null;
                            try {
                                customer = DBCustomers.getCustomer(appointment.getCustomerId());
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AddCust.fxml"));
                            try {
                                Parent root = loader.load();
                                AddCustController addCustController = loader.getController();
                                addCustController.viewCustomer(customer);
                                Stage stage = new Stage();
                                stage.setTitle("Smart Calendar - Edit Customer");
                                stage.setScene(new Scene(root, 600, 450));
                                stage.show();
                            } catch (IOException e) {
                            }
                        });
                    }

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        viewCol.setCellFactory(cellFactory);
    }

    public void updateTable(){
        aptTable.refresh();
    }
}

