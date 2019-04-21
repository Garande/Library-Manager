
package library.manager.ui.returnlist;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import library.manager.database.DatabaseHandler;


public class ReturnlistController implements Initializable {
    ObservableList<Return> list = FXCollections.observableArrayList();

    @FXML
    private TableView<Return> tableView;
    @FXML
    private TableColumn<Return, String> idCol;
    @FXML
    private TableColumn<Return, String> titleCol;
    @FXML
    private TableColumn<Return, String> memberIDCol;
    @FXML
    private TableColumn<Return, String> memberNameCol;
    @FXML
    private TableColumn<Return, String> memberContactCol;
    @FXML
    private TableColumn<Return, String> issueDateCol;
    @FXML
    private TableColumn<Return, String> returnDateCol;
    @FXML
    private TableColumn<Return, String> sNoCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }    

    private void initCol() {
        sNoCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        memberIDCol.setCellValueFactory(new PropertyValueFactory<>("memberID"));
        memberNameCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        memberContactCol.setCellValueFactory(new PropertyValueFactory<>("memberMobile"));
        issueDateCol.setCellValueFactory(new PropertyValueFactory<>("issueTime"));
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnTime"));
    }

    private void loadData() {
        DatabaseHandler handler = DatabaseHandler.getInstance();
        try {
            String qu = "SELECT * FROM RETURN";
            ResultSet rs = handler.execQuery(qu);
            while(rs.next()){
                String id = rs.getString("id");
                String bookID = rs.getString("bookID");
                String bookTitle = rs.getString("bookTitle");
                String memberID = rs.getString("memberID");
                String memberName = rs.getString("memberName");
                String memberMobile = rs.getString("memberMobile");
                String issueTime = rs.getString("issueTime");
                Timestamp returnTime = rs.getTimestamp("returnTime");
                
                list.add(new Return(id, bookID, bookTitle, memberID, memberName, memberMobile, issueTime, returnTime.toGMTString()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReturnlistController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.getItems().setAll(list);
    }

    
    public static class Return{
        private final SimpleStringProperty id;
        private final SimpleStringProperty bookID;
        private final SimpleStringProperty bookTitle;
        private final SimpleStringProperty memberID;
        private final SimpleStringProperty memberName;
        private final SimpleStringProperty memberMobile;
        private final SimpleStringProperty issueTime;
        private final SimpleStringProperty returnTime;

        public Return(String id, String bookID, String bookTitle, String memberID, String memberName, String memberMobile, String issueTime, String returnTime) {
            this.id = new SimpleStringProperty(id);
            this.bookID = new SimpleStringProperty(bookID);
            this.bookTitle = new SimpleStringProperty(bookTitle);
            this.memberID = new SimpleStringProperty(memberID);
            this.memberName = new SimpleStringProperty(memberName);
            this.memberMobile = new SimpleStringProperty(memberMobile);
            this.issueTime = new SimpleStringProperty(issueTime);
            this.returnTime = new SimpleStringProperty(returnTime);
        }
        
        public String getId(){
            return id.get();
        }

        public String getBookID() {
            return bookID.get();
        }

        public String getBookTitle() {
            return bookTitle.get();
        }

        public String getMemberID() {
            return memberID.get();
        }

        public String getMemberName() {
            return memberName.get();
        }

        public String getMemberMobile() {
            return memberMobile.get();
        }

        public String getIssueTime() {
            return issueTime.get();
        }

        public String getReturnTime() {
            return returnTime.get();
        }

        
    }
}