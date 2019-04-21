
package library.manager.ui.memberlist;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.manager.database.DatabaseHandler;
import library.manager.ui.addmember.AddMemberController;
  
public class MemberListController implements Initializable {
    ObservableList<Member> list = FXCollections.observableArrayList();
    DatabaseHandler handler;
    @FXML
    private TableView<Member> tableView;
    @FXML
    private TableColumn<Member, String> idCol;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> regNoCol;
    @FXML
    private TableColumn<Member, String> intakeCol;
    @FXML
    private TableColumn<Member, String> emailCol;
    @FXML
    private TableColumn<Member, String> mobileCol;
    

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }
      private void initCol() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        regNoCol.setCellValueFactory(new PropertyValueFactory<>("regNo"));
        intakeCol.setCellValueFactory(new PropertyValueFactory<>("intake"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
    }
      
      private void loadData() {
        list.clear();
        handler = DatabaseHandler.getInstance();
        try {
            String qu = "SELECT * FROM MEMBER";
            ResultSet rs = handler.execQuery(qu);
            while(rs.next()){
                String id = rs.getString("id");
                String name = rs.getString("name");
                String regNo = rs.getString("regNo");
                String intake = rs.getString("intake");
                String email = rs.getString("email");
                String mobile = rs.getString("mobile");
                Boolean mStatus = rs.getBoolean("isMemberAvail");
                
                if(mStatus){
                    list.add(new Member(id,name,regNo,intake,email,mobile));
                }
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.setItems(list);
    }

    @FXML
    private void handleRefreshMemberList(ActionEvent event) {
        loadData();
    }

    @FXML
    private void handleEditMemberOp(ActionEvent event) {
        //Fetch selected row
        Member selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Select Member!");
            alert.setContentText("No Member selected");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/manager/ui/addmember/addMember.fxml"));
            Parent parent = loader.load();
            
            AddMemberController controller = (AddMemberController) loader.getController();
            controller.inflateUI(selectedForEdit);
            
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            stage.show();
            
            stage.setOnCloseRequest((e)->{
                handleRefreshMemberList(new ActionEvent());
            });
            
        } catch (IOException ex) {
            Logger.getLogger(MemberListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void loadWindow(String loc, String title){
        
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MemberListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleDeleteMemberOp(ActionEvent event) {
        //Fetch selected row
        Member selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if (selectedForDeletion == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Select Member!");
            alert.setContentText("No Member selected");
            return;
        }
        
        Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
        alert1.setTitle("Deleting Member");
        alert1.setHeaderText(null);
        alert1.setContentText("Are you sure want to delete " + selectedForDeletion.getName() + "?");
        Optional<ButtonType> answer = alert1.showAndWait();
        if(answer.get() == ButtonType.OK){
            DatabaseHandler dataHandler =  DatabaseHandler.getInstance();//.deleteBook(selectedForDeletion);
            String stmt = "UPDATE MEMBER SET isMemberAvail = false WHERE id = '" + selectedForDeletion.getId() + "'";
            System.out.println(stmt);
            if(dataHandler.execAction(stmt)){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Member deleted");
                alert.setContentText(selectedForDeletion.getName() + " was deleted successfully");
                alert.showAndWait();
                list.remove(selectedForDeletion);
                
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Deletion Failed");
                alert.setContentText(selectedForDeletion.getName() + " couldn't be deleted successfully");
                alert.showAndWait();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Deletion cancelled");
            alert.setContentText("Member deletion cancelled");
            alert.showAndWait();
                
        }
    }

     public static class Member{
        private final SimpleStringProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty regNo;
        private final SimpleStringProperty intake;
        private final SimpleStringProperty email;
        private final SimpleStringProperty mobile;

        public Member(String id, String name, String regNo, String intake, String email, String mobile) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.regNo = new SimpleStringProperty(regNo);
            this.intake = new SimpleStringProperty(intake);
            this.email = new SimpleStringProperty(email);
            this.mobile = new SimpleStringProperty(mobile);
        }

        public String getId() {
            return id.get();
        }

        public String getName() {
            return name.get();
        }

        public String getRegNo() {
            return regNo.get();
        }

        public String getIntake() {
            return intake.get();
        }

        public String getEmail() {
            return email.get();
        }

        public String getMobile() {
            return mobile.get();
        }       
    }
}