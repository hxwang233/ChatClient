package client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public interface HistoryDao {
	public Connection getConnectionUser();
	public void closeConnectionUser(Connection con);
    public void closeResultSet(ResultSet res);
    public void closeStatement(Statement stm);
    public ArrayList<String> getGChatHistory();
    public ArrayList<String> getPChatHistory();
}
