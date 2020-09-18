package Controller;

import Utils.DBConnect;
import Utils.LoginTracker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Text langTxt, userTxt, passTxt, localeTxt, langDisplay, localeDisplay, statusTxt;
    @FXML
    private Button logBtn;
    @FXML
    private TextField userVal;
    @FXML
    private PasswordField passVal;
    private Connection connection;
    private final ResourceBundle rb = ResourceBundle.getBundle("login", Locale.getDefault());
    private final ObservableList<String> lang = FXCollections.observableArrayList("English", "Español");
    private Parent root;
    private Stage stage = new Stage();
    private final String un = "test";
    private final String pw = "test";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        langDisplay.setText(getLocale().getLanguage());
        localeDisplay.setText(getLocale().getCountry());
        updateLang(getLocale());
        logBtn.setOnAction(event -> {
            try {
                login(event);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void updateLang(Locale local) {
        langDisplay.setText(local.getLanguage());
        localeDisplay.setText(local.getCountry());
        langTxt.setText(getRB().getString("langTxt"));
        localeTxt.setText(getRB().getString("localeTxt"));
        userTxt.setText(getRB().getString("userTxt"));
        passTxt.setText(getRB().getString("passTxt"));
        logBtn.setText(getRB().getString("logBtn"));
    }

    public void login(ActionEvent event) throws SQLException {
        String userValText = userVal.getText();
        String passValText = passVal.getText();
        String logSuccess = "";
        try {
            connection = DBConnect.getConnection();
            if (un.equals(userValText.trim()) && pw.equals(passValText.trim())) {
                logSuccess = " SUCCESS ";
                LoginTracker.log(userValText,logSuccess);
                stage = (Stage) logBtn.getScene().getWindow();
                root = FXMLLoader.load(getClass().getResource("/View/CalView.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Smart Calendar");
                stage.show();
            } else {
                logSuccess = " FAILED ";
                LoginTracker.log(userValText,logSuccess);
                statusTxt.setFill(Color.RED);
                statusTxt.setText(getRB().getString("connFail"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Locale getLocale() {
//        Locale locale = new Locale("es", "MX");   *Used to test locale change to Spanish*
        Locale locale = Locale.getDefault();
        return locale;
    }

    private ResourceBundle getRB() {
        ResourceBundle rb = ResourceBundle.getBundle("login", getLocale());
        return rb;
    }


}