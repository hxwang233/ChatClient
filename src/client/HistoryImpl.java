package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class HistoryImpl implements HistoryDao{

	@Override
	public Connection getConnectionUser() {
		Connection con=null;
		try{
		    Class.forName("com.mysql.cj.jdbc.Driver");
		    System.out.println("数据库加载驱动成功");
		}catch(ClassNotFoundException e){
		    e.printStackTrace();
		}
		try{
		    con=DriverManager.getConnection("jdbc:mysql://localhost:3306/qquser?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false","root","WHX@YWX");
		    System.out.println("数据库连接成功");
		}catch(SQLException e){
		    e.printStackTrace();
		}
		
		return con;
	}

	@Override
	public void closeConnectionUser(Connection con) {
		// TODO Auto-generated method stub
		try {
			if(con!=null&&(!con.isClosed())) {
				con.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void closeResultSet(ResultSet res) {
		// TODO Auto-generated method stub
		try {
			if(res!=null) {
				res.close();
				res=null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void closeStatement(Statement stm) {
		// TODO Auto-generated method stub
		try {
			if(stm!=null) {
				stm.close();
				stm=null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<String> getGChatHistory() {
		// TODO Auto-generated method stub
		ArrayList<String> list=new ArrayList<String>();
		Connection con=getConnectionUser();
		try {
			Statement stm=con.createStatement();
			ResultSet res=stm.executeQuery("SELECT history FROM groupchathistory");
			while(res.next()) {
				list.add(res.getString("history"));
			}
			closeResultSet(res);
			closeStatement(stm);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {		
			closeConnectionUser(con);
		}
		return list;
	}

	@Override
	public ArrayList<String> getPChatHistory() {
		// TODO Auto-generated method stub
		return null;
	}

}
