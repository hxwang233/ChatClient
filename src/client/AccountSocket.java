package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**���ӷ�����*/
public class AccountSocket {
	private Socket client;	//�ͻ���socket
	/**���ط���*/
	public AccountSocket(int port){
		String serverName="127.0.0.1";
		try{
			System.out.println("���ӵ�������" + serverName + " ���˿ںţ�" + port);
			client = new Socket(serverName, port);
			System.out.println("Զ��������ַ��" + client.getRemoteSocketAddress());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**��������*/
	public AccountSocket(String serverName,int port){
		try{
			InetAddress inet=InetAddress.getByName(serverName);
			System.out.println("���ӵ�������" + serverName + " ���˿ںţ�" + port);
			client = new Socket(inet, port);
			System.out.println("Զ��������ַ��" + client.getRemoteSocketAddress());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * �����������������Ϣ
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
	 * ���շ�������Ӧ
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
	 * �رտͻ���Socket
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
