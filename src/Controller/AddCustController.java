package Controller;

import Resources.Customer;
import Utils.DBCountries;
import Utils.DBCustomers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;

public class AddCustController implements Initializable {

    @FXML
    Label label;
    @FXML
    TextField name, add1, add2, city, postal, phone;
    @FXML
    ComboBox<String> country;
    @FXML
    Button saveBtn, cnclBtn;
    @FXML
    Text customerError, add1Error, add2Error, cityError, countryError, postalError, phoneError;
    ObservableList<String> countries = FXCollections.observableArrayList();
    Boolean isUpdate = false;
    Boolean recordUpdated = false;
    Customer addCust = new Customer();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
             countries = DBCountries.getCountries();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        country.getItems().addAll(countries);

        saveBtn.setOnAction(event -> {
            try {
                checkForm();
            } catch (SQLException | ParseException | IOException throwables) {
                throwables.printStackTrace();
            }
        });

        cnclBtn.setOnAction(event -> closeWindow(cnclBtn));
    }

    private void checkForm() throws SQLException, ParseException, IOException {
        clearErrors();
        boolean errorFree = true;

        if(check(name.getText())){
            addCust.setCustomerName(name.getText());
        }else {
            customerError.setText("Name cannot be blank");
            errorFree = false;
        }

        if(check(add1.getText())){
            addCust.setAddress(add1.getText());
        }else {
            add1Error.setText("Address cannot be blank");
            errorFree = false;
        }

        if(check(add2.getText())){
            addCust.setAddress2(add2.getText());
        }else {
            addCust.setAddress2("");
        }

        if(check(city.getText())){
            addCust.setCity(city.getText());
        }else {
            cityError.setText("City cannot be blank");
            errorFree = false;
        }
        if(!country.getSelectionModel().getSelectedItem().trim().isEmpty()){
            if(countries.contains(country.getSelectionModel().getSelectedItem())) {
                addCust.setCountryId(DBCountries.getCountryID(country.getSelectionModel().getSelectedItem()));
            }else{
                addCust.setCountryId(DBCountries.addCountry(country.getSelectionModel().getSelectedItem()));
            }
            addCust.setCountry(country.getSelectionModel().getSelectedItem());
        }else {
            countryError.setText("Country cannot be blank");
            errorFree = false;
        }

        if(check(postal.getText())){
            addCust.setPostalCode(postal.getText());
        }else {
            postalError.setText("Postal Code cannot be blank");
            errorFree = false;
        }

        if(check(phone.getText())){
            addCust.setPhone(phone.getText());
        }else {
            phoneError.setText("Postal Code cannot be black");
            errorFree = false;
        }
        if(errorFree){
            if(isUpdate){
                recordUpdated = DBCustomers.updateCustomer(addCust);
            }else {
                recordUpdated = DBCustomers.addCustomer(addCust);
            }
            if(recordUpdated){
                execUpdate();
                closeWindow(saveBtn);
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Write to DB failed contact your system administrator.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }
        }
    }

    private void execUpdate() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CusView.fxml"));
        loader.load();
        CustomerController customerController = loader.getController();
        customerController.updateTable();
    }

    private boolean check(String check) {
        if(!check.trim().isEmpty()) {
            return true;
        }else {
            return false;
        }
    }

    private void clearErrors() {
        customerError.setText("");
        add1Error.setText("");
        add2Error.setText("");
        cityError.setText("");
        countryError.setText("");
        postalError.setText("");
        phoneError.setText("");
    }

    public void editCustomer(Customer customer) {
        label.setText("Edit Customer");
        isUpdate = true;
        name.setText(customer.getCustomerName());
        add1.setText(customer.getAddress());
        add2.setText(customer.getAddress2());
        city.setText(customer.getCity());
        country.setValue(customer.getCountry());
        postal.setText(customer.getPostalCode());
        phone.setText(customer.getPhone());
        addCust = customer;
    }

    public void viewCustomer(Customer customer) {
        label.setText("View Customer");
        isUpdate = true;
        name.setText(customer.getCustomerName());
        name.setEditable(false);
        add1.setText(customer.getAddress());
        add1.setEditable(false);
        add2.setText(customer.getAddress2());
        add2.setEditable(false);
        city.setText(customer.getCity());
        city.setEditable(false);
        country.setValue(customer.getCountry());
        postal.setText(customer.getPostalCode());
        postal.setEditable(false);
        phone.setText(customer.getPhone());
        phone.setEditable(false);
        saveBtn.setVisible(false);
        cnclBtn.setText("Close");
    }

    private void closeWindow(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
