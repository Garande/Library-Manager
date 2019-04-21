
package library.manager.ui.main;

import com.gluonhq.charm.glisten.control.AutoCompleteTextField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.manager.database.DatabaseHandler;


public class MainController implements Initializable {
    ArrayList autoFill = new ArrayList();
    DatabaseHandler handler;
    @FXML
    private JFXTextField bookIDInput;
    @FXML
    private Text bookName;
    @FXML
    private Text bookAuthor;
    @FXML
    private Text bookStatus;
    @FXML
    private HBox book_Info;
    @FXML
    private HBox member_Info;
    @FXML
    private JFXTextField memberIDInput;
    @FXML
    private Text memberName;
    @FXML
    private Text contact;
    private JFXTextField bookID;
    @FXML
    private ListView<String> issueDataList;
    @FXML
    private JFXTextField rBookID;
    
    Boolean isReadForReturn = false;
    @FXML
    private Text memberStatus;
    @FXML
    private AutoCompleteTextField<?> bookTitleInput;
    @FXML
    private StackPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
        JFXDepthManager.setDepth(book_Info, 1);
        JFXDepthManager.setDepth(member_Info, 1);
        
    }    

    @FXML
    private void addMemberLoader(ActionEvent event) {
        loadWindow("/library/manager/ui/addmember/addMember.fxml","Add New Member");
    }

    @FXML
    private void addBookLoader(ActionEvent event) {
        loadWindow("/library/manager/ui/addbook/addBook.fxml","Add New Book");
    }

    @FXML
    private void memberTableLoader(ActionEvent event) {
        loadWindow("/library/manager/ui/memberlist/memberList.fxml","Member List");
    }
    
    @FXML
    private void returnTableLoader(ActionEvent event) {
        loadWindow("/library/manager/ui/returnlist/returnlist.fxml","Return Log");
    }

    @FXML
    private void bookTableLoader(ActionEvent event) {
        loadWindow("/library/manager/ui/booklist/bookList.fxml","Book List");
    }
    
    void loadWindow(String loc, String title){
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadBookInfo(ActionEvent event) {
        clearBookCache();
       /* String title = bookIDInput.getText();
        String qu = "SELECT * FROM BOOK WHERE title = '" + title +"'";
        System.out.println(qu);
        ResultSet rs = handler.execQuery(qu);
        try{
            while(rs.next()){
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");
                Boolean notDeleted = rs.getBoolean("isNotDeleted");
                
                
                autoFill.add(bName);
                
                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                //String status = (bStatus)?"Available" : "Not Available";
                if(bStatus && notDeleted){
                    bookStatus.setText("Available");
                }else{
                    bookStatus.setText("Not Available");
                }
            
        }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        String id = bookIDInput.getText();
                
        id=id.toUpperCase();
        String qu = "SELECT * FROM BOOK WHERE id = '" + id +"'";
        System.out.println(qu);
        ResultSet rs = handler.execQuery(qu);
        Boolean flag = false;
        try {
            while(rs.next()){
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");
                Boolean notDeleted = rs.getBoolean("isNotDeleted");
                
                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                //String status = (bStatus)?"Available" : "Not Available";
                if(bStatus && notDeleted){
                    bookStatus.setText("Available");
                }else{
                    bookStatus.setText("Not Available");
                }
                flag = true;
            }
            if(!flag){
                bookName.setText("Book not Found");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void autoComplete(String test){
        String complete = "";
        int start = test.length();
        int last = test.length();
        int i;
        for(i = 0; i<autoFill.size(); i++){
            if(autoFill.get(i).toString().startsWith(test)){
                complete = autoFill.get(i).toString();
                last = complete.length();
                break;
            }
            
        }
        if(last>start){
            bookIDInput.setText(complete);
            bookIDInput.getCaretPosition();
            
            
            //bookIDInput.setCaretPosition(last);
            //bookIDInput.moveCaretPosition(start);
            
        }
    }
    void clearBookCache(){
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
    }
    

    @FXML
    private void loadMemberInfo(ActionEvent event) {
        clearMemberCache();
        String id = memberIDInput.getText();
        String qu = "SELECT * FROM MEMBER WHERE id = '" + id +"'";
        System.out.println(qu);
        ResultSet rs = handler.execQuery(qu);
        Boolean flag = false;
        try {
            while(rs.next()){
                String mName = rs.getString("name");
                String bContact = rs.getString("mobile");
                Boolean mAvail = rs.getBoolean("isMemberAvail");
                
                String status = (mAvail)?"Available" : "Not Available";
                memberStatus.setText(status);
                memberName.setText(mName);
                contact.setText(bContact);
                flag = true;
            }
            if(!flag){
                memberName.setText("Member not Found");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void clearMemberCache(){
        memberName.setText("");
        contact.setText("");
    }

    @FXML
    private void loadBookIssue(ActionEvent event) {
        String bookID = bookIDInput.getText();
        String memberID = memberIDInput.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure, you want to issue " + bookName.getText() + " to \n " + memberName.getText() + "?");
            
        Optional<ButtonType> response = alert.showAndWait();
        if(response.get() == ButtonType.OK){
            String st1 = "INSERT INTO ISSUE(bookID,memberID) VALUES ("+
                "'"+ bookID +"',"+
                "'"+ memberID +"'"+
                ")";
            String st2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookID + "'";
            System.out.println(st1 + " and " + st2);
       
            if(handler.execAction(st1)&& handler.execAction(st2)){
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Issue Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Issue was Successful");
                alert1.showAndWait();
                clearBookCache();
                clearMemberCache();
        
            }else{
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("Issue Fail");
                alert2.setHeaderText(null);
                alert2.setContentText("Issue Operation Failed");
                alert2.showAndWait();
            }      
        }else{
            Alert alert3 = new Alert(Alert.AlertType.ERROR);
                alert3.setTitle("Issue Cancelled");
                alert3.setHeaderText(null);
                alert3.setContentText("Issue Operation cancelled");
                alert3.showAndWait();
        }
           
    }

    @FXML
    private void loadBookIssueInfo(ActionEvent event) {
        ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadForReturn = false;
        
        String id = rBookID.getText();
        String qu = "SELECT * FROM ISSUE WHERE bookID = '" + id +"'";
        System.out.println(qu);
        ResultSet rs = handler.execQuery(qu);
        try {
            while(rs.next()){
                String rBookId = rs.getString("bookID");
                String rMemberId = rs.getString("memberID");
                Timestamp rIssueTime = rs.getTimestamp("issueTime");
                int rRenewCount = rs.getInt("renew_count");
                
                issueData.add("Issued Time and Date : " + rIssueTime.toGMTString());
                issueData.add("Renew Count : " + rRenewCount);
                issueData.add("");
                issueData.add("Book Information:~");
                
                qu = "SELECT * FROM BOOK WHERE id = '" + id +"'";
                System.out.println(qu);
                ResultSet rl = handler.execQuery(qu);
                while(rl.next()){
                    String rBookTitle = rl.getString("title");
                    String rBookAuthor = rl.getString("author");
                    String rBookPublisher = rl.getString("publisher");
                    String rBookCategory = rl.getString("category");
                    
                    issueData.add("Book ID : " + rBookId);
                    issueData.add("Title : " + rBookTitle);
                    issueData.add("Author : " + rBookAuthor);
                    issueData.add("Publisher : " + rBookPublisher);
                    issueData.add("Category : " + rBookCategory);
                    issueData.add("");
                }
                
                qu = "SELECT * FROM MEMBER WHERE id = '" + rMemberId +"'";
                System.out.println(qu);
                rl = handler.execQuery(qu);
                while(rl.next()){
                    String rMemberName = rl.getString("name");
                    String rMemberRegNo = rl.getString("regNo");
                    String rMemberIntake = rl.getString("intake");
                    String rMemberEmail = rl.getString("email");
                    String rMemberContact = rl.getString("mobile");
                    
                    issueData.add("Member Information:~");
                    issueData.add("Member ID : " + rMemberId);
                    issueData.add("Name : " + rMemberName);
                    issueData.add("Registration Number : " + rMemberRegNo);
                    issueData.add("Intake : " + rMemberIntake);
                    issueData.add("Email : " + rMemberEmail);
                    issueData.add("Telephone : " + rMemberContact );
                }
                isReadForReturn = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        issueDataList.getItems().setAll(issueData);
    }

    @FXML
    private void returnBookOperation(ActionEvent event) {
        if(!isReadForReturn){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please enter the correct Book ID");
            alert.showAndWait();
            return;
        }
        
        String id = rBookID.getText();
        String st1 = "SELECT memberID,issueTime FROM ISSUE WHERE bookID = '" + id +"'";
        System.out.println(st1);
        String st2 = "SELECT title FROM BOOK WHERE id = '" + id +"'";
        System.out.println(st2);
        ResultSet r1,r2,r3;
        r1 = handler.execQuery(st1);
        r2 = handler.execQuery(st2);
        
        try {
            while(r2.next()){
                String rBookTitle = r2.getString("title");
            
            while(r1.next()){
                String rMemberId = r1.getString("memberID");
                String rIssueTime = r1.getString("issueTime");
                
                String st3 = "SELECT name,mobile FROM MEMBER WHERE id = '" + rMemberId +"'";
                System.out.println(st3);
                r3 = handler.execQuery(st3);
                while(r3.next()){
                    String rMemberName = r3.getString("name");
                    String rMemberContact = r3.getString("mobile");
                
     
                String qu = "INSERT INTO RETURN(bookID,bookTitle,memberID,memberName,memberMobile,issueTime) VALUES ("+
                    "'"+ id +"',"+
                    "'"+ rBookTitle +"',"+
                    "'"+ rMemberId +"',"+
                    "'"+ rMemberName +"',"+
                    "'"+ rMemberContact +"',"+
                    "'"+ rIssueTime +"'"+
                ")";
                System.out.println(qu);
                if(handler.execAction(qu)){
                    System.out.println("Book added to return list Successfully");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Book Return success");
                    alert.showAndWait();
                        
                    String st4 = "DELETE FROM ISSUE WHERE bookID = '"+ id + "'";
                    String st5 = "UPDATE BOOK SET isAvail = TRUE WHERE id = '"+ id + "'";
                    if(handler.execAction(st4)&&handler.execAction(st5)){
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("Success");
                        alert1.setHeaderText(null);
                        alert1.setContentText("Book Return success");
                        alert1.showAndWait();
                    }
                }else{
                    System.out.println("Failed to add Book to Return list");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Book Return failed");
                    alert.showAndWait();
                }
            }
            }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }    

    @FXML
    private void loadSearchMode(ActionEvent event) {
    }

    @FXML
    private void loadAutoCompleteBookInfo(KeyEvent event) {
        switch(event.getCode()){
            case BACK_SPACE:
                break;
            case ENTER:
                bookIDInput.setText(bookIDInput.getText());
                
                break;
            default:
                EventQueue.invokeLater(new Runnable(){
                    
                    public void run(){
                        String test = bookIDInput.getText();
                        autoComplete(test);
                }
                
            });
        
    }
    }

    @FXML
    private void loadAutoCompleteBookInfo2(KeyEvent event) {
        String title = bookIDInput.getText();
        String qu = "SELECT * FROM BOOK WHERE title = '" + title +"'";
        System.out.println(qu);
        ResultSet rs = handler.execQuery(qu);
        try{
            while(rs.next()){
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");
                Boolean notDeleted = rs.getBoolean("isNotDeleted");
                
                autoFill.add(bName);
                switch(event.getCode()){
                    case BACK_SPACE:
                        break;
                    case ENTER:
                        bookIDInput.setText(bookIDInput.getText());
                
                        break;
                    default:
                    EventQueue.invokeLater(new Runnable(){
                    
                        public void run(){
                            String test = bookIDInput.getText();
                            autoComplete(test);
                        }
                
                    });
                }
                
                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                //String status = (bStatus)?"Available" : "Not Available";
                if(bStatus && notDeleted){
                    bookStatus.setText("Available");
                }else{
                    bookStatus.setText("Not Available");
                }
            
        }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @FXML
    private void loadSettingsWindow(ActionEvent event) {
        loadWindow("/library/manager/ui/settings/settings.fxml","Settings");
    }

    @FXML
    private void handleMenuCloseOp(ActionEvent event) {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    @FXML
    private void handleMenuAbout(ActionEvent event) {
    }
}
