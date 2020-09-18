package Controller;

import Resources.Customer;
import Utils.DBCustomers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML
    TableView<Customer> customerTable;
    @FXML
    TableColumn<Customer, String> customerCol, phoneCol, addressCol, address2Col, cityCol, countryCol, postalCol;
    @FXML
    Button newBtn, editBtn, dltBtn;
    ObservableList<Customer> customers = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* A lambda expression is used here to set the cellData of the cellValueFactory for each column in the table to a
        *  StringProperty value that is retrieved from the Customer object associated with the row using a getter method
        *  from the Customer class.  */
        customerCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerNameProp());
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().getPhoneProp());
        addressCol.setCellValueFactory(cellData -> cellData.getValue().getAddressProp());
        address2Col.setCellValueFactory(cellData -> cellData.getValue().getAddress2Prop());
        cityCol.setCellValueFactory(cellData -> cellData.getValue().getCityProp());
        countryCol.setCellValueFactory(cellData -> cellData.getValue().getCountryProp());
        postalCol.setCellValueFactory(cellData -> cellData.getValue().getPostalCodeProp());
        try {
            customers = DBCustomers.getCustomers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        customerTable.setItems(customers);

        dltBtn.setOnAction(event -> {
            Customer customer = customerTable.getSelectionModel().getSelectedItem();
            customerTable.getItems().remove(customer);
            try {
                DBCustomers.delete(customer);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        newBtn.setOnAction(event -> {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("/View/AddCust.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Smart Calendar - Add Customer");
                stage.setScene(new Scene(root, 600, 450));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        editBtn.setOnAction(event -> {
            if (customerTable.getSelectionModel().getSelectedItem() != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AddCust.fxml"));
                try {
                    Parent root = loader.load();
                    AddCustController addCustController = loader.getController();
                    addCustController.editCustomer(customerTable.getSelectionModel().getSelectedItem());
                    Stage stage = new Stage();
                    stage.setTitle("Smart Calendar - Edit Customer");
                    stage.setScene(new Scene(root, 600, 450));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public void updateTable(){
        customerTable.refresh();
    }
}
