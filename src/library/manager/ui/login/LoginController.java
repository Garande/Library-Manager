
package library.manager.ui.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.manager.database.DatabaseHandler;
import library.manager.ui.main.MainController;
import org.apache.commons.codec.digest.DigestUtils;

public class LoginController implements Initializable {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField pass;
    @FXML
    private JFXButton loginButton;
    @FXML
    private JFXButton cancelLoginButton;
    
    DatabaseHandler handler;

   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
        
    }    

    @FXML
    private void handleLoginButton(ActionEvent event) {
        String uname = username.getText();
        String pswd = DigestUtils.shaHex(pass.getText());
        
        
        if(uname.isEmpty()||pswd.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter Data in all Fields Corectly");
            alert.showAndWait();
            return;
        }
        
        String qu = "SELECT * FROM LOGIN WHERE userName like '%" + uname + "%'";
        System.out.println(qu);
        ResultSet rs = handler.execQuery(qu);
        try {
            while(rs.next()){
                String user = rs.getString("userName");
                String pasword = rs.getString("pasword");
                
                if(uname.equals(user) && pswd.equals(pasword)){
                    closeStage();
                    loadMain();
                    
                }else{
                    //invalid credetials
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter valid credetials"); 
                    alert.showAndWait();
                    
                    username.setStyle("-fx-text-fill:#f44336");
                    pass.setStyle("-fx-text-fill:#f44336");
                }
            }
        } catch (SQLException ex) {  
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    

    @FXML
    private void handleCancelLoginButton(ActionEvent event) {
        System.exit(0);
    }
    
    private void closeStage() {
        ((Stage)username.getScene().getWindow()).close();
    }
    
    void loadMain(){
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/library/manager/ui/main/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Datamine Library Manager");
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
