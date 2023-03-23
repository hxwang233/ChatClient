package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**连接服务器*/
public class AccountSocket {
	private Socket client;	//客户端socket
	/**本地访问*/
	public AccountSocket(int port){
		String serverName="127.0.0.1";
		try{
			System.out.println("连接到主机：" + serverName + " ，端口号：" + port);
			client = new Socket(serverName, port);
			System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**外网访问*/
	public AccountSocket(String serverName,int port){
		try{
			InetAddress inet=InetAddress.getByName(serverName);
			System.out.println("连接到主机：" + serverName + " ，端口号：" + port);
			client = new Socket(inet, port);
			System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 向服务器发送请求信息
	 */
	public void sendRequest(String request) {
		OutputStream outToServer;
		try {
			outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF(request);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 接收服务器响应
	 */
	public String getResponse() {
		InputStream inFromServer;
		String response=null;
		try {
			inFromServer = client.getInputStream();
			DataInputStream in=new DataInputStream(inFromServer);
			response=in.readUTF();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	/**
	 * 关闭客户端Socket
	 */
	public void closeClientSocket() {
		
		try {
			this.sendRequest("close#");
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean isClosed() {
		return client.isClosed();
	}
}
