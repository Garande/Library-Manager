
package library.manager.ui.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import library.manager.database.DatabaseHandler;
import org.apache.commons.codec.digest.DigestUtils;


public class SettingsController implements Initializable {
    DatabaseHandler handler = DatabaseHandler.getInstance();

    @FXML
    private JFXTextField nDays;
    @FXML
    private JFXTextField finePerDay;
    @FXML
    private JFXTextField nBooks;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private Label AccountsettingsLabel;
    @FXML
    private JFXPasswordField current_password;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField new_pass;
    @FXML
    private JFXPasswordField confirm_new_pass;
    @FXML
    private JFXButton saveButton1;
    @FXML
    private JFXButton cancelButton1;
    @FXML
    private StackPane rootPane;

   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDefaultValues();
    }    

    @FXML
    private void handleSaveSettingsButton(ActionEvent event) {
        Preferences preferences = Preferences.getPreferences();
        int days = Integer.parseInt(nDays.getText());
        float fine = Float.parseFloat(finePerDay.getText());
        int nbook = Integer.parseInt(nBooks.getText());
        
        preferences.setnDays(days);
        preferences.setFinePerDay(fine);
        preferences.setnBooks(nbook);
        
        Preferences.writePreferenceToFile(preferences);
        
    }
    
    private void initDefaultValues(){
        Preferences preferences = Preferences.getPreferences();
        nDays.setText(String.valueOf(preferences.getnDays()) );
        finePerDay.setText(String.valueOf(preferences.getFinePerDay()));
        nBooks.setText(String.valueOf(preferences.getnBooks()));
    }

    @FXML
    private void handleCancelSettingsButton(ActionEvent event) {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
        
    }

    @FXML
    private void handleSaveAccountSettingsButton(ActionEvent event) {
        String currentPassword = current_password.getText();
        String userName = username.getText();
        String newPassword = new_pass.getText();
        String confirmPassword = confirm_new_pass.getText();
        
        
        //Check to verify if all fields have been filled with data
        if(userName.isEmpty()||currentPassword.isEmpty()||newPassword.isEmpty()||confirmPassword.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("ERROR");
            alert.setContentText("Please populate all fields with data");
            alert.showAndWait();
            return;
        }
        
        
        //Quering the database to confirm the current password
        String qu = "SELECT * FROM LOGIN WHERE userName = '"+ userName +"'";
        System.out.println(qu);
        ResultSet rs = handler.execQuery(qu);
        try {
            while(rs.next()){
                if(rs.getString("userName").equals(userName) && rs.getString("pasword").equals(DigestUtils.shaHex(currentPassword))){
                    if(newPassword.equals(confirmPassword)){
                        String st = "UPDATE LOGIN SET pasword = '"+ DigestUtils.shaHex(confirmPassword)+ "' WHERE userName = '"+ userName +"'";
                        System.out.println(st);
                        if(handler.execAction(st)){//on Success
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText(null);
                            alert.setTitle("Success");
                            alert.setContentText("Password changed successfully");
                            alert.showAndWait();
                            clearAccountsCache();
                        }else{//On Failure
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText(null);
                            alert.setTitle("Failed");
                            alert.setContentText("Failed to save the new password");
                            alert.showAndWait();
                        }
                    }else{
                        //Error when the new Passwords don't match
                        AccountsettingsLabel.setStyle("-fx-background-color:#ef5350; -fx-font-size:16; -fx-text-fill:white");
                        AccountsettingsLabel.setText("The new passwords don't match");
                    }
                    
                }else{
                    //If the username and password don't match with the current password in the database
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Invalid Credentials!");
                    alert.setTitle("ERROR");
                    alert.setContentText("The current Password or Username entered are invalid \n Please Enter correct credentials");
                    alert.showAndWait();
                    
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    void clearAccountsCache(){
        AccountsettingsLabel.setText("Change Password");
        AccountsettingsLabel.setStyle("-fx-background-color:white; -fx-font-size:16; -fx-font-color:black");
        current_password.setText("");
        username.setText("");
        new_pass.setText("");
        confirm_new_pass.setText("");
        
    }
    
}
