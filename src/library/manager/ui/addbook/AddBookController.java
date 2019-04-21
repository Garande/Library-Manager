
package library.manager.ui.addbook;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.manager.database.DatabaseHandler;
import library.manager.ui.booklist.BookListController;


public class AddBookController implements Initializable {

     @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField id;
     @FXML
    private JFXTextField title;
     @FXML
    private JFXTextField author;
     @FXML
    private JFXTextField publisher;
     @FXML
    private JFXTextField category;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;

    DatabaseHandler databaseHandler;
    @FXML
    private JFXTextField quantity;
    private Boolean isInEditMode = Boolean.FALSE;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
        checkData();
        
    }    

     @FXML
    private void addBook(ActionEvent event) {
        
        String BookID = id.getText();
        String BookTitle = title.getText();
        String BookAuthor = author.getText();
        String BookPublisher = publisher.getText();
        String BookCategory = category.getText();
        String sBookQuantity = quantity.getText();
        int BookQuantity = Integer.parseInt(sBookQuantity);
        
        if(BookID.isEmpty()||BookTitle.isEmpty()||BookAuthor.isEmpty()||BookPublisher.isEmpty()||BookCategory.isEmpty()||sBookQuantity.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter Data in all Fields Corectly");
            alert.showAndWait();
            return;
        }
        
        if(isInEditMode){
            handleEditBookOp();
            return;
        }
        
        String qu = "INSERT INTO BOOK VALUES ("+
                "'"+ BookID +"',"+
                "'"+ BookTitle +"',"+
                "'"+ BookAuthor +"',"+
                "'"+ BookPublisher +"',"+
                "'"+ BookCategory +"',"+
                ""+ BookQuantity +","+
                ""+ "true" +","+
                ""+ "true" +""+
                ")";
        System.out.println(qu);
       
        if(databaseHandler.execAction(qu)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Book added Successfully");
            alert.showAndWait();
            clearBookCache();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Book addition failed");
            alert.showAndWait();
        }
    }
    void clearBookCache(){
        id.setText("");
        title.setText("");
        author.setText("");
        publisher.setText("");
        category.setText("");
        quantity.setText("");
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
    }

    private void checkData() {
        try {
            String qu = "SELECT id FROM BOOK";
            ResultSet rs = databaseHandler.execQuery(qu);
            while(rs.next()){
                String idNo = rs.getString("id");
                System.out.println(idNo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void inflateUI(BookListController.Book book){
        id.setText(book.getId());
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        category.setText(book.getCategory());
        quantity.setText(book.getQuantity());
        id.setEditable(false);
        isInEditMode = Boolean.TRUE;
    }

    private void handleEditBookOp() {
        BookListController.Book book = new BookListController.Book(id.getText(), title.getText(), author.getText(), publisher.getText(), category.getText(), true, quantity.getText());
        if(databaseHandler.updateBook(book)){
            Alert alert = new Alert (Alert.AlertType.INFORMATION);
            alert.setHeaderText("Success");
            alert.setTitle("Book Update");
            alert.setContentText("Book Updated successfully");
            alert.showAndWait();
        }else{
            Alert alert = new Alert (Alert.AlertType.ERROR);
            alert.setHeaderText("Failed");
            alert.setTitle("Book Update");
            alert.setContentText("Book Update failed");
            alert.showAndWait();
        }
        
    }
}
   
