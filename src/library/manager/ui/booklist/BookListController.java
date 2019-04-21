
package library.manager.ui.booklist;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
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
import library.manager.ui.addbook.AddBookController;
import library.manager.ui.main.MainController;


public class BookListController implements Initializable {
    ObservableList<Book> list = FXCollections.observableArrayList();

    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> idCol;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> publisherCol;
    @FXML
    private TableColumn<Book, String> categoryCol;
    @FXML
    private TableColumn<Book, Boolean> availabilityCol;
    @FXML
    private TableColumn<Book, String> quantityCol;
   

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }    

    private void initCol() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void loadData() {
        list.clear();
        DatabaseHandler handler = DatabaseHandler.getInstance();
        try {
            String qu = "SELECT * FROM BOOK";
            ResultSet rs = handler.execQuery(qu);
            while(rs.next()){
                String id = rs.getString("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String publisher = rs.getString("publisher");
                String category = rs.getString("category");
                String quantity = rs.getString("quantity");
                Boolean avail = rs.getBoolean("isavail");
                Boolean bookStatus = rs.getBoolean("isNotDeleted");
                
                /**
                 * Checking if the book is still available #NOT DELETE
                 */
                if(bookStatus){
                    list.add(new Book(id, title, author, publisher, category, avail, quantity));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.setItems(list);
    }

    @FXML
    private void handleRefreshBookList(ActionEvent event) {
        loadData();
        
    }

    @FXML
    private void handleEditBookOp(ActionEvent event) {
        //Fetch selected row
        Book selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Select Book!");
            alert.setContentText("No book selected");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/manager/ui/addbook/addBook.fxml"));
            Parent parent = loader.load();
            
            AddBookController controller = (AddBookController) loader.getController();
            controller.inflateUI(selectedForEdit);
            
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            stage.show();
            
            
            stage.setOnCloseRequest((e)->{
                handleRefreshBookList(new ActionEvent());
            });
            
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @FXML
    private void handleDeleteBookOp(ActionEvent event) {
        //Fetch selected row
        Book selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if (selectedForDeletion == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Select Book!");
            alert.setContentText("No book selected");
            return;
        }
        
        Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
        alert1.setTitle("Deleting Book");
        alert1.setHeaderText(null);
        alert1.setContentText("Are you sure want to delete " + selectedForDeletion.getTitle() + "?");
        Optional<ButtonType> answer = alert1.showAndWait();
        if(answer.get() == ButtonType.OK){
            //if the Button pressed is an OK Button
            try {
                DatabaseHandler dataHandler =  DatabaseHandler.getInstance();//.deleteBook(selectedForDeletion);
                /**
                 * Creating an exception for deleting already issued Book
                 */
                
                String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE bookID = '" + selectedForDeletion.getId()+ "'";
                System.out.println(checkstmt);
                ResultSet rs = dataHandler.execQuery(checkstmt);
                if(rs.next()){
                    int count = rs.getInt(1);
                    if(count>0){
                        //when true; i.e. when there is a record of the same book already issued to a member
                        System.out.println(count);
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Can't be deleted");
                        alert.setContentText(selectedForDeletion.getTitle() + " \n This Book is already Issued and can't be deleted");
                        alert.showAndWait();
                    }else{
                        //when false; i.e. when there is no record of the same book issued to a member
                        System.out.println(count);
                        String stmt = "UPDATE BOOK SET isNotDeleted = false WHERE id = '" + selectedForDeletion.getId() + "'";
                        System.out.println(stmt);
                        if(dataHandler.execAction(stmt)){
                            //If the deletion is a success
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Book deleted");
                            alert.setContentText(selectedForDeletion.getTitle() + " was deleted successfully");
                            alert.showAndWait();
                            list.remove(selectedForDeletion);
                        }else{
                            //if the deletion is a failure
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Deletion Failed");
                            alert.setContentText(selectedForDeletion.getTitle() + " couldn't be deleted successfully");
                            alert.showAndWait();
                        }//end of else(deletion failure)
                    }//end of else (deletion success)
                }//end of query execution statement
            } catch (SQLException ex) {
                Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }else{
            //if the Button Button pressed is a cancel Button 
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Deletion cancelled");
            alert.setContentText("Book deletion cancelled");
            alert.showAndWait();
                
        }//end of else (Button cancel)
    }// end of if (Button OK)

    
    public static class Book{
        private final SimpleStringProperty id;
        private final SimpleStringProperty title;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleStringProperty category;
        private final SimpleBooleanProperty availability;
        private final SimpleStringProperty quantity;

        public Book(String id, String title, String author, String publisher, String category, Boolean avail, String quantity) {
            this.id = new SimpleStringProperty(id);
            this.title = new SimpleStringProperty(title);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(publisher);
            this.category = new SimpleStringProperty(category);
            this.availability = new SimpleBooleanProperty(avail);
            this.quantity = new SimpleStringProperty(quantity);
        }

        public String getId() {
            return id.get();
        }

        public String getTitle() {
            return title.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getPublisher() {
            return publisher.get();
        }

        public String getCategory() {
            return category.get();
        }

        public Boolean getAvailability() {
            return availability.get();
        }

        public String getQuantity() {
            return quantity.get();
        }

        
    }
}
    
    
   
