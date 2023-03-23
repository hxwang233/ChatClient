package server;

import java.sql.*;

public class UserDaoImpl implements UserDao{
	
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
	public boolean loginCheck(String account, String pwd) {
		// TODO Auto-generated method stub
		Connection con=getConnectionUser();
		try {
			Statement stm=con.createStatement();
			ResultSet res=stm.executeQuery("SELECT * FROM userinfo WHERE account='"+account+"'and pwd='"+pwd+"' ");
			while(res.next()) {
				return true;
			}
			closeResultSet(res);
			closeStatement(stm);
		}catch(Exception e){
			e.printStackTrace();
		}
		finally {		
			closeConnectionUser(con);
		}
		return false;
	}


	@Override
	public int AddGChatHistory(String message) {
		// TODO Auto-generated method stub
		int flag=0;
		if(message!=null) {
			try {
				Connection con=getConnectionUser();	
				Statement stm=con.createStatement();
				flag=stm.executeUpdate("INSERT INTO groupchathistory (history) VALUES('"+message+"')");		
				closeStatement(stm);
				closeConnectionUser(con);
			}catch(Exception e){
				e.printStackTrace();
			}		
		}
		return flag;
	}
}
