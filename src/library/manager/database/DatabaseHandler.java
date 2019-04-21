package library.manager.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import library.manager.ui.booklist.BookListController.Book;
import library.manager.ui.memberlist.MemberListController.Member;
import org.apache.commons.codec.digest.DigestUtils;


public final class DatabaseHandler {
    private static DatabaseHandler handler = null;
    
    private static final String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;
    
    private DatabaseHandler(){
        createConnection();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();
        setupReturnTable();
        setupLginTable();
    }
    
    public static DatabaseHandler getInstance(){
		if(handler == null){
			handler = new DatabaseHandler();
		}
		return handler;
	}
    
   void createConnection(){
       try{
           Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
           conn = DriverManager.getConnection(DB_URL);
           
       }catch(ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e){
           JOptionPane.showMessageDialog(null, "Can't Load Database", "Database Error", JOptionPane.ERROR_MESSAGE);
           System.exit(0);
       }
   }
   
   private void setupBookTable(){
       String TABLE_NAME = "BOOK";
       try{
           stmt = conn.createStatement();
           
           DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
           
           if(tables.next()){
               System.out.println("Table " + TABLE_NAME + " already exists. Ready for go!");
           }else{
               stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                       + "      id varchar(200) primary key,\n"
                       + "      title varchar(200),\n"
                       + "      author varchar(200),\n"
                       + "      publisher varchar(100),\n"
                       + "      category varchar(100),\n"
                       + "      quantity Integer,\n"
                       + "      isAvail boolean default true,\n"
                       + "      isNotDeleted boolean default true"
                       + ")");
           }
       }catch(SQLException e){
           System.err.println(e.getMessage() + "--- setupDatabase");
       }finally{
       }
   }
   
   private void setupMemberTable() {
        String TABLE_NAME = "MEMBER";
        try{
            stmt = conn.createStatement();
            
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go");
            }else{
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                       + "      id varchar(200) primary key,\n"
                       + "      regNo varchar(200),\n"
                       + "      name varchar(200),\n"
                       + "      intake varchar(100),\n"
                       + "      email varchar(100),\n"
                       + "      mobile varchar(15),\n"
                       + "      isMemberAvail boolean default true"
                       + ")");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
   /**
    * Setting up a login database
    */
   private void setupLginTable() {
        String TABLE_NAME = "LOGIN";
        try{
            stmt = conn.createStatement();
            
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go");
            }else{
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                       + "      userName varchar(200) primary key,\n"
                       + "      pasword varchar(200)"
                       + ")");
                
                
                stmt.execute("INSERT INTO LOGIN VALUES ("+
                        "'"+ "admin" +"',"+
                        "'"+ DigestUtils.shaHex("admin123") +"'"+
                        ")");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
   
   public ResultSet execQuery(String query){
       ResultSet result;
       try{
           stmt = conn.createStatement();
           result = stmt.executeQuery(query);
       }catch(SQLException ex){
           System.out.println("Exception at execQuery:databaseHandler " + ex.getLocalizedMessage());
           return null;
       }finally{
           
       }
       return result;
   }
   
   public boolean execAction(String qu){
       try{
           stmt = conn.createStatement();
           stmt.execute(qu);
           return true;
       }catch(SQLException ex){
           JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
           System.out.println("Exception at execAction:databaseHandler " + ex.getLocalizedMessage());
           return false;
       }
   }

    private void setupIssueTable() {
        String TABLE_NAME = "ISSUE";
       try{
           stmt = conn.createStatement();
           
           DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
           
           if(tables.next()){
               System.out.println("Table " + TABLE_NAME + " already exists. Ready for go!");
           }else{
               stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                       + "      bookID varchar(200) primary key,\n"
                       + "      memberID varchar(200),\n"
                       + "      issueTime timestamp default CURRENT_TIMESTAMP,\n"
                       + "      renew_count integer default 0,\n"
                       + "      FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                       + "      FOREIGN KEY (memberID) REFERENCES MEMBER(id)"
                       + ")");
           }
       }catch(SQLException e){
           System.err.println(e.getMessage() + "--- setupDatabase");
       }finally{
       }
    }
       
    private void setupReturnTable(){
        String TABLE_NAME = "RETURN";
       try{
           stmt = conn.createStatement();
           
           DatabaseMetaData dbm = conn.getMetaData();
           ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
           
           if(tables.next()){
               System.out.println("Table " + TABLE_NAME + " already exists. Ready for go!");
           }else{
               stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                       + "      id Integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n"
                       + "      bookID varchar(200),\n"
                       + "      bookTitle varchar(200),\n"
                       + "      memberID varchar(200),\n"
                       + "      memberName varchar(200),\n"
                       + "      memberMobile varchar(15),\n"
                       + "      issueTime varchar(100),\n"
                       + "      returnTime timestamp default CURRENT_TIMESTAMP,\n"
                       + "      FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                       + "      FOREIGN KEY (memberID) REFERENCES MEMBER(id)"
                       + ")");
           }
       }catch(SQLException e){
           System.err.println(e.getMessage() + "--- setupDatabase");
       }finally{
       }
    }
   /* 
    public boolean isBookAlreadyIssued(Book book){
        try {
            String checkstmt = "SELECT COUNT(*) ISSUE WHERE bookID = ?";
            PreparedStatement ps = conn.prepareStatement(checkstmt);
            ps.setString(1, book.getId());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                if(count >0){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
        
    }
*/
    
    public boolean updateBook(Book book){
        /*stmt.execute("CREATE TABLE " + TABLE_NAME + "("
        + "      id varchar(200) primary key,\n"
        + "      title varchar(200),\n"
        + "      author varchar(200),\n"
        + "      publisher varchar(100),\n"
        + "      category varchar(100),\n"
        + "      quantity Integer,\n"
        + "      isAvail boolean default true,\n"
        + "      isNotDeleted boolean default true"
        + ")");
        }*/
        try {
            String stmt = "UPDATE BOOK SET title = ?, author = ?, publisher=?, category=?, quantity=? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublisher());
            ps.setString(4, book.getCategory());
            ps.setString(5, book.getQuantity());
            ps.setString(6, book.getId());
            int rs = ps.executeUpdate();
            System.out.println(stmt);
            return (rs>0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
        
    }

    public boolean updateMember(Member member) {
        /*+ "      id varchar(200) primary key,\n"
        + "      regNo varchar(200),\n"
        + "      name varchar(200),\n"
        + "      intake varchar(100),\n"
        + "      email varchar(100),\n"
        + "      mobile varchar(15),\n"
        + "      isMemberAvail boolean default true"
        + ")");*/
        
        try {
            String stmt = "UPDATE MEMBER SET regNo = ?, name = ?, intake =?, email = ?, mobile=? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.setString(1, member.getRegNo());
            ps.setString(2, member.getName());
            ps.setString(3, member.getIntake());
            ps.setString(4, member.getEmail());
            ps.setString(5, member.getMobile());
            ps.setString(6, member.getId());
            int rs = ps.executeUpdate();
            System.out.println(stmt);
            return (rs>0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
 
}
   
