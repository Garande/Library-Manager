
package library.manager.ui.addmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.manager.database.DatabaseHandler;
import library.manager.ui.memberlist.MemberListController;

public class AddMemberController implements Initializable {
    DatabaseHandler handler;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField regNo;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField intake;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXTextField mobile;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    
    private Boolean isInEditMode = Boolean.FALSE;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
    //    checkData();
    }    

    @FXML
    private void addMember(ActionEvent event) {
        
        String mID = id.getText();
        String mRegNo = regNo.getText();
        String mName = name.getText();
        String mIntake = intake.getText();
        String mEmail = email.getText();
        String mMobile = mobile.getText();
        
        if(mID.isEmpty()||mRegNo.isEmpty()||mName.isEmpty()||mIntake.isEmpty()||mEmail.isEmpty()||mMobile.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter Data in all Fields Corectly");
            alert.showAndWait();
            return;
        }
        
        if(isInEditMode){
            handleEditMemberOp();
            return;
        }
      
        String st = "INSERT INTO MEMBER VALUES ("+
                "'"+ mID +"',"+
                "'"+ mRegNo +"',"+
                "'"+ mName +"',"+
                "'"+ mIntake +"',"+
                "'"+ mEmail +"',"+
                "'"+ mMobile +"',"+
                ""+ "true" +""+
                ")";
        System.out.println(st);
        if(handler.execAction(st)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Member added Successfully");
            alert.showAndWait();
            clearMemberCache();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Member creation failed");
            alert.showAndWait();
        }
    }
    /*String mID = id.getText();
    String mRegNo = regNo.getText();
    String mName = name.getText();
    String mIntake = intake.getText();
    String mEmail = email.getText();
    String mMobile = mobile.getText();*/
    void clearMemberCache(){
        id.setText("");
        regNo.setText("");
        name.setText("");
        intake.setText("");
        email.setText("");
        mobile.setText("");
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
    }
    
    public void inflateUI(MemberListController.Member member){
        /*id.setText("");
        regNo.setText("");
        name.setText("");
        intake.setText("");
        email.setText("");
        mobile.setText("");*/
        id.setText(member.getId());
        regNo.setText(member.getRegNo());
        name.setText(member.getName());
        intake.setText(member.getIntake());
        email.setText(member.getEmail());
        mobile.setText(member.getMobile());
        id.setEditable(false);
        isInEditMode = Boolean.TRUE;
    }

    private void handleEditMemberOp() {
        MemberListController.Member member = new MemberListController.Member(id.getText(), name.getText(), regNo.getText(), intake.getText(), email.getText(), mobile.getText());
        if(handler.updateMember(member)){
            Alert alert = new Alert (Alert.AlertType.INFORMATION);
            alert.setHeaderText("Success");
            alert.setTitle("Member Update");
            alert.setContentText("Member Updated successfully");
            alert.showAndWait();
        }else{
            Alert alert = new Alert (Alert.AlertType.ERROR);
            alert.setHeaderText("Failed");
            alert.setTitle("Member Update");
            alert.setContentText("Member Update failed");
            alert.showAndWait();
        }
        
    }
    
/*
    private void checkData() {
        try {
            String qu = "SELECT id FROM MEMBER";
            ResultSet rs = handler.execQuery(qu);
            while(rs.next()){
                String mIdNo = rs.getString("id");
                System.out.println(mIdNo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
*/
    
}


