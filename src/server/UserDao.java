package server;

import java.sql.*;
import java.util.ArrayList;

public interface UserDao {
    public Connection getConnectionUser();
    public void closeConnectionUser(Connection con);
    public void closeResultSet(ResultSet res);
    public void closeStatement(Statement stm);
    public boolean loginCheck(String id, String pwd);
    public int AddGChatHistory(String message);
//  public ArrayList<String> GetGChatHistory();
}
