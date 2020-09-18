package Controller;

import Resources.Report;
import Utils.DBReport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    @FXML
    TableView reportTable;
    @FXML
    TableColumn<Report, String> firstCol, secondCol, thirdCol, fourthCol;
    @FXML
    ComboBox selectReport;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> reportSelection = FXCollections.observableArrayList("Appointments by Month", "Consultant Schedule", "Appointments by Location");
        firstCol.setCellValueFactory(new PropertyValueFactory<>("firstCol"));
        secondCol.setCellValueFactory(new PropertyValueFactory<>("secondCol"));
        thirdCol.setCellValueFactory(new PropertyValueFactory<>("thirdCol"));
        fourthCol.setCellValueFactory(new PropertyValueFactory<>("fourthCol"));
        selectReport.setItems(reportSelection);
        selectReport.setOnAction(event -> {
            try {
                reportTable.setItems(loadReport());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        }

    private ObservableList<Report> loadReport() throws SQLException {
        if(selectReport.getValue().equals("Appointments by Month")) {
            ObservableList<Report> aptByMonth = DBReport.getAptByMon();
            firstCol.setText("Month");
            secondCol.setText("Type");
            thirdCol.setText("Total");
            fourthCol.setText("");
            return aptByMonth;
        }else if(selectReport.getValue().equals("Consultant Schedule")) {
            ObservableList<Report> cnsltSchedule = DBReport.getCnsltSched();
            firstCol.setText("Consultant");
            secondCol.setText("Start");
            thirdCol.setText("End");
            fourthCol.setText("Customer");
            return cnsltSchedule;
        }else {
            ObservableList<Report> aptByLoc = DBReport.getAptByLoc();
            firstCol.setText("Consultant");
            secondCol.setText("Location");
            thirdCol.setText("Total");
            fourthCol.setText("");
            return aptByLoc;
        }
    }
}
