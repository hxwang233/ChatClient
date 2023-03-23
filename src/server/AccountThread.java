package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextArea;

class AccountThread extends Thread{
	DataList datalist;//数据存储器
	Socket socket;
	JTextArea textarea;
	Boolean exit=false;
	 DataInputStream in;
	 DataOutputStream out;
	public AccountThread(Socket socket,JTextArea textarea) {

		this.socket=new Socket();
		this.socket=socket;
		this.textarea=textarea;
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		start();
				textarea.append("\n------------新线程------------------------\n");
				textarea.setCaretPosition(textarea.getText().length());//信息定位到末尾
	}
	public void run() {
		 String response=null;
		while(!exit) {
			try {
				textarea.append("远程主机地址：" + socket.getRemoteSocketAddress()+"\n");
				
				String requestData=in.readUTF();
				textarea.append(requestData+"\n");
				
				response=feachInstruction(requestData);
				textarea.append(response+"\n");
				out.flush();
				out.writeUTF(response);
			//处理指令
				if(response.equals("success")&&datalist.getDataNamed("inst").equals("REGISTER")) {
					writeUserInfo(datalist);
					textarea.append("用户注册信息写入成功\n");
				}else if(response.equals("exist")&&datalist.getDataNamed("inst").equals("RETRIEVE_Search")) {
					String data="exist#"+getUserData(datalist.getDataNamed("account"));
					out.writeUTF(data);
					out.flush();
					textarea.append("用户信息发送成功\n");
				}else if(response.equals("OK")&&datalist.getDataNamed("inst").equals("MODIFYPASSWORD")) {
					modifyPassword(datalist.getDataNamed("account"), datalist.getDataNamed("password"));
				}
	    	 	textarea.setCaretPosition(textarea.getText().length());//信息定位到末尾
			}catch(Exception e) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
					break;
			}
		}
			 textarea.append("\n-----------------线程停止！----------------------\n");
			 textarea.setCaretPosition(textarea.getText().length());//信息定位到末尾
	}
	/**
	 * 处理请求信息,返回响应指令
	 * @param resquest
	 */
	public String feachInstruction(String requestData) {
		boolean exist=false;
		String response="null";
		datalist=this.getDataList(requestData);
		if(isExistUser(datalist.getDataNamed("account"))) {
			exist=true;
		}
		datalist.printData();
		String inst=datalist.getDataNamed("inst");
		if(inst.equals("REGISTER")) {	//注册
			if(!exist)
				response="success";
			else
				response="exist";
		}else if(inst.equals("RETRIEVE_Search")) {	//找回密码
			if(exist)
				response="exist";
			else
				response="notfound";
		}else if(inst.equals("MODIFYPASSWORD")) {//修改密码
			response="OK";
		}else if(inst.equals("close")) {//关闭线程
			exit=true;
		}
		return response;
	}
	/**
	 * 获取请求数据列表
	 */
	public DataList getDataList(String requestData) {
		DataList dataList=new DataList();
		String dataName;
		String data;
		int to=0,from=0;
			to=requestData.indexOf("#");
			String inst=requestData.substring(from, to);//获取指令
			dataList.addParameter("inst", inst);
		while(to<requestData.length()) {

			from=to+1;
			to=requestData.indexOf("=",from);
			if(to==-1) {
				to=requestData.length();
			}
			dataName=requestData.substring(from, to);//获取参数名
			
			from=to+1;
			to=requestData.indexOf("&",from);
			if(to==-1) {
				to=requestData.length();
			}
			data=requestData.substring(from,to);//获取数据
			dataList.addParameter(dataName, data);
		}
		return dataList;
	}
	/**
	* 数据列表类
	*/
	class DataList {
		class DataStruct{
			private String name;
			private String data;
			public void setName(String name) {this.name=name;}
			public void setData(String data) {this.data=data;}
			public String getName() {return name;}
			public String getData() {return data;}
		}
		private List<DataStruct> datalist=new ArrayList<DataStruct>();
		/**
		 * 存入数据
		 * @param name 数据名
		 * @param data 具体数据
		 */
		public void addParameter(String name,String data) {
			DataStruct dataStruct=new DataStruct();
			dataStruct.setName(name);
			dataStruct.setData(data);
			datalist.add(dataStruct);
		}
		/**
		 * 根据数据名获取数据内容
		 * @param name
		 * @return data，null
		 */
		public String getDataNamed(String name) {
			Iterator<DataStruct> t=datalist.iterator();
			while(t.hasNext()) {
				DataStruct ds=t.next();
				if(ds.getName().equals(name)) {
					return ds.getData();
				}
			}
			return null;
		}
		/**
		 * 查看存入数据
		 */
		public void printData() {
			Iterator<DataStruct> t=datalist.iterator();
			while(t.hasNext()) {
				DataStruct ds=t.next();
				System.out.println("参数名："+ds.getName()+"数据："+ds.getData());
			}
		}
	}


	/**注册用户数据写入数据库*/
	public void writeUserInfo(DataList data) {
		UserDaoImpl user=new UserDaoImpl();
		Connection con=user.getConnectionUser();
		try {
			Statement stmt=con.createStatement();
			String sql="INSERT INTO userinfo VALUES("+null+",\""+data.getDataNamed("account")+"\""+
					",\""+data.getDataNamed("password")+"\","+Integer.parseInt(data.getDataNamed("answer1ID"))+
					",\""+data.getDataNamed("answer1")+"\","+Integer.parseInt(data.getDataNamed("answer2ID"))+
					",\""+data.getDataNamed("answer2")+"\")";
			stmt.executeUpdate(sql);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**判断账号知否存在*/
	public boolean isExistUser(String account){
		boolean exist=false;
		UserDaoImpl user=new UserDaoImpl();
		Connection con=user.getConnectionUser();
		try {
			Statement stmt=con.createStatement();
			String sql="SELECT id FROM userinfo WHERE account=\""+account+"\"";
			ResultSet rs=stmt.executeQuery(sql);
			if(rs.next()) exist=true;
			
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exist;
	}
	/**获取数据数据库数据*/
	public String getUserData(String account) {
		String data="";
		UserDaoImpl user=new UserDaoImpl();
		Connection con=user.getConnectionUser();
		try {
			Statement stmt=con.createStatement();
			String sql="SELECT answer1ID,answer1,answer2ID,answer2 FROM userinfo WHERE account=\""+account+"\"";
			ResultSet rs=stmt.executeQuery(sql);
			if(rs.next()) {
				data="answer1ID="+rs.getString("answer1ID")+"&answer1="+rs.getString("answer1")+
						"&answer2ID="+rs.getString("answer2ID")+"&answer2="+rs.getString("answer2");
			}
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	/**修改密码*/
	public void modifyPassword(String account,String newPassword) {
		UserDaoImpl user=new UserDaoImpl();
		Connection con=user.getConnectionUser();
		try {
			Statement stmt=con.createStatement();
			String sql="UPDATE userInfo SET pwd=\""+newPassword+"\" WHERE account=\""+account+"\"";
			stmt.executeUpdate(sql);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

